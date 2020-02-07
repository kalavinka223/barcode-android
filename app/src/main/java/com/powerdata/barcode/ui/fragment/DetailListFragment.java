package com.powerdata.barcode.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
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

import java.util.List;
import java.util.Objects;

/**
 * 明细页面
 */
public class DetailListFragment extends Fragment {

    private DetailListViewModel viewModel;
    private AlertDialogFragment deleteAlertDialog;

    public DetailListFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentDetailListBinding binding = FragmentDetailListBinding.inflate(inflater);
        binding.setLifecycleOwner(this);
        viewModel = ViewModelProviders.of(this).get(DetailListViewModel.class);
        binding.setViewModel(viewModel);
        final View root = binding.getRoot();
        setHasOptionsMenu(true);

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

        viewModel.getDetails()
                .observe(getViewLifecycleOwner(), new Observer<List<BarcodeDetail>>() {
                    @Override
                    public void onChanged(List<BarcodeDetail> list) {
                        recyclerView.setAdapter(new BarcodeDetailAdapter(list));
                    }
                });

        if (getArguments() != null) {
            String shipNo = getArguments().getString(ImportFragment.ARG_SHIP_NO);
            Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar())
                    .setTitle(getString(R.string.title_detail, shipNo));
            viewModel.loadDetails(shipNo);
        }

        return root;
    }

    private void showDeleteAlertDialog() {
        if (deleteAlertDialog == null)
            deleteAlertDialog = new AlertDialogFragment();
        deleteAlertDialog.setTitle(getString(R.string.text_delete));
        deleteAlertDialog.setMessage(getString(R.string.message_delete_alert));
        deleteAlertDialog.show(requireFragmentManager(), "delete_alert_dialog");
        deleteAlertDialog.setListener(new AlertDialogFragment.AlertDialogListener() {
            @Override
            public void onDialogPositiveClick(DialogFragment dialog) {
                viewModel.delete();
            }

            @Override
            public void onDialogNegativeClick(DialogFragment dialog) {

            }
        });
    }

}
