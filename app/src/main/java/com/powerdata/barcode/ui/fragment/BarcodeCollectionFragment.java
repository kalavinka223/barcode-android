package com.powerdata.barcode.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.powerdata.barcode.R;
import com.powerdata.barcode.databinding.FragmentBarcodeCollectionBinding;
import com.powerdata.barcode.model.BarcodeInfo;
import com.powerdata.barcode.ui.adapter.BarcodeAdapter;
import com.powerdata.barcode.viewModel.BarcodeCollectionViewModel;

import java.io.IOException;
import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * 直接采集页面
 */
public class BarcodeCollectionFragment extends Fragment implements BarcodeCollectionViewModel.Listener {

    private static final int CREATE_FILE = 1;

    private BarcodeCollectionViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        final FragmentBarcodeCollectionBinding binding = FragmentBarcodeCollectionBinding.inflate(inflater);
        binding.setLifecycleOwner(this);
        viewModel = ViewModelProviders.of(this).get(BarcodeCollectionViewModel.class);
        viewModel.setListener(this);
        binding.setViewModel(viewModel);
        final View root = binding.getRoot();

        binding.shipNoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                viewModel.setShipNoItemPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.recyclerView.setLayoutManager(layoutManager);

        binding.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteAlertDialog();
            }
        });

        binding.exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("text/csv");
                intent.putExtra(Intent.EXTRA_TITLE, "直接采集.csv");
                startActivityForResult(intent, CREATE_FILE);
            }
        });

        binding.barcodeEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_NULL || actionId == EditorInfo.IME_ACTION_DONE) {
                    viewModel.save();
                    return true;
                }
                return false;
            }
        });
        binding.barcodeEditText.requestFocus();

        viewModel.getBarcodeInfos()
                .observe(this, new Observer<List<BarcodeInfo>>() {
                    @Override
                    public void onChanged(List<BarcodeInfo> list) {
                        binding.recyclerView.setAdapter(new BarcodeAdapter(list));
                        binding.noDataTextView.setVisibility(list.size() > 0 ? View.GONE : View.VISIBLE);
                    }
                });

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CREATE_FILE && resultCode == Activity.RESULT_OK && data != null) {
            try {
                Uri uri = data.getData();
                assert uri != null;
                ParcelFileDescriptor pfd = requireContext().getContentResolver()
                        .openFileDescriptor(uri, "w");
                assert pfd != null;
                viewModel.export(pfd.getFileDescriptor());
                pfd.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showDeleteAlertDialog() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.setTitle(getString(R.string.text_delete));
        dialog.setMessage(getString(R.string.message_delete_alert));
        dialog.show(requireFragmentManager(), "delete_alert_dialog");
        dialog.setListener(new AlertDialogFragment.AlertDialogListener() {
            @Override
            public void onDialogPositiveClick(DialogFragment dialog) {
                viewModel.delete();
            }

            @Override
            public void onDialogNegativeClick(DialogFragment dialog) {

            }
        });
    }

    @Override
    public void onSaveSuccess() {
        MediaPlayer.create(requireContext(), R.raw.success).start();
        Toasty.success(requireContext(), R.string.message_save_success).show();
    }

    @Override
    public void onExportSuccess() {
        Toasty.success(requireContext(), R.string.message_export_success).show();
    }

    @Override
    public void onDeleteSuccess() {
        Toasty.success(requireContext(), R.string.message_delete_success).show();
    }
}