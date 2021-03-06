
package com.goldsand.collaboration.server_test.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.goldsand.collaboration.connection.SocketConnectThreadListener;
import com.goldsand.collaboration.server.Utils;
import com.goldsand.collaboration.server.connection.SocketServerService;
import com.goldsand.collaboration.server.connection.SocketServerService.MyBinder;
import com.goldsand.collaboration.server_test.R;

public class TestServerActivity extends Activity implements ServiceConnection, SocketConnectThreadListener {
    private TextView showView;
    private Button submitButton;
    private EditText editText;
    private SocketServerService serverService;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_server_activity);
        showView = (TextView) findViewById(R.id.showMessage);
        submitButton = (Button) findViewById(R.id.submit);
        submitButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (serverService != null) {
                    serverService.sendDataToClient(editText.getText().toString());
                }
            }
        });
        scrollView = (ScrollView)findViewById(R.id.scroll_view);
        editText = (EditText) findViewById(R.id.input);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = SocketServerService.createServerInIntent();
        startService(intent);
        bindService(intent, this, BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(this);
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            showView.append((String) msg.obj + "\n");
            scrollView.fullScroll(ScrollView.FOCUS_DOWN);
        };
    };

    @Override
    public void onNewMessageReceived(String threadName, String message) {
        Message message2 = handler.obtainMessage();
        message2.obj = message;
        message2.sendToTarget();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        serverService = ((MyBinder) service).getService();
        serverService.addSocketServerMessageListener(this);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
    }

    @Override
    public void onSocketConnectStateChange(boolean connected) {
    }

    @Override
    public void onNewMessageReceived(String threadName, Object message) {
        Message message2 = handler.obtainMessage();
        message2.obj = message.toString();
        message2.sendToTarget();
    }

}
