package com.powerdata.barcode.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.powerdata.barcode.R;
import com.powerdata.barcode.databinding.FragmentBarcodeCollectionBinding;
import com.powerdata.barcode.model.BarcodeInfo;
import com.powerdata.barcode.ui.adapter.BarcodeAdapter;
import com.powerdata.barcode.viewModel.BarcodeCollectionViewModel;

import java.util.List;

public class BarcodeCollectionFragment extends Fragment {

    static final String ARG_SHIP_NO = "arg_ship_no";

    private BarcodeCollectionViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FragmentBarcodeCollectionBinding binding = FragmentBarcodeCollectionBinding.inflate(inflater);
        binding.setLifecycleOwner(this);
        viewModel = ViewModelProviders.of(this).get(BarcodeCollectionViewModel.class);
        binding.setViewModel(viewModel);
        final View root = binding.getRoot();
        setHasOptionsMenu(true);

        Spinner spinner = root.findViewById(R.id.ship_no_spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                viewModel.setShipNoItemPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        viewModel.getBarcodeInfos()
                .observe(this, new Observer<List<BarcodeInfo>>() {
                    @Override
                    public void onChanged(List<BarcodeInfo> list) {
                        recyclerView.setAdapter(new BarcodeAdapter(list));
                    }
                });

        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.save_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.save_item) {
            viewModel.save();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}