package com.flw.rxjavademo;


import android.graphics.drawable.Drawable;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.flw.rxjavademo.subject.PublishSubTest;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageView;
    private Button button1, button2, button3, button4;
    private TextView textView;
    private PublishSubTest publishSubTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init ui
        initView();
    }

    /**
     * subscribeOn(): 指定 subscribe() 所发生的线程，即 Observable.OnSubscribe 被激活时所处的线程。或者叫做事件产生的线程。
     * observeOn(): 指定 Subscriber 所运行在的线程。或者叫做事件消费的线程。
     */
    private void schedulerThread() {
        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.mipmap.s13);
                subscriber.onNext(drawable);  //观察者观察事件的变化
                subscriber.onNext(Thread.currentThread().getName());
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())    // 指定 subscribe() 发生在 IO 线程
                .observeOn(AndroidSchedulers.mainThread())  // 指定 Subscriber 的回调发生在主线程
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(Object object) {
                        if (object instanceof Drawable) {
                            imageView.setImageDrawable((Drawable) object);
                        }
                        if (object instanceof String){
                            textView.setText("所在线程名："+object);
                        }
                    }
                });
    }

    private void initView() {
        imageView = (ImageView) findViewById(R.id.imageView);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);
        textView = (TextView) findViewById(R.id.textView);

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);

        publishSubTest = new PublishSubTest(textView);
    }

    private void showImageView() {
        Observable.create(new Observable.OnSubscribe<Drawable>() {
            //创建被观察者，定义出发事件
            @Override
            public void call(Subscriber<? super Drawable> subscriber) {
                Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.mipmap.s4);
                subscriber.onNext(drawable);  //观察者观察事件的变化
                subscriber.onCompleted();
            }
            //被观察者Observable和观察者observer通过subscribe()方法来实现订阅关系
        }).subscribe(new Observer<Drawable>() {
            //创建observer观察者
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(getApplicationContext(), "Error!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(Drawable drawable) {
                imageView.setImageDrawable(drawable);
                textView.setText("所在线程名："+Thread.currentThread().getName());
            }
        });
    }

    private void rxBroadcast() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button1:
                //use RxJava in mainThread
                showImageView();
                break;
            case R.id.button2:
                //use Scheduler to change thread
                schedulerThread();
                break;
            case R.id.button3:
                publishSubTest.sayHello();
                break;
            case R.id.button4:
                rxBroadcast();
                break;
        }
    }
}
