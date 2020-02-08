package com.powerdata.barcode.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;

import com.powerdata.barcode.R;
import com.powerdata.barcode.common.Constant;
import com.powerdata.barcode.databinding.FragmentBarcodeImportBinding;
import com.powerdata.barcode.viewModel.BarcodeImportViewModel;

import java.io.IOException;

import es.dmoral.toasty.Toasty;

public class BarcodeImportFragment extends Fragment implements BarcodeImportViewModel.Listener {

    private static final int PICK_FILE = 1;
    private BarcodeImportViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final FragmentBarcodeImportBinding binding = FragmentBarcodeImportBinding.inflate(inflater);
        binding.setLifecycleOwner(this);
        viewModel = ViewModelProviders.of(this).get(BarcodeImportViewModel.class);
        viewModel.setListener(this);
        binding.setViewModel(viewModel);
        final View root = binding.getRoot();
        setHasOptionsMenu(true);

        binding.shipNoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                viewModel.setShipNoItemPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.importCsvButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(intent, PICK_FILE);
            }
        });

        binding.viewDetailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(Constant.ARG_SHIP_NO, viewModel.getShipNo().getValue());
                NavHostFragment.findNavController(BarcodeImportFragment.this)
                        .navigate(R.id.navigation_barcode_detail_list, bundle);
            }
        });

        binding.viewErrorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(Constant.ARG_SHIP_NO, viewModel.getShipNo().getValue());
                NavHostFragment.findNavController(BarcodeImportFragment.this)
                        .navigate(R.id.navigation_barcode_error_list, bundle);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_FILE && resultCode == Activity.RESULT_OK && data != null) {
            try {
                Uri uri = data.getData();
                assert uri != null;
                ParcelFileDescriptor pfd = requireContext().getContentResolver()
                        .openFileDescriptor(uri, "r");
                assert pfd != null;
                viewModel.importCSV(pfd.getFileDescriptor());
                pfd.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onSaveSuccess() {
        MediaPlayer.create(requireContext(), R.raw.success).start();
        Toasty.success(requireContext(), R.string.message_save_success).show();
    }

    @Override
    public void onSaveError() {
        MediaPlayer.create(requireContext(), R.raw.error).start();
        Toasty.warning(requireContext(), R.string.message_save_error).show();
    }

    @Override
    public void onImportSuccess() {
        Toasty.success(requireContext(), R.string.message_import_success).show();
    }
}