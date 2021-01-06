package com.example.runshell;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MyActivity";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d(TAG, "Started!!!!!!!!!!\n");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView tv = (TextView) findViewById(R.id.textView);
        tv.setMovementMethod(new ScrollingMovementMethod());


        // TESTE PINGAR IP
//        String ip = "";
//        Enumeration<NetworkInterface> enumNetworkInterfaces = null;
//        try {
//            enumNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
//        } catch (SocketException e) {
//            e.printStackTrace();
//        }
//        Log.d(TAG, "chegou " + enumNetworkInterfaces.hasMoreElements());
//
//        while (enumNetworkInterfaces.hasMoreElements()) {
//            NetworkInterface networkInterface = enumNetworkInterfaces.nextElement();
//            Enumeration<InetAddress> enumInetAddress = networkInterface.getInetAddresses();
//            Log.d(TAG, "chegouu " + enumInetAddress.hasMoreElements());
//
//            while (enumInetAddress.hasMoreElements()) {
//                InetAddress inetAddress = enumInetAddress.nextElement();
//                String ipAddress = "";
//                Log.d(TAG, "chegouuu " + enumInetAddress);
//                if (inetAddress.isSiteLocalAddress()) {
//                    ipAddress = "SiteLocalAddress: ";
//                }
//                Log.d(TAG, "ta aqqqq " + ip);
//                ip += ipAddress + inetAddress.getHostAddress() + "\n";
//                Log.d(TAG, "ip " + ip);
//                Log.d(TAG, "ip2 " + Integer.parseInt(ip));
//                String subnet = getSubnetAddress(ip);
//            }
//        }

        WifiManager mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
        String subnet = getSubnetAddress(mWifiManager.getDhcpInfo().gateway);
        Log.d(TAG, "info1: " + mWifiManager.getDhcpInfo().gateway);
        Log.d(TAG, "subnet: " + subnet);
        Log.d(TAG, "extraaa: " + mWifiInfo.getMacAddress());

        //checkHosts(subnet);
        tv.setText(subnet);

        String host=subnet + "." + 1;
        try {
            tv.setText("alo " + InetAddress.getByName(host).isReachable(5));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void decompress(String in, File out) throws IOException {
        try (TarArchiveInputStream fin = new TarArchiveInputStream(new GzipCompressorInputStream(new FileInputStream(in)))) {
            TarArchiveEntry entry;
            while ((entry = fin.getNextTarEntry()) != null) {
                if (entry.isDirectory()) {
                    continue;
                }
                File curfile = new File(out, entry.getName());
                File parent = curfile.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }
                IOUtils.copy(fin, new FileOutputStream(curfile));
            }
        }
    }

    private String getSubnetAddress(int address) {
        String ipString = String.format(
                "%d.%d.%d",
                (address & 0xff),
                (address >> 8 & 0xff),
                (address >> 16 & 0xff));

        Log.d(TAG, "subnet address: " + ipString);
        return ipString;
    }

    private void checkHosts(String subnet)
    {
        try
        {
            Log.d(TAG, "entrou aqui!");
            int timeout=5;
            for (int i=1;i<255;i++)
            {
                String host=subnet + "." + i;
                Log.d(TAG, "entrou aqui! host: " + host);
                if (InetAddress.getByName(host).isReachable(timeout))
                {
                    Log.d(TAG, "checkHosts() :: "+host + " is reachable");
                }
            }
        }
        catch (UnknownHostException e)
        {
            Log.d(TAG, "checkHosts() :: UnknownHostException e : "+e);
            e.printStackTrace();
        }
        catch (IOException e)
        {
            Log.d(TAG, "checkHosts() :: IOException e : "+e);
            e.printStackTrace();
        }
    }
}