package com.mercadopago;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mercadopago.adapters.CustomerCardsAdapter;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.util.CurrenciesUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.MercadoPagoUtil;

public class CongratsActivity extends AppCompatActivity {

    private String mCouponUrl;
    private PaymentMethod mPaymentMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView();

        // Get activity params
        String paymentString = this.getIntent().getStringExtra("payment");
        String paymentMethodString = this.getIntent().getStringExtra("paymentMethod");
        if ((paymentString == null) || (paymentMethodString == null)) {
            Intent returnIntent = new Intent();
            setResult(RESULT_CANCELED, returnIntent);
            finish();
            return;
        }
        Payment payment = JsonUtil.getInstance().fromJson(paymentString, Payment.class);
        mPaymentMethod = JsonUtil.getInstance().fromJson(paymentMethodString, PaymentMethod.class);

        // Set layout
        setLayout(payment);
    }

    protected void setContentView() {

        setContentView(R.layout.activity_congrats);
    }

    @Override
    public void onBackPressed() {

        Intent returnIntent = new Intent();
        returnIntent.putExtra("backButtonPressed", true);
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    public void finalize(View view) {

        // Navigate coupon url if needed
        if (mCouponUrl != null) {
            navigateUrl(mCouponUrl);
        }

        // Return to parent with success result
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    private void setLayout(Payment payment) {

        // Title
        setTitle(payment);

        // Icon
        setIcon(payment);

        // Description
        setDescription(payment);

        // Amount
        setAmount(payment);

        // payment id
        setPaymentId(payment);

        // payment method description
        setPaymentMethodDescription(payment);

        // payment creation date
        setDateCreated(payment);

        // Button text
        setButtonText(payment);
    }

    private void setTitle(Payment payment) {

        if (payment != null) {
            if (payment.getStatus().equals("approved")) {
                setTitle(getString(R.string.mpsdk_approved_title));
            } else if (payment.getStatus().equals("pending")) {
                setTitle(getString(R.string.mpsdk_pending_title));
            } else if (payment.getStatus().equals("in_process")) {
                setTitle(getString(R.string.mpsdk_in_process_title));
            } else if (payment.getStatus().equals("rejected")) {
                setTitle(getString(R.string.mpsdk_rejected_title));
            }
        }
    }

    private void setIcon(Payment payment) {

        if (payment != null) {
            ImageView iconIV = (ImageView) findViewById(R.id.icon);
            if (payment.getStatus().equals("approved")) {
                iconIV.setImageResource(R.drawable.ic_approved);
            } else if (payment.getStatus().equals("pending")) {
                iconIV.setImageResource(R.drawable.ic_pending);
            } else if (payment.getStatus().equals("in_process")) {
                iconIV.setImageResource(R.drawable.ic_pending);
            } else if (payment.getStatus().equals("rejected")) {
                iconIV.setImageResource(R.drawable.ic_rejected);
            }
        }
    }

    private void setDescription(Payment payment) {

        if (payment != null) {
            TextView descriptionText = (TextView) findViewById(R.id.description);
            if (payment.getStatus().equals("approved")) {
                descriptionText.setText(getString(R.string.mpsdk_approved_message));
            } else if (payment.getStatus().equals("pending")) {
                descriptionText.setText(getString(R.string.mpsdk_pending_ticket_message));
            } else if (payment.getStatus().equals("in_process")) {
                descriptionText.setText(getString(R.string.mpsdk_in_process_message));
            } else if (payment.getStatus().equals("rejected")) {
                descriptionText.setText(getString(R.string.mpsdk_rejected_message));
            }
        }
    }

    private void setAmount(Payment payment) {

        if ((payment.getTransactionDetails().getTotalPaidAmount() != null) && (payment.getCurrencyId() != null)) {
            String formattedAmount = CurrenciesUtil.formatNumber(payment.getTransactionDetails().getTotalPaidAmount(),
                    payment.getCurrencyId());
            if (formattedAmount != null) {
                TextView amount = (TextView) findViewById(R.id.amount);
                amount.setText(formattedAmount);
            }
        }
    }

    private void setPaymentId(Payment payment) {

        if (payment.getId() != null) {
            TextView paymentIdText = (TextView) findViewById(R.id.paymentId);
            paymentIdText.setText(Long.toString(payment.getId()));
        }
    }

    private void setPaymentMethodDescription(Payment payment) {

        TextView paymentMethodText = (TextView) findViewById(R.id.paymentMethod);
        if ((payment.getCard() != null) && (payment.getCard().getPaymentMethod() != null)) {
            paymentMethodText.setText(CustomerCardsAdapter.getPaymentMethodLabel(this,
                    payment.getCard().getPaymentMethod().getName(), payment.getCard().getLastFourDigits(), true));
            paymentMethodText.setCompoundDrawablesWithIntrinsicBounds(
                    MercadoPagoUtil.getPaymentMethodIcon(this, payment.getCard().getPaymentMethod().getId()),
                    0, 0, 0);
        } else if (mPaymentMethod != null) {
            paymentMethodText.setText(mPaymentMethod.getName());
            paymentMethodText.setCompoundDrawablesWithIntrinsicBounds(
                    MercadoPagoUtil.getPaymentMethodIcon(this, mPaymentMethod.getId()),
                    0, 0, 0);
        }
    }

    private void setDateCreated(Payment payment) {

        if (payment.getDateCreated() != null) {
            TextView dateCreatedText = (TextView) findViewById(R.id.dateCreated);
            dateCreatedText.setText(MercadoPagoUtil.formatDate(this, payment.getDateCreated()));
        }
    }

    private void setButtonText(Payment payment) {

        if (payment != null) {
            Button button = (Button) findViewById(R.id.button);
            if (payment.getStatus().equals("pending")) {
                button.setText(R.string.mpsdk_print_ticket_label);
                mCouponUrl = payment.getTransactionDetails().getExternalResourceUrl();
            } else {
                button.setText(R.string.mpsdk_finish_label);
                mCouponUrl = null;
            }
        }
    }

    private void navigateUrl(String url) {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
}
