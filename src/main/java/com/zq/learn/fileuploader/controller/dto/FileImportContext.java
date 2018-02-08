package com.zq.learn.fileuploader.controller.dto;

/**
 * 文件导入上下文
 *
 * @author qun.zheng
 * @create 2018/2/8
 **/
public class FileImportContext {
    /**
     * 是否校验身份证信息
     */
    private boolean validateIDCard = false;

    /**
     * 身份证所在列
     */
    private String idcardColumns;

    public boolean isValidateIDCard() {
        return validateIDCard;
    }

    public void setValidateIDCard(boolean validateIDCard) {
        this.validateIDCard = validateIDCard;
    }

    public String getIdcardColumns() {
        return idcardColumns;
    }

    public void setIdcardColumns(String idcardColumns) {
        this.idcardColumns = idcardColumns;
    }
}
