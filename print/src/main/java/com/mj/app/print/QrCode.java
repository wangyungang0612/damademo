package com.mj.app.print;

/**
 * Created by wdongjia on 2016/12/27.
 */

public class QrCode {

    /**
     * time : 16:53:47
     * code : HC5
     * qrcode : HC5
     * status : true
     */

    private String time;
    private String code;
    private String qrcode;
    private boolean status;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
