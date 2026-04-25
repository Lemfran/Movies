package edu.cuit.yingpingsxitong.Entity;

import java.sql.Timestamp;

import java.io.Serializable;

public class Log implements Serializable {
    private static final long serialVersionUID = 1L;
    Integer id;
    String methodName;
    String userName;
    Timestamp timestamp;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
}
