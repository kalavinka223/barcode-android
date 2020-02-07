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
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;

import com.powerdata.barcode.R;
import com.powerdata.barcode.databinding.FragmentBarcodeCollectionBinding;
import com.powerdata.barcode.viewModel.BarcodeCollectionViewModel;

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

        Button detailButton = root.findViewById(R.id.view_detail_button);
        detailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(ARG_SHIP_NO, viewModel.getShipNo());
                NavHostFragment.findNavController(BarcodeCollectionFragment.this)
                        .navigate(R.id.navigation_barcode_list, bundle);
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