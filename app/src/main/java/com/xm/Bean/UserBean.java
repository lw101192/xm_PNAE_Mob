package com.xm.Bean;

import java.io.Serializable;

public class UserBean implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 7211455034929963945L;
    private long totallength;


    private String id;
    private String type;
    private String nickname;
    private String originpw;
    private String newpw;

    public long getTotallength() {
        return totallength;
    }

    public void setTotallength(long totallength) {
        this.totallength = totallength;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getOriginpw() {
        return originpw;
    }

    public void setOriginpw(String originpw) {
        this.originpw = originpw;
    }

    public String getNewpw() {
        return newpw;
    }

    public void setNewpw(String newpw) {
        this.newpw = newpw;
    }

}
