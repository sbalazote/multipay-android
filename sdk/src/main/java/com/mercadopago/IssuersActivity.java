package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mercadopago.adapters.ErrorHandlingCallAdapter;
import com.mercadopago.adapters.IssuersAdapter;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.decorations.DividerItemDecoration;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.LayoutUtil;

import java.util.List;

import retrofit2.Response;

public class IssuersActivity extends AppCompatActivity {

    private Activity mActivity;
    private String mMerchantPublicKey;
    private PaymentMethod mPaymentMethod;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView();

        mActivity = this;

        // Get activity parameters
        mMerchantPublicKey = this.getIntent().getStringExtra("merchantPublicKey");
        String paymentMethod = this.getIntent().getStringExtra("paymentMethod");
        if ((mMerchantPublicKey == null) || (paymentMethod == null)) {
            Intent returnIntent = new Intent();
            setResult(RESULT_CANCELED, returnIntent);
            finish();
            return;
        }
        mPaymentMethod = JsonUtil.getInstance().fromJson(paymentMethod, PaymentMethod.class);

        // Set recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.issuers_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        // Set a linear layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load payment methods
        getIssuersAsync(mMerchantPublicKey);
    }

    protected void setContentView() {

        setContentView(R.layout.activity_issuers);
    }

    @Override
    public void onBackPressed() {

        Intent returnIntent = new Intent();
        returnIntent.putExtra("backButtonPressed", true);
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    public void refreshLayout(View view) {

        getIssuersAsync(mMerchantPublicKey);
    }

    private void getIssuersAsync(String merchantPublicKey) {

        LayoutUtil.showProgressLayout(mActivity);

        MercadoPago mercadoPago = new MercadoPago.Builder()
                .setContext(mActivity)
                .setPublicKey(merchantPublicKey)
                .build();

        ErrorHandlingCallAdapter.MyCall<List<Issuer>> call = mercadoPago.getIssuers(mPaymentMethod.getId());
        call.enqueue(new ErrorHandlingCallAdapter.MyCallback<List<Issuer>>() {
            @Override
            public void success(Response<List<Issuer>> response) {

                mRecyclerView.setAdapter(new IssuersAdapter(response.body(), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // Return to parent
                        Intent returnIntent = new Intent();
                        Issuer selectedIssuer = (Issuer) view.getTag();
                        returnIntent.putExtra("issuer", JsonUtil.getInstance().toJson(selectedIssuer));
                        setResult(RESULT_OK, returnIntent);
                        finish();
                    }
                }));
                LayoutUtil.showRegularLayout(mActivity);
            }

            @Override
            public void failure(ApiException apiException) {

                ApiUtil.finishWithApiException(mActivity, apiException);
            }
        });
    }
}
