package com.powerdata.barcode.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.powerdata.barcode.MyApplication;
import com.powerdata.barcode.model.BarcodeInfo;
import com.powerdata.barcode.repository.BarcodeInfoDao;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

public class BarcodeListViewModel extends ViewModel {

    private MutableLiveData<List<BarcodeInfo>> barcodeInfos = new MutableLiveData<>();

    private String shipNo;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private BarcodeInfoDao infoDao = MyApplication.db.barcodeInfoDao();

    public LiveData<List<BarcodeInfo>> getBarcodeInfos() {
        return barcodeInfos;
    }

    public void load(String shipNo) {
        this.shipNo = shipNo;
        infoDao.listByShipNo(shipNo)
                .observeForever(new Observer<List<BarcodeInfo>>() {
                    @Override
                    public void onChanged(List<BarcodeInfo> list) {
                        barcodeInfos.setValue(list);
                    }
                });
    }

}