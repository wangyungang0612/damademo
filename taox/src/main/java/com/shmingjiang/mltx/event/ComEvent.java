package com.shmingjiang.mltx.event;

/**
 * 用于串口事件处理
 * 4个口
 */
public class ComEvent {

    public final static int ACTION_GET_CODE = 81; //获取扫到的码
    public final static int ACTION_GET_PCI = 82;  //
    public final static int ACTION_GET_ROB = 83; //
    public final static int ACTION_GET_PLC = 84; //
    public final static int ACTION_SEND_GUN = 91; //
    public final static int ACTION_SEND_PCI = 92; //
    public final static int ACTION_SEND_ROB = 93; //
    public final static int ACTION_SEND_PLC = 94; //

    protected String message;       //传递字符串数据
    protected String EAN;       //传递字符串数据

    protected int actionType;       //事件类型
    private Object objectMsg = null;//传递对象数据
    private byte[] bytes; //

    public ComEvent(Object objectMsg, int actionType) {
        this.objectMsg = objectMsg;
        this.actionType = actionType;
    }

    public ComEvent(String message, int actionType) {
        this.message = message;
        this.actionType = actionType;
    }

    public ComEvent(String message, String EAN, int actionType) {
        this.message = message;
        this.EAN = EAN;
        this.actionType = actionType;
    }

    public ComEvent(byte[] message, int actionType) {
        this.bytes = message;
        this.actionType = actionType;
    }

    public int getActionType() {
        return actionType;
    }

    public String getMessage() {
        return message;
    }

    public String getEAN() {
        return EAN;
    }

    public Object getObjectMsg() {
        return objectMsg;
    }

    public byte[] getBytes() {
        return bytes;
    }
}
