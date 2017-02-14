package com.xyy.net;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/27.
 */
public class StringRequestItem extends RequestItem {
    //文本参数
    Map<String, String> strParam;

    public static class Builder {
        private StringRequestItem item;
        public Builder(){
            item = new StringRequestItem();
        }

        public Builder url(String url){
            item.url = url;
            return this;
        }

        public Builder method(String method){
            item.method = method;
            return this;
        }

        public StringRequestItem build(){
            return item;
        }

        public Builder addStringParam(String key, String value) {
            item.addStringParam(key,value);
            return this;
        }

    }

    protected void addStringParam(String key, String value) {
        if (strParam == null) {
            strParam = new HashMap<String, String>();
        }
        strParam.put(key, value);
    }

    public StringRequestItem() {
    }
}
