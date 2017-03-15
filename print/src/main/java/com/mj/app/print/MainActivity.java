package com.mj.app.print;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ListView;

import com.mj.app.print.event.ComEvent;
import com.mj.app.print.service.ComService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class MainActivity extends Activity {

    @InjectView(R.id.lv_item)
    ListView lvItem;
    private CodeAdapter cadapter;
    private List<QrCode> qrcodeList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        initView();
        EventBus.getDefault().register(this);
        startService(new Intent(this, ComService.class));
    }

    private void initView() {
        cadapter = new CodeAdapter(qrcodeList, this);
        lvItem.setAdapter(cadapter);//显示数据
    }

    public void onEventMainThread(ComEvent event) {
        if (event.getActionType() == ComEvent.ACTION_GET_CODE) {
            String code = event.getMessage();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            String date = sdf.format(new Date());
            QrCode qrcode = new QrCode();
            qrcode.setTime(date);
            qrcode.setCode(code);
            qrcode.setQrcode(code);
            qrcode.setStatus(true);
            qrcodeList.add(qrcode);
            cadapter.mDatas = qrcodeList;
            cadapter.notifyDataSetChanged();
            EventBus.getDefault().post(new ComEvent(code, ComEvent.ACTION_SEND_PRINT));
        }
    }
}
