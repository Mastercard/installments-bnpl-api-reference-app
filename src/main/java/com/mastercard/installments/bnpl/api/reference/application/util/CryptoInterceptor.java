package com.mastercard.installments.bnpl.api.reference.application.util;
import com.mastercard.developer.encryption.EncryptionConfig;

import com.mastercard.developer.encryption.EncryptionException;
import com.mastercard.developer.interceptors.OkHttpJweInterceptor;
import com.mastercard.developer.utils.StringUtils;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import okio.Buffer;

@Slf4j
public class CryptoInterceptor extends OkHttpJweInterceptor {

    public CryptoInterceptor(EncryptionConfig config) {
        super(config);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request encryptedRequest = this.handleRequest(chain.request());
        Response encryptedResponse = chain.proceed(encryptedRequest);
        try {
            if ((encryptedRequest.url().toString().contains("plans")) || (encryptedRequest.url().toString().contains("merchants/participations"))
                    || (encryptedRequest.url().toString().contains("merchants/mids/searches")) ) {
                return this.handleResponse(encryptedResponse);
            }
        } catch (EncryptionException e) {
            log.error("Failed to intercept and encrypt request!", e);
        }
        return encryptedResponse;
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

            RequestBody encryptedBody = RequestBody.create(encryptedPayload,requestBody.contentType());
            return requestBuilder
                    .method(request.method(), encryptedBody)
                    .header("Content-Length", String.valueOf(encryptedBody.contentLength()))
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