package com.powerdata.barcode.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.powerdata.barcode.MyApplication;
import com.powerdata.barcode.model.BarcodeDetail;
import com.powerdata.barcode.repository.BarcodeDetailDao;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class BarcodeDetailListViewModel extends ViewModel {

    private DetailListViewModelListener listener;
    private MutableLiveData<List<BarcodeDetail>> details = new MutableLiveData<>();
    private String shipNo;
    private List<BarcodeDetail> allDetails = Collections.emptyList();
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private BarcodeDetailDao detailDao = MyApplication.db.barcodeDetailDao();

    public void setStatus(Status status) {
        switch (status) {
            case ALL:
                details.setValue(allDetails);
                break;
            case SCANNED:
                details.setValue(filter(1));
                break;
            case NOT_SCANNED:
                details.setValue(filter(0));
                break;
        }
    }

    public void setListener(DetailListViewModelListener listener) {
        this.listener = listener;
    }

    public LiveData<List<BarcodeDetail>> getDetails() {
        return details;
    }

    public void loadDetails(String shipNo) {
        this.shipNo = shipNo;
        detailDao.listByShipNo(shipNo)
                .observeForever(new Observer<List<BarcodeDetail>>() {
                    @Override
                    public void onChanged(List<BarcodeDetail> list) {
                        allDetails = list;
                        details.setValue(allDetails);
                    }
                });
    }

    public void delete() {
        Disposable disposable = detailDao.deleteByShipNo(shipNo)
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
        FutureTask<Boolean> task = new FutureTask<>(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream, "GBK");
                    Appendable printWriter = new PrintWriter(writer);
                    CSVPrinter csvPrinter = CSVFormat.EXCEL
                            .withHeader("序号", "钢卷号", "扫描状态", "货垛号", "创建时间", "扫描时间", "船号", "厚度", "重量", "标记")
                            .print(printWriter);
                    for (int i = 0; i < allDetails.size(); i++) {
                        BarcodeDetail detail = allDetails.get(i);
                        String status = detail.status == 1 ? "已扫描" : "未扫描";
                        csvPrinter.printRecord(i + 1, detail.barcode, status, detail.pileNo, detail.createdAt, detail.updatedAt,
                                shipNo, detail.thickness, detail.weight, detail.mark);
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

    private List<BarcodeDetail> filter(int status) {
        List<BarcodeDetail> results = new ArrayList<>();
        for (BarcodeDetail detail : allDetails) {
            if (status == detail.status) {
                results.add(detail);
            }
        }
        return results;
    }

    public enum Status {
        ALL, SCANNED, NOT_SCANNED
    }

    public interface DetailListViewModelListener {
        void onDeleteSuccess();

        void onExportSuccess();
    }

}