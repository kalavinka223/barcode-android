package com.powerdata.barcode.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.powerdata.barcode.R;
import com.powerdata.barcode.databinding.FragmentBarcodeListBinding;
import com.powerdata.barcode.viewModel.BarcodeListViewModel;

import java.util.Objects;

/**
 * 直接采集明细页面
 */
public class BarcodeListFragment extends Fragment {

    private BarcodeListViewModel viewModel;
    private String shipNo;

    public BarcodeListFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        assert getArguments() != null;
        shipNo = getArguments().getString(BarcodeCollectionFragment.ARG_SHIP_NO);

        FragmentBarcodeListBinding binding = FragmentBarcodeListBinding.inflate(inflater);
        binding.setLifecycleOwner(this);
        viewModel = ViewModelProviders.of(this).get(BarcodeListViewModel.class);

        binding.setViewModel(viewModel);

        final View root = binding.getRoot();
        setHasOptionsMenu(true);

        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar())
                .setTitle(getString(R.string.title_detail, shipNo));

        return root;
    }

}
