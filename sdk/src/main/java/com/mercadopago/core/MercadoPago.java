package com.mercadopago.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mercadopago.BankDealsActivity;
import com.mercadopago.CongratsActivity;
import com.mercadopago.CustomerCardsActivity;
import com.mercadopago.InstallmentsActivity;
import com.mercadopago.IssuersActivity;
import com.mercadopago.PaymentMethodsActivity;
import com.mercadopago.VaultActivity;
import com.mercadopago.adapters.ErrorHandlingCallAdapter;
import com.mercadopago.model.BankDeal;
import com.mercadopago.model.Card;
import com.mercadopago.model.CardToken;
import com.mercadopago.model.IdentificationType;
import com.mercadopago.model.Installment;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.SavedCardToken;
import com.mercadopago.model.Token;
import com.mercadopago.services.BankDealService;
import com.mercadopago.services.GatewayService;
import com.mercadopago.services.IdentificationService;
import com.mercadopago.services.PaymentService;
import com.mercadopago.util.HttpClientUtil;
import com.mercadopago.util.JsonUtil;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;

import retrofit2.Retrofit;

public class MercadoPago {

    public static final String KEY_TYPE_PUBLIC = "public_key";
    public static final String KEY_TYPE_PRIVATE = "private_key";

    public static final int CUSTOMER_CARDS_REQUEST_CODE = 0;
    public static final int PAYMENT_METHODS_REQUEST_CODE = 1;
    public static final int INSTALLMENTS_REQUEST_CODE = 2;
    public static final int ISSUERS_REQUEST_CODE = 3;
    public static final int NEW_CARD_REQUEST_CODE = 4;
    public static final int CONGRATS_REQUEST_CODE = 5;
    public static final int VAULT_REQUEST_CODE = 6;
    public static final int BANK_DEALS_REQUEST_CODE = 7;

    public static final int BIN_LENGTH = 6;

    private static final String MP_API_BASE_URL = "https://api.mercadopago.com";
    private String mKey = null;
    private String mKeyType = null;
    private Context mContext = null;
    private Retrofit mRestAdapterMPApi;

    private MercadoPago(Builder builder) {

        this.mContext = builder.mContext;
        this.mKey = builder.mKey;
        this.mKeyType = builder.mKeyType;

        System.setProperty("http.keepAlive", "false");

        mRestAdapterMPApi = new Retrofit.Builder()
                .baseUrl(MP_API_BASE_URL)
                .client(HttpClientUtil.getClient(this.mContext))
                .addConverterFactory(JsonUtil.getInstance().getGsonConverterFactory())
                .addCallAdapterFactory(new ErrorHandlingCallAdapter.ErrorHandlingCallAdapterFactory())
                .build();
    }

    public ErrorHandlingCallAdapter.MyCall<Token> createToken(final SavedCardToken savedCardToken) {

        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            savedCardToken.setDevice(mContext);
            GatewayService service = mRestAdapterMPApi.create(GatewayService.class);
            return service.getToken(this.mKey, savedCardToken);
        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }

    public ErrorHandlingCallAdapter.MyCall<Token> createToken(final CardToken cardToken) {

        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            cardToken.setDevice(mContext);
            GatewayService service = mRestAdapterMPApi.create(GatewayService.class);
            return service.getToken(this.mKey, cardToken);
        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }

    public ErrorHandlingCallAdapter.MyCall<List<PaymentMethod>> getPaymentMethods() {

        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            PaymentService service = mRestAdapterMPApi.create(PaymentService.class);
            return service.getPaymentMethods(this.mKey);
        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }

    public ErrorHandlingCallAdapter.MyCall<List<IdentificationType>> getIdentificationTypes() {

        IdentificationService service = mRestAdapterMPApi.create(IdentificationService.class);
        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            return service.getIdentificationTypes(this.mKey, null);
        } else {
            return service.getIdentificationTypes(null, this.mKey);
        }
    }

    public ErrorHandlingCallAdapter.MyCall<List<Installment>> getInstallments(String bin, BigDecimal amount, Long issuerId, String paymentTypeId) {

        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            PaymentService service = mRestAdapterMPApi.create(PaymentService.class);
            return service.getInstallments(this.mKey, bin, amount, issuerId, paymentTypeId,
                    mContext.getResources().getConfiguration().locale.toString());
        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }

    public ErrorHandlingCallAdapter.MyCall<List<Issuer>> getIssuers(String paymentMethodId) {

        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            PaymentService service = mRestAdapterMPApi.create(PaymentService.class);
            return service.getIssuers(this.mKey, paymentMethodId);
        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }

    public ErrorHandlingCallAdapter.MyCall<List<BankDeal>> getBankDeals() {

        if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
            BankDealService service = mRestAdapterMPApi.create(BankDealService.class);
            return service.getBankDeals(this.mKey, mContext.getResources().getConfiguration().locale.toString());
        } else {
            throw new RuntimeException("Unsupported key type for this method");
        }
    }

    public Retrofit retrofit() {

        return mRestAdapterMPApi;
    }

    // * Static methods for StartActivityBuilder implementation

    private static void startBankDealsActivity(Activity activity, String merchantPublicKey) {

        Intent bankDealsIntent = new Intent(activity, BankDealsActivity.class);
        bankDealsIntent.putExtra("merchantPublicKey", merchantPublicKey);
        activity.startActivityForResult(bankDealsIntent, BANK_DEALS_REQUEST_CODE);
    }

    private static void startCongratsActivity(Activity activity, Payment payment, PaymentMethod paymentMethod) {

        Intent congratsIntent = new Intent(activity, CongratsActivity.class);
        congratsIntent.putExtra("payment", JsonUtil.getInstance().toJson(payment));
        congratsIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        activity.startActivityForResult(congratsIntent, CONGRATS_REQUEST_CODE);
    }

    private static void startCustomerCardsActivity(Activity activity, List<Card> cards) {

        if ((activity == null) || (cards == null)) {
            throw new RuntimeException("Invalid parameters");
        }
        Intent paymentMethodsIntent = new Intent(activity, CustomerCardsActivity.class);
        Gson gson = new Gson();
        paymentMethodsIntent.putExtra("cards", gson.toJson(cards));
        activity.startActivityForResult(paymentMethodsIntent, CUSTOMER_CARDS_REQUEST_CODE);
    }

    private static void startInstallmentsActivity(Activity activity, List<PayerCost> payerCosts) {

        Intent installmentsIntent = new Intent(activity, InstallmentsActivity.class);
        Gson gson = new Gson();
        installmentsIntent.putExtra("payerCosts", gson.toJson(payerCosts));
        activity.startActivityForResult(installmentsIntent, INSTALLMENTS_REQUEST_CODE);
    }

    private static void startIssuersActivity(Activity activity, String merchantPublicKey, PaymentMethod paymentMethod) {

        Intent issuersIntent = new Intent(activity, IssuersActivity.class);
        issuersIntent.putExtra("merchantPublicKey", merchantPublicKey);
        issuersIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        activity.startActivityForResult(issuersIntent, ISSUERS_REQUEST_CODE);
    }

    private static void startNewCardActivity(Activity activity, String keyType, String key, PaymentMethod paymentMethod, Boolean requireSecurityCode) {

        Intent newCardIntent = new Intent(activity, com.mercadopago.NewCardActivity.class);
        newCardIntent.putExtra("keyType", keyType);
        newCardIntent.putExtra("key", key);
        newCardIntent.putExtra("paymentMethod", JsonUtil.getInstance().toJson(paymentMethod));
        if (requireSecurityCode != null) {
            newCardIntent.putExtra("requireSecurityCode", requireSecurityCode);
        }
        activity.startActivityForResult(newCardIntent, NEW_CARD_REQUEST_CODE);
    }

    private static void startPaymentMethodsActivity(Activity activity, String merchantPublicKey, List<String> supportedPaymentTypes, Boolean showBankDeals) {

        Intent paymentMethodsIntent = new Intent(activity, PaymentMethodsActivity.class);
        paymentMethodsIntent.putExtra("merchantPublicKey", merchantPublicKey);
        putListExtra(paymentMethodsIntent, "supportedPaymentTypes", supportedPaymentTypes);
        paymentMethodsIntent.putExtra("showBankDeals", showBankDeals);
        activity.startActivityForResult(paymentMethodsIntent, PAYMENT_METHODS_REQUEST_CODE);
    }

    private static void startVaultActivity(Activity activity, String merchantPublicKey, String merchantBaseUrl, String merchantGetCustomerUri, String merchantAccessToken, BigDecimal amount, List<String> supportedPaymentTypes, Boolean showBankDeals) {

        Intent vaultIntent = new Intent(activity, VaultActivity.class);
        vaultIntent.putExtra("merchantPublicKey", merchantPublicKey);
        vaultIntent.putExtra("merchantBaseUrl", merchantBaseUrl);
        vaultIntent.putExtra("merchantGetCustomerUri", merchantGetCustomerUri);
        vaultIntent.putExtra("merchantAccessToken", merchantAccessToken);
        vaultIntent.putExtra("amount", amount.toString());
        putListExtra(vaultIntent, "supportedPaymentTypes", supportedPaymentTypes);
        vaultIntent.putExtra("showBankDeals", showBankDeals);
        activity.startActivityForResult(vaultIntent, VAULT_REQUEST_CODE);
    }

    private static void putListExtra(Intent intent, String listName, List<String> list) {

        if (list != null) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<String>>(){}.getType();
            intent.putExtra(listName, gson.toJson(list, listType));
        }
    }

    public static class Builder {

        private Context mContext;
        private String mKey;
        private String mKeyType;

        public Builder() {

            mContext = null;
            mKey = null;
        }

        public Builder setContext(Context context) {

            if (context == null) throw new IllegalArgumentException("context is null");
            this.mContext = context;
            return this;
        }

        public Builder setKey(String key, String keyType) {

            this.mKey = key;
            this.mKeyType = keyType;
            return this;
        }

        public Builder setPrivateKey(String key) {

            this.mKey = key;
            this.mKeyType = MercadoPago.KEY_TYPE_PRIVATE;
            return this;
        }

        public Builder setPublicKey(String key) {

            this.mKey = key;
            this.mKeyType = MercadoPago.KEY_TYPE_PUBLIC;
            return this;
        }

        public MercadoPago build() {

            if (this.mContext == null) throw new IllegalStateException("context is null");
            if (this.mKey == null) throw new IllegalStateException("key is null");
            if (this.mKeyType == null) throw new IllegalStateException("key type is null");
            if ((!this.mKeyType.equals(MercadoPago.KEY_TYPE_PRIVATE)) &&
                    (!this.mKeyType.equals(MercadoPago.KEY_TYPE_PUBLIC))) throw new IllegalArgumentException("invalid key type");
            return new MercadoPago(this);
        }
    }

    public static class StartActivityBuilder {

        private Activity mActivity;
        private BigDecimal mAmount;
        private List<Card> mCards;
        private String mKey;
        private String mKeyType;
        private String mMerchantAccessToken;
        private String mMerchantBaseUrl;
        private String mMerchantGetCustomerUri;
        private List<PayerCost> mPayerCosts;
        private Payment mPayment;
        private PaymentMethod mPaymentMethod;
        private Boolean mRequireSecurityCode;
        private Boolean mShowBankDeals;
        private List<String> mSupportedPaymentTypes;

        public StartActivityBuilder() {

            mActivity = null;
            mKey = null;
            mKeyType = KEY_TYPE_PUBLIC;
        }

        public StartActivityBuilder setActivity(Activity activity) {

            if (activity == null) throw new IllegalArgumentException("context is null");
            this.mActivity = activity;
            return this;
        }

        public StartActivityBuilder setAmount(BigDecimal amount) {

            this.mAmount = amount;
            return this;
        }

        public StartActivityBuilder setCards(List<Card> cards) {

            this.mCards = cards;
            return this;
        }

        public StartActivityBuilder setKey(String key, String keyType) {

            this.mKey = key;
            this.mKeyType = keyType;
            return this;
        }

        public StartActivityBuilder setPrivateKey(String key) {

            this.mKey = key;
            this.mKeyType = MercadoPago.KEY_TYPE_PRIVATE;
            return this;
        }

        public StartActivityBuilder setPublicKey(String key) {

            this.mKey = key;
            this.mKeyType = MercadoPago.KEY_TYPE_PUBLIC;
            return this;
        }

        public StartActivityBuilder setMerchantAccessToken(String merchantAccessToken) {

            this.mMerchantAccessToken = merchantAccessToken;
            return this;
        }

        public StartActivityBuilder setMerchantBaseUrl(String merchantBaseUrl) {

            this.mMerchantBaseUrl = merchantBaseUrl;
            return this;
        }

        public StartActivityBuilder setMerchantGetCustomerUri(String merchantGetCustomerUri) {

            this.mMerchantGetCustomerUri = merchantGetCustomerUri;
            return this;
        }

        public StartActivityBuilder setPayerCosts(List<PayerCost> payerCosts) {

            this.mPayerCosts = payerCosts;
            return this;
        }

        public StartActivityBuilder setPayment(Payment payment) {

            this.mPayment = payment;
            return this;
        }

        public StartActivityBuilder setPaymentMethod(PaymentMethod paymentMethod) {

            this.mPaymentMethod = paymentMethod;
            return this;
        }

        public StartActivityBuilder setRequireSecurityCode(Boolean requireSecurityCode) {

            this.mRequireSecurityCode = requireSecurityCode;
            return this;
        }

        public StartActivityBuilder setShowBankDeals(boolean showBankDeals) {

            this.mShowBankDeals = showBankDeals;
            return this;
        }

        public StartActivityBuilder setSupportedPaymentTypes(List<String> supportedPaymentTypes) {

            this.mSupportedPaymentTypes = supportedPaymentTypes;
            return this;
        }

        public void startBankDealsActivity() {

            if (this.mActivity == null) throw new IllegalStateException("activity is null");
            if (this.mKey == null) throw new IllegalStateException("key is null");
            if (this.mKeyType == null) throw new IllegalStateException("key type is null");

            if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
                MercadoPago.startBankDealsActivity(this.mActivity, this.mKey);
            } else {
                throw new RuntimeException("Unsupported key type for this method");
            }
        }

        public void startCongratsActivity() {

            if (this.mActivity == null) throw new IllegalStateException("activity is null");
            if (this.mPayment == null) throw new IllegalStateException("payment is null");
            if (this.mPaymentMethod == null) throw new IllegalStateException("payment method is null");

            MercadoPago.startCongratsActivity(this.mActivity, this.mPayment, this.mPaymentMethod);
        }

        public void startCustomerCardsActivity() {

            if (this.mActivity == null) throw new IllegalStateException("activity is null");
            if (this.mCards == null) throw new IllegalStateException("cards is null");

            MercadoPago.startCustomerCardsActivity(this.mActivity, this.mCards);
        }

        public void startInstallmentsActivity() {

            if (this.mActivity == null) throw new IllegalStateException("activity is null");
            if (this.mPayerCosts == null) throw new IllegalStateException("payer costs are null");

            MercadoPago.startInstallmentsActivity(this.mActivity, this.mPayerCosts);
        }

        public void startIssuersActivity() {

            if (this.mActivity == null) throw new IllegalStateException("activity is null");
            if (this.mKey == null) throw new IllegalStateException("key is null");
            if (this.mKeyType == null) throw new IllegalStateException("key type is null");
            if (this.mPaymentMethod == null) throw new IllegalStateException("payment method is null");

            if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
                MercadoPago.startIssuersActivity(this.mActivity,
                        this.mKey, this.mPaymentMethod);
            } else {
                throw new RuntimeException("Unsupported key type for this method");
            }
        }

        public void startNewCardActivity() {

            if (this.mActivity == null) throw new IllegalStateException("activity is null");
            if (this.mKey == null) throw new IllegalStateException("key is null");
            if (this.mKeyType == null) throw new IllegalStateException("key type is null");
            if (this.mPaymentMethod == null) throw new IllegalStateException("payment method is null");

            MercadoPago.startNewCardActivity(this.mActivity, this.mKeyType, this.mKey,
                    this.mPaymentMethod, this.mRequireSecurityCode);
        }

        public void startPaymentMethodsActivity() {

            if (this.mActivity == null) throw new IllegalStateException("activity is null");
            if (this.mKey == null) throw new IllegalStateException("key is null");
            if (this.mKeyType == null) throw new IllegalStateException("key type is null");

            if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
                MercadoPago.startPaymentMethodsActivity(this.mActivity, this.mKey,
                        this.mSupportedPaymentTypes, this.mShowBankDeals);
            } else {
                throw new RuntimeException("Unsupported key type for this method");
            }
        }

        public void startVaultActivity() {

            if (this.mActivity == null) throw new IllegalStateException("activity is null");
            if (this.mAmount == null) throw new IllegalStateException("amount is null");
            if (this.mKey == null) throw new IllegalStateException("key is null");
            if (this.mKeyType == null) throw new IllegalStateException("key type is null");

            if (this.mKeyType.equals(KEY_TYPE_PUBLIC)) {
                MercadoPago.startVaultActivity(this.mActivity, this.mKey, this.mMerchantBaseUrl,
                        this.mMerchantGetCustomerUri, this.mMerchantAccessToken,
                        this.mAmount, this.mSupportedPaymentTypes, this.mShowBankDeals);
            } else {
                throw new RuntimeException("Unsupported key type for this method");
            }
        }
    }
}