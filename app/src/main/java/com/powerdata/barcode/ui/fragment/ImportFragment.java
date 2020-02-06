package com.powerdata.barcode.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.powerdata.barcode.R;
import com.powerdata.barcode.viewModel.ImportViewModel;

public class ImportFragment extends Fragment {

    private ImportViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(ImportViewModel.class);
        View root = inflater.inflate(R.layout.fragment_import, container, false);

        return root;
    }
}