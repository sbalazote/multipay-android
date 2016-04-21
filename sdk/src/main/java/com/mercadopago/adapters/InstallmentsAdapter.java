package com.mercadopago.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mercadopago.R;
import com.mercadopago.model.PayerCost;

import java.util.List;

public class InstallmentsAdapter extends  RecyclerView.Adapter<InstallmentsAdapter.ViewHolder> {

    private List<PayerCost> mData;
    private View.OnClickListener mListener = null;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextView;

        public ViewHolder(View v, View.OnClickListener listener) {

            super(v);
            mTextView = (TextView) v.findViewById(R.id.label);
            if (listener != null) {
                v.setOnClickListener(listener);
            }
        }
    }

    public InstallmentsAdapter(List<PayerCost> data, View.OnClickListener listener) {

        mData = data;
        mListener = listener;
    }

    @Override
    public InstallmentsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_simple_list, parent, false);

        return new ViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // Set payer cost
        PayerCost payerCost = mData.get(position);

        // Set recommended label
        String recommendedLabel = payerCost.getRecommendedMessage();

        // Set row label
        holder.mTextView.setText(recommendedLabel);

        // Set view tag item
        holder.itemView.setTag(payerCost);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public PayerCost getItem(int position) {
        return mData.get(position);
    }

    public View.OnClickListener getListener() { return mListener; }
}