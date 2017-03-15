package com.mj.app.print;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by wdongjia on 2016/8/15.
 */
public class CodeAdapter extends BaseAdapter {

    private Context mContext;

    private LayoutInflater mInflater;

    public List<QrCode> mDatas;

    //定义hashMap 用来存放之前创建的每一项item
    HashMap<Integer, View> hashMap = new HashMap<Integer, View>();

    public CodeAdapter(List<QrCode> msg, Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mDatas = msg;
    }

    @Override
    public int getCount() {
        return (mDatas != null ? mDatas.size() : 0);
    }

    @Override
    public Object getItem(int position) {
        return (mDatas != null ? mDatas.get(position) : null);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        //创建每一个滑动出来的item项，将创建出来的项，放入数组中，为下次复用使用
        if (hashMap.get(position) == null) {
            convertView = mInflater.inflate(R.layout.item_layout, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
            hashMap.put(position, convertView);
        } else {
            convertView = hashMap.get(position);
            holder = (ViewHolder) convertView.getTag();
        }
        QrCode qrCode = mDatas.get(position);
        //颜色区分
        if (position % 2 == 0) {
            convertView.setBackgroundResource(R.color.item_1);
        } else {
            convertView.setBackgroundResource(R.color.item_2);
        }

        if (qrCode != null) {
            holder.tvDate.setText(qrCode.getTime());
            String code = qrCode.getCode();
            //给R3码添加HTML标记
            if (code.length()==24){
                code = code.substring(0, 3) + "<font color='#FF0000'>" +
                        code.substring(3, 10) + "</font>" + code.substring(10, 24);
            }
            holder.tvCode.setText(Html.fromHtml(code));
            holder.tvQrcode.setText(Html.fromHtml(code));
            holder.tvStatus.setText("已打印");
        }
        return convertView;
    }

    class ViewHolder {
        @butterknife.InjectView(R.id.tv_date)
        TextView tvDate;
        @butterknife.InjectView(R.id.tv_code)
        TextView tvCode;
        @butterknife.InjectView(R.id.tv_qrcode)
        TextView tvQrcode;
        @butterknife.InjectView(R.id.tv_status)
        TextView tvStatus;

        ViewHolder(View view) {
            butterknife.ButterKnife.inject(this, view);
        }
    }
}