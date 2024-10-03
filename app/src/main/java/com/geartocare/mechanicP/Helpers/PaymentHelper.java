package com.geartocare.mechanicP.Helpers;

public class PaymentHelper {

    String price,paymentType,paymentStatus;


    public PaymentHelper(String price, String paymentType, String paymentStatus) {
        this.price = price;
        this.paymentType = paymentType;
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
