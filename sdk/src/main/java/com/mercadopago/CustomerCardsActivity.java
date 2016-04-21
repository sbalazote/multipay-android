package com.mercadopago;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mercadopago.adapters.CustomerCardsAdapter;
import com.mercadopago.decorations.DividerItemDecoration;
import com.mercadopago.model.Card;
import com.mercadopago.model.PaymentMethodRow;
import com.mercadopago.util.JsonUtil;

import java.lang.reflect.Type;
import java.util.List;

public class CustomerCardsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView();

        // Get activity parameters
        List<Card> cards;
        try {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Card>>(){}.getType();
            cards = gson.fromJson(this.getIntent().getStringExtra("cards"), listType);
        } catch (Exception ex) {
            cards = null;
        }
        if (cards == null) {
            Intent returnIntent = new Intent();
            setResult(RESULT_CANCELED, returnIntent);
            finish();
            return;
        }

        // Set recycler view
        mRecyclerView = (RecyclerView) findViewById(R.id.customer_cards_list);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        // Set a linear layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load cards
        mRecyclerView.setAdapter(new CustomerCardsAdapter(this, cards, new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Return to parent
                Intent returnIntent = new Intent();
                PaymentMethodRow selectedRow = (PaymentMethodRow) view.getTag();
                returnIntent.putExtra("paymentMethodRow", JsonUtil.getInstance().toJson(selectedRow));
                setResult(RESULT_OK, returnIntent);
                finish();
            }
        }));
    }

    protected void setContentView() {

        setContentView(R.layout.activity_customer_cards);
    }

    @Override
    public void onBackPressed() {

        Intent returnIntent = new Intent();
        returnIntent.putExtra("backButtonPressed", true);
        setResult(RESULT_CANCELED, returnIntent);
        finish();
    }
}
