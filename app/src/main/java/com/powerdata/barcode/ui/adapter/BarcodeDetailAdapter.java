package com.powerdata.barcode.ui.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.powerdata.barcode.R;
import com.powerdata.barcode.model.BarcodeDetail;

import java.util.List;

public class BarcodeDetailAdapter extends RecyclerView.Adapter<BarcodeDetailAdapter.BarcodeDetailViewHolder> {

    private List<BarcodeDetail> barcodeDetails;

    public BarcodeDetailAdapter(List<BarcodeDetail> barcodeDetails) {
        this.barcodeDetails = barcodeDetails;
    }

    @NonNull
    @Override
    public BarcodeDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_barcode_detail, parent, false);
        return new BarcodeDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BarcodeDetailViewHolder holder, int position) {
        BarcodeDetail detail = barcodeDetails.get(position);
        holder.barcodeTextView.setText(detail.barcode);
        holder.statusTextView.setText(detail.status == 0 ? R.string.text_not_scanned : R.string.text_scanned);
        holder.statusTextView.setTextColor(detail.status == 0 ? Color.RED : Color.GREEN);
    }

    @Override
    public int getItemCount() {
        return barcodeDetails.size();
    }

    class BarcodeDetailViewHolder extends RecyclerView.ViewHolder {
        TextView barcodeTextView;
        TextView statusTextView;

        private BarcodeDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            barcodeTextView = itemView.findViewById(R.id.barcode_text_view);
            statusTextView = itemView.findViewById(R.id.status_text_view);
        }
    }

}
