package com.company.order.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.api.DisplayName;

class PricingServiceTest {

    @ParameterizedTest(name = "Total={0}, Premium={1}, Festival={2} => Discount={3}%")
    @CsvFileSource(resources = "/discount-rules.csv", numLinesToSkip = 1)
    @DisplayName("Discount percentage should be calculated based on pricing rules")
    void shouldCalculateCorrectDiscountPercentage(
            double totalAmount,
            boolean isPremiumCustomer,
            boolean isFestivalOfferEnabled,
            int expectedDiscountPercent) {

        // Arrange
        PricingService pricingService = new PricingService();

        // Act
        int actualDiscountPercent =
                pricingService.calculateDiscountPercentage(
                        totalAmount,
                        isPremiumCustomer,
                        isFestivalOfferEnabled);

        // Assert
        assertEquals(
                expectedDiscountPercent,
                actualDiscountPercent,
                "Discount percentage calculation is incorrect");
    }
    
    
    @ParameterizedTest(name = "Total={0}, Premium={1}, Festival={2} => GST={3}")
    @CsvFileSource(resources = "/gst-rules.csv", numLinesToSkip = 1)
    @DisplayName("GST should be calculated correctly after discount")
    void shouldCalculateCorrectGST(
            double totalAmount,
            boolean isPremiumCustomer,
            boolean isFestivalOfferEnabled,
            double expectedGST) {

        // Arrange
        PricingService pricingService = new PricingService();

        // Act
        double actualGST = pricingService.calculateGST(totalAmount, isPremiumCustomer, isFestivalOfferEnabled);

        // Assert
        assertEquals(expectedGST, actualGST, 0.01, "GST calculation is incorrect after discount");
    }


}
