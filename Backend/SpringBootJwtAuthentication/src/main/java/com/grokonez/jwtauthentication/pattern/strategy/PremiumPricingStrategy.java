package com.grokonez.jwtauthentication.pattern.strategy;

import java.math.BigDecimal;

/**
 * Premium pricing strategy - Applies premium/surge pricing
 * For VIP customers or high-demand items
 */
public class PremiumPricingStrategy implements PricingStrategy {

    private static final BigDecimal PREMIUM_MULTIPLIER = new BigDecimal("1.15"); // 15% premium

    @Override
    public BigDecimal calculatePrice(BigDecimal basePrice, int quantity) {
        return basePrice.multiply(BigDecimal.valueOf(quantity))
                .multiply(PREMIUM_MULTIPLIER);
    }

    @Override
    public String getStrategyName() {
        return "PREMIUM";
    }
}

