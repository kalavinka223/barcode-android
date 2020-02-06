package com.powerdata.barcode.viewModel;

import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.powerdata.barcode.common.Constant;

public class ImportViewModel extends ViewModel {

    private MutableLiveData<Integer> shipNoItemPosition = new MutableLiveData<>();
    private MutableLiveData<String> barcode = new MutableLiveData<>();

    private LiveData<String> shipNo;

    public ImportViewModel() {
        shipNo = Transformations.map(shipNoItemPosition, new Function<Integer, String>() {
            @Override
            public String apply(Integer input) {
                return Constant.SHIP_NO_ARRAY[input];
            }
        });
    }

    public MutableLiveData<Integer> getShipNoItemPosition() {
        return shipNoItemPosition;
    }

    public MutableLiveData<String> getBarcode() {
        return barcode;
    }
}