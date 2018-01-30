package com.zq.learn.fileuploader.support.fileparser;

import com.zq.learn.fileuploader.support.batch.properties.BatchTaskProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 批量解析
 *
 * @author qun.zheng
 * @create 2018/1/26
 **/
@Component
@Deprecated
public class BatchParser {
    private static final Logger logger = LoggerFactory.getLogger(BatchParser.class);

    private static final String[] columns = StringUtils.commaDelimitedListToStringArray(
            "A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z,AA,AB,AC,AD,AE,AF,AG,AH,AI,AJ,AK,AL,AM");

    private ThreadPoolExecutor pool;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private BatchTaskProperties batchProperties;

    @PostConstruct
    public void postConstruct(){
        logger.info("初始化批量excel解析池");
        pool = new ThreadPoolExecutor(
                batchProperties.getCoreSize(), batchProperties.getMaxSize(), 120, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(3000));
    }

    public void parse(FileData excelData){
        pool.execute(new FileDataParseTask(excelData));
    }

    @PreDestroy
    public void onDestroy(){
        logger.info("销毁批量excel解析池");
        pool.shutdown();
    }

    public static class FileData {
        private InputStream stream;
        private String fileName;
        private String group;
        private String tableName;

        public FileData(String tableName,String group, String fileName, InputStream stream) {
            this.tableName = tableName;
            this.stream = stream;
            this.group = group;
            this.fileName = fileName;
        }

        public InputStream getStream() {
            return stream;
        }

        public String getFileName() {
            return fileName;
        }

        public String getGroup() {
            return group;
        }

        public String getTableName() {
            return tableName;
        }
    }

    private class FileDataParseTask implements Runnable{

        private FileData fileData;

        public FileDataParseTask(FileData fileData) {
            this.fileData = fileData;
        }

        @Override
        public void run() {
            long mill = System.currentTimeMillis();

            final int chunkSize = batchProperties.getChunkSize();

            Parser parser = ParserFactory.getParser(fileData.fileName);

           /* AtomicInteger increment = new AtomicInteger(1);
            parser.read(fileData.getStream(), item -> logger.info(fileData.fileName + "_no" + increment.getAndAdd(1) + JSON.toJSONString(item)));*/

            if (parser != null) {
                /*parser.read(fileData.getStream(), item -> {
                    chunk[0].add(item);
                    if (chunk[0].size() > chunkSize) {
                        saveToDb(chunk[0]);
                        chunk[0] = new ArrayList<>(chunkSize);
                    }
                });*/

                parser.read(fileData.getStream(), new ItemProcessor<String[]>() {
                    List<String[]> chunk = new ArrayList<>();

                    @Override
                    public void process(String[] item) {
                        chunk.add(item);
                        if (chunk.size() > chunkSize) {
                            saveToDb(chunk);
                            chunk = new ArrayList<>(chunkSize);
                        }
                    }

                    @Override
                    public void complete() {
                        if (!CollectionUtils.isEmpty(chunk)) {
                            saveToDb(chunk);
                            chunk = null;
                        }
                    }
                });
            }

            System.out.println("耗时:" + (System.currentTimeMillis() - mill) + "ms");
        }

        private void saveToDb(List<String[]> itemList) {
            if (CollectionUtils.isEmpty(itemList)) {
                return;
            }

           /* int columnLength = itemList.get(0).length;
            checkTableExists(this.fileData.getTableName(), columnLength, columns);
            StringBuilder sql = new StringBuilder();
            sql.append(new StringBuilder().append("insert into ").append(this.fileData.getTableName()).append("(").toString());

            for (int i = 0; i < columnLength; i++) {
                sql.append(columns[i]);

                if (i < columnLength - 1) {
                    sql.append(",");
                }
            }
            sql.append(") values ");
            for (int i = 0; i < itemList.size(); i++) {
                String[] item = itemList.get(i);
                sql.append("(");
                for (int j = 0; j < item.length; j++) {
                    sql.append(new StringBuilder().append("'").append(item[j]).append("'").toString());

                    if (j < item.length - 1) {
                        sql.append(",");
                    }
                }
                sql.append(")");

                if (i < itemList.size() - 1) {
                    sql.append(",");
                }
            }
            jdbcTemplate*/

//            jdbcTemplate.batchUpdate()

            /*int columnLength = itemList.get(0).length;
            checkTableExists(fileData.getTableName(),columnLength,columns);

            List<String> sqlList = new ArrayList<>();

            StringBuilder prefixSql = new StringBuilder();
            prefixSql.append("insert into "+fileData.getTableName()+"(");

            for(int i = 0;i < columnLength;i++) {
                prefixSql.append(columns[i]);

                if (i < columnLength - 1) {
                    prefixSql.append(",");
                }
            }
            prefixSql.append(") values ");

            int chunk = 500;
            int index = 0;
            StringBuilder sql = null;
            for (String[] items : itemList) {
                if (sql == null) {
                    sql = new StringBuilder(prefixSql.toString());
                }
                if (index == chunk - 1) {
                    String s = sql.toString();
                    sqlList.add(s.charAt(s.length() - 1) == ',' ? s.substring(0, s.length() - 1) : s);
                    sql = new StringBuilder(prefixSql.toString());
                }
                sql.append("(");
                for (int j = 0;j < items.length;j++) {
                    sql.append("'" + items[j] + "'");

                    if (j < items.length - 1) {
                        sql.append(",");
                    }
                }
                sql.append("),");

                index++;
            }

            if(sql.charAt(sql.length() - 1) == ','){
                sqlList.add(sql.substring(0, sql.length() - 1));
            }
            jdbcTemplate.batchUpdate(sqlList.toArray(new String[sqlList.size()]));*/

            logger.info("begin insert data to db[num={}]",itemList.size());
            String sql = parseSql(fileData.getTableName(),itemList.get(0).length,columns);
            jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    String[] item = itemList.get(i);

                    for (int index = 0;index < item.length;index++) {
                        ps.setString(index + 1, item[index]);
                    }
                }

                @Override
                public int getBatchSize() {
                    return itemList.size();
                }
            });
            /*StringBuilder sb = new StringBuilder()
                    .append("insert into employee(name,age,dept,salary) values");
            for (int i = 0;i < itemList.size();i++) {
                String[] item = itemList.get(i);
                sb.append("(").append("'"+item[0]+"',"+item[1]+",'"+item[2]+"',"+item[3]).append(")");

                if (i < itemList.size() - 1) {
                    sb.append(",");
                }
            }

            jdbcTemplate.batchUpdate(sb.toString());*/
        }

        private String parseSql(String tableName, int columnLength,String[] columnNames) {
            checkTableExists(tableName,columnLength,columnNames);

            StringBuilder sql = new StringBuilder();
            StringBuilder valueBuilder = new StringBuilder();
            sql.append("insert into "+tableName+"(");

            for(int i = 0;i < columnLength;i++) {
                sql.append(columnNames[i]);
                valueBuilder.append("?");

                if (i < columnLength - 1) {
                    sql.append(",");
                    valueBuilder.append(",");
                }
            }
            sql.append(") values ("+valueBuilder.toString()+")");
            return sql.toString();
        }

        private void checkTableExists(String tableName,int fieldLength,String[] columnNames) {
            Connection conn = null;
            try {
                conn = jdbcTemplate.getDataSource().getConnection();
                DatabaseMetaData metaData = conn.getMetaData();
                ResultSet rs = metaData.getTables(null, null, tableName, null);
                if (rs.next()) {
                    //Table Exist
                }else{
                    StringBuilder sb = new StringBuilder();
                    sb.append("create table " + tableName + "(");
                    for(int i = 0;i < fieldLength;i++) {
                        sb.append(columnNames[i] + " text CHARACTER SET utf8 DEFAULT NULL");

                        if (i < fieldLength - 1) {
                            sb.append(",");
                        }
                    }
                    sb.append(")");
                    jdbcTemplate.execute(sb.toString());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
