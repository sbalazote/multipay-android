package com.mercadopago.util;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.converter.gson.GsonConverterFactory;

public class JsonUtil {

    private static JsonUtil mInstance = null;
    private Gson mGson;

    protected JsonUtil() {
        mGson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).serializeNulls().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
    }

    public static JsonUtil getInstance() {
        if (mInstance == null) {
            mInstance = new JsonUtil();
        }
        return mInstance;
    }

    public <T> T fromJson(String json, Class<T> classOfT) {

        return mGson.fromJson(json, classOfT);
    }

    public String toJson(Object src) {

        return mGson.toJson(src);
    }

    public Gson getGson() {

        return mGson;
    }

    public GsonConverterFactory getGsonConverterFactory() {

        return GsonConverterFactory.create(mGson);
    }
}
