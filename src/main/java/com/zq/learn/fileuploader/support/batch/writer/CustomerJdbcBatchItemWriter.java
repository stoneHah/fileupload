package com.zq.learn.fileuploader.support.batch.writer;

import com.zq.learn.fileuploader.support.batch.model.ParsedItem;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.AfterStep;
import org.springframework.batch.core.annotation.BeforeJob;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

/**
 * ${DESCRIPTION}
 *
 * @author qun.zheng
 * @create 2018/1/30
 **/
public class CustomerJdbcBatchItemWriter implements ItemWriter<ParsedItem>,InitializingBean {

    private JdbcBatchItemWriter<ParsedItem> delegate;

    private NamedParameterJdbcOperations namedParameterJdbcTemplate;

    private DataSource dataSource;

    /**
     *
     */
    private ExecutionContext stepEc;

    private JobExecution jobExecution;

    private String table;

    private String sql = null;

    @BeforeStep
    void beforeStep(StepExecution stepExecution){
        stepEc = stepExecution.getExecutionContext();
    }

    public CustomerJdbcBatchItemWriter(JdbcBatchItemWriter delegate, String table) {
        this.delegate = delegate;
        this.table = table;

        delegate.setItemPreparedStatementSetter((ItemPreparedStatementSetter<ParsedItem>) (item, ps) -> {
            int index = 1;
            Iterator<Entry<String, String>> iterator = item.iterator();
            while (iterator.hasNext()) {
                Entry<String, String> next = iterator.next();
                ps.setString(index++, next.getValue());
            }
        });
    }

    /**
     * Public setter for the data source for injection purposes.
     *
     * @param dataSource {@link javax.sql.DataSource} to use for querying against
     */
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        if (namedParameterJdbcTemplate == null) {
            this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

            delegate.setJdbcTemplate(this.namedParameterJdbcTemplate);
        }
    }

    @Override
    public void write(List<? extends ParsedItem> items) throws Exception {
        if (CollectionUtils.isEmpty(items)) {
            return;
        }

//        if (Math.random() < 0.2) {
//            throw new SQLException("sql error");
//        }

        parseSql(getColumnNames(items.get(0)));
        delegate.write(items);
    }

    private String[] getColumnNames(ParsedItem parsedItem) {
        List<String> columnNames = new ArrayList<>();
        parsedItem.forEach((key,val) -> columnNames.add(key));
        return columnNames.toArray(new String[columnNames.size()]);
    }

    private void parseSql(String[] columnNames) {
        if (sql != null) {
            return;
        }

        checkTableExists(table,columnNames);

        StringBuilder sqlBuilder = new StringBuilder();
        StringBuilder valueBuilder = new StringBuilder();
        sqlBuilder.append("insert into "+table+"(");

        int columnLength = columnNames.length;
        for(int i = 0;i < columnLength;i++) {
            sqlBuilder.append(columnNames[i]);
            valueBuilder.append("?");

            if (i < columnLength - 1) {
                sqlBuilder.append(",");
                valueBuilder.append(",");
            }
        }
        sqlBuilder.append(") values ("+valueBuilder.toString()+")");

        this.sql = sqlBuilder.toString();
        delegate.setSql(sql);
    }

    private void checkTableExists(String tableName,String[] columnNames) {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet rs = metaData.getTables(null, null, tableName, null);
            if (rs.next()) {
                //Table Exist
            }else{
                StringBuilder sb = new StringBuilder();
                sb.append("create table " + tableName + "(");
                sb.append("id int(11) primary key NOT NULL AUTO_INCREMENT,");
                int fieldLength = columnNames.length;
                for(int i = 0;i < fieldLength;i++) {
                    sb.append(columnNames[i] + " text CHARACTER SET utf8 DEFAULT NULL");

                    if (i < fieldLength - 1) {
                        sb.append(",");
                    }
                }
                sb.append(")");
                namedParameterJdbcTemplate.getJdbcOperations().execute(sb.toString());
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

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(namedParameterJdbcTemplate, "A DataSource or a NamedParameterJdbcTemplate is required.");
    }

}
