package com.hytera.fcls.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hytera.fcls.service.FireService;
import com.hytera.fcls.IMainAtv;
import com.hytera.fcls.R;
import com.hytera.fcls.mqtt.MQTT;
import com.hytera.fcls.mqtt.event.MessageEvent;
import com.hytera.fcls.presenter.MainAtvPresenter;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends Activity implements IMainAtv {

    public static final String TAG = "y20650" + MainActivity.class.getSimpleName();

    private static final String ServerIP = "192.168.43.22"; // 测试地址
    private static final String PORTID = "1883"; // MQTT 协议的对应的端口
    private static final String ClientID = "y20650";

    @BindView(R.id.textview)
    public TextView textView;

    @BindView(R.id.image_view)
    public ImageView imageView;

    private MainAtvPresenter mainPresenter;

    private static MqttAndroidClient client;
    private MQTT mqtt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        textView.setText("ButterKnife");

        Intent intent1 = new Intent(this, FireService.class);
        startService(intent1);

        mainPresenter = new MainAtvPresenter(this, this);
        //mainPresenter.initMQTT(this);
        EventBus.getDefault().register(this); // 订阅消息总线
    }

    @OnClick(R.id.textview)
    public void onClick(View view){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, LoginAtivity.class);
                startActivity(intent);
            }
        }).start();

//        mainPresenter.startCamera(this);
        //startConnect(ClientID,ServerIP,PORTID);
    }

    private void initMQTT() {
        //mqtt = new MQTT(this, this);
        //mqtt.startConnect(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            String sdStatus = Environment.getExternalStorageState();
            if(!sdStatus.equals(Environment.MEDIA_MOUNTED)){
                Log.e(TAG, "SD card is not available right now.");
            }
//            Bundle bundle = data.getExtras();
//            Bitmap bitmap = (Bitmap)bundle.get("data");
            Bitmap bitmap = mainPresenter.getBitmapFromCamera();


            imageView.setImageBitmap(bitmap);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event){
        Log.i(TAG, "getMessage from MQ : " + event.getString()
                + ", topic is : " + event.getTopic());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(client!=null) {
            try {
                client.disconnect();
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
        EventBus.getDefault().unregister(this);
    }

    /*private void startConnect(String clientID, String serverIP, String port) {
        //服务器地址
        String  uri ="tcp://";
        uri=uri+serverIP+":"+port;
        Log.d(TAG, uri+"  "+clientID);
        *//**
         * 连接的选项
         *//*
        MqttConnectOptions conOpt = new MqttConnectOptions();
        *//**设计连接超时时间*//*
        conOpt.setConnectionTimeout(3000);
        *//**设计心跳间隔时间300秒*//*
        conOpt.setKeepAliveInterval(300);
        *//**
         * 创建连接对象
         *//*
        client = new MqttAndroidClient(this,uri, clientID);
        *//**
         * 连接后设计一个回调
         *//*
        client.setCallback(new MqttCallbackHandler(this, clientID));
        *//**
         * 开始连接服务器，参数：ConnectionOptions,  IMqttActionListener
         *//*
        try {
            client.connect(conOpt, null, new ConnectCallBackHandler(this, imqConn));
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }*/
}
