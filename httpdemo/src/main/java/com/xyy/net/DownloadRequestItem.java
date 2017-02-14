package com.xyy.net;

/**
 * Created by Administrator on 2016/12/27.
 */
public class DownloadRequestItem extends RequestItem {
    int from;
    int to;
    String savePath;

    public int getFrom() {
        return from;
    }

    public RequestItem setFrom(int from) {
        this.from = from;
        return this;
    }

    public int getTo() {
        return to;
    }

    public RequestItem setTo(int to) {
        this.to = to;
        return this;
    }

    public String getSavePath() {
        return savePath;
    }

    public RequestItem setSavePath(String savePath) {
        this.savePath = savePath;
        return this;
    }
}
