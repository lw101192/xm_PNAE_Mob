package com.xm.Bean;

import java.io.Serializable;

public class ContentBean implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -5669492743446896032L;
    private long totallength;
    private String contenttype;
    private String stringcontent;
    private byte[] bytecontent;
	public long getTotallength() {
		return totallength;
	}
	public void setTotallength(long totallength) {
		this.totallength = totallength;
	}
	public String getContenttype() {
		return contenttype;
	}
	public void setContenttype(String contenttype) {
		this.contenttype = contenttype;
	}
	public String getStringcontent() {
		return stringcontent;
	}
	public void setStringcontent(String stringcontent) {
		this.stringcontent = stringcontent;
	}
	public byte[] getBytecontent() {
		return bytecontent;
	}
	public void setBytecontent(byte[] bytecontent) {
		this.bytecontent = bytecontent;
	}

   
}
