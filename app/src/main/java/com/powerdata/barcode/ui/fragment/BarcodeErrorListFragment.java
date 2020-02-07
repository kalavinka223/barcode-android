package com.powerdata.barcode.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.powerdata.barcode.R;
import com.powerdata.barcode.databinding.FragmentBarcodeErrorListBinding;
import com.powerdata.barcode.model.BarcodeError;
import com.powerdata.barcode.ui.adapter.BarcodeErrorAdapter;
import com.powerdata.barcode.viewModel.BarcodeErrorListViewModel;

import java.util.List;
import java.util.Objects;

/**
 * 失败明细页面
 */
public class BarcodeErrorListFragment extends Fragment {

    private static final int CREATE_FILE = 1;

    private BarcodeErrorListViewModel viewModel;
    private String shipNo;

    public BarcodeErrorListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        assert getArguments() != null;
        shipNo = getArguments().getString(BarcodeImportFragment.ARG_SHIP_NO);

        FragmentBarcodeErrorListBinding binding = FragmentBarcodeErrorListBinding.inflate(inflater);
        binding.setLifecycleOwner(this);
        viewModel = ViewModelProviders.of(this).get(BarcodeErrorListViewModel.class);
        //viewModel.setListener(this);
        binding.setViewModel(viewModel);

        final View root = binding.getRoot();
        setHasOptionsMenu(true);

        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar())
                .setTitle(getString(R.string.title_error_detail, shipNo));

        final RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        viewModel.getErrors()
                .observe(this, new Observer<List<BarcodeError>>() {
                    @Override
                    public void onChanged(List<BarcodeError> list) {
                        recyclerView.setAdapter(new BarcodeErrorAdapter(list));
                    }
                });

        viewModel.load(shipNo);

        return root;
    }

}
