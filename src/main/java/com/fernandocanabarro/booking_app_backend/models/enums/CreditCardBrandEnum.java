package com.fernandocanabarro.booking_app_backend.models.enums;

public enum CreditCardBrandEnum {

    VISA(1),
    MASTERCARD(2);

    private Integer value;

    private CreditCardBrandEnum(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    public static CreditCardBrandEnum fromValue(int value) {
        for (CreditCardBrandEnum creditCardBrand : CreditCardBrandEnum.values()) {
            if (creditCardBrand.getValue() == value) {
                return creditCardBrand;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }

}
