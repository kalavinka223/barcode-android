package com.powerdata.barcode.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.powerdata.barcode.R;
import com.powerdata.barcode.model.BarcodeError;

import java.util.List;

public class BarcodeErrorAdapter extends RecyclerView.Adapter<BarcodeErrorAdapter.BarcodeErrorViewHolder> {

    private List<BarcodeError> barcodeDetails;

    public BarcodeErrorAdapter(List<BarcodeError> barcodeDetails) {
        this.barcodeDetails = barcodeDetails;
    }

    @NonNull
    @Override
    public BarcodeErrorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_barcode_error, parent, false);
        return new BarcodeErrorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BarcodeErrorViewHolder holder, int position) {
        BarcodeError error = barcodeDetails.get(position);
        holder.barcodeTextView.setText(error.barcode);
        holder.timeTextView.setText(error.createdAt);
    }

    @Override
    public int getItemCount() {
        return barcodeDetails.size();
    }

    class BarcodeErrorViewHolder extends RecyclerView.ViewHolder {
        TextView barcodeTextView;
        TextView timeTextView;

        private BarcodeErrorViewHolder(@NonNull View itemView) {
            super(itemView);
            barcodeTextView = itemView.findViewById(R.id.barcode_text_view);
            timeTextView = itemView.findViewById(R.id.time_text_view);
        }
    }

}
