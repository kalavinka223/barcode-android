package com.powerdata.barcode.viewModel;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.powerdata.barcode.MyApplication;
import com.powerdata.barcode.common.Constant;
import com.powerdata.barcode.common.SingleLiveEvent;
import com.powerdata.barcode.common.Util;
import com.powerdata.barcode.model.BarcodeDetail;
import com.powerdata.barcode.repository.BarcodeDetailDao;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileDescriptor;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class ImportViewModel extends ViewModel {

    public MutableLiveData<Integer> shipNoItemPosition = new MutableLiveData<>();
    public MutableLiveData<String> barcode = new MutableLiveData<>();
    public SingleLiveEvent<Void> openDocumentAction = new SingleLiveEvent<>();
    public MutableLiveData<FileDescriptor> file = new MutableLiveData<>();
    public LiveData<String> totalCount;
    public LiveData<String> scannedCount;
    public LiveData<String> notScannedCount;

    private LiveData<String> shipNo;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private BarcodeDetailDao detailDao = MyApplication.db.barcodeDetailDao();

    public ImportViewModel() {
        shipNo = Transformations.map(shipNoItemPosition, new Function<Integer, String>() {
            @Override
            public String apply(Integer input) {
                return Constant.SHIP_NO_ARRAY[input];
            }
        });

        file.observeForever(new Observer<FileDescriptor>() {
            @Override
            public void onChanged(FileDescriptor file) {
                importCSV(file);
            }
        });

        Function<Integer, String> int2StrFunc = new Function<Integer, String>() {
            @Override
            public String apply(Integer input) {
                return String.valueOf(input);
            }
        };

        totalCount = Transformations.map(Transformations.switchMap(shipNo, new Function<String, LiveData<Integer>>() {
            @Override
            public LiveData<Integer> apply(String s) {
                return Util.transform(detailDao.countByShiNo(s), compositeDisposable);
            }
        }), int2StrFunc);

        scannedCount = Transformations.map(Transformations.switchMap(shipNo, new Function<String, LiveData<Integer>>() {
            @Override
            public LiveData<Integer> apply(String s) {
                return Util.transform(detailDao.countByShiNoAndStatus(s, 1), compositeDisposable);
            }
        }), int2StrFunc);

        notScannedCount = Transformations.map(Transformations.switchMap(shipNo, new Function<String, LiveData<Integer>>() {
            @Override
            public LiveData<Integer> apply(String s) {
                return Util.transform(detailDao.countByShiNoAndStatus(s, 0), compositeDisposable);
            }
        }), int2StrFunc);
    }

    public void onSaveButtonClick() {

    }

    public void onImportButtonClick() {
        openDocumentAction.call();
    }

    private void importCSV(FileDescriptor file) {
        try {
            Reader in = new FileReader(file);
            Iterable<CSVRecord> records = CSVFormat.RFC4180.withHeader().parse(in);
            List<BarcodeDetail> list = new ArrayList<>();

            for (CSVRecord record : records) {
                BarcodeDetail detail = new BarcodeDetail();
                detail.barcode = record.get(1);
                detail.thickness = Float.valueOf(record.get(2));
                detail.weight = Float.valueOf(record.get(3));
                detail.pileNo = record.get(4);
                detail.mark = record.get(5);
                detail.createdAt = Util.formatDate(System.currentTimeMillis());
                detail.shipNo = shipNo.getValue();
                detail.status = 0;
                list.add(detail);
            }

            Disposable disposable = detailDao.inserts(list)
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Action() {
                        @Override
                        public void run() {
                        }
                    });

            compositeDisposable.add(disposable);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}