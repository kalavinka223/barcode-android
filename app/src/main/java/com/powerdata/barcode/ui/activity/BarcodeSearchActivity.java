package com.powerdata.barcode.ui.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.powerdata.barcode.R;
import com.powerdata.barcode.common.Constant;
import com.powerdata.barcode.model.BarcodeDetail;
import com.powerdata.barcode.viewModel.BarcodeSearchViewModel;

public class BarcodeSearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_search);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BarcodeSearchViewModel viewModel = ViewModelProviders.of(this)
                .get(BarcodeSearchViewModel.class);

        Intent intent = getIntent();

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Bundle bundle = intent.getBundleExtra(SearchManager.APP_DATA);
            assert bundle != null;
            String shipNo = bundle.getString(Constant.ARG_SHIP_NO);
            viewModel.search(query, shipNo);
        }

        viewModel.getDetail()
                .observe(this, new Observer<BarcodeDetail>() {
                    @Override
                    public void onChanged(BarcodeDetail barcodeDetail) {
                        System.out.println(barcodeDetail);
                    }
                });
    }
}
