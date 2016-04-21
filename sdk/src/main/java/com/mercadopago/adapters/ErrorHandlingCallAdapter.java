package com.mercadopago.adapters;

/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.google.gson.reflect.TypeToken;
import com.mercadopago.model.ApiException;
import com.mercadopago.util.ApiUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A sample showing a custom {@link CallAdapter} which adapts the built-in {@link Call} to a custom
 * version whose callback has more granular methods.
 */
public final class ErrorHandlingCallAdapter {

    /** A callback which offers granular callbacks for various conditions. */
    public interface MyCallback<T> {
        /** Called for [200, 300) responses. */
        void success(Response<T> response);
        /** Called for all errors. */
        void failure(ApiException apiException);
    }

    public interface MyCall<T> {
        void cancel();
        void enqueue(MyCallback<T> callback);
        MyCall<T> clone();

        // Left as an exercise for the reader...
        // TODO MyResponse<T> execute() throws MyHttpException;
    }

    public static class ErrorHandlingCallAdapterFactory extends CallAdapter.Factory {

        @Override
        public CallAdapter<MyCall<?>> get(Type returnType, Annotation[] annotations,
                                          Retrofit retrofit) {
            TypeToken<?> token = TypeToken.get(returnType);
            if (token.getRawType() != MyCall.class) {
                return null;
            }
            if (!(returnType instanceof ParameterizedType)) {
                throw new IllegalStateException(
                        "MyCall must have generic type (e.g., MyCall<ResponseBody>)");
            }
            final Type responseType = ((ParameterizedType) returnType).getActualTypeArguments()[0];
            return new CallAdapter<MyCall<?>>() {
                @Override public Type responseType() {
                    return responseType;
                }

                @Override public <R> MyCall<R> adapt(Call<R> call) {
                    return new MyCallAdapter<>(call);
                }
            };
        }
    }

    // This adapter runs its callbacks on the main thread always
    // TODO: customize executor
    /** Adapts a {@link Call} to {@link MyCall}. */
    static class MyCallAdapter<T> implements MyCall<T> {
        private final Call<T> call;

        MyCallAdapter(Call<T> call) {
            this.call = call;
        }

        @Override public void cancel() {
            call.cancel();
        }

        @Override public void enqueue(final MyCallback<T> callback) {
            call.enqueue(new Callback<T>() {
                @Override
                public void onResponse(Call<T> call, Response<T> response) {

                    final Response<T> r = response;
                    executeOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            int code = r.code();
                            if (code >= 200 && code < 300) {
                                callback.success(r);
                            } else {
                                callback.failure(ApiUtil.getApiException(r));
                            }
                        }
                    });
                }

                @Override
                public void onFailure(Call<T> call, Throwable t) {

                    final Throwable th  = t;
                    executeOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            callback.failure(ApiUtil.getApiException(th));
                        }
                    });
                }
            });
        }

        @Override public MyCall<T> clone() {
            return new MyCallAdapter<>(call.clone());
        }
    }

    private static void executeOnMainThread(@NonNull Runnable r) {

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.post(r);
    }
}

