
package com.goldsand.collaboration.connection.test;

import com.goldsand.collaboration.connection.Connection;
import com.goldsand.collaboration.connection.Connection.ConnectionType;
import com.goldsand.collaboration.connection.ConnectionInterface;
import com.goldsand.collaboration.connection.ConnectionListener;
import com.goldsand.collaboration.connection.NetWorkInfo.ConnectStatus;

import java.util.Scanner;

public class TestClient {
    private ConnectionInterface connection = new MyClientConnection(ConnectionType.TYPE_CLIENT);

    public void start() {
        connection.registConnectionListener(new ConnectionListener() {

            @Override
            public void onNewDataReceive(String data) {
                System.out.println("DEBUG:onNewDataReceive message=" + data);
            }

            @Override
            public void onNewDataReceive(Object data) {
                System.out.println("DEBUG:onNewDataReceive object=" + data);
            }

            @Override
            public void onConnectionStatusChanged(ConnectStatus newStatus) {
                System.out.println("DEBUG:onConnectionStatusChanged newStatus=" + newStatus);
                System.out.println("DEBUG:onConnectionStatusChanged getNetWorkInfo=" + connection.getNetWorkInfo());
            }
        });
        connection.start();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String s = scanner.next();
            if ("status".equals(s)) {
                System.out.println("DEBUG:onConnectionStatusChanged getNetWorkInfo=" + connection.getNetWorkInfo());
            } else if ("stop".equals(s)) {
                connection.stop();
            } else {
                connection.sendDataToTarget(s);
            }
        }
    }

    public static void main(String args[]) {
        new TestClient().start();
    }

    class MyClientConnection extends Connection {

        public MyClientConnection(ConnectionType type) {
            super(type);
        }

        @Override
        public int getServerBroadcastPort() {
            return 10081;
        }

        @Override
        public int getClientBroadcastPort() {
            return 10082;
        }

        @Override
        public int getConnectionPort() {
            return 10083;
        }

        @Override
        public String getLocalHostIp() {
            return "127.0.0.1";
        }

        @Override
        protected String getDeviceMac() {
            return "1234567890";
        }

        @Override
        protected String getDeviceName() {
            // TODO Auto-generated method stub
            return "htc one2";
        }
    }
}
