package com.goldsand.collaboration.server.connect;

import android.util.Log;

import com.goldsand.collaboration.connection.Authorizer;
import com.goldsand.collaboration.connection.Connection;

import org.apache.http.conn.util.InetAddressUtils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class ConnectionImpl extends Connection {
    public ConnectionImpl(ConnectionType type) {
        super(type);
    }

    public ConnectionImpl(ConnectionType type, Authorizer authorizer) {
        super(type, authorizer);
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
        Log.i("ApplicationManager", "getLocalHostIp()");
        String ipaddress = "";
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface nif = en.nextElement();
                Enumeration<InetAddress> inet = nif.getInetAddresses();
                while (inet.hasMoreElements()) {
                    InetAddress ip = inet.nextElement();
                    if (!ip.isLoopbackAddress()
                            && InetAddressUtils.isIPv4Address(ip.getHostAddress())) {
                        return ip.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }

        return ipaddress;
    }
}
