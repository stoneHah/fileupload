package com.zq.learn.fileuploader.support.batch.reader;

import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.eventusermodel.*;
import org.apache.poi.hssf.eventusermodel.dummyrecord.LastCellOfRowDummyRecord;
import org.apache.poi.hssf.record.*;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Row;
import org.apache.xmlbeans.impl.common.IOUtil;
import org.jberet.support._private.SupportLogger;
import org.jberet.support._private.SupportMessages;
import org.jberet.support.io.ItemReaderWriterBase;
import org.springframework.batch.item.*;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.batch.item.support.AbstractItemStreamItemReader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.batch.api.BatchProperty;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * ${DESCRIPTION}
 *
 * @author qun.zheng
 * @create 2018/2/5
 **/
public class ExcelEventItemReader<T> extends AbstractItemStreamItemReader<T> implements ResourceAwareItemReaderItemStream<T>{

    private int sheetIndex = 0;
    private int end = 0;
    private int start = 0;
    private String sheetName;
    private int maxColumnNum = 0;
    protected int currentRowNum;
    protected String[] header;
    protected Map<String, String> headerMapping;
    protected Integer headerRow = 0;

    private int linesToSkip = 0;
    private Resource resource;
    private RowMapper<T> rowMapper;

    /**
     * Maximum worksheet row numbers for Excel 2003: 65,536 (2 ** 16)
     * http://office.microsoft.com/en-us/excel-help/excel-specifications-and-limits-HP005199291.aspx
     */
    protected static final int MAX_WORKSHEET_ROWS = 65536;

    /**
     * the capacity of the queue used by {@code org.apache.poi.hssf.eventusermodel.HSSFListener} to hold pre-fetched
     * data rows. Optional property and defaults to {@link #MAX_WORKSHEET_ROWS} (65536).
     */
    protected int queueCapacity;

    private BlockingQueue<Object> queue;
    private DocumentInputStream documentInputStream;
    private FormatTrackingHSSFListener formatListener;

    @Override
    public T read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        final Object result = queue.take();
        if (result instanceof Exception) {
            if (result instanceof ReadCompletedException) {
                return null;
            }
            throw (Exception) result;
        }

        Map<String,String> colMap = (Map<String, String>) result;
        String[] values = new String[maxColumnNum + 1];
        for(int i = 0;i <= maxColumnNum;i++) {
            String val = colMap.get(String.valueOf(i));
            values[i] = val == null ? "" : val;
        }


        return (T) rowMapper.mapRow(values);
    }

    @Override
    public void open(ExecutionContext executionContext) {
        if (this.end == 0) {
            this.end = Integer.MAX_VALUE;
        }
        if (headerRow == null) {
            if (header == null) {
                throw SupportMessages.MESSAGES.invalidReaderWriterProperty(null, null, "header | headerRow");
            }
            headerRow = -1;
        }
        if (start == headerRow) {
            start += 1;
        }


        try {
            initWorkbookAndSheet();
        }
        catch (Exception e) {
            throw new ItemStreamException("Failed to initialize the reader", e);
        }
    }

    private void initWorkbookAndSheet() throws Exception {
        queue = new ArrayBlockingQueue<Object>(queueCapacity == 0 ? MAX_WORKSHEET_ROWS : queueCapacity);
        final POIFSFileSystem poifs = new POIFSFileSystem(resource.getInputStream());
        // get the Workbook (excel part) stream in a InputStream
        documentInputStream = poifs.createDocumentInputStream("Workbook");
        final HSSFRequest req = new HSSFRequest();
        final MissingRecordAwareHSSFListener missingRecordAwareHSSFListener = new MissingRecordAwareHSSFListener(new HSSFListenerImpl(this));
        /*
         * Need to use English locale her because Jackson double parsing might break in certain regions
         * where ',' is used as decimal separator instead of '.'.
         */
        formatListener = new FormatTrackingHSSFListener(missingRecordAwareHSSFListener, Locale.ENGLISH);
        req.addListenerForAllRecords(formatListener);
        final HSSFEventFactory factory = new HSSFEventFactory();


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    factory.processEvents(req, documentInputStream);
                } catch (final ReadCompletedException e) {
                    SupportLogger.LOGGER.tracef("Completed reading %s%n", resource);
                }
            }
        }).start();
    }

    @Override
    public void close() {
        super.close();
        try {
            IOUtils.closeQuietly(resource.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (documentInputStream != null) {
            try {
                documentInputStream.close();
            } catch (final Exception e) {
                SupportLogger.LOGGER.tracef(e, "Failed to close DocumentInputStream for %s%n", resource);
            }
        }
    }

    @Override
    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public void setRowMapper(RowMapper<T> rowMapper) {
        this.rowMapper = rowMapper;
    }

    public void setLinesToSkip(int linesToSkip) {
        this.linesToSkip = linesToSkip;
    }

    private static final class HSSFListenerImpl implements HSSFListener {
        private final ExcelEventItemReader itemReader;

        //to store 1 row of data
        Map<String, String> resultMap = new HashMap<String, String>();

        private SSTRecord sstrec;
        private String currentSheetName;
        private int currentSheetIndex = -1;
        private final ArrayList<BoundSheetRecord> boundSheetRecords = new ArrayList<BoundSheetRecord>();
        private BoundSheetRecord[] orderedBSRs;
        private final Map<Integer, String> headerIndexToLabelMapping = new HashMap<Integer, String>();
        private boolean readingHeaderRow;
        private boolean readingDataRow;

        /**
         * true if we are reading a sheet and this sheet is the target sheet
         */
        private boolean readingTargetSheet;

        private HSSFListenerImpl(final ExcelEventItemReader itemReader) {
            this.itemReader = itemReader;
            if (itemReader.header != null) {
                for (int i = 0; i < itemReader.header.length; ++i) {
                    headerIndexToLabelMapping.put(i, itemReader.header[i]);
                }
            }
        }

        @Override
        public void processRecord(final Record record) {
            String keyForNextStringRecord = null;
            try {
//                if (currentSheetName == null || itemReader.sheetName.equals(currentSheetName)) {
                    switch (record.getSid()) {
                        case BoundSheetRecord.sid:
                            final BoundSheetRecord sheetRec = (BoundSheetRecord) record;
                            boundSheetRecords.add(sheetRec);
                            break;
                        case BOFRecord.sid:
                            final BOFRecord bofRecord = (BOFRecord) record;
                            if (bofRecord.getType() != BOFRecord.TYPE_WORKBOOK) {
                                currentSheetIndex++;
                            }
                            if (bofRecord.getType() == BOFRecord.TYPE_WORKSHEET) {
                                orderedBSRs = BoundSheetRecord.orderByBofPosition(boundSheetRecords);
                                currentSheetName = orderedBSRs[currentSheetIndex].getSheetname();
                                readingTargetSheet = true;

                                /*orderedBSRs = BoundSheetRecord.orderByBofPosition(boundSheetRecords);
                                currentSheetName = orderedBSRs[currentSheetIndex].getSheetname();
                                if (itemReader.sheetName != null) {
                                    readingTargetSheet = currentSheetName.equals(itemReader.sheetName);
                                } else {
                                    if (currentSheetIndex == itemReader.sheetIndex) {
                                        itemReader.sheetName = currentSheetName;
                                        readingTargetSheet = true;
                                    } else {
                                        readingTargetSheet = false;
                                    }
                                }*/

                            }
                            break;
                        case SSTRecord.sid:
                            sstrec = (SSTRecord) record;
                            break;

                        case BlankRecord.sid:
                            if (readingTargetSheet) {
                                final BlankRecord rec = (BlankRecord) record;
                                readCellValues(rec.getRow(), (int) rec.getColumn(), null);
                            }
                            break;
                        case BoolErrRecord.sid:
                            if (readingTargetSheet) {
                                final BoolErrRecord rec = (BoolErrRecord) record;
                                final String val = rec.isError() ? String.valueOf(rec.getErrorValue()) : String.valueOf(rec.getBooleanValue());
                                readCellValues(rec.getRow(), (int) rec.getColumn(), val);
                            }
                            break;
                        case FormulaRecord.sid:
                            if (readingTargetSheet) {
                                final FormulaRecord rec = (FormulaRecord) record;

                                final int row = rec.getRow();
                                final int column = rec.getColumn();
                                String val;
                                if (Double.isNaN(rec.getValue())) {
                                    // Formula result is a string This is stored in the next record
                                    keyForNextStringRecord = headerIndexToLabelMapping.get(column);
                                } else {
                                    val = itemReader.formatListener.formatNumberDateCell(rec);
                                    readCellValues(row, column, val);
                                }
                            }
                            break;
                        case StringRecord.sid:
                            if (readingTargetSheet) {
                                if (keyForNextStringRecord != null) {
                                    // String for formula
                                    final StringRecord rec = (StringRecord) record;
                                    final String val = rec.getString();
                                    resultMap.put(keyForNextStringRecord, val);
                                    keyForNextStringRecord = null;
                                }
                            }
                            break;
                        case LabelRecord.sid:
                            if (readingTargetSheet) {
                                final LabelRecord rec = (LabelRecord) record;
                                readCellValues(rec.getRow(), rec.getColumn(), rec.getValue());
                            }
                            break;
                        case LabelSSTRecord.sid:
                            if (readingTargetSheet) {
                                final LabelSSTRecord rec = (LabelSSTRecord) record;
                                final String val = sstrec.getString(rec.getSSTIndex()).toString();
                                readCellValues(rec.getRow(), rec.getColumn(), val);
                            }
                            break;
                        case NumberRecord.sid:
                            if (readingTargetSheet) {
                                final NumberRecord rec = (NumberRecord) record;
                                final double val = rec.getValue();
                                readCellValues(rec.getRow(), rec.getColumn(), String.valueOf(val));
                            }
                            break;
                        case EOFRecord.sid:
//                            if (readingTargetSheet && readingDataRow) {
                            if (currentSheetIndex == boundSheetRecords.size() - 1) {
                                queueRowData(null, true);
                            }
                            break;
                        default:
                            break;
                    }

                // Handle end of row
                if (readingTargetSheet && record instanceof LastCellOfRowDummyRecord) {
                    final LastCellOfRowDummyRecord lastCellOfRowDummyRecord = (LastCellOfRowDummyRecord) record;
                    final int row = lastCellOfRowDummyRecord.getRow();
                    if (readingHeaderRow) {
                        itemReader.headerMapping = new HashMap<String, String>();
                        for (final Map.Entry<Integer, String> e : headerIndexToLabelMapping.entrySet()) {
                            itemReader.headerMapping.put(String.valueOf(e.getKey()), e.getValue());
                        }
                        if (itemReader.header == null) {
                            final List<String> headerList = new ArrayList<String>();
                            final int headerColumnCount = headerIndexToLabelMapping.size();
                            for (int i = 0; headerList.size() < headerColumnCount; i++) {
                                final String val = headerIndexToLabelMapping.get(i);
                                if (val != null) {
                                    headerList.add(val);
                                }
                            }
                            itemReader.header = headerList.toArray(new String[headerColumnCount]);
                        }
                        readingHeaderRow = false;
                    } else if (readingDataRow) {
                        queueRowData(null, false);
                    }
                    if (row >= itemReader.end) {
                        queueRowData(null, true);
                    }
                    itemReader.currentRowNum = row;
                }
            } catch (final Exception e) {
                if (readingTargetSheet) {
                    queueRowData(e, false);
                }
            }

        }

        private void readCellValues(final int row, final int column, final String val) {
            if (itemReader.header == null && row == itemReader.headerRow) {
                readingHeaderRow = true;
                readingDataRow = false;
                headerIndexToLabelMapping.put(column, String.valueOf(column));
//                headerIndexToLabelMapping.put(column, val);

                if (itemReader.maxColumnNum < column && StringUtils.hasText(val)) {
                    itemReader.maxColumnNum = column;
                }
            } else if (row >= itemReader.start) {
                readingDataRow = true;
                readingHeaderRow = false;
                resultMap.put(headerIndexToLabelMapping.get(column), val);

            }
        }

        /**
         * puts data, {@link #resultMap} for regular row data, and {@link ReadCompletedException} to indicate the end
         * of data stream. It also re-initialize {@link #resultMap}
         *
         * @param exception any exception occurred during event record processing
         * @param eof       true if reached the end of data stream
         */
        private void queueRowData(final Exception exception, final boolean eof) throws ReadCompletedException {
            try {
                if (eof) {
                    final ReadCompletedException readCompletedException = new ReadCompletedException();
                    itemReader.queue.put(readCompletedException);
                    throw readCompletedException;
                } else if (exception != null) {
                    itemReader.queue.put(exception);
                    resultMap = new HashMap<String, String>();
                } else {
                    if(!isEmptyRowData(resultMap)){
                        final Object obj = resultMap;
                        /*if (itemReader.beanType == List.class) {
                            final List<String> resultList = new ArrayList<String>();
                            for (int i = 0; i < itemReader.header.length; ++i) {
                                resultList.add(resultMap.get(itemReader.header[i]));
                            }
                            obj = resultList;
                        } else if (itemReader.beanType == Map.class) {
                            obj = resultMap;
                        } else {
                            obj = itemReader.objectMapper.convertValue(resultMap, itemReader.beanType);
                            if (!itemReader.skipBeanValidation) {
                                ItemReaderWriterBase.validate(obj);
                            }
                        }*/
                        itemReader.queue.put(obj);
                        resultMap = new HashMap<String, String>();
                    }
                }
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        private boolean isEmptyRowData(Map<String, String> resultMap) {
            if(CollectionUtils.isEmpty(resultMap)){
                return true;
            }

            boolean valuesEmpty = true;
            for (String  val : resultMap.values()){
                if (StringUtils.hasText(val)) {
                    valuesEmpty = false;
                    break;
                }
            }
            return valuesEmpty;
        }
    }

    /**
     * Exception to forcefully end excel parsing and reading. {@code org.apache.poi.hssf.eventusermodel.AbortableHSSFListener}
     * cannot be chained with {@code MissingRecordAwareHSSFListener}, so this exception has to be thrown from
     * {@link HSSFListenerImpl#processRecord(org.apache.poi.hssf.record.Record)} to indicate the end.
     */
    private static final class ReadCompletedException extends RuntimeException {
        private static final long serialVersionUID = -8693208957107027254L;
    }

    public static interface RowMapper<T>{
        T mapRow(String[] row) throws Exception;
    }

    public static void main(String[] args) throws Exception {
        ExcelEventItemReader<Object> itemReader = new ExcelEventItemReader<>();
        itemReader.setResource(new FileSystemResource("/Users/bianweiping/Documents/Book1.xls"));

        itemReader.open(null);

        Object item = null;
        while ((item = itemReader.read()) != null) {
            System.out.println(Arrays.toString((Object[]) item));
        }

    }
}
