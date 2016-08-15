package com.sp.android.views.sample.pickerview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.sp.android.views.sample.R;

public class PickerActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_picker_activity);

        findViewById(R.id.btn_select_month).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_select_month) {
            MonthSelectDialog dialog = new MonthSelectDialog(PickerActivity.this);
            dialog.show();
        }
    }
}
