package com.grokonez.jwtauthentication.pattern.strategy;

import java.math.BigDecimal;

/**
 * Bulk pricing strategy - Applies discount for bulk orders
 * Discount increases with quantity
 */
public class BulkPricingStrategy implements PricingStrategy {

    private static final BigDecimal BULK_DISCOUNT_5_10 = new BigDecimal("0.95"); // 5% discount
    private static final BigDecimal BULK_DISCOUNT_10_PLUS = new BigDecimal("0.90"); // 10% discount

    @Override
    public BigDecimal calculatePrice(BigDecimal basePrice, int quantity) {
        BigDecimal totalPrice = basePrice.multiply(BigDecimal.valueOf(quantity));

        if (quantity >= 10) {
            return totalPrice.multiply(BULK_DISCOUNT_10_PLUS);
        } else if (quantity >= 5) {
            return totalPrice.multiply(BULK_DISCOUNT_5_10);
        }

        return totalPrice;
    }

    @Override
    public String getStrategyName() {
        return "BULK";
    }
}

