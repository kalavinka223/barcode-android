package com.powerdata.barcode.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.powerdata.barcode.R;
import com.powerdata.barcode.model.BarcodeInfo;

import java.util.List;

public class BarcodeAdapter extends RecyclerView.Adapter<BarcodeAdapter.BarcodeViewHolder> {

    private List<BarcodeInfo> barcodeInfos;

    public BarcodeAdapter(List<BarcodeInfo> barcodeInfos) {
        this.barcodeInfos = barcodeInfos;
    }

    @NonNull
    @Override
    public BarcodeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_barcode, parent, false);
        return new BarcodeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BarcodeViewHolder holder, int position) {
        BarcodeInfo barcodeInfo = barcodeInfos.get(position);
        holder.barcodeTextView.setText(barcodeInfo.barcode);
        holder.timeTextView.setText(barcodeInfo.createdAt);
    }

    @Override
    public int getItemCount() {
        return barcodeInfos.size();
    }

    class BarcodeViewHolder extends RecyclerView.ViewHolder {
        TextView barcodeTextView;
        TextView timeTextView;

        private BarcodeViewHolder(@NonNull View itemView) {
            super(itemView);
            barcodeTextView = itemView.findViewById(R.id.barcode_text_view);
            timeTextView = itemView.findViewById(R.id.time_text_view);
        }
    }

}
