package com.shmingjiang.mltx.bean;

/**
 * Created by familywei on 2015/12/11.
 */
public class SendPLCMsg {

    private IceBox ib;
    private String tm;
    private Boolean hd = false;
    private Boolean hx = false;
    //   private Boolean d0 = false;
    // private Boolean d2 = false;
    private Boolean x0 = false;
    private Boolean w0 = false;
    private Boolean xh = false;

    public String getTm() {
        return tm;
    }

    public void setTm(String tm) {
        this.tm = tm;
    }

    public Boolean getXh() {
        return xh;
    }

    public void setXh(Boolean xh) {
        this.xh = xh;
    }

    public SendPLCMsg(IceBox ib) {
        this.ib = ib;
    }

    public Boolean getHd() {
        return hd;
    }

    public void setHd(Boolean hd) {
        this.hd = hd;
    }

    public Boolean getHx() {
        return hx;
    }

    public void setHx(Boolean hx) {
        this.hx = hx;
    }

    public IceBox getIb() {
        return ib;
    }

    public void setIb(IceBox ib) {
        this.ib = ib;
    }

    public Boolean getW0() {
        return w0;
    }

    public void setW0(Boolean w0) {
        this.w0 = w0;
    }

    public Boolean getX0() {
        return x0;
    }

    public void setX0(Boolean x0) {
        this.x0 = x0;
    }

    @Override
    public String toString() {
        return "SendPLCMsg{" +
                "hd=" + hd +
                ", ib=" + ib +
                ", hx=" + hx +
                ", x0=" + x0 +
                ", w0=" + w0 +
                '}';
    }
}
