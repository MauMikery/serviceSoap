package com.equipo3.service.libs;

public class ValidatorData {

  public boolean isNotBlank(String str) {
    return str != null && !str.trim().isEmpty();
  }

  public boolean isPriceValid(Double price) {
    return price != null && price != 0.0;
  }


}
