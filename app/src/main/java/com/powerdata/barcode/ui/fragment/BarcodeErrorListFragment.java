package com.powerdata.barcode.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.powerdata.barcode.R;
import com.powerdata.barcode.common.Constant;
import com.powerdata.barcode.databinding.FragmentBarcodeErrorListBinding;
import com.powerdata.barcode.model.BarcodeError;
import com.powerdata.barcode.ui.adapter.BarcodeErrorAdapter;
import com.powerdata.barcode.viewModel.BarcodeErrorListViewModel;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

/**
 * 失败明细页面
 */
public class BarcodeErrorListFragment extends Fragment implements BarcodeErrorListViewModel.Listener {

    private static final int CREATE_FILE = 1;

    private BarcodeErrorListViewModel viewModel;
    private String shipNo;

    public BarcodeErrorListFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        assert getArguments() != null;
        shipNo = getArguments().getString(Constant.ARG_SHIP_NO);

        final FragmentBarcodeErrorListBinding binding = FragmentBarcodeErrorListBinding.inflate(inflater);
        binding.setLifecycleOwner(this);
        viewModel = ViewModelProviders.of(this).get(BarcodeErrorListViewModel.class);
        viewModel.setListener(this);
        binding.setViewModel(viewModel);

        final View root = binding.getRoot();
        setHasOptionsMenu(true);

        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar())
                .setTitle(getString(R.string.title_error_detail, shipNo));

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

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
                intent.putExtra(Intent.EXTRA_TITLE, String.format("船%s-失败明细.csv", shipNo));
                startActivityForResult(intent, CREATE_FILE);
            }
        });

        viewModel.getErrors()
                .observe(this, new Observer<List<BarcodeError>>() {
                    @Override
                    public void onChanged(List<BarcodeError> list) {
                        binding.recyclerView.setAdapter(new BarcodeErrorAdapter(list));
                        binding.noDataTextView.setVisibility(list.size() > 0 ? View.GONE : View.VISIBLE);
                    }
                });

        viewModel.load(shipNo);

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
    public void onDeleteSuccess() {
        Toasty.success(requireContext(), R.string.message_delete_success).show();
    }

    @Override
    public void onExportSuccess() {
        Toasty.success(requireContext(), R.string.message_export_success).show();
    }
}
