package com.thd.db;

public class Process {

    private int id;

    private String code;

    private String station;

    private String assembly;

    private String part;

    private String title;

    private int qty;

    private String tag;

    private String status;

    private boolean success;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public String getAssembly() {
        return assembly;
    }

    public void setAssembly(String assembly) {
        this.assembly = assembly;
    }

    public String getPart() {
        return part;
    }

    public void setPart(String part) {
        this.part = part;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Process{" +
                "id=" + id +
                ", code='" + code + '\'' +
                ", station='" + station + '\'' +
                ", assembly='" + assembly + '\'' +
                ", part='" + part + '\'' +
                ", title='" + title + '\'' +
                ", qty=" + qty +
                ", tag='" + tag + '\'' +
                '}';
    }
}
