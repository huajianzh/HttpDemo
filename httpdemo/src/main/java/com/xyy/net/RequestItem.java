package com.xyy.net;

/**
 * 请求的参数和地址信息
 */
public class RequestItem {
    //请求地址
    String url;

    //请求方式
    String method = "get";

    public static class Builder {
        private String url;
        private String method = "get";

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder method(String method) {
            this.method = method;
            return this;
        }

        public RequestItem build() {
            return new RequestItem(this);
        }
    }

    private RequestItem(Builder builder) {
        this.url = builder.url;
        this.method = builder.method;
    }

    public RequestItem() {
    }
}
