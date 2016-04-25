package com.goldsand.collaboration.server.test;

import com.goldsand.collaboration.server.manager.ApplicationManager;
import com.goldsand.collaboration.server.CollaborationService;
import com.goldsand.collaboration.server.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MyServerTest  extends Activity{

    private TextView mMsgTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server_activity);
        mMsgTextView = (TextView) findViewById(R.id.showMessage);
        Button mSendButton = (Button) findViewById(R.id.submit);
        mSendButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //
            }
        });

        Intent collaborationIntent = null;
        collaborationIntent = new Intent(MyServerTest.this, CollaborationService.class);
        startService(collaborationIntent);

    }
}
