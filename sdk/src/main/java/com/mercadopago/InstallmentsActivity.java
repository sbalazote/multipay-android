package com.mercadopago;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mercadopago.adapters.InstallmentsAdapter;
import com.mercadopago.decorations.DividerItemDecoration;
import com.mercadopago.model.PayerCost;
import com.mercadopago.util.JsonUtil;

import java.lang.reflect.Type;
import java.util.List;

public class InstallmentsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();

        // Get activity parameters
        List<PayerCost> payerCosts;
        try {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<PayerCost>>(){}.getType();
            payerCosts = gson.fromJson(this.getIntent().getStringExtra("payerCosts"), listType);
        } catch (Exception ex) {
            payerCosts = null;
        }
        if (payerCosts == null) {
            Intent returnIntent = new Intent();
            setResult(RESULT_CANCELED, returnIntent);
            finish();
            return;
        }

        // Set recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.installments_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        // Set a linear layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load payment methods
        mRecyclerView.setAdapter(new InstallmentsAdapter(payerCosts, new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Return to parent
                Intent returnIntent = new Intent();
                PayerCost selectedPayerCost = (PayerCost) view.getTag();
                returnIntent.putExtra("payerCost", JsonUtil.getInstance().toJson(selectedPayerCost));
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        }));
    }

    protected void setContentView() {

        setContentView(R.layout.activity_installments);
    }

    @Override
    public void onBackPressed() {

        Intent returnIntent = new Intent();
        returnIntent.putExtra("backButtonPressed", true);
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }
}
