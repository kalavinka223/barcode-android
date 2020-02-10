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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.tabs.TabLayout;
import com.powerdata.barcode.R;
import com.powerdata.barcode.common.Constant;
import com.powerdata.barcode.databinding.FragmentBarcodeDetailListBinding;
import com.powerdata.barcode.ui.adapter.BarcodeDetailAdapter;
import com.powerdata.barcode.viewModel.BarcodeDetailListViewModel;

import java.io.IOException;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

/**
 * 明细页面
 */
public class BarcodeDetailListFragment extends Fragment implements BarcodeDetailListViewModel.Listener {

    private static final int CREATE_FILE = 1;

    private BarcodeDetailListViewModel viewModel;
    private String shipNo;

    public BarcodeDetailListFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        assert getArguments() != null;
        shipNo = getArguments().getString(Constant.ARG_SHIP_NO);

        final FragmentBarcodeDetailListBinding binding = FragmentBarcodeDetailListBinding.inflate(inflater);
        binding.setLifecycleOwner(this);
        viewModel = ViewModelProviders.of(this).get(BarcodeDetailListViewModel.class);
        viewModel.setListener(this);
        binding.setViewModel(viewModel);
        setHasOptionsMenu(true);

        final View root = binding.getRoot();
        setHasOptionsMenu(true);

        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar())
                .setTitle(getString(R.string.title_detail, shipNo));

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                BarcodeDetailListViewModel.Status status = BarcodeDetailListViewModel.Status.ALL;
                switch (tab.getPosition()) {
                    case 0:
                        status = BarcodeDetailListViewModel.Status.ALL;
                        break;
                    case 1:
                        status = BarcodeDetailListViewModel.Status.SCANNED;
                        break;
                    case 2:
                        status = BarcodeDetailListViewModel.Status.NOT_SCANNED;
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

        binding.deleteButton.setOnClickListener(v -> showDeleteAlertDialog());

        binding.exportButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("text/csv");
            intent.putExtra(Intent.EXTRA_TITLE, String.format("船%s-明细.csv", shipNo));
            startActivityForResult(intent, CREATE_FILE);
        });

        viewModel.getDetails()
                .observe(this, list -> {
                    binding.recyclerView.setAdapter(new BarcodeDetailAdapter(list));
                    binding.noDataTextView.setVisibility(list.size() > 0 ? View.GONE : View.VISIBLE);
                });

        viewModel.load(shipNo);

        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.search_item) {
            Bundle bundle = new Bundle();
            bundle.putString(Constant.ARG_SHIP_NO, shipNo);
            requireActivity().startSearch(null, false, bundle, false);
            return true;
        }
        return super.onOptionsItemSelected(item);
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
