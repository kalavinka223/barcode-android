package com.powerdata.barcode.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.powerdata.barcode.MyApplication;
import com.powerdata.barcode.model.BarcodeError;
import com.powerdata.barcode.repository.BarcodeErrorDao;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

public class BarcodeErrorListViewModel extends ViewModel {

    private MutableLiveData<List<BarcodeError>> errors = new MutableLiveData<>();
    private String shipNo;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private BarcodeErrorDao errorDao = MyApplication.db.barcodeErrorDao();

    public LiveData<List<BarcodeError>> getErrors() {
        return errors;
    }

    public void load(String shipNo) {
        this.shipNo = shipNo;
        errorDao.listByShipNo(shipNo)
                .observeForever(new Observer<List<BarcodeError>>() {
                    @Override
                    public void onChanged(List<BarcodeError> list) {
                        errors.setValue(list);
                    }
                });
    }

}