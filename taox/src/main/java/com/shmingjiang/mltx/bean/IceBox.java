package com.shmingjiang.mltx.bean;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by familywei on 2015/12/5.
 */
public class IceBox implements Serializable {
    private String R3code;
    private String iceBoxName;
    private String deth;
    private String with;
    private String height;
    private String cmd;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    private String location;
    private String[] array;
    private boolean flag = false;

    public String[] ib2Arrary() {
        if (flag) {
            array = new String[]{R3code, iceBoxName, deth, with, height, cmd, location};
            return array;
        } else {
            return null;
        }
    }

    public IceBox() {
        flag = false;
    }

    public void setFlag(boolean b) {
        flag = b;
    }

    public IceBox(String r3code, String iceBoxName, String deth, String with, String height, String cmd, String location) {

        this.deth = deth;
        this.height = height;
        this.iceBoxName = iceBoxName;
        this.R3code = r3code;
        this.with = with;
        this.cmd = cmd;
        this.location = location;
        flag = true;//?
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public String[] getArray() {
        return array;
    }

    public void setArray(String[] array) {
        this.array = array;
    }

    public String getDeth() {
        return deth;
    }

    public void setDeth(String deth) {
        this.deth = deth;
    }

    public boolean isFlag() {
        return flag;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getIceBoxName() {
        return iceBoxName;
    }

    public void setIceBoxName(String iceBoxName) {
        this.iceBoxName = iceBoxName;
    }

    public String getR3code() {
        return R3code;
    }

    public void setR3code(String r3code) {
        R3code = r3code;
    }

    public String getWith() {
        return with;
    }

    public void setWith(String with) {
        this.with = with;
    }

    @Override
    public String toString() {
        return "IceBox{" +
                "array=" + Arrays.toString(array) +
                ", R3code='" + R3code + '\'' +
                ", iceBoxName='" + iceBoxName + '\'' +
                ", deth='" + deth + '\'' +
                ", with='" + with + '\'' +
                ", height='" + height + '\'' +
                ", cmd='" + cmd + '\'' +
                ",location='" + location + '\'' +
                ", flag=" + flag +
                '}';
    }
}
