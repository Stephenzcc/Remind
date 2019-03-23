package com.example.remind;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class POISearch extends AppCompatActivity {

    private EditText edirsearch, editsave1, editsave2, editsave3;
    private ListView listview = null;
    private ArrayAdapter<String> adapter = null;
    private String data[] = new String[6];
    private AlertDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poisearch);
        SharedPreferences sharedPreferences = getSharedPreferences("address", MODE_PRIVATE);
        String es1 = sharedPreferences.getString("save1","null");
        String es2 = sharedPreferences.getString("save2","null");
        String es3 = sharedPreferences.getString("save3","null");
        String es4 = sharedPreferences.getString("save4","null");
        String es5 = sharedPreferences.getString("save5","null");
        String es6 = sharedPreferences.getString("save6","null");

        data[0] = es1;
        data[1] = es2;
        data[2] = es3;
        data[3] = es4;
        data[4] = es5;
        data[5] = es6;

        edirsearch = findViewById(R.id.searchText);
        listview = findViewById(R.id.listview);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,data);
        listview.setAdapter(adapter);
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        setAlertDialog(view, position);
                        dialog.show();
                        //Toast.makeText(POISearch.this, "你长按了" + position + "按钮", Toast.LENGTH_SHORT).show();
                        break;

                    case 1:
                        setAlertDialog(view, position);
                        dialog.show();
                        //Toast.makeText(POISearch.this, "你长按了" + position + "按钮", Toast.LENGTH_SHORT).show();
                        break;

                    case 2:
                        setAlertDialog(view, position);
                        dialog.show();
                        //Toast.makeText(POISearch.this, "你长按了" + position + "按钮", Toast.LENGTH_SHORT).show();
                        break;

                    case 3:
                        setAlertDialog(view, position);
                        dialog.show();
                        //Toast.makeText(POISearch.this, "你长按了" + position + "按钮", Toast.LENGTH_SHORT).show();
                        break;

                    case 4:
                        setAlertDialog(view, position);
                        dialog.show();
                        //Toast.makeText(POISearch.this, "你长按了" + position + "按钮", Toast.LENGTH_SHORT).show();
                        break;

                    case 5:
                        setAlertDialog(view, position);
                        dialog.show();
                        //Toast.makeText(POISearch.this, "你长按了" + position + "按钮", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = POISearch.this.getIntent();
                switch (position){
                    case 0:
                        intent.putExtra("location", data[0]);
                        setResult(RESULT_OK, intent);
                        finish();
                        //Toast.makeText(POISearch.this,"你点击了"+position+"按钮",Toast.LENGTH_SHORT).show();
                        break;

                    case 1:
                        intent.putExtra("location", data[1]);
                        setResult(RESULT_OK, intent);
                        finish();
                        //Toast.makeText(POISearch.this,"你点击了"+position+"按钮",Toast.LENGTH_SHORT).show();
                        break;

                    case 2:
                        intent.putExtra("location", data[2]);
                        setResult(RESULT_OK, intent);
                        finish();
                        //Toast.makeText(POISearch.this,"你点击了"+position+"按钮",Toast.LENGTH_SHORT).show();
                        break;

                    case 3:
                        intent.putExtra("location", data[3]);
                        setResult(RESULT_OK, intent);
                        finish();
                        //Toast.makeText(POISearch.this,"你点击了"+position+"按钮",Toast.LENGTH_SHORT).show();
                        break;

                    case 4:
                        intent.putExtra("location", data[4]);
                        setResult(RESULT_OK, intent);
                        finish();
                        //Toast.makeText(POISearch.this,"你点击了"+position+"按钮",Toast.LENGTH_SHORT).show();
                        break;

                    case 5:
                        intent.putExtra("location", data[5]);
                        setResult(RESULT_OK, intent);
                        finish();
                        //Toast.makeText(POISearch.this,"你点击了"+position+"按钮",Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

    }


    private void setAlertDialog(final View view, final int position) {
        LayoutInflater factory = LayoutInflater.from(getApplicationContext());
        // 引入一个外部布局
        View contview = factory.inflate(R.layout.dialog, null);
        contview.setBackgroundColor(Color.WHITE);// 设置该外部布局的背景
        final EditText edit = (EditText) contview.findViewById(R.id.edit_dialog);// 找到该外部布局对应的EditText控件
        Button btOK = (Button) contview.findViewById(R.id.btOK_dialog);
        btOK.setOnClickListener(new View.OnClickListener() {// 设置按钮的点击事件

            @Override
            public void onClick(View v) {
                String regEx = "\\s*";
                Pattern pattern = Pattern.compile(regEx);
                Matcher matcher = pattern.matcher(edit.getText().toString());
                if(!matcher.matches()){
                    SharedPreferences sharedPreferences = getSharedPreferences("address", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if(position == 0){
                        editor.putString("save1",edit.getText().toString());
                        editor.commit();
                    }
                    else if(position == 1){
                        editor.putString("save2",edit.getText().toString());
                        editor.commit();
                    }
                    else if(position == 2){
                        editor.putString("save3",edit.getText().toString());
                        editor.commit();
                    }
                    else if(position == 3){
                        editor.putString("save4",edit.getText().toString());
                        editor.commit();
                    }
                    else if(position == 4){
                        editor.putString("save5",edit.getText().toString());
                        editor.commit();
                    }
                    else if(position == 5){
                        editor.putString("save6",edit.getText().toString());
                        editor.commit();
                    }
                    String es1 = sharedPreferences.getString("save1","null");
                    String es2 = sharedPreferences.getString("save2","null");
                    String es3 = sharedPreferences.getString("save3","null");
                    String es4 = sharedPreferences.getString("save4","null");
                    String es5 = sharedPreferences.getString("save5","null");
                    String es6 = sharedPreferences.getString("save6","null");

                    data[0] = es1;
                    data[1] = es2;
                    data[2] = es3;
                    data[3] = es4;
                    data[4] = es5;
                    data[5] = es6;

                    ((TextView) view).setText(edit.getText().toString());
                    dialog.dismiss();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Can't be space only!", Toast.LENGTH_LONG).show();
                }
            }
        });
        dialog = new AlertDialog.Builder(POISearch.this).setView(contview).create();
    }

    public void toMap(View view) {
        finish();
    }


    public void returnAddress(View view) {
        Intent intent=this.getIntent();
        switch (view.getId()){
            case R.id.searchicon:
                String regEx = "\\s*";
                Pattern pattern = Pattern.compile(regEx);
                Matcher matcher = pattern.matcher(edirsearch.getText().toString());
                if(!matcher.matches()){
                    intent.putExtra("location", edirsearch.getText().toString());
                    setResult(RESULT_OK, intent);
                    finish();
                }
                else{
                    Toast.makeText(getApplicationContext(), "Can't be space only!", Toast.LENGTH_LONG).show();
                }
                break;
            default:

        }
    }
}
