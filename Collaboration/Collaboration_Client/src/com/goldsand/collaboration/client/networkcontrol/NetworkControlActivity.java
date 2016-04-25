package com.goldsand.collaboration.client.networkcontrol;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.util.Log;

import com.goldsand.collaboration.client.ControlCenterDeamonService;
import com.goldsand.collaboration.client.R;

import com.goldsand.collaboration.client.manager.ApplicationManager;

import com.goldsand.collaboration.connection.NetWorkInfo;
import com.goldsand.collaboration.connection.NetWorkInfo.ConnectStatus;

public class NetworkControlActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "NetworkControlActivity";

    private Button mRefreshButton;
    private TextView mConnStateTextView;
    private TextView mConnNumTextView;
    private TextView mLocalIpTextView;
    private TextView mRemoteIpTextView;
    private TextView mRevPortEditView;
    private TextView mSendPortEditView;

    private Button mStartConnectButton;
    private Button mStopConnectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.network_info);

        mRefreshButton = (Button) findViewById(R.id.refresh);
        mRefreshButton.setOnClickListener(this);
        mConnStateTextView = (TextView) findViewById(R.id.net_conn_content);
        mConnNumTextView = (TextView) findViewById(R.id.net_conn_num_content);
        mLocalIpTextView = (TextView) findViewById(R.id.net_local_ip_content);
        mRemoteIpTextView = (TextView) findViewById(R.id.net_remote_ip_content);
        mRevPortEditView = (TextView) findViewById(R.id.net_rev_port_content);
        mSendPortEditView = (TextView) findViewById(R.id.net_send_port_content);

        mStartConnectButton = (Button) findViewById(R.id.start_connect);
        mStopConnectButton = (Button) findViewById(R.id.stop_connect);

        mStartConnectButton.setOnClickListener(this);
        mStopConnectButton.setOnClickListener(this);

        // test
        Intent collaborationIntent = new Intent(this, ControlCenterDeamonService.class);
        startService(collaborationIntent);

        updateNetWorkInfo();
    }

    private void updateNetWorkInfo() {
        NetWorkInfo info = ApplicationManager.getInstance(this).getNetworkInfo();

        mConnStateTextView.setText(getStatus(info.getStatus()));
        mConnNumTextView.setText(Integer.toString(info.getConnectionNumber()));
        mLocalIpTextView.setText(info.getLocalIp());
        mRemoteIpTextView.setText(info.getRemoteIp());
        mRevPortEditView.setText(Integer.toString(info.getConnectionPort()));
        mSendPortEditView.setText(Integer.toString(info.getClientBroadcastPort()));
    }

    private String getStatus(ConnectStatus status) {
        int resId;
        if (ConnectStatus.CONNECTED == status) {
            resId = R.string.net_state_connected;
        } else if (ConnectStatus.DISCONNECTED == status) {
            resId = R.string.net_state_disconnected;
        } else if (ConnectStatus.ERROR_CLIENT_BROADCAST_CONFLICT == status) {
            resId = R.string.net_state_client_broadcast_conflict;
        } else if (ConnectStatus.ERROR_SERVER_BROADCAST_CONFLICT == status) {
            resId = R.string.net_state_client_broadcast_conflict;
        } else if (ConnectStatus.ERROR_CONNECTION_CONFLICT == status) {
            resId = R.string.net_state_connect_conflict;
        } else if (ConnectStatus.ERROR_EXCEPTION == status) {
            resId = R.string.net_state_exception;
        } else {
            resId = R.string.net_unknow;
        }

        return this.getResources().getString(resId);
    }

    @Override
    public void onClick(View v) {
        if (v == mRefreshButton) {
            updateNetWorkInfo();
        } else if (v == mStartConnectButton) {
            ApplicationManager.getInstance(this).startConnect();
        } else if (v == mStopConnectButton) {
            ApplicationManager.getInstance(this).stopConnect();
        }
    }
}
