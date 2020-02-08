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
import com.powerdata.barcode.databinding.ActivityBarcodeSearchBinding;
import com.powerdata.barcode.model.BarcodeDetail;
import com.powerdata.barcode.viewModel.BarcodeSearchViewModel;

public class BarcodeSearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityBarcodeSearchBinding binding = ActivityBarcodeSearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.setLifecycleOwner(this);
        BarcodeSearchViewModel viewModel = ViewModelProviders.of(this).get(BarcodeSearchViewModel.class);
        binding.setViewModel(viewModel);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
                    public void onChanged(BarcodeDetail detail) {
                        if (detail != null) {
                            binding.barcodeTextView.setText(detail.barcode);
                            binding.statusTextView.setText(getString(detail.status == 1 ? R.string.text_scanned : R.string.text_not_scanned));
                            binding.createdAtTextView.setText(detail.createdAt);
                            binding.updatedAtTextView.setText(detail.updatedAt);
                            binding.pileNoTextView.setText(detail.pileNo);
                            binding.thicknessTextView.setText(String.valueOf(detail.thickness));
                            binding.weightTextView.setText(String.valueOf(detail.weight));
                            binding.markTextView.setText(detail.mark);
                        }
                    }
                });
    }
}
