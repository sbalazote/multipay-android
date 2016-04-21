package com.mercadopago;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mercadopago.adapters.ErrorHandlingCallAdapter;
import com.mercadopago.adapters.PaymentMethodsAdapter;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.decorations.DividerItemDecoration;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.util.ApiUtil;
import com.mercadopago.util.JsonUtil;
import com.mercadopago.util.LayoutUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;

public class PaymentMethodsActivity extends AppCompatActivity {

    private Activity mActivity;
    private String mMerchantPublicKey;
    private RecyclerView mRecyclerView;
    protected boolean mShowBankDeals;
    private List<String> mSupportedPaymentTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView();

        mActivity = this;

        // Get activity parameters
        mMerchantPublicKey = this.getIntent().getStringExtra("merchantPublicKey");
        if (mMerchantPublicKey == null) {
            Intent returnIntent = new Intent();
            setResult(RESULT_CANCELED, returnIntent);
            finish();
            return;
        }
        if (this.getIntent().getStringExtra("supportedPaymentTypes") != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<String>>(){}.getType();
            mSupportedPaymentTypes = gson.fromJson(this.getIntent().getStringExtra("supportedPaymentTypes"), listType);
        }
        mShowBankDeals = this.getIntent().getBooleanExtra("showBankDeals", true);

        // Set recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.payment_methods_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        // Set a linear layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load payment methods
        getPaymentMethodsAsync(mMerchantPublicKey);
    }

    protected void setContentView() {

        setContentView(R.layout.activity_payment_methods);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mShowBankDeals) {
            getMenuInflater().inflate(R.menu.payment_methods, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_bank_deals) {
            new MercadoPago.StartActivityBuilder()
                    .setActivity(this)
                    .setPublicKey(mMerchantPublicKey)
                    .startBankDealsActivity();
        } else {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onBackPressed() {

        Intent returnIntent = new Intent();
        returnIntent.putExtra("backButtonPressed", true);
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }

    public void refreshLayout(View view) {

        getPaymentMethodsAsync(mMerchantPublicKey);
    }

    private void getPaymentMethodsAsync(String merchantPublicKey) {

        LayoutUtil.showProgressLayout(mActivity);

        MercadoPago mercadoPago = new MercadoPago.Builder()
                .setContext(mActivity)
                .setPublicKey(merchantPublicKey)
                .build();

        ErrorHandlingCallAdapter.MyCall<List<PaymentMethod>> call = mercadoPago.getPaymentMethods();
        call.enqueue(new ErrorHandlingCallAdapter.MyCallback<List<PaymentMethod>>() {
            @Override
            public void success(Response<List<PaymentMethod>> response) {

                mRecyclerView.setAdapter(new PaymentMethodsAdapter(mActivity, getSupportedPaymentMethods(response.body()), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        // Return to parent
                        Intent returnIntent = new Intent();
                        PaymentMethod selectedPaymentMethod = (PaymentMethod) view.getTag();
                        returnIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(selectedPaymentMethod));
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

    private List<PaymentMethod> getSupportedPaymentMethods(List<PaymentMethod> paymentMethods) {

        if (mSupportedPaymentTypes != null) {

            List<PaymentMethod> spm = new ArrayList<>();

            for (int i = 0; i < paymentMethods.size(); i++) {
                for (int j = 0; j < mSupportedPaymentTypes.size(); j++) {
                    if (paymentMethods.get(i).getPaymentTypeId().equals(mSupportedPaymentTypes.get(j))) {
                        spm.add(paymentMethods.get(i));
                        break;
                    }
                }
            }

            return spm;

        } else {

            return paymentMethods;
        }
    }
}

