package com.powerdata.barcode.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.powerdata.barcode.MyApplication;
import com.powerdata.barcode.model.BarcodeDetail;
import com.powerdata.barcode.repository.BarcodeDetailDao;

import io.reactivex.MaybeObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class BarcodeSearchViewModel extends ViewModel {

    private MutableLiveData<BarcodeDetail> detail = new MutableLiveData<>();
    private BarcodeDetailDao detailDao = MyApplication.db.barcodeDetailDao();

    public LiveData<BarcodeDetail> getDetail() {
        return detail;
    }

    public void search(final String barcode, String shipNo) {
        detailDao.findById(barcode, shipNo)
                .subscribeOn(Schedulers.io())
                .subscribe(new MaybeObserver<BarcodeDetail>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(BarcodeDetail barcodeDetail) {
                        detail.postValue(barcodeDetail);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        detail.postValue(null);
                    }
                });
    }

}