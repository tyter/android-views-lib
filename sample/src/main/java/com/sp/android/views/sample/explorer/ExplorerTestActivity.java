package com.sp.android.views.sample.explorer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.sp.android.views.explorer.ExplorerActivity;
import com.sp.android.views.sample.R;

public class ExplorerTestActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_test_explorer_activity);

        findViewById(R.id.btn_test).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_test) {
            Intent intent = new Intent(this, ExplorerActivity.class);
            startActivity(intent);
        }
    }
}
