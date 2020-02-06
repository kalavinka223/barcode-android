package com.powerdata.barcode.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ImportViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ImportViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is import fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}