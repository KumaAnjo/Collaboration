
package com.goldsand.collaboration.connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

public class Utils {
    private static final boolean DEBUG = true;
    /**
     * Returns true if a and b are equal, including if they are both null.
     * <p>
     * <i>Note: In platform versions 1.1 and earlier, this method only worked
     * well if both the arguments were instances of String.</i>
     * </p>
     * 
     * @param a first CharSequence to check
     * @param b second CharSequence to check
     * @return true if a and b are equal
     */
    public static boolean equals(CharSequence a, CharSequence b) {
        if (a == b)
            return true;
        int length;
        if (a != null && b != null && (length = a.length()) == b.length()) {
            if (a instanceof String && b instanceof String) {
                return a.equals(b);
            } else {
                for (int i = 0; i < length; i++) {
                    if (a.charAt(i) != b.charAt(i))
                        return false;
                }
                return true;
            }
        }
        return false;
    }

    public static boolean checkPort(int port) {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            return true;
        } catch (IOException exception) {
            return false;
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy:MM:dd:HH:mm:ss");

    public static String getTime() {
        Calendar calendar = Calendar.getInstance();
        return FORMAT.format(calendar.getTime());
    }

    public static void logd(String TAG, String message) {
        if (DEBUG) {
            System.out.println("DEBUG:" + TAG +":" + message);
        }
    }

    public static void loge(String TAG, String message) {
        System.out.println("ERROR:" + TAG +":" + message);
    }

    public static String createRamdomId() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            int result = -1;
            if (random.nextInt(2) == 0) {
                result = random.nextInt('Z' - 'A' + 1) + 'A';
            } else {
                result = random.nextInt('9' - '0' + 1) + '0';
            }
            sb.append((char)result);
        }
        return sb.toString();
    }

    public static boolean isEmpty(String s) {
        return s == null || "".equals(s);
    }

    public static String createDeviceName(String deviceName, String mac) {
        String id = mac.substring(mac.length()-5);
        StringBuilder builder = new StringBuilder();
        builder.append(deviceName.replaceAll(" ", "_"));
        builder.append("_");
        builder.append(id);
        return builder.toString();
    }

    public static String createDeviceInfo(String deviceName, String mac) {
        StringBuilder deviceIdBudiler = new StringBuilder();
        deviceIdBudiler.append(Utils.createDeviceName(deviceName, mac));
        deviceIdBudiler.append(":");
        deviceIdBudiler.append(mac);
        return deviceIdBudiler.toString();
    }

    public static String getDeviceName(String deviceInfo) {
        return deviceInfo.substring(0, deviceInfo.lastIndexOf(":"));
    }

    public static String getDeviceMac(String deviceInfo) {
        return deviceInfo.substring(deviceInfo.lastIndexOf(":") + 1);
    }

    public static String createEncrypyMac(String mac) {
        String reStr = null;
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(mac.getBytes());
            StringBuffer stringBuffer = new StringBuffer();
            for (byte b : bytes){
                int bt = b&0xff;
                if (bt < 16){
                    stringBuffer.append(0);
                }
                stringBuffer.append(Integer.toHexString(bt));
            }
            reStr = stringBuffer.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return reStr;
    }

    static String changeIp(String ip) {
        if (ip.indexOf("/") != -1) {
            return ip.substring(ip.lastIndexOf("/")+1);
        }
        return ip;
    }

    static String createSendThreadName(String name) {
        StringBuilder builder = new StringBuilder();
        builder.append(name);
        builder.append(":sender");
        return builder.toString();
    }

    static String getConnectionThreadName(String sendThreadName) {
        return sendThreadName.substring(0, sendThreadName.lastIndexOf(":sender"));
    }
}
