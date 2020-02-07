package com.powerdata.barcode.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.powerdata.barcode.MyApplication;
import com.powerdata.barcode.model.BarcodeError;
import com.powerdata.barcode.repository.BarcodeErrorDao;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class BarcodeErrorListViewModel extends ViewModel {

    private MutableLiveData<List<BarcodeError>> errors = new MutableLiveData<>();
    private String shipNo;
    private Listener listener;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private BarcodeErrorDao errorDao = MyApplication.db.barcodeErrorDao();

    public LiveData<List<BarcodeError>> getErrors() {
        return errors;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
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

    public void delete() {
        Disposable disposable = errorDao.deleteByShipNo(shipNo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() {
                        if (listener != null)
                            listener.onDeleteSuccess();
                    }
                });
        compositeDisposable.add(disposable);
    }

    public void export(final FileDescriptor file) {
        if (errors.getValue() == null)
            return;

        FutureTask<Boolean> task = new FutureTask<>(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                try {
                    List<BarcodeError> list = errors.getValue();
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream, "GBK");
                    Appendable printWriter = new PrintWriter(writer);
                    CSVPrinter csvPrinter = CSVFormat.EXCEL.withHeader("钢卷号", "扫描时间", "船号").print(printWriter);
                    for (int i = 0; i < list.size(); i++) {
                        BarcodeError error = list.get(i);
                        csvPrinter.printRecord(error.barcode, error.createdAt, error.shipNo);
                    }
                    csvPrinter.flush();
                    csvPrinter.close();
                    return true;
                } catch (IOException e) {
                    return false;
                }
            }
        });

        Thread thread = new Thread(task);
        thread.start();

        try {
            if (task.get() && listener != null) {
                listener.onExportSuccess();
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public interface Listener {
        void onDeleteSuccess();

        void onExportSuccess();
    }

}