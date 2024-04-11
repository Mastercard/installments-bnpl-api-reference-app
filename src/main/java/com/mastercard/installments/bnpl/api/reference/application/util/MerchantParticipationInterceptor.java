package com.mastercard.installments.bnpl.api.reference.application.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mastercard.developer.encryption.EncryptionException;
import com.mastercard.developer.interceptors.OkHttpJweInterceptor;
import com.mastercard.installments.bnpl.api.reference.application.configuration.ApiConfiguration;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;
import org.openapitools.client.model.MerchantParticipationsInner;

import java.io.IOException;
import java.util.List;

public class MerchantParticipationInterceptor extends OkHttpJweInterceptor {

  private ApiConfiguration apiConfig;

  public MerchantParticipationInterceptor(ApiConfiguration config) {
    super(config.getJweConfigForPostMP());
    this.apiConfig = config;
  }

  @Override
  public Response intercept(Chain chain) throws IOException {
    Request encryptedRequest = this.handleRequest(chain.request());
    return chain.proceed(encryptedRequest);
  }

  private Request handleRequest(Request request) throws IOException {
    RequestBody requestBody = request.body();
    if (null == requestBody || requestBody.contentLength() == 0)
      return request;

    String requestPayload;
    try (Buffer buffer = new Buffer()) {
      request.body().writeTo(buffer);
      requestPayload = buffer.readUtf8();
    }

    List<MerchantParticipationsInner> list = new Gson().fromJson(requestPayload, new TypeToken<List<MerchantParticipationsInner>>() {}.getType());
    Request.Builder requestBuilder = request.newBuilder();
    list.forEach(mer -> {
      if(apiConfig.getPiiClassifiedAlpha3CountryCode().contains(mer.getCountryCode())){
        // Encrypt fields & update headers
        encryptRequestBody(request, requestBuilder, mer);
      }
    });
    RequestBody encryptedBody = RequestBody.create(new Gson().toJson(list),requestBody.contentType());
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
      mer.setEncryptedValues(encryptedMerchant.getEncryptedValues());
    } catch (EncryptionException e) {
      throw new RuntimeException(e);
    }
  }
}
