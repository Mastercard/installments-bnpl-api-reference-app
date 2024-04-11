package com.mastercard.installments.bnpl.api.reference.application.util;

import lombok.Getter;
import lombok.Setter;
import org.openapitools.client.model.Address;

import java.util.List;

@Getter
@Setter
public class EncryptMerchant {
  private String merchantLegalName;
  private List<String> dbaNames;
  private Address address;
  private String encryptedValues;

  public EncryptMerchant(){

  }

  public EncryptMerchant(String merchantLegalName, List<String> dbaNames, Address address) {
    this.merchantLegalName = merchantLegalName;
    this.dbaNames = dbaNames;
    this.address = address;
  }
}
