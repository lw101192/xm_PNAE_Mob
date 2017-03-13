package com.xm.Bean;

import java.io.Serializable;

public class ConfigBean implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -5996571077565312703L;
    private long totallength;
    private String configitem;
    private String status;

    public String getConfigitem() {
        return configitem;
    }

    public void setConfigitem(String configitem) {
        this.configitem = configitem;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTotallength() {
        return totallength;
    }

    public void setTotallength(long totallength) {
        this.totallength = totallength;
    }
}
