package com.powerdata.barcode.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.powerdata.barcode.R;
import com.powerdata.barcode.databinding.FragmentDetailListBinding;
import com.powerdata.barcode.model.BarcodeDetail;
import com.powerdata.barcode.ui.adapter.BarcodeDetailAdapter;
import com.powerdata.barcode.viewModel.DetailListViewModel;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

/**
 * 明细页面
 */
public class DetailListFragment extends Fragment implements DetailListViewModel.DetailListViewModelListener {

    private static final int CREATE_FILE = 1;

    private DetailListViewModel viewModel;
    private String shipNo;

    public DetailListFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        assert getArguments() != null;
        shipNo = getArguments().getString(ImportFragment.ARG_SHIP_NO);

        FragmentDetailListBinding binding = FragmentDetailListBinding.inflate(inflater);
        binding.setLifecycleOwner(this);
        viewModel = ViewModelProviders.of(this).get(DetailListViewModel.class);
        viewModel.setListener(this);
        binding.setViewModel(viewModel);

        final View root = binding.getRoot();
        setHasOptionsMenu(true);

        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar())
                .setTitle(getString(R.string.title_detail, shipNo));

        final RecyclerView recyclerView = root.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        TabLayout tabLayout = root.findViewById(R.id.tab_layout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                DetailListViewModel.Status status = DetailListViewModel.Status.ALL;
                switch (tab.getPosition()) {
                    case 0:
                        status = DetailListViewModel.Status.ALL;
                        break;
                    case 1:
                        status = DetailListViewModel.Status.SCANNED;
                        break;
                    case 2:
                        status = DetailListViewModel.Status.NOT_SCANNED;
                        break;
                }
                viewModel.setStatus(status);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        Button deleteButton = root.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteAlertDialog();
            }
        });

        Button exportButton = root.findViewById(R.id.export_button);
        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("text/csv");
                intent.putExtra(Intent.EXTRA_TITLE, "船" + shipNo + "明细.csv");
                startActivityForResult(intent, CREATE_FILE);
            }
        });

        viewModel.getDetails()
                .observe(getViewLifecycleOwner(), new Observer<List<BarcodeDetail>>() {
                    @Override
                    public void onChanged(List<BarcodeDetail> list) {
                        recyclerView.setAdapter(new BarcodeDetailAdapter(list));
                    }
                });

        viewModel.loadDetails(shipNo);

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
