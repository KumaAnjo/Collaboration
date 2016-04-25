
package com.goldsand.collaboration.server.authorize;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.goldsand.collaboration.server.R;
import com.goldsand.collaboration.server.manager.ApplicationManager;

public class AuthorizeCheckerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authorize_checker_activity);
        String message = getIntent().getStringExtra("message");
        TextView title = (TextView) findViewById(R.id.title);
        String titleStr = String.format(
                getResources().getString(R.string.request_connection_title), message);
        title.setText(titleStr);
        final CheckBox checkBox = (CheckBox)findViewById(R.id.save_device_info);
        ((Button) findViewById(R.id.button_ok)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean saveDeviceInfo = checkBox.isChecked();
                ApplicationManager.getInstance(AuthorizeCheckerActivity.this).checkPass(saveDeviceInfo);
                AuthorizeCheckerActivity.this.finish();
            }
        });
        ((Button) findViewById(R.id.button_cancel)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplicationManager.getInstance(AuthorizeCheckerActivity.this).checkFail();
                AuthorizeCheckerActivity.this.finish();
            }
        });
    }
}
