package com.sp.android.views.sample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.sp.android.views.sample.pickerview.PickerActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main_activity);
        findViewById(R.id.btn_test_picker).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_test_picker) {
            Intent intent = new Intent(this, PickerActivity.class);
            startActivity(intent);
        }
    }
}
