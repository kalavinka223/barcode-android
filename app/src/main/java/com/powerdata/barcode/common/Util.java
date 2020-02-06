package com.powerdata.barcode.common;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class Util {

    public static String formatDate(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date(time));
    }

    public static <T> LiveData<T> transform(Flowable<T> flowable, CompositeDisposable compositeDisposable) {
        final MutableLiveData<T> data = new MutableLiveData<>();
        Disposable disposable = flowable
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<T>() {
                    @Override
                    public void accept(T t) {
                        data.postValue(t);
                    }
                });
        compositeDisposable.add(disposable);
        return data;
    }

}
