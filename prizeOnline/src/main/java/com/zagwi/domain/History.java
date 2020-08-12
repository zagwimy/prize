package com.zagwi.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 临时表
 */
public class History  implements Serializable {
    private Integer id;
    private String name;
    private Integer version;
    private String number;
    private Date insertTime;
    private int batch;
    private int isdelete;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }

    public int getBatch() {
        return batch;
    }

    public void setBatch(int batch) {
        this.batch = batch;
    }

    public int getIsdelete() {
        return isdelete;
    }

    public void setIsdelete(int isdelete) {
        this.isdelete = isdelete;
    }

    @Override
    public String toString() {
        return "History{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", version=" + version +
                ", number='" + number + '\'' +
                ", insertTime=" + insertTime +
                ", batch=" + batch +
                ", isdelete=" + isdelete +
                '}';
    }
}
