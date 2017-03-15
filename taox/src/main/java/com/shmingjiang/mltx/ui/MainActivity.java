package com.shmingjiang.mltx.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.shmingjiang.mltx.Constant;
import com.shmingjiang.mltx.db.IbDataBaseUtil;

import com.shmingjiang.mltx.App;
import com.shmingjiang.mltx.R;
import com.shmingjiang.mltx.bean.IceBox;
import com.shmingjiang.mltx.bean.SendPLCMsg;
import com.shmingjiang.mltx.event.ComEvent;
import com.shmingjiang.mltx.service.ComService;
import com.shmingjiang.mltx.util.Utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PieChartView;
import rx.functions.Action1;

public class MainActivity extends Activity implements View.OnClickListener {

    protected static final String TAG = MainActivity.class.getSimpleName();
    private long exitTime = 0;
    private ListView mLv, mPlcLv;
    private IceBox ib, ib_update;
    private ArrayList<IceBox> ibList = new ArrayList<>();
    private ArrayList<SendPLCMsg> plcMsgsList = new ArrayList<>();
    private AlertDialog mDialog;
    private MyAdapter mAdapter;
    private MyPlc1Adapter mPlc1Adapter;
    private Button addItemOkBtn, addItemCancelBtn, updateBtn,
            deleteBtn, udBtn, importIbBtn,
            clearListBtn, openComBtn, closeComBtn;
    private EditText r3Et, icsEt, dethEt, withEt, heightEt, cmdEt, locationEt;
    private TextView logTv, setCompletionTv;
    private LineChartView linechart;
    private LineChartData linedata;
    private int numberOfLines = 1;
    private int maxNumberOfLines = 4;
    private int numberOfPoints = 12;
    float[][] randomNumbersTab = new float[maxNumberOfLines][numberOfPoints];
    private boolean hasAxes = true;
    private boolean hasAxesNames = true;
    private boolean hasLines = true;
    private boolean hasPoints = true;
    private ValueShape shape = ValueShape.CIRCLE;
    private boolean isFilled = false;
    private boolean isCubic = false;
    private boolean pointsHaveDifferentColor;
    private PieChartView chart;
    private PieChartData data;
    private boolean hasLabels = false;
    private boolean hasLabelsOutside = false;
    private boolean hasCenterCircle = false;
    private boolean hasCenterText1 = false;
    private boolean hasCenterText2 = false;
    private boolean isExploded = false;
    private boolean hasLabelForSelected = false;
    private int intSetCompletion = 0;
    private int intCount, intCurrentNum;
    private long frsClick, secClick;
    private boolean isRece = true;
    private String byte1 = "100";
    private String byte2 = "100";
    private String byte3 = "100";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        EventBus.getDefault().register(this);
        initLineChart();
        initPieChart();
//        emptyCode();
//        sendCode2Server("HC5901455210000609201044");
    }

    private void emptyCode() {
        for (int i = 0; i < 5; i++) {
            //生成一个空码
            ib = new IceBox();
            ib.setR3code("未扫到码");
            ib.setIceBoxName("9");
            ib.setDeth("9");
            ib.setWith("9");
            ib.setHeight("9");
            //cmd要用到
            ib.setCmd("9");
            ib.setLocation("9");
            logTv.setText("没有扫描到二维码，自动解除报警生成空码");
            SendPLCMsg msg = new SendPLCMsg(ib);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            String date = sdf.format(new java.util.Date());
            msg.setTm(date);
            Constant.plcMsgsList.add(msg);
            mPlc1Adapter.plcMsgsList = Constant.plcMsgsList;
            mPlc1Adapter.notifyDataSetChanged();
            logTv.setText(ib.getIceBoxName().toString());
        }

    }

    private void sendCode2Server(String serial_number) {
        Map<String, String> map = new HashMap<>();
        int last = serial_number.indexOf("\r\n");
        serial_number = serial_number.substring(0, last);
        map.put("serial_number", serial_number);
        Log.e(TAG, "serial_number-" + serial_number);
        App.getApp().getNetService(this).sendData(map).subscribe(new Action1<String>() {
            @Override
            public void call(String weldmentResult) {
                Log.i(TAG, weldmentResult);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.i(TAG, throwable.getMessage());
            }
        });
    }

    private void initView() {
        mPlcLv = (ListView) findViewById(R.id.lv_plc);
        mLv = (ListView) findViewById(R.id.lv_ib_list);
        logTv = (TextView) findViewById(R.id.tv_scanningcode);
        setCompletionTv = (TextView) findViewById(R.id.tv_set_completion_quantity);
        importIbBtn = (Button) findViewById(R.id.btn_import_ib);
        clearListBtn = (Button) findViewById(R.id.btn_clear_list);
        openComBtn = (Button) findViewById(R.id.btn_open_com);
        closeComBtn = (Button) findViewById(R.id.btn_close_com);
        closeComBtn.setEnabled(false);
        importIbBtn.setOnClickListener(this);
        clearListBtn.setOnClickListener(this);
        openComBtn.setOnClickListener(this);
        closeComBtn.setOnClickListener(this);
        File path = getDatabasePath(Constant.dataBaseName);
        if (!checkDatabase()) {
            //
            IbDataBaseUtil.createDataBase(this);
//            InsertPreparedData.insertData(this);
        }
        ibList = IbDataBaseUtil.queryAll(this);
        plcMsgsList.clear();
        plcMsgsList.addAll(getSendPlcList());
        mPlc1Adapter = new MyPlc1Adapter(plcMsgsList, this);
        mAdapter = new MyAdapter(ibList, this);
        mPlcLv.setAdapter(mPlc1Adapter);
        mLv.setAdapter(mAdapter);
        mLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                intCount++;// int count = 0;
                if (intCount == 1) {
                    frsClick = System.currentTimeMillis();
                    intCurrentNum = position;
                } else if (intCount == 2) {
                    secClick = System.currentTimeMillis();
                    if ((secClick - frsClick <= 1500) && (intCurrentNum == position)) {
                        //双击事件
                        Toast.makeText(MainActivity.this, "双击了!", Toast.LENGTH_LONG).show();
                        updateOrDeleteItem(ibList.get(position));
                    }
                    intCount = 0;
                    frsClick = 0;
                    secClick = 0;
                }
            }
        });

        mPlcLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                // int count = 0;
                intCount++;
                if (intCount == 1) {
                    frsClick = System.currentTimeMillis();
                    intCurrentNum = position;
                } else if (intCount == 2) {
                    secClick = System.currentTimeMillis();
                    if ((secClick - frsClick <= 1500) && (intCurrentNum == position)) {
                        //双击事件
                        Toast.makeText(MainActivity.this, "双击了!", Toast.LENGTH_LONG).show();
                        Dialog dialog = new AlertDialog.Builder(MainActivity.this)
                                .setTitle("删除对话框")
                                .setIcon(R.drawable.meiling_logo)
                                .setMessage("确认删除吗？")
                                //相当于点击确认按钮
                                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Constant.plcMsgsList.remove(position);
                                        mPlc1Adapter.plcMsgsList = Constant.plcMsgsList;
                                        mPlc1Adapter.notifyDataSetChanged();
                                        Toast.makeText(MainActivity.this, "删除成功!", Toast.LENGTH_LONG).show();
                                    }
                                })
                                //相当于点击取消按钮
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {// TODO Auto-generated method stub
                                        Toast.makeText(MainActivity.this, "取消删除!", Toast.LENGTH_LONG).show();
                                    }
                                })
                                .create();
                        dialog.show();
                    }
                    intCount = 0;
                    frsClick = 0;
                    secClick = 0;
                }
            }
        });
    }

    private void initPieChart() {
        chart = (PieChartView) findViewById(R.id.piechart);
        generatePieData();
    }

    private void generatePieData() {
        //获取到冰箱种类 并在饼状图中显示
//        ibList = IbDataBaseUtil.queryAll(this);
//        int numValues = ibList.size();
        int numValues = 6;
        List<SliceValue> values = new ArrayList<SliceValue>();
        //产生随机数
        for (int i = 0; i < numValues; ++i) {
            //关键代码
            SliceValue sliceValue = new SliceValue((float) Math.random() * 30 + 15, ChartUtils.pickColor());
            values.add(sliceValue);//values
        }
        data = new PieChartData(values);
        data.setHasLabels(hasLabels);
        data.setHasLabelsOnlyForSelected(hasLabelForSelected);
        data.setHasLabelsOutside(hasLabelsOutside);
        data.setHasCenterCircle(hasCenterCircle);
        chart.setPieChartData(data);
    }

    private void initLineChart() {
        linechart = (LineChartView) findViewById(R.id.linechart);
//        linechart.setOnValueTouchListener(new ValueTouchListener());
        generateLineValues();
        generateLineData();
        linechart.setViewportCalculationEnabled(false);
    }

    private void generateLineValues() {
        //获取需要的数值
        for (int i = 0; i < maxNumberOfLines; ++i) {
            for (int j = 0; j < numberOfPoints; ++j) {
                randomNumbersTab[i][j] = (float) Math.random() * 100f;
//                randomNumbersTab[i][j] = (float)0.12 * 100f;//套箱完成率
            }
        }
    }

    private void generateLineData() {
        List<Line> lines = new ArrayList<Line>();
        for (int i = 0; i < numberOfLines; ++i) {
            List<PointValue> values = new ArrayList<PointValue>();
            for (int j = 0; j < numberOfPoints; ++j) {
                values.add(new PointValue(j, randomNumbersTab[i][j]));
            }
            Line line = new Line(values);
            line.setColor(ChartUtils.COLORS[i]);
            line.setShape(shape);
            line.setCubic(isCubic);
            line.setFilled(isFilled);
            line.setHasLabels(hasLabels);
            line.setHasLabelsOnlyForSelected(hasLabelForSelected);
            line.setHasLines(hasLines);
            line.setHasPoints(hasPoints);
            if (pointsHaveDifferentColor) {
                line.setPointColor(ChartUtils.COLORS[(i + 1) % ChartUtils.COLORS.length]);
            }
            lines.add(line);
        }
        linedata = new LineChartData(lines);
        if (hasAxes) {
            Axis axisX = new Axis();
            Axis axisY = new Axis().setHasLines(true);
            if (hasAxesNames) {
                axisX.setName("时间");//以整点为基准
                axisY.setName("完成率");//只统计套箱完成率 因为封箱完成率接近100%
            }
            linedata.setAxisXBottom(axisX);
            linedata.setAxisYLeft(axisY);
        } else {
            linedata.setAxisXBottom(null);
            linedata.setAxisYLeft(null);
        }
        linedata.setBaseValue(Float.NEGATIVE_INFINITY);
        linechart.setLineChartData(linedata);
    }

    private IceBox searchItemFromR2code(String s) {
        if (s.length() < 24) {
            return null;
        }
        Log.i(TAG, "qrcode=====" + s);
        s = s.substring(3, 10);
        Log.i(TAG, "r3code=====" + s);
        return IbDataBaseUtil.queryItem(s, this);
    }

    /**
     * 双击之后的操作 修改或删除
     **/
    private void updateOrDeleteItem(IceBox ib) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        mDialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_update_or_delete, null);
        mDialog.setView(view, 0, 0, 0, 0);
        mDialog.show();
        updateBtn = (Button) view.findViewById(R.id.update_btn);
        deleteBtn = (Button) view.findViewById(R.id.delete_btn);
        udBtn = (Button) view.findViewById(R.id.ud_cancel_btn);
        updateBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);
        udBtn.setOnClickListener(this);
        this.ib_update = ib;
    }

    private Boolean checkDatabase() {
        String path = getFilesDir().getAbsolutePath(); //+"/databases/";
        Log.i(TAG, path.substring(0, path.lastIndexOf("/"))); //path.substring(path.lastIndexOf("/")+1);
        path = path.substring(0, path.lastIndexOf("/")) + "/databases/";
        File file = new File(path + Constant.dataBaseName);
        if (file.exists()) {
            Toast.makeText(this, "数据库是存在的!", Toast.LENGTH_LONG).show();
            return true;
        }
        Toast.makeText(this, "数据库不存在！", Toast.LENGTH_LONG).show();
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_import_ib) {
            importIc();
        } else if (v.getId() == R.id.btn_clear_list) {
            clearList();
            ibList.clear();
            ibList.addAll(IbDataBaseUtil.queryAll(this));
            mAdapter = new MyAdapter(ibList, this);
            mLv.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        } else if (v.getId() == R.id.btn_open_com) {
            openCom();
        } else if (v.getId() == R.id.btn_close_com) {
            closeCom();
        } else if (v.getId() == R.id.cancelitem_btn) {
            mDialog.dismiss();
        } else if (v.getId() == R.id.update_btn) {
            updateClikedItem();
        } else if (v.getId() == R.id.delete_btn) {
            deleteClickedItem();
        } else if (v.getId() == R.id.ud_cancel_btn) {
            mDialog.dismiss();
        } else if (v.getId() == R.id.additem_btn) {
            IbDataBaseUtil.delete(this, ib_update);
            checkTheDataAndInert2DB();
        } else {

        }
    }

    private void checkTheDataAndInert2DB() {
        String r3code = r3Et.getText().toString().trim();
        String ics = icsEt.getText().toString().trim();
        String deth = dethEt.getText().toString().trim();
        String with = withEt.getText().toString().trim();
        String heght = heightEt.getText().toString().trim();
        String cmd = cmdEt.getText().toString().trim();
        String location = locationEt.getText().toString().trim();
        if (r3code.isEmpty() ||
                ics.isEmpty() ||
                deth.isEmpty() ||
                with.isEmpty() ||
                heght.isEmpty() ||
                cmd.isEmpty() ||
                location.isEmpty()) {
            Toast.makeText(this, "请输入完整", Toast.LENGTH_LONG).show();
            return;
        } else {
            IceBox ic = new IceBox(r3code, ics, deth, with, heght, cmd, location);
            IbDataBaseUtil.insert(this, ic);
            ibList.clear();
            ibList.addAll(IbDataBaseUtil.queryAll(this));
            mAdapter = new MyAdapter(ibList, this);
            mLv.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            mDialog.dismiss();
        }
    }

    private void clearList() {
        Dialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("警告")
                .setIcon(R.drawable.meiling_logo)
                .setMessage("确认清空吗？")
                //相当于点击确认按钮
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (ibList.size() > 0) {
                            for (int j = 0; j < ibList.size(); j++) {
//            ibList.remove(j);
                                IbDataBaseUtil.delete(MainActivity.this, ibList.get(j));
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "列表已经清空!", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                //相当于点击取消按钮
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        Toast.makeText(MainActivity.this, "取消", Toast.LENGTH_LONG).show();
                    }
                })
                .create();
        dialog.show();
    }

    private void importIc() {
        startActivityForResult(new Intent(this, SelectActivity.class), 1);

    }

    /**
     * 为了得到传回的数据，必须重写onActivityResult方法
     *
     * @param requestCode 请求码，即调用startActivityForResult()传递过去的值
     * @param resultCode  结果码，结果码用于标识返回数据来自哪个新Activity
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case 1:
                //来自导入按钮的请求，作相应业务处理
//                selectList = data.getExtras().getSerializable("item");
//                ArrayList<String> selectList = data.getStringArrayListExtra("item");
                List<IceBox> selectList = (List<IceBox>) data.getExtras().getSerializable("item");
//                System.out.print(selectList.get(1));
                //判断是否为空
                for (int i = 0; i < selectList.size(); i++) {
                    IbDataBaseUtil.insert(this, selectList.get(i));
//                    DataBaseUtil.insert(this, item);
                    ibList.clear();
                    ibList.addAll(IbDataBaseUtil.queryAll(this));
                    mAdapter = new MyAdapter(ibList, this);
                    mLv.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
//                IceBox item = (IceBox)data.getExtras().getSerializable("item");//得到新Activity 关闭后返回的对象 不能包含CheckBox
            case 2:
        }
    }

    public void onEventMainThread(ComEvent event) {
        if (event.getActionType() == ComEvent.ACTION_GET_CODE) {
            String code = event.getMessage();
            sendCode2Server(code);/////////////////////

            ib = searchItemFromR2code(code);
            if (ib == null) {
                // 没有找到冰箱 设置成9
                ib = new IceBox();
                ib.setR3code("未知型号");
                ib.setIceBoxName("9");
                ib.setDeth("9");
                ib.setWith("9");
                ib.setHeight("9");
                ib.setCmd("9");
                ib.setLocation("9");
                logTv.setText("没有匹配到冰箱");
                SendPLCMsg msg = new SendPLCMsg(ib);
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                String date = sdf.format(new java.util.Date());
                msg.setTm(date);
                Constant.plcMsgsList.add(msg);
                mPlc1Adapter.plcMsgsList = Constant.plcMsgsList;
                mPlc1Adapter.notifyDataSetChanged();
                logTv.setText(ib.getIceBoxName().toString());
                sendOK();
//                try {
//                    isRece = false;
//                    Log.i(TAG, "false");
//                    Thread.sleep(8000);//
//                    Log.i(TAG, "true");
//                    isRece = true;
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            } else {
                SendPLCMsg msg = new SendPLCMsg(ib);
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                String date = sdf.format(new java.util.Date());
                msg.setTm(date);
                Constant.plcMsgsList.add(msg);
                mPlc1Adapter.plcMsgsList = Constant.plcMsgsList;
                mPlc1Adapter.notifyDataSetChanged();
                logTv.setText(ib.getIceBoxName().toString());
                sendOK();
            }
        } else if (event.getActionType() == ComEvent.ACTION_GET_PCI) {
            String code = event.getMessage().toUpperCase();
            Log.i(TAG, code);
            sendMsg2PCI(code);
//            if (isRece) {//true
//                sendMsg2PCI(code);
//            } else {
//                Log.i(TAG, "等待");
//            }
        } else if (event.getActionType() == ComEvent.ACTION_GET_ROB) {
            String code = event.getMessage();
            String EAN = event.getEAN();
            Log.i(TAG, "EAN:" + EAN + "-code:" + code);
//            sendMsg2ROB(code,EAN);
            sendMsg2ROB1(code, EAN);
        } else if (event.getActionType() == ComEvent.ACTION_GET_PLC) {
            String code = event.getMessage();
            Log.i(TAG, code);
        }
    }

    private void openCom() {
        openComBtn.setText("正在通信");
        openComBtn.setEnabled(false);
        closeComBtn.setEnabled(true);
        //开启服务
        startService(new Intent(this, ComService.class));
    }

    private void closeCom() {
        Dialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("警告")
                .setIcon(R.drawable.meiling_logo)
                .setMessage("确认关闭吗？")
                //相当于点击确认按钮
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        openComBtn.setText("开始通信");
                        openComBtn.setEnabled(true);
                        closeComBtn.setEnabled(false);
                        stopService(new Intent(MainActivity.this, ComService.class));//停止服务
                        logTv.setText("已经停止通信");
                    }
                })
                //相当于点击取消按钮
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        Toast.makeText(MainActivity.this, "取消", Toast.LENGTH_LONG).show();
                    }
                })
                .create();
        dialog.show();

    }

    public void sendOK() {
        String s = "U";
        EventBus.getDefault().post(new ComEvent(s, ComEvent.ACTION_SEND_PCI));
    }

    public void sendR() {
        String s2 = "R";
        EventBus.getDefault().post(new ComEvent(s2, ComEvent.ACTION_SEND_PCI));
    }

    public void sendMsg2PCI(String str) {
        if (str.contains("BJ")) {
            //解除报警
            sendR();
            //生成一个空码
            ib = new IceBox();
            ib.setR3code("未扫到码");
            ib.setIceBoxName("9");
            ib.setDeth("9");
            ib.setWith("9");
            ib.setHeight("9");
            //cmd要用到
            ib.setCmd("9");
            ib.setLocation("9");
            logTv.setText("没有扫描到二维码，自动解除报警生成空码");
            SendPLCMsg msg = new SendPLCMsg(ib);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            String date = sdf.format(new java.util.Date());
            msg.setTm(date);
            Constant.plcMsgsList.add(msg);
            mPlc1Adapter.plcMsgsList = Constant.plcMsgsList;
            mPlc1Adapter.notifyDataSetChanged();
            logTv.setText(ib.getIceBoxName().toString());
        } else {
            logTv.setText("警告：从板卡接收到未知数据！");
        }
    }

    public void sendMsg2ROB(String str, String EAN) {
        if (str.contains("XH")) {
            sendXH(EAN);
            logTv.setText("XH");
        } else {
            sendOthers2(EAN);
            logTv.setText("接收的不是定义操作");
        }
        plcMsgsList.clear();
        plcMsgsList.addAll(getSendPlcList());
        mPlc1Adapter.notifyDataSetChanged();
    }

    public void sendMsg2ROB1(String str, String EAN) {
        if (EAN.equals("85") || EAN.equals("42")) {
            if (EAN.equals(byte3)) {//直接发送byte1,byte2,byte3（均是上次保留的值）,不从队列中抽取值
                byte[] sendBytes = byteMerger(new byte[]{Utils.strToIntNum(byte1)}, new byte[]{Utils.strToIntNum(byte2)});
                sendBytes = byteMerger(sendBytes, new byte[]{Utils.strToIntNum(byte3)});
                EventBus.getDefault().post(new ComEvent(sendBytes, ComEvent.ACTION_SEND_ROB));
            } else {//校验码
                if (str.contains("xh")) {//从队列里抽取值到 byte1,byte2中；
                    sendXH(EAN);
                } else if (str.contains("cs")) {//则byte1,byte2均赋值为51；
                    byte1 = "51";
                    byte2 = "51";
                    sendOthers3(EAN);
                } else {//忽略
                    Log.e(TAG, "不是xh或cs");
                }
                byte3 = EAN;
            }
        } else {//若byteRcv ！=85或170则，则忽略当前消息；提前返回。
            Log.e(TAG, "byteRcv ！=85或170");
        }
        plcMsgsList.clear();
        plcMsgsList.addAll(getSendPlcList());
        mPlc1Adapter.notifyDataSetChanged();
    }

    public static byte[] byteMerger(byte[] byte_1, byte[] byte_2) {
        byte[] byte_3 = new byte[byte_1.length + byte_2.length];
        System.arraycopy(byte_1, 0, byte_3, 0, byte_1.length);
        System.arraycopy(byte_2, 0, byte_3, byte_1.length, byte_2.length);
        return byte_3;
    }

    public void sendOthers2(String EANCode) {
        byte[] sendBytes = new byte[]{0, 0, 0, 0, 0, 0, 0, 0};
        sendBytes = byteMerger(sendBytes, new byte[]{Utils.strToIntNum(EANCode)});
        EventBus.getDefault().post(new ComEvent(sendBytes, ComEvent.ACTION_SEND_ROB));
//        EventBus.getDefault().post(new ComEvent(new byte[]{0, 0, 0, 0}, ComEvent.ACTION_SEND_ROB));
    }

    public void sendOthers3(String EANCode) {
        byte[] sendBytes = byteMerger(new byte[]{Utils.strToIntNum("51")}, new byte[]{Utils.strToIntNum("51")});
        sendBytes = byteMerger(sendBytes, new byte[]{Utils.strToIntNum(EANCode)});
        EventBus.getDefault().post(new ComEvent(sendBytes, ComEvent.ACTION_SEND_ROB));

//        EventBus.getDefault().post(new ComEvent(new byte[]{0, 0, 0, 0}, ComEvent.ACTION_SEND_ROB));
    }

    public void sendXH(String EANCode) {
        if (Constant.plcMsgsList.size() == 0) {
            // 没有冰箱消息在队列中 给予警告
            byte[] sendBytes = byteMerger(new byte[]{Utils.strToIntNum("51")}, new byte[]{Utils.strToIntNum("51")});
            byte1 = "51";
            byte2 = "51";
            sendBytes = byteMerger(sendBytes, new byte[]{Utils.strToIntNum(EANCode)});
            EventBus.getDefault().post(new ComEvent(sendBytes, ComEvent.ACTION_SEND_ROB));

            logTv.setText("没有冰箱");
            return;
        }
        //按顺序发送参数
        for (int i = 0; i < Constant.plcMsgsList.size(); i++) {
            SendPLCMsg msg = Constant.plcMsgsList.get(i);
            if (msg.getHx() == false) {
                //将消息发送给机械臂
                msg.setHx(true);
                IceBox ib = msg.getIb();
                String xh = ib.getCmd();
                String loc = ib.getLocation();
                byte[] sendBytes = byteMerger(new byte[]{Utils.strToIntNum(xh)}, new byte[]{Utils.strToIntNum(loc)});
                byte1 = xh;
                byte2 = loc;
                sendBytes = byteMerger(sendBytes, new byte[]{Utils.strToIntNum(EANCode)});
                EventBus.getDefault().post(new ComEvent(sendBytes, ComEvent.ACTION_SEND_ROB));
                //正常套箱计数
                int xh1 = Integer.parseInt(ib.getCmd());
                if (xh1 <= 6) {
                    intSetCompletion++;
                    setCompletionTv.setText(String.valueOf(intSetCompletion));
                }
                return;
            }
        }
        byte[] sendBytes = byteMerger(new byte[]{Utils.strToIntNum("51")}, new byte[]{Utils.strToIntNum("51")});
        sendBytes = byteMerger(sendBytes, new byte[]{Utils.strToIntNum(EANCode)});
        byte1 = "51";
        byte2 = "51";
        EventBus.getDefault().post(new ComEvent(sendBytes, ComEvent.ACTION_SEND_ROB));
        logTv.setText("没有冰箱");
    }

    private void updateClikedItem() {
        mDialog.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        mDialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_update_item, null);
        mDialog.setView(view, 0, 0, 0, 0);
        mDialog.show();
        addItemOkBtn = (Button) view.findViewById(R.id.additem_btn);
        addItemCancelBtn = (Button) view.findViewById(R.id.cancelitem_btn);

        r3Et = (EditText) view.findViewById(R.id.r3_et);
        r3Et.setText(ib_update.getR3code());

        icsEt = (EditText) view.findViewById(R.id.ics_et);
        icsEt.setText(ib_update.getIceBoxName());

        dethEt = (EditText) view.findViewById(R.id.deth_et);
        dethEt.setText(ib_update.getDeth());

        withEt = (EditText) view.findViewById(R.id.with_et);
        withEt.setText(ib_update.getWith());

        heightEt = (EditText) view.findViewById(R.id.height_et);
        heightEt.setText(ib_update.getHeight());

        cmdEt = (EditText) view.findViewById(R.id.cmd_et);//设置成下拉框
        cmdEt.setText(ib_update.getCmd());

        locationEt = (EditText) view.findViewById(R.id.loc_et);
        locationEt.setText(ib_update.getLocation());

        //添加更新后的
        addItemOkBtn.setOnClickListener(this);
        addItemCancelBtn.setOnClickListener(this);
    }

    private void deleteClickedItem() {
        // 删除该行
        mDialog.dismiss();
        IbDataBaseUtil.delete(this, ib_update);
        ibList.clear();
        ibList.addAll(IbDataBaseUtil.queryAll(this));
        mAdapter.notifyDataSetChanged();
    }

    private void deleteClickedPItem() {
        //删除PLC list
        mDialog.dismiss();
        mPlc1Adapter.notifyDataSetChanged();
    }

    private class MyAdapter extends BaseAdapter {

        private ArrayList<IceBox> ibList;
        private Context context;

        public MyAdapter(ArrayList<IceBox> list, Context context) {
            ibList = list;
            this.context = context;
            Log.i(TAG, list.size() + "~~~~listsize");
        }

        @Override
        public int getCount() {
            return ibList.size();
        }

        @Override
        public Object getItem(int position) {
            return ibList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v;
            if (convertView != null) {
                v = convertView;
            } else {
                v = View.inflate(context, R.layout.item_list_loc, null);
            }
            TextView r3View = (TextView) v.findViewById(R.id.r3code);
            TextView styView = (TextView) v.findViewById(R.id.icstyle);
            TextView dethView = (TextView) v.findViewById(R.id.deth);
            TextView withView = (TextView) v.findViewById(R.id.with);
            TextView hView = (TextView) v.findViewById(R.id.height);
            TextView codeView = (TextView) v.findViewById(R.id.code);
            TextView locationView = (TextView) v.findViewById(R.id.location);

            r3View.setText(ibList.get(position).getR3code());
            styView.setText(ibList.get(position).getIceBoxName());
            dethView.setText(ibList.get(position).getDeth());
            withView.setText(ibList.get(position).getWith());
            hView.setText(ibList.get(position).getHeight());
            codeView.setText(ibList.get(position).getCmd());
            locationView.setText(ibList.get(position).getLocation());

            return v;
        }
    }

    private ArrayList<SendPLCMsg> getSendPlcList() {
        ArrayList<SendPLCMsg> a = new ArrayList<>();
        for (SendPLCMsg msg : Constant.plcMsgsList) {
            a.add(msg);
        }
        return a;
    }

    private class MyPlc1Adapter extends BaseAdapter {
        public ArrayList<SendPLCMsg> plcMsgsList;
        private Context context;

        public MyPlc1Adapter(ArrayList<SendPLCMsg> msg, Context context) {
            plcMsgsList = msg;
            this.context = context;
        }

        @Override
        public int getCount() {
            return plcMsgsList.size();
        }

        @Override
        public Object getItem(int position) {
            return plcMsgsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v;
            if (convertView != null) {
                v = convertView;
            } else {
                v = View.inflate(context, R.layout.item_list_plc, null);
            }
            TextView tm = (TextView) v.findViewById(R.id.plc1_tm);
            TextView r3 = (TextView) v.findViewById(R.id.plc1_r3);
            TextView hw = (TextView) v.findViewById(R.id.plc1_hw);
            //设置扫码时间
            tm.setText(plcMsgsList.get(position).getTm());
            r3.setText(plcMsgsList.get(position).getIb().getR3code());
            if (plcMsgsList.get(position).getHx()) {
                hw.setTextColor(Color.RED);
                hw.setText("已送");
            } else {
                hw.setTextColor(Color.BLACK);
                hw.setText("未送");
            }
            return v;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        stopService(new Intent(this, ComService.class));
        Constant.plcMsgsList.clear();
        plcMsgsList.clear();
    }

    /**
     * 按两次返回键退出程序
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次退出程序",
                        Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}