package com.powerdata.barcode.viewModel;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.powerdata.barcode.MyApplication;
import com.powerdata.barcode.common.Constant;
import com.powerdata.barcode.common.Util;
import com.powerdata.barcode.model.BarcodeInfo;
import com.powerdata.barcode.repository.BarcodeInfoDao;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.StringUtils;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class BarcodeCollectionViewModel extends ViewModel {

    public MutableLiveData<String> barcode = new MutableLiveData<>();
    private LiveData<List<BarcodeInfo>> barcodeInfos;
    private MutableLiveData<String> shipNo = new MutableLiveData<>();

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private BarcodeInfoDao infoDao = MyApplication.db.barcodeInfoDao();

    public BarcodeCollectionViewModel() {
        barcodeInfos = Transformations.switchMap(shipNo, new Function<String, LiveData<List<BarcodeInfo>>>() {
            @Override
            public LiveData<List<BarcodeInfo>> apply(String s) {
                return infoDao.listByShipNo(s);
            }
        });
    }

    public void setShipNoItemPosition(int position) {
        shipNo.setValue(Constant.SHIP_NO_ARRAY[position]);
    }

    public String getShipNo() {
        return shipNo.getValue();
    }

    public LiveData<List<BarcodeInfo>> getBarcodeInfos() {
        return barcodeInfos;
    }

    public void save() {
        String code = barcode.getValue();
        barcode.setValue("");
        if (StringUtils.isNotEmpty(code)) {
            BarcodeInfo info = new BarcodeInfo();
            info.shipNo = shipNo.getValue();
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

    public void delete() {
        Disposable disposable = infoDao.deleteByShipNo(shipNo.getValue())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() {

                    }
                });
        compositeDisposable.add(disposable);
    }

    public void export(FileDescriptor file) {
        try {
            List<BarcodeInfo> list = barcodeInfos.getValue();
            if (list != null) {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream, "GBK");
                Appendable printWriter = new PrintWriter(writer);
                CSVPrinter csvPrinter = CSVFormat.EXCEL.withHeader("钢卷号", "扫描时间", "船号").print(printWriter);
                for (int i = 0; i < list.size(); i++) {
                    BarcodeInfo barcodeInfo = list.get(i);
                    csvPrinter.printRecord(barcodeInfo.barcode, barcodeInfo.createdAt, barcodeInfo.shipNo);
                }
                csvPrinter.flush();
                csvPrinter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}