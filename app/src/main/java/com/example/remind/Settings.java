package com.example.remind;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class Settings extends AppCompatActivity {

    private TextView numText = null;
    private Switch mSwitch = null;
    private int num = 2;
    private boolean bool = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        actionBar.setCustomView(R.layout.settings_bar);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        numText = findViewById(R.id.rangeNum);
        SharedPreferences sharedPreferences = getSharedPreferences("rangnum", MODE_PRIVATE);
        num = sharedPreferences.getInt("num", 2);
        numText.setText(Integer.toString(num));

        mSwitch = findViewById(R.id.switchCon);
        sharedPreferences = getSharedPreferences("isChecked", MODE_PRIVATE);
        bool = sharedPreferences.getBoolean("check", true);
        mSwitch.setChecked(bool);
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    //Todo
                    SharedPreferences sharedPreferences = getSharedPreferences("isChecked", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("check", mSwitch.isChecked());
                    editor.commit();
                }else {
                    //Todo
                    SharedPreferences sharedPreferences = getSharedPreferences("isChecked", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("check", mSwitch.isChecked());
                    editor.commit();
                }
            }
        });
    }

    public void toLeft(View view) {
        Intent intent = new Intent();
        intent.putExtra("rangenum",num);
        setResult(RESULT_OK, intent);
        finish();
    }
    public void iv_1(View view){
        numText = findViewById(R.id.rangeNum);
        num=Integer.parseInt(numText.getText().toString());
        if(num>1){
            num-=1;
        }
        numText.setText(Integer.toString(num));

        SharedPreferences sharedPreferences = getSharedPreferences("rangnum", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("num",Integer.parseInt(numText.getText().toString()));
        editor.commit();
    }
    public void iv_2(View view){
        numText = findViewById(R.id.rangeNum);
        Log.e("MainActivity",numText.getText().toString());
        num=Integer.parseInt(numText.getText().toString());
        if(num<10){
            num+=1;
        }
        numText.setText(Integer.toString(num));

        SharedPreferences sharedPreferences = getSharedPreferences("rangnum", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("num",Integer.parseInt(numText.getText().toString()));
        editor.commit();
    }

}
