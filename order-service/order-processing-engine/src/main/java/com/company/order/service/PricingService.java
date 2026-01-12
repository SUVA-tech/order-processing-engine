package com.company.order.service;

public class PricingService {

    private static final int MAX_DISCOUNT_PERCENT = 25;

    public int calculateDiscountPercentage(double totalAmount, boolean isPremiumCustomer, boolean isFestivalOfferEnabled) {
        int discountPercent = 0;

        if (totalAmount >= 25_000) {
            discountPercent = 10;
        } else if (totalAmount >= 10_000) {
            discountPercent = 5;
        }

        if (isPremiumCustomer) {
            discountPercent += 5;
        }

        if (isFestivalOfferEnabled) {
            discountPercent += 5;
        }

        discountPercent = Math.min(discountPercent, MAX_DISCOUNT_PERCENT);

        return discountPercent;
    }

    // GREEN: Implement GST calculation
    private double roundToTwoDecimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    public double calculateGST(double totalAmount, boolean isPremiumCustomer, boolean isFestivalOfferEnabled) {
        int discountPercent = calculateDiscountPercentage(totalAmount, isPremiumCustomer, isFestivalOfferEnabled);
        double discountedAmount = totalAmount * (1 - discountPercent / 100.0);
        double gst = discountedAmount * 0.18;
        return roundToTwoDecimals(gst);
    }

}