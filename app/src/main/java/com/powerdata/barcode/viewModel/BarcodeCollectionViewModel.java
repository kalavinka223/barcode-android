package com.powerdata.barcode.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.powerdata.barcode.MyApplication;
import com.powerdata.barcode.common.Constant;
import com.powerdata.barcode.common.Util;
import com.powerdata.barcode.model.BarcodeInfo;
import com.powerdata.barcode.repository.BarcodeInfoDao;

import org.apache.commons.lang3.StringUtils;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class BarcodeCollectionViewModel extends ViewModel {

    public MutableLiveData<String> barcode = new MutableLiveData<>();
    private String shipNo = Constant.SHIP_NO_ARRAY[0];

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private BarcodeInfoDao infoDao = MyApplication.db.barcodeInfoDao();

    public void setShipNoItemPosition(int position) {
        shipNo = Constant.SHIP_NO_ARRAY[position];
    }

    public String getShipNo() {
        return shipNo;
    }

    public void save() {
        String code = barcode.getValue();
        barcode.setValue("");
        if (StringUtils.isNotEmpty(code)) {
            BarcodeInfo info = new BarcodeInfo();
            info.shipNo = shipNo;
            info.barcode = code;
            info.createdAt = Util.formatDate(System.currentTimeMillis());
            Disposable disposable = infoDao.insert(info)
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Action() {
                        @Override
                        public void run() {
                            System.out.println("insert ok");
                        }
                    });
            compositeDisposable.add(disposable);
        }
    }

}