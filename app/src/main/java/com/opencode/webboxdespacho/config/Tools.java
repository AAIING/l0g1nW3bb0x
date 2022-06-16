package com.opencode.webboxdespacho.config;

import android.app.Activity;
import android.content.Context;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Tools {

    private Context context;

    public Tools(Context context) {
        this.context = context;
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
