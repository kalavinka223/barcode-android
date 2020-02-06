package com.powerdata.barcode.viewModel;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.powerdata.barcode.MyApplication;
import com.powerdata.barcode.common.Constant;
import com.powerdata.barcode.common.Util;
import com.powerdata.barcode.model.BarcodeDetail;
import com.powerdata.barcode.model.BarcodeError;
import com.powerdata.barcode.repository.BarcodeDetailDao;
import com.powerdata.barcode.repository.BarcodeErrorDao;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;

import java.io.FileDescriptor;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ImportViewModel extends ViewModel {

    private final Function<Integer, String> int2Str = new Function<Integer, String>() {
        @Override
        public String apply(Integer input) {
            return String.valueOf(input);
        }
    };

    public MutableLiveData<String> barcode = new MutableLiveData<>();

    private ImportViewModelListener listener;
    private LiveData<Integer> totalCount;
    private LiveData<Integer> scannedCount;
    private LiveData<Integer> notScannedCount;
    private LiveData<Integer> errorCount;
    private MutableLiveData<String> shipNo = new MutableLiveData<>();

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private BarcodeDetailDao detailDao = MyApplication.db.barcodeDetailDao();
    private BarcodeErrorDao errorDao = MyApplication.db.barcodeErrorDao();

    public ImportViewModel() {
        totalCount = Transformations.switchMap(shipNo, new Function<String, LiveData<Integer>>() {
            @Override
            public LiveData<Integer> apply(String s) {
                return detailDao.countByShiNo(s);
            }
        });

        scannedCount = Transformations.switchMap(shipNo, new Function<String, LiveData<Integer>>() {
            @Override
            public LiveData<Integer> apply(String s) {
                return detailDao.countByShiNoAndStatus(s, 1);
            }
        });

        notScannedCount = Transformations.switchMap(shipNo, new Function<String, LiveData<Integer>>() {
            @Override
            public LiveData<Integer> apply(String s) {
                return detailDao.countByShiNoAndStatus(s, 0);
            }
        });

        errorCount = Transformations.switchMap(shipNo, new Function<String, LiveData<Integer>>() {
            @Override
            public LiveData<Integer> apply(String s) {
                return errorDao.countByShiNo(s);
            }
        });
    }

    public void setListener(ImportViewModelListener listener) {
        this.listener = listener;
    }

    public void setShipNoItemPosition(int position) {
        shipNo.setValue(Constant.SHIP_NO_ARRAY[position]);
    }

    public LiveData<String> getTotalCount() {
        return Transformations.map(totalCount, int2Str);
    }

    public LiveData<String> getScannedCount() {
        return Transformations.map(scannedCount, int2Str);
    }

    public LiveData<String> getNotScannedCount() {
        return Transformations.map(notScannedCount, int2Str);
    }

    public LiveData<String> getErrorCount() {
        return Transformations.map(errorCount, int2Str);
    }

    private List<BarcodeDetail> build(Iterable<CSVRecord> records) {
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
        return list;
    }

    public void importCSV(FileDescriptor file) {
        try {
            Reader in = new FileReader(file);
            Iterable<CSVRecord> records = CSVFormat.RFC4180.withHeader().parse(in);
            Disposable disposable = detailDao.inserts(build(records))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action() {
                        @Override
                        public void run() {
                            if (listener != null)
                                listener.onImportSuccess();
                        }
                    });
            compositeDisposable.add(disposable);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        final String s = barcode.getValue();
        barcode.setValue("");
        if (StringUtils.isNotEmpty(s)) {
            Disposable disposable = detailDao.updateStatusByBarcode(s)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Integer>() {
                        @Override
                        public void accept(Integer integer) {
                            if (integer > 0 && listener != null)
                                listener.onSaveSuccess();
                            else if (listener != null) {
                                listener.onSaveError();
                                saveError(s);
                            }
                        }
                    });
            compositeDisposable.add(disposable);
        }
    }

    private void saveError(String barcode) {
        BarcodeError error = new BarcodeError();
        error.createdAt = Util.formatDate(System.currentTimeMillis());
        error.shipNo = shipNo.getValue();
        error.barcode = barcode;
        Disposable disposable = errorDao.insert(error)
                .subscribeOn(Schedulers.io())
                .subscribe();
        compositeDisposable.add(disposable);
    }

    public interface ImportViewModelListener {
        void onSaveSuccess();

        void onSaveError();

        void onImportSuccess();
    }

}