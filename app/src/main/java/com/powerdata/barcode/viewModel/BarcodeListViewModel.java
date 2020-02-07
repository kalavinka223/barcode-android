package com.powerdata.barcode.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.powerdata.barcode.MyApplication;
import com.powerdata.barcode.model.BarcodeInfo;
import com.powerdata.barcode.repository.BarcodeInfoDao;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

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

    public void delete() {
        Disposable disposable = infoDao.deleteByShipNo(shipNo)
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