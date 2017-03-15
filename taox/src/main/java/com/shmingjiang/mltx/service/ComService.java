package com.shmingjiang.mltx.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;


import com.shmingjiang.mltx.R;
import com.shmingjiang.mltx.event.ComEvent;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

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
    private final static String STR_DEV_ONE = "/dev/ttyS1";//板卡
    private final static String STR_DEV_TWO = "/dev/ttyS2";//机器人
    private final static String STR_DEV_THREE = "/dev/ttyS3";//封箱机
    private final static String STR_DEV_FOUR = "/dev/ttyS4";//固定扫码枪
    SerialControl comOne, comTwo, comThree, comFour;
    private boolean flag = false;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.e("ComService", "onCreate()");
        super.onCreate();
        initData();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        Log.e("ComService", "onDestroy()");
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    class Task extends TimerTask {

        public Task() {
        }

        @Override
        public void run() {
            flag = false;
        }
    }

    public void onEventBackgroundThread(ComEvent event) {
        if (event.getActionType() == ComEvent.ACTION_SEND_PCI) {
            comOne.sendTxt(event.getMessage());
            flag = true;
            Timer timer = new Timer();
            timer.schedule(new Task(), 8000);// 8秒后启动任务
        } else if (event.getActionType() == ComEvent.ACTION_SEND_ROB) {
            comTwo.send(event.getBytes());
        }
    }

    public void initData() {
        comOne = new SerialControl();
        comTwo = new SerialControl();
//        comThree = new SerialControl();
        comFour = new SerialControl();
        comOne.setPort(STR_DEV_ONE);
        comTwo.setPort(STR_DEV_TWO);
//        comThree.setPort(STR_DEV_THREE);
        comFour.setPort(STR_DEV_FOUR);
        comOne.setBaudRate(STR_BAUD_RATE1);
        comTwo.setBaudRate(STR_BAUD_RATE1);
//        comThree.setBaudRate(STR_BAUD_RATE1);
        comFour.setBaudRate(STR_BAUD_RATE1);
        openComPort(comOne);
        openComPort(comTwo);
//        openComPort(comThree);
        openComPort(comFour);
    }

    public class SerialControl extends SerialHelper {
        public SerialControl() {
        }

        //从串口收取数据
        @Override
        public void onDataReceived(final ComBean comRecData) {
            if (comRecData.sComPort.equals(STR_DEV_ONE)) {//板卡
                if (flag) {
                    Log.i(TAG, "8s内不处理");
                } else {
                    Log.e("onDataReceived bRec PCI", Arrays.toString(comRecData.bRec));
                    String code = new String(comRecData.bRec);
                    Log.e("onDataReceived code PCI", code);
                    EventBus.getDefault().post(new ComEvent(code, ComEvent.ACTION_GET_PCI));
                }
            } else if (comRecData.sComPort.equals(STR_DEV_TWO)) {//机器人
                Log.e("onDataReceived bRec ROB", Arrays.toString(comRecData.bRec));
                String temp = Arrays.toString(comRecData.bRec);
//                String EAN = temp.substring(10,temp.length()-1);
                String EAN = String.valueOf(comRecData.bRec[2]);
                String code = new String(comRecData.bRec);
                Log.e("onDataReceived code ROB", code);
                EventBus.getDefault().post(new ComEvent(code, EAN, ComEvent.ACTION_GET_ROB));
            } else if (comRecData.sComPort.equals(STR_DEV_FOUR)) {//固定扫码枪
                Log.e("onDataReceived bRec GUN", Arrays.toString(comRecData.bRec));
                String code = new String(comRecData.bRec);
                Log.e("onDataReceived code GUN", code);
                EventBus.getDefault().post(new ComEvent(code, ComEvent.ACTION_GET_CODE));
            }
        }

        @Override
        public void stopSend() {
            Log.e("ComService", "stopSend()");
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

    private void closeComPort(SerialHelper ComPort) {
        try {
            ComPort.close();
            showMessage(R.string.com_start_success);
        } catch (SecurityException e) {
            showMessage(R.string.com_start_fail_security);
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
