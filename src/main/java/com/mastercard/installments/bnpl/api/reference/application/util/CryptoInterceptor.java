package com.mastercard.installments.bnpl.api.reference.application.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mastercard.developer.encryption.EncryptionConfig;
import com.mastercard.developer.encryption.EncryptionException;
import com.mastercard.developer.interceptors.OkHttpJweInterceptor;
import com.mastercard.developer.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import org.jetbrains.annotations.NotNull;
import org.openapitools.client.model.MerchantParticipationsInner;
import org.openapitools.client.model.PostMerchantMidSearchesRequest;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Slf4j
public class CryptoInterceptor extends OkHttpJweInterceptor {

    List<String> piiClassifiedAlpha3CountryCodes;

    public CryptoInterceptor(EncryptionConfig config) {
        super(config);
    }

    public CryptoInterceptor(EncryptionConfig config, List<String> piiClassifiedAlpha3CountryCodes) {
        super(config);
        this.piiClassifiedAlpha3CountryCodes = piiClassifiedAlpha3CountryCodes;
    }

    @NotNull
    private static String getRequestPayload(RequestBody requestBody) throws IOException {
        String requestPayload;
        try (Buffer buffer = new Buffer()) {
            requestBody.writeTo(buffer);
            requestPayload = buffer.readUtf8();
        }
        return requestPayload;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request requestChain = chain.request();
        Request encryptedRequest;

        if ((requestChain.url().toString().contains("merchants/mids/searches")) && (requestChain.method().contains("POST"))) {
            encryptedRequest = this.handleMidSearchesRequest(requestChain);
        } else if ((requestChain.url().toString().contains("merchants/participations")) && (requestChain.method().contains("POST"))) {
            encryptedRequest = this.handlePostMerchantParticipationsRequest(requestChain);
        } else {
            encryptedRequest = this.handleRequest(requestChain);
        }

        Response encryptedResponse = chain.proceed(encryptedRequest);
        try {
            if (encryptedRequest.url().toString().contains("plans")
                    || ((encryptedRequest.url().toString().contains("merchants/participations")) && (encryptedRequest.method().contains("GET")))
                    || (encryptedRequest.url().toString().contains("merchants/mids/searches"))) {
                return this.handleResponse(encryptedResponse);
            }
        } catch (EncryptionException e) {
            log.error("Failed to intercept and encrypt request!", e);
        }
        return encryptedResponse;
    }

    private Request handleMidSearchesRequest(Request request) throws IOException {
        try {
            String requestPayload = getRequestPayload(Objects.requireNonNull(request.body()));

            PostMerchantMidSearchesRequest postMidSearches = new Gson().fromJson(requestPayload, PostMerchantMidSearchesRequest.class);
            Request.Builder requestBuilder = request.newBuilder();
            if (piiClassifiedAlpha3CountryCodes.contains(postMidSearches.getCountryCode())) {
                // Encrypt fields & update headers
                String encryptedPayload = encryptPayload(request, requestBuilder, requestPayload);

                RequestBody encryptedBody = RequestBody.create(encryptedPayload, Objects.requireNonNull(request.body()).contentType());
                return requestBuilder
                        .method(request.method(), encryptedBody)
                        .header("Content-Length", String.valueOf(encryptedBody.contentLength()))
                        .build();

            } else {
                return requestBuilder
                        .method(request.method(), request.body())
                        .header("Content-Length", String.valueOf(Objects.requireNonNull(request.body()).contentLength()))
                        .build();
            }
        } catch (EncryptionException e) {
            throw new IOException("Failed to intercept and encrypt request!", e);
        }

    }

    private Request handlePostMerchantParticipationsRequest(Request request) throws IOException {

        String requestPayload = getRequestPayload(Objects.requireNonNull(request.body()));

        List<MerchantParticipationsInner> list = new Gson().fromJson(requestPayload, new TypeToken<List<MerchantParticipationsInner>>() {
        }.getType());
        Request.Builder requestBuilder = request.newBuilder();
        list.forEach(mer -> {
            if (piiClassifiedAlpha3CountryCodes.contains(mer.getCountryCode())) {
                // Encrypt fields & update headers
                encryptRequestBody(request, requestBuilder, mer);
            }
        });
        RequestBody encryptedBody = RequestBody.create(new Gson().toJson(list), Objects.requireNonNull(request.body()).contentType());
        return requestBuilder
                .method(request.method(), encryptedBody)
                .header("Content-Length", String.valueOf(encryptedBody.contentLength()))
                .build();

    }

    private void encryptRequestBody(Request request, Request.Builder requestBuilder, MerchantParticipationsInner mer) {
        try {
            EncryptMerchant encryptMerchant = new EncryptMerchant(mer.getMerchantLegalName(), mer.getDbaNames(), mer.getAddress());
            String encryptedPayload = encryptPayload(request, requestBuilder, new Gson().toJson(encryptMerchant));
            EncryptMerchant encryptedMerchant = new Gson().fromJson(encryptedPayload, EncryptMerchant.class);
            mer.setMerchantLegalName(null);
            mer.setDbaNames(null);
            mer.setAddress(null);
            mer.setEncryptedValues(encryptedMerchant.getEncryptedMerchantLegalName());
        } catch (EncryptionException e) {
            throw new RuntimeException(e);
        }
    }


    private Request handleRequest(Request request) throws IOException {
        try {
            RequestBody requestBody = request.body();
            if (null == requestBody || requestBody.contentLength() == 0) {
                return request;
            }

            String requestPayload;
            try (Buffer buffer = new Buffer()) {
                request.body().writeTo(buffer);
                requestPayload = buffer.readUtf8();
            }

            // Encrypt fields & update headers
            Request.Builder requestBuilder = request.newBuilder();
            String encryptedPayload = encryptPayload(request, requestBuilder, requestPayload);

            RequestBody encryptedBody = RequestBody.create(encryptedPayload, request.body().contentType());
            return requestBuilder
                    .method(request.method(), encryptedBody)
                    .header("Content-Length", String.valueOf(encryptedBody.contentLength()))
                    .header("Accept", MediaType.APPLICATION_JSON_VALUE)
                    .build();

        } catch (EncryptionException e) {
            throw new IOException("Failed to intercept and encrypt request!", e);
        }
    }

    private Response handleResponse(Response response) throws IOException, EncryptionException {
        try {
            ResponseBody responseBody = response.body();
            if (null == responseBody) {
                return response;
            } else {
                String responsePayload = responseBody.string();
                if (StringUtils.isNullOrEmpty(responsePayload)) {
                    return response;
                } else {
                    Response.Builder responseBuilder = response.newBuilder();
                    String decryptedPayload = this.decryptPayload(response, responseBuilder, responsePayload);
                    ResponseBody decryptedBody = ResponseBody.create(decryptedPayload, responseBody.contentType());
                    Response decryptedResponse;
                    decryptedResponse = responseBuilder.body(decryptedBody).header("Content-Length", String.valueOf(decryptedBody.contentLength())).build();
                    decryptedBody.close();
                    return decryptedResponse;
                }
            }
        } catch (EncryptionException e) {
            throw new IOException("Failed to intercept and decrypt response!", e);
        }
    }
}