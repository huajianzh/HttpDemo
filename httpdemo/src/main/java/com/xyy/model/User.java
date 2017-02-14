package com.xyy.model;

/**
 * Created by Administrator on 2016/12/8.
 */
public class User {

    /**
     * distance : -1
     * id : 10000071
     * nick : asdf
     * photo : /photo/1481183681530.jpg
     * question :
     * sex : 男
     * sign : sdsdsd
     */

    private int distance;
    private int id;
    private String nick;
    private String photo;
    private String question;
    private String sex;
    private String sign;

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
