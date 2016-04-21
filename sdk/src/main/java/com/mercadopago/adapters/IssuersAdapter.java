package com.mercadopago.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mercadopago.R;
import com.mercadopago.model.Issuer;

import java.util.List;

public class IssuersAdapter extends  RecyclerView.Adapter<IssuersAdapter.ViewHolder> {

    private List<Issuer> mData;
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

    public IssuersAdapter(List<Issuer> data, View.OnClickListener listener) {

        mData = data;
        mListener = listener;
    }

    @Override
    public IssuersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_simple_list, parent, false);

        return new ViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // Set current issuer
        Issuer issuer = mData.get(position);

        // Set label
        holder.mTextView.setText(issuer.getName());

        // Set view tag item
        holder.itemView.setTag(issuer);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public Issuer getItem(int position) {
        return mData.get(position);
    }

    public View.OnClickListener getListener() { return mListener; }
}