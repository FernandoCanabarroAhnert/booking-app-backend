package com.fernandocanabarro.booking_app_backend.models.enums;

public enum PaymentTypeEnum {

    DINHEIRO(1),
    CARTAO(2),
    PIX(3),
    BOLETO(4);

    private int paymentType;

    private PaymentTypeEnum(int paymentType) {
        this.paymentType = paymentType;
    }

    public int getPaymentType() {
        return paymentType;
    }

    public static PaymentTypeEnum fromValue(int value) {
        for (PaymentTypeEnum type : PaymentTypeEnum.values()) {
            if (type.paymentType == value) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid payment type value: " + value);
    }

}
