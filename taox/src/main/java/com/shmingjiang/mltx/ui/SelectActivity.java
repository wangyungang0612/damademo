package com.shmingjiang.mltx.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.shmingjiang.mltx.Constant;
import com.shmingjiang.mltx.db.AllIbDataBaseUtil;
import com.shmingjiang.mltx.db.IbDataBaseUtil;
import com.shmingjiang.mltx.db.InsertPreparedData;

import com.shmingjiang.mltx.R;
import com.shmingjiang.mltx.bean.IceBox;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hasee on 2016/2/18.
 */
public class SelectActivity extends Activity implements View.OnClickListener {

    protected final String TAG = SelectActivity.class.getSimpleName();
    private ListView listview;
    private Context context;
    private AlertDialog dialog;
    private List<IceBox> ibs = new ArrayList<IceBox>();
    private List<IceBox> ibs_select = new ArrayList<IceBox>();
    private boolean isMulChoice = false; //是否多选
    private List<IceBox> selectid = new ArrayList<IceBox>();
    private Adapter itemadapter;
    private RelativeLayout layout;
    private Button importok, delete, addic, queryic, queryIc_ok, queryIc_cancel,
            queryall, returnmain;
    private Button additem_okbtn, additem_cancelbtn;
    private EditText et_r3, et_ics, et_deth, et_with, et_height, et_cmd,
            et_location, et_query;
    private TextView txtcount;

    private int count;
    private int currentNum;
    private long frsClick;
    private long secClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);
        context = this;

//        if (!checkTheDatabase()) {
//            //
//            AllIbDataBaseUtil.createDataBase(this);
//            InsertPreparedData.insertData(this);
//        }

        //ListView操作
        listview = (ListView) findViewById(R.id.my_list_view);
        layout = (RelativeLayout) findViewById(R.id.relative);
        txtcount = (TextView) findViewById(R.id.txtcount);
        importok = (Button) findViewById(R.id.btn_import_ok);
        delete = (Button) findViewById(R.id.btn_delete);
        addic = (Button) findViewById(R.id.bt_add_item);
        queryic = (Button) findViewById(R.id.bt_queryic);
        queryall = (Button) findViewById(R.id.bt_queryall);
        returnmain = (Button) findViewById(R.id.bt_return_main);

        importok.setOnClickListener(this);
        delete.setOnClickListener(this);
        addic.setOnClickListener(this);
        queryic.setOnClickListener(this);
        queryall.setOnClickListener(this);
        returnmain.setOnClickListener(this);
        if (!checkTheDatabase()) {
            IbDataBaseUtil.createDataBase(this);
            InsertPreparedData.insertData(this);
        }
        ibs = AllIbDataBaseUtil.queryAll(this);
//        ibs = MockDataUtils.getItems();//测试数据
        itemadapter = new Adapter(context, txtcount);//适配数据
        listview.setAdapter(itemadapter);//显示数据

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                // int count = 0;
                count++;
                if (count == 1) {
                    frsClick = System.currentTimeMillis();
                    currentNum = position;
                } else if (count == 2) {
                    secClick = System.currentTimeMillis();
                    if ((secClick - frsClick <= 500) && (currentNum == position)) {
                        //双击事件
                        Toast.makeText(SelectActivity.this, "双击了!", Toast.LENGTH_LONG).show();
                        Dialog dialog = new AlertDialog.Builder(SelectActivity.this)
                                .setTitle("删除对话框")
                                .setIcon(R.drawable.meiling_logo)
                                .setMessage("确认删除吗？")
                                //相当于点击确认按钮
                                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        AllIbDataBaseUtil.delete(context, ibs.get(position));
                                        ibs.clear();
                                        ibs = AllIbDataBaseUtil.queryAll(context);
                                        itemadapter.notifyDataSetChanged();
                                        Toast.makeText(SelectActivity.this, "删除成功!", Toast.LENGTH_LONG).show();
                                    }
                                })
                                //相当于点击取消按钮
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {// TODO Auto-generated method stub
                                        Toast.makeText(SelectActivity.this, "取消删除!", Toast.LENGTH_LONG).show();
                                    }
                                })
                                .create();
                        dialog.show();
                    }

                    count = 0;
                    frsClick = 0;
                    secClick = 0;
                }
            }
        });

    }

    private Boolean checkTheDatabase() {
        String path = getFilesDir().getAbsolutePath(); //+"/databases/";
        Log.i(TAG, path.substring(0, path.lastIndexOf("/"))); //path.substring(path.lastIndexOf("/")+1);
        path = path.substring(0, path.lastIndexOf("/")) + "/databases/";
        File file = new File(path + Constant.dataBaseName1);
        if (file.exists()) {
            Toast.makeText(this, "数据库是存在的!", Toast.LENGTH_LONG).show();
            return true;
        }
        Toast.makeText(this, "数据库不存在！", Toast.LENGTH_LONG).show();
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_import_ok) {
            isMulChoice = false;
            for (int i = 0; i < selectid.size(); i++) {
                for (int j = 0; j < ibs.size(); j++) {
                    if (selectid.get(i).equals(ibs.get(j))) {
                        //用一个列表存储已经选择的冰箱
                        ibs_select.add(ibs.get(j));
                    }
                }
            }
            //跳转  将列表传到主页
            Intent intent = new Intent();
            intent.putExtra("item", (Serializable) ibs_select);
            //设置返回数据
            SelectActivity.this.setResult(1, intent);
            //关闭Activity
            SelectActivity.this.finish();
        } else if (v.getId() == R.id.btn_delete) {
            //删除item
            isMulChoice = false;
            for (int i = 0; i < selectid.size(); i++) {
                for (int j = 0; j < ibs.size(); j++) {
                    if (selectid.get(i).equals(ibs.get(j))) {
                        AllIbDataBaseUtil.delete(this, ibs.get(j));
                        ibs.remove(j);
                    }
                }
            }
            selectid.clear();
            itemadapter = new Adapter(context, txtcount);
            listview.setAdapter(itemadapter);
            layout.setVisibility(View.INVISIBLE);
        } else if (v.getId() == R.id.bt_add_item) {
            addIcItemDialog();
        } else if (v.getId() == R.id.cancelitem_btn) {
            dialog.dismiss();
        } else if (v.getId() == R.id.bt_queryic) {
            queryIcDialog();
        } else if (v.getId() == R.id.bt_queryall) {
            queryAllAndUpdateList();
        } else if (v.getId() == R.id.bt_return_main) {
            Intent intent1 = new Intent();
            intent1.putExtra("item", (Serializable) ibs_select);
            SelectActivity.this.setResult(1, intent1);
            SelectActivity.this.finish();
        } else if (v.getId() == R.id.additem_btn) {
            checkTheDataAndInert2DB();
        } else if (v.getId() == R.id.btn_query_cancel) {
            dialog.dismiss();
        } else if (v.getId() == R.id.btn_query_ok) {
            queryItemAndUpdateList();
        } else {

        }

    }

    private void checkTheDataAndInert2DB() {
        String r3code = et_r3.getText().toString().trim();
        String ics = et_ics.getText().toString().trim();
        String deth = et_deth.getText().toString().trim();
        String with = et_with.getText().toString().trim();
        String heght = et_height.getText().toString().trim();
        String cmd = et_cmd.getText().toString().trim();
        String location = et_location.getText().toString().trim();
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
            AllIbDataBaseUtil.insert(this, ic);
            ibs.clear();
            ibs = AllIbDataBaseUtil.queryAll(this);
            //必须重新适配数据 否则会出错
            itemadapter = new Adapter(context, txtcount);//适配数据
            listview.setAdapter(itemadapter);//显示数据
            dialog.dismiss();
        }
    }

    private void queryItemAndUpdateList() {
        dialog.dismiss();
        String s = et_query.getText().toString();
        IceBox ib = AllIbDataBaseUtil.queryItem(s, this);
        ibs.clear();
        ibs.add(ib);
        itemadapter.notifyDataSetChanged();
    }

    private void queryAllAndUpdateList() {
        ibs.clear();
        ibs.addAll(AllIbDataBaseUtil.queryAll(this));
        itemadapter.notifyDataSetChanged();
    }

    private void queryIcDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_query_item, null);
        dialog.setView(view, 0, 0, 0, 0);
        dialog.show();
        queryIc_ok = (Button) view.findViewById(R.id.btn_query_ok);
        queryIc_cancel = (Button) view.findViewById(R.id.btn_query_cancel);
        et_query = (EditText) view.findViewById(R.id.et_query);
        queryIc_ok.setOnClickListener(this);
        queryIc_cancel.setOnClickListener(this);
    }

    private void addIcItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        dialog = builder.create();
        View view = View.inflate(this, R.layout.dialog_update_item, null);
        dialog.setView(view, 0, 0, 0, 0);
        dialog.show();
        additem_okbtn = (Button) view.findViewById(R.id.additem_btn);
        additem_cancelbtn = (Button) view.findViewById(R.id.cancelitem_btn);
        et_r3 = (EditText) view.findViewById(R.id.r3_et);
        et_ics = (EditText) view.findViewById(R.id.ics_et);
        et_deth = (EditText) view.findViewById(R.id.deth_et);
        et_with = (EditText) view.findViewById(R.id.with_et);
        et_height = (EditText) view.findViewById(R.id.height_et);
        et_cmd = (EditText) view.findViewById(R.id.cmd_et);//设置成下拉框
        et_location = (EditText) view.findViewById(R.id.loc_et);
        additem_okbtn.setOnClickListener(this);
        additem_cancelbtn.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 自定义Adapter
     *
     * @author wangdongjia
     */
    class Adapter extends BaseAdapter {
        private Context context;
        private LayoutInflater inflater = null;
        private HashMap<Integer, View> mView;
        private HashMap<Integer, Integer> visiblecheck;//用来记录是否显示checkBox
        private HashMap<Integer, Boolean> ischeck;
        private TextView txtcount;

        public Adapter(Context context, TextView txtcount) {
            this.context = context;
            this.txtcount = txtcount;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mView = new HashMap<Integer, View>();
            visiblecheck = new HashMap<Integer, Integer>();
            ischeck = new HashMap<Integer, Boolean>();
            if (isMulChoice) {
                for (int i = 0; i < ibs.size(); i++) {
                    ischeck.put(i, false);
                    visiblecheck.put(i, CheckBox.VISIBLE);
                }
            } else {
                for (int i = 0; i < ibs.size(); i++) {
                    ischeck.put(i, false);
                    visiblecheck.put(i, CheckBox.INVISIBLE);
                }
            }
        }

        public int getCount() {
            // TODO Auto-generated method stub
            return ibs.size();
        }

        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return ibs.get(position);
        }

        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View view = mView.get(position);
            if (view == null) {
                view = inflater.inflate(R.layout.item_list_bx, null);
                final CheckBox ceb = (CheckBox) view.findViewById(R.id.cb_item);
                TextView code = (TextView) view.findViewById(R.id.tv_code);
                TextView category = (TextView) view.findViewById(R.id.tv_category);
                TextView death = (TextView) view.findViewById(R.id.tv_death);
                TextView width = (TextView) view.findViewById(R.id.tv_width);
                TextView height = (TextView) view.findViewById(R.id.tv_height);
                TextView cmd = (TextView) view.findViewById(R.id.tv_cmd);
                TextView location = (TextView) view.findViewById(R.id.tv_location);
                code.setText(ibs.get(position).getR3code());
                category.setText(ibs.get(position).getIceBoxName());
                death.setText(ibs.get(position).getDeth());
                width.setText(ibs.get(position).getWith());
                height.setText(ibs.get(position).getHeight());
                cmd.setText(ibs.get(position).getCmd());
                location.setText(ibs.get(position).getLocation());
                ceb.setChecked(ischeck.get(position));
                ceb.setVisibility(visiblecheck.get(position));
                view.setOnLongClickListener(new Onlongclick());
                view.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        if (isMulChoice) {
                            if (ceb.isChecked()) {
                                ceb.setChecked(false);
                                selectid.remove(ibs.get(position));
                            } else {
                                ceb.setChecked(true);
                                selectid.add(ibs.get(position));
                            }
                            txtcount.setText("共选择了" + selectid.size() + "项");
                        } else {
                            Toast.makeText(context, "点击了" + ibs.get(position), Toast.LENGTH_LONG).show();
                        }
                    }
                });

                mView.put(position, view);
            }
            return view;
        }

        class Onlongclick implements View.OnLongClickListener {
            public boolean onLongClick(View v) {
                // TODO Auto-generated method stub
                isMulChoice = true;
                selectid.clear();
                layout.setVisibility(View.VISIBLE);
                for (int i = 0; i < ibs.size(); i++) {
                    itemadapter.visiblecheck.put(i, CheckBox.VISIBLE);
                }
                itemadapter = new Adapter(context, txtcount);
                listview.setAdapter(itemadapter);
                return true;
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {

            //返回到主页面
            Intent intent1 = new Intent();
            intent1.putExtra("item", (Serializable) ibs_select);
            SelectActivity.this.setResult(1, intent1);
            SelectActivity.this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
