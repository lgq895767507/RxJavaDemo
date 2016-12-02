package com.flw.rxjavademo.subject;

import android.widget.TextView;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.PublishSubject;

/**
 * Created by lgq on 2016/12/1.
 */

/**
 * 没有数据要发送，因此我 们的观察者只能等待，没有阻塞线程，也没有消耗资源。就在这随时准备从subject 接收值，
 * 如果subject没有发射值那么我们的观察者就会一直在等待。
 */

public class PublishSubTest {

    private TextView textView;

    public PublishSubTest(TextView textView){
        this.textView = textView;
    }

    public void sayHello() {

        final PublishSubject<Boolean> publishSubject = PublishSubject.create();
        //publish关联观察者
        publishSubject.subscribe(new Observer<Boolean>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Boolean aBoolean) {
                System.out.println("publishSubject");
            }
        });

        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("hello ");
                subscriber.onCompleted();
            }
        }).observeOn(AndroidSchedulers.mainThread())
        .doOnCompleted(new Action0() {        // doOnCompleted()方法指定当Observable结束时要做什么事情
            @Override
            public void call() {
                publishSubject.onNext(true);
            }
        }).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                textView.setText(s);
            }
        });
    }


}
