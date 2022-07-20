package com.opencode.webboxdespacho.config;

import android.app.Activity;
import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Tools {

    private Context context;

    public Tools(Context context) {
        this.context = context;
    }

    public byte[] decodeByte(String filePath)  {
        byte[] fileByteArray;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer =new byte[1024];
        int read;
        try {
            while ((read = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, read);
            }
            baos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileByteArray = baos.toByteArray();
        return fileByteArray;
    }



    public static class InternetCheck {
        private Consumer mConsumer;
        private boolean status;
        private Activity mActivity;

        public interface Consumer {
            void accept(Boolean internet);
        }
        public InternetCheck(Activity activity, Consumer consumer) {
            mConsumer = consumer;
            mActivity = activity;
            execute();
        }
        void execute(){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //do in background
                    try {
                        Socket sock = new Socket();
                        sock.connect(new InetSocketAddress("8.8.8.8", 53), 1500);
                        sock.close();
                        status = true;
                    } catch (IOException e) {
                        status = false;
                    }
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mConsumer.accept(status);
                        }
                    });
                }
            }).start();
        }
    }
}
