package com.mj.app.print.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.mj.app.print.R;
import com.mj.app.print.event.ComEvent;
import com.mj.app.print.util.PrintLabel;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Arrays;

import android_serialport_api.ComBean;
import android_serialport_api.SerialHelper;
import de.greenrobot.event.EventBus;

/*
* 串口服务类
* 这里可以控制串口的启动和串口数据的分发
* */
public class ComService extends Service {

    protected final static String TAG = ComService.class.getSimpleName();

    private final static String STR_BAUD_RATE1 = "9600";
    private final static String STR_BAUD_RATE2 = "115200";
    private final static String STR_DEV_ONE = "/dev/ttyS1";//打印机
    private final static String STR_DEV_TWO = "/dev/ttyS2";//打印机
    private final static String STR_DEV_THREE = "/dev/ttyS3";//扫码枪9600
    private final static String STR_DEV_FOUR = "/dev/ttyS4";//扫码枪115200
    SerialControl comOne, comTwo, comThree, comFour;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate()");
        super.onCreate();
        initData();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy()");
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEventBackgroundThread(ComEvent event) {
        if (event.getActionType() == ComEvent.ACTION_SEND_PRINT) {
            String code = event.getMessage();
            Log.e(TAG,code);
            code = PrintLabel.ZebraLabel(code);
            Log.e(TAG,code);
            comOne.sendTxt(code);
            comTwo.sendTxt(code);
        }
    }

    public void initData() {
        comOne = new SerialControl();
        comTwo = new SerialControl();
        comThree = new SerialControl();
        comFour = new SerialControl();
        comOne.setPort(STR_DEV_ONE);
        comTwo.setPort(STR_DEV_TWO);
        comThree.setPort(STR_DEV_THREE);
        comFour.setPort(STR_DEV_FOUR);
        comOne.setBaudRate(STR_BAUD_RATE1);
        comTwo.setBaudRate(STR_BAUD_RATE1);
        comThree.setBaudRate(STR_BAUD_RATE1);//3号口9600
        comFour.setBaudRate(STR_BAUD_RATE2);//4号口115200
        openComPort(comOne);
        openComPort(comTwo);
        openComPort(comThree);
        openComPort(comFour);
    }

    public class SerialControl extends SerialHelper {
        public SerialControl() {
        }
        //从串口收取数据
        @Override
        public void onDataReceived(final ComBean comRecData) {
            if (comRecData.sComPort.equals(STR_DEV_ONE)) {//
                Log.e("onDataReceived bRec 1", Arrays.toString(comRecData.bRec));
                String code = new String(comRecData.bRec);
                Log.e("onDataReceived code 1", code);
                EventBus.getDefault().post(new ComEvent(code, ComEvent.ACTION_GET_CODE));
            } else if (comRecData.sComPort.equals(STR_DEV_TWO)) {//
                Log.e("onDataReceived bRec 2", Arrays.toString(comRecData.bRec));
                String code = new String(comRecData.bRec);
                Log.e("onDataReceived code 2", code);
                EventBus.getDefault().post(new ComEvent(code, ComEvent.ACTION_GET_CODE));
            } else if (comRecData.sComPort.equals(STR_DEV_THREE)) {//
                Log.e("onDataReceived bRec 3", Arrays.toString(comRecData.bRec));
                String code = new String(comRecData.bRec);
                Log.e("onDataReceived code 3", code);
                EventBus.getDefault().post(new ComEvent(code, ComEvent.ACTION_GET_CODE));
            }else if (comRecData.sComPort.equals(STR_DEV_FOUR)) {//
                Log.e("onDataReceived bRec 4", Arrays.toString(comRecData.bRec));
                String code = new String(comRecData.bRec);
                Log.e("onDataReceived code 4", code);
                EventBus.getDefault().post(new ComEvent(code, ComEvent.ACTION_GET_CODE));
            }
        }

        @Override
        public void stopSend() {
            Log.e(TAG, "stopSend()");
            super.stopSend();
        }
    }

    private void openComPort(SerialHelper ComPort) {
        try {
            ComPort.open();
            showMessage(R.string.com_start_success);
        } catch (SecurityException e) {
            showMessage(R.string.com_start_fail_security);
        } catch (IOException e) {
            showMessage(R.string.com_start_fail_io);
        } catch (InvalidParameterException e) {
            showMessage(R.string.com_start_fail_param);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void showMessage(int sMsg) {
        Toast.makeText(this.getApplicationContext(),
                getString(sMsg), Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }
}
