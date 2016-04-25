
package com.goldsand.collaboration.connection.test;

import com.goldsand.collaboration.connection.Authorizer;
import com.goldsand.collaboration.connection.Connection;
import com.goldsand.collaboration.connection.Connection.ConnectionType;
import com.goldsand.collaboration.connection.ConnectionInterface;
import com.goldsand.collaboration.connection.ConnectionListener;
import com.goldsand.collaboration.connection.NetWorkInfo.ConnectStatus;

import java.util.Scanner;

public class TestServer implements Authorizer {
    ConnectionInterface connection = new MyServerConnection(ConnectionType.TYPE_SERVER, this);

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
                System.out.println("DEBUG:onConnectionStatusChanged getNetWorkInfo="
                        + connection.getNetWorkInfos());
            }
        });
        connection.start();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String s = scanner.next();
            if ("status".equals(s)) {
                System.out.println("DEBUG:onConnectionStatusChanged getNetWorkInfo="
                        + connection.getNetWorkInfo());
            } else if ("stop".equals(s)) {
                connection.stop();
            } else {
                connection.sendDataToTarget(s);
            }
        }
    }

    public static void main(String args[]) {
        new TestServer().start();
    }

    class MyServerConnection extends Connection {

        public MyServerConnection(ConnectionType type, Authorizer authorizer) {
            super(type, authorizer);
        }

        @Override
        public String getLocalHostIp() {
            return "127.0.0.1";
        }

        @Override
        public int getServerBroadcastPort() {
            // TODO Auto-generated method stub
            return 10081;
        }

        @Override
        public int getClientBroadcastPort() {
            // TODO Auto-generated method stub
            return 10082;
        }

        @Override
        public int getConnectionPort() {
            // TODO Auto-generated method stub
            return 10083;
        }
    }

    @Override
    public boolean authorize(String remoteIp) {
        System.out.println("remoteIp request to connection:" + remoteIp);
        String input = "yes";
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if ("yes".equals(input)) {
            return true;
        } else {
            return false;
        }
    }
}
