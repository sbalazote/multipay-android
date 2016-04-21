package com.mercadopago.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mercadopago.R;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.util.MercadoPagoUtil;

import java.util.List;

public class PaymentMethodsAdapter extends  RecyclerView.Adapter<PaymentMethodsAdapter.ViewHolder> {

    private Activity mActivity;
    private List<PaymentMethod> mData;
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

    public PaymentMethodsAdapter(Activity activity, List<PaymentMethod> data, View.OnClickListener listener) {

        mActivity = activity;
        mData = data;
        mListener = listener;
    }

    @Override
    public PaymentMethodsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_simple_list, parent, false);

        return new ViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // Set current payment method
        PaymentMethod paymentMethod = mData.get(position);

        // Set label
        holder.mTextView.setText(paymentMethod.getName());

        // Set picture
        holder.mTextView.setCompoundDrawablesWithIntrinsicBounds(MercadoPagoUtil.getPaymentMethodIcon(mActivity, paymentMethod.getId()), 0, 0, 0);

        // Set view tag item
        holder.itemView.setTag(paymentMethod);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public PaymentMethod getItem(int position) {
        return mData.get(position);
    }

    public View.OnClickListener getListener() { return mListener; }
}