package com.fernandocanabarro.booking_app_backend.factories;

import java.math.BigDecimal;

import com.fernandocanabarro.booking_app_backend.models.dtos.booking.BookingPaymentRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.entities.DinheiroPayment;

public class PaymentFactory {

    public static DinheiroPayment createDinheiroPayment() {
        DinheiroPayment payment = new DinheiroPayment(BigDecimal.valueOf(600),false);
        payment.setId(1L);
        return payment;
    }

    public static BookingPaymentRequestDTO createDinheiroPaymentRequest() {
        BookingPaymentRequestDTO payment = new BookingPaymentRequestDTO();
        payment.setIsOnlinePayment(false);
        payment.setPaymentType(1);
        return payment;
    }

    public static BookingPaymentRequestDTO createOnlineCartaoPaymentRequest() {
        BookingPaymentRequestDTO payment = new BookingPaymentRequestDTO();
        payment.setIsOnlinePayment(true);
        payment.setPaymentType(2);
        payment.setInstallmentQuantity(2);
        payment.setCreditCardId(1L);
        return payment;
    }

    public static BookingPaymentRequestDTO createOfflineCartaoPaymentRequest() {
        BookingPaymentRequestDTO payment = new BookingPaymentRequestDTO();
        payment.setIsOnlinePayment(false);
        payment.setPaymentType(2);
        payment.setInstallmentQuantity(2);
        payment.setCreditCardId(null);
        return payment;
    }

    public static BookingPaymentRequestDTO createPixPaymentRequest() {
        BookingPaymentRequestDTO payment = new BookingPaymentRequestDTO();
        payment.setIsOnlinePayment(true);
        payment.setPaymentType(3);
        return payment;
    }

    public static BookingPaymentRequestDTO createBoletoPaymentRequest() {
        BookingPaymentRequestDTO payment = new BookingPaymentRequestDTO();
        payment.setIsOnlinePayment(false);
        payment.setPaymentType(4);
        return payment;
    }

}
