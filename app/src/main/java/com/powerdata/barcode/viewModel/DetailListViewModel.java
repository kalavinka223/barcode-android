package com.powerdata.barcode.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.powerdata.barcode.MyApplication;
import com.powerdata.barcode.model.BarcodeDetail;
import com.powerdata.barcode.repository.BarcodeDetailDao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DetailListViewModel extends ViewModel {
    private MutableLiveData<List<BarcodeDetail>> details = new MutableLiveData<>();
    private String shipNo;
    private List<BarcodeDetail> allDetails = Collections.emptyList();
    private BarcodeDetailDao detailDao = MyApplication.db.barcodeDetailDao();

    public DetailListViewModel() {
    }

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

}