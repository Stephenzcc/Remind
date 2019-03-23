package com.example.remind;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class POISearch extends AppCompatActivity {

    private EditText edirsearch, editsave1, editsave2, editsave3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poisearch);
        SharedPreferences sharedPreferences = getSharedPreferences("address", MODE_PRIVATE);
        String es1 = sharedPreferences.getString("save1","null");
        String es2 = sharedPreferences.getString("save2","null");
        String es3 = sharedPreferences.getString("save3","null");
        edirsearch = findViewById(R.id.searchText);
        editsave1 = findViewById(R.id.editText1);
        editsave2 = findViewById(R.id.editText2);
        editsave3 = findViewById(R.id.editText3);
        editsave1.setText(es1);
        editsave2.setText(es2);
        editsave3.setText(es3);
    }


    public void toMap(View view) {
        finish();
    }

    public void saveAddress(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences("address", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        switch (view.getId()){
            case R.id.save1:
                editor.putString("save1",editsave1.getText().toString());
                editor.commit();
                break;
            case R.id.save2:
                editor.putString("save2",editsave2.getText().toString());
                editor.commit();
                break;
            case R.id.save3:
                editor.putString("save3",editsave3.getText().toString());
                editor.commit();
                break;
            default:

        }
    }

    public void returnAddress(View view) {
        Intent intent=this.getIntent();
        switch (view.getId()){
            case R.id.submit1:
                intent.putExtra("location", editsave1.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.submit2:
                intent.putExtra("location", editsave2.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.submit3:
                intent.putExtra("location", editsave3.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.searchicon:
                intent.putExtra("location", edirsearch.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
                break;
            default:

        }
    }
}
