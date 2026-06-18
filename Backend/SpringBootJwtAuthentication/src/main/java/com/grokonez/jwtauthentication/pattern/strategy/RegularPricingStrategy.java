package com.grokonez.jwtauthentication.pattern.strategy;

import java.math.BigDecimal;

/**
 * Regular pricing strategy - No discount
 */
public class RegularPricingStrategy implements PricingStrategy {

    @Override
    public BigDecimal calculatePrice(BigDecimal basePrice, int quantity) {
        return basePrice.multiply(BigDecimal.valueOf(quantity));
    }

    @Override
    public String getStrategyName() {
        return "REGULAR";
    }
}

