package com.xyy.model;

/**
 * Created by Administrator on 2016/12/26.
 */
public class DiaryItem {

    /**
     * content : abc123
     * id : 1
     * pub_time : 1482733686282
     * title : abc
     */

    private String content;
    private int id;
    private String pub_time;
    private String title;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPub_time() {
        return pub_time;
    }

    public void setPub_time(String pub_time) {
        this.pub_time = pub_time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public DiaryItem(String content, int id, String pub_time, String title) {
        this.content = content;
        this.id = id;
        this.pub_time = pub_time;
        this.title = title;
    }

    public DiaryItem() {
    }
}
