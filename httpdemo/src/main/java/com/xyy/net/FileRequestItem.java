package com.xyy.net;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/12/27.
 */
public class FileRequestItem extends StringRequestItem {
    //文件参数
    Map<String, File> fileParam;

    private void addFileParam(String key, File value) {
        if (fileParam == null) {
            fileParam = new HashMap<String, File>();
        }
        fileParam.put(key, value);
    }

    public static class Builder {
        FileRequestItem item;

        public Builder() {
            item = new FileRequestItem();
        }

        public Builder url(String url) {
            item.url = url;
            return this;
        }

        public Builder method(String method) {
            item.method = method;
            return this;
        }

        public StringRequestItem build() {
            return item;
        }

        public Builder addStringParam(String key, String value) {
            item.addStringParam(key, value);
            return this;
        }

        public Builder addFileParam(String key, File value) {
            item.addFileParam(key, value);
            return this;
        }
    }
}
