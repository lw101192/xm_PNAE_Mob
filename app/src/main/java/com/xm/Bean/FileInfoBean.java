package com.xm.Bean;

import java.io.Serializable;

public class FileInfoBean implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -5407743474913465760L;
    private int totallength;//总长度
    private int filenamelength;//图片名字长度
    private long filelength;//图片长度
    private String filename;//图片名字
    private byte[] filecontent;//图片

    public int getTotallength() {
        return totallength;
    }

    public void setTotallength(int totallength) {
        this.totallength = totallength;
    }

    public int getFilenamelength() {
        return filenamelength;
    }

    public void setFilenamelength(int filenamelength) {
        this.filenamelength = filenamelength;
    }

    public long getFilelength() {
        return filelength;
    }

    public void setFilelength(long filelength) {
        this.filelength = filelength;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public byte[] getFilecontent() {
        return filecontent;
    }

    public void setFilecontent(byte[] filecontent) {
        this.filecontent = filecontent;
    }

}
