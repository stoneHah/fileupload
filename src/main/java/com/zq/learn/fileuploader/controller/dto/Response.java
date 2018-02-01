package com.zq.learn.fileuploader.controller.dto;

/**
 * restful 接口返回信息的实体类
 * @param <T>
 */
public class Response<T> {
    /**
     * 请求接口正常返回
     */
    public static final String CODE_OK = "OK";
    public static final String CODE_ERROR = "ERROR";

    /**
     * 参数错误
     */
    public static final String CODE_PARAM_ERROR = "E00001";

    private T result;
    private String code;

    private String msg;

    private Response() {
    }

    private Response(ResponseBuilder<T> builder) {
        this.result = builder.result;
        this.code = builder.code;
        this.msg = builder.msg;
    }

    public T getResult() {
        if(result == null){
            return (T) "";
        }
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    /**
     *
     * @param result
     * @param <T>
     * @return
     */
    public static <T> Response<T> ok(T result){
        return new ResponseBuilder(result)
                .code(CODE_OK)
                .build();
    }

    public static Response error(String code, String message) {
        return new ResponseBuilder("")
                .code(code)
                .msg(message)
                .build();
    }

    public static class ResponseBuilder<T> {
        private T result;
        private String  code;

        private String msg;

        public ResponseBuilder() {
            this.result = null;
        }

        public ResponseBuilder(T result) {
            this.result = result;
        }

        public ResponseBuilder code(String code) {
            this.code = code;
            if(CODE_OK.equalsIgnoreCase(code)){
                this.msg = "操作成功";
            }else{
                this.msg = "操作失败";
            }
            return this;
        }

        public ResponseBuilder result(T result) {
            this.result = result;
            return this;
        }

        public ResponseBuilder msg(String msg) {
            this.msg = msg;
            return this;
        }

        public Response<T> build() {
            return new Response(this);
        }
    }
}
