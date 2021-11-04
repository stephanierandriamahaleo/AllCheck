package com.example.check_all.models;

public class QRRequest {
    String code;
    int checkerId;
    String action;
    boolean isEntrance;
    int eventId;

    public QRRequest(String code, int checkerId, String action, boolean isEntrance) {
        this.code = code;
        this.checkerId = checkerId;
        this.action = action;
        this.isEntrance = isEntrance;
    }

    public QRRequest(String code, int checkerId, String action) {
        this.code = code;
        this.checkerId = checkerId;
        this.action = action;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getCheckerId() {
        return checkerId;
    }

    public void setCheckerId(int checkerId) {
        this.checkerId = checkerId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public boolean isEntrance() {
        return isEntrance;
    }

    public void setEntrance(boolean entrance) {
        isEntrance = entrance;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }
}
