package com.powerdata.barcode.ui.fragment;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;

import com.powerdata.barcode.R;
import com.powerdata.barcode.databinding.FragmentImportBinding;
import com.powerdata.barcode.viewModel.ImportViewModel;

import java.io.IOException;

import es.dmoral.toasty.Toasty;

public class ImportFragment extends Fragment implements ImportViewModel.ImportViewModelListener {

    public static final String ARG_SHIP_NO = "arg_ship_no";
    private static final int PICK_FILE = 1;
    private ImportViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FragmentImportBinding binding = FragmentImportBinding.inflate(inflater);
        binding.setLifecycleOwner(this);
        viewModel = ViewModelProviders.of(this).get(ImportViewModel.class);
        viewModel.setListener(this);
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

        Button importButton = root.findViewById(R.id.import_csv_button);
        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(intent, PICK_FILE);
            }
        });

        Button detailButton = root.findViewById(R.id.view_detail_button);
        detailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString(ARG_SHIP_NO, viewModel.getShipNo().getValue());
                NavHostFragment.findNavController(ImportFragment.this)
                        .navigate(R.id.navigation_detail_list, bundle);
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
        String message = getString(R.string.message_save_success);
        Toasty.success(requireContext(), message, Toast.LENGTH_SHORT, true)
                .show();
    }

    @Override
    public void onSaveError() {
        String message = getString(R.string.message_save_error);
        Toasty.warning(requireContext(), message, Toast.LENGTH_SHORT, true)
                .show();
    }

    @Override
    public void onImportSuccess() {
        String message = getString(R.string.message_import_successfully);
        Toasty.success(requireContext(), message, Toast.LENGTH_SHORT, true)
                .show();
    }
}