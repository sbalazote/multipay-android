package com.mercadopago.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mercadopago.R;
import com.mercadopago.model.Card;
import com.mercadopago.model.PaymentMethodRow;
import com.mercadopago.util.MercadoPagoUtil;

import java.util.ArrayList;
import java.util.List;

public class CustomerCardsAdapter extends  RecyclerView.Adapter<CustomerCardsAdapter.ViewHolder> {

    private List<PaymentMethodRow> mData;
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

    public CustomerCardsAdapter(Activity activity, List<Card> data, View.OnClickListener listener) {

        mData = getRows(activity, data);
        mListener = listener;
    }

    @Override
    public CustomerCardsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_simple_list, parent, false);

        return new ViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // Set current row
        PaymentMethodRow row = mData.get(position);

        // Set row label
        holder.mTextView.setText(row.getLabel());

        // Set row picture
        holder.mTextView.setCompoundDrawablesWithIntrinsicBounds(row.getIcon(), 0, 0, 0);

        // Set view tag item
        holder.itemView.setTag(row);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    private List<PaymentMethodRow> getRows(Context context, List<Card> data) {

        List<PaymentMethodRow> rows = new ArrayList<>();

        // Add cards
        for (int i = 0; i < data.size(); i++) {
            rows.add(getPaymentMethodRow(context, data.get(i)));
        }

        // Add other payment method row
        rows.add(new PaymentMethodRow(null, context.getString(R.string.mpsdk_other_pm_label), 0));

        return rows;
    }

    public static PaymentMethodRow getPaymentMethodRow(Context context, Card card) {

        int icon = MercadoPagoUtil.getPaymentMethodIcon(context, card.getPaymentMethod().getId());
        return new PaymentMethodRow(card, getPaymentMethodLabel(context, card.getPaymentMethod().getName(), card.getLastFourDigits()), icon);
    }

    public static String getPaymentMethodLabel(Context context, String name, String lastFourDigits) {

        return getPaymentMethodLabel(context, name, lastFourDigits, false);
    }

    public static String getPaymentMethodLabel(Context context, String name, String lastFourDigits, boolean noName) {

        if (noName) {
            return context.getString(R.string.mpsdk_last_digits_label) + " " + lastFourDigits;
        } else {
            return name + " " + context.getString(R.string.mpsdk_last_digits_label) + " " + lastFourDigits;
        }
    }

    public PaymentMethodRow getItem(int position) {
        return mData.get(position);
    }

    public View.OnClickListener getListener() { return mListener; }
}