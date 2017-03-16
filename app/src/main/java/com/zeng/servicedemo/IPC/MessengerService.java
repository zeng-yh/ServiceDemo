package com.zeng.servicedemo.IPC;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import java.util.Stack;

public class MessengerService extends Service {
    public static final int MSG_UPLOAD = 1;
    public static final int MSG_DOWNLOAD = 2;
    public static final int MSG_REPLY = 3;

    public static final String FLAG_DATA = "data";

    private final Stack<String> mStack = new Stack<>();
    private Messenger mMessenger = new Messenger(new ServerMessageHandler());

    private class ServerMessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPLOAD:
                    if (msg.obj != null) {
                        String data = (String) msg.obj;
                        upload(data);
                        Log.d("ServerMessageHandler", "Data upload --> " + data);
                    }
                    break;
                case MSG_DOWNLOAD:
                    if (msg.replyTo != null) {
                        Messenger client = msg.replyTo;
                        Message reply = Message.obtain(null, MSG_REPLY, 0, 0);
                        reply.obj = download();
                        try {
                            client.send(reply);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    public MessengerService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    public void upload(String msg) {
        mStack.push(msg);
    }

    public String download() {
        if (mStack.isEmpty())
            return "Stack is empty.";
        else
            return mStack.pop();
    }
}
