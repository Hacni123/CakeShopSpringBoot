package com.grokonez.jwtauthentication.service;

import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.grokonez.jwtauthentication.pattern.strategy.*;

/**
 * Pricing Service
 * Uses Strategy Design Pattern to calculate prices based on different strategies
 * Demonstrates polymorphism and runtime strategy selection
 */
@Service
public class PricingService {

    private static final Logger logger = LoggerFactory.getLogger(PricingService.class);

    private PricingStrategy regularStrategy = new RegularPricingStrategy();
    private PricingStrategy bulkStrategy = new BulkPricingStrategy();
    private PricingStrategy premiumStrategy = new PremiumPricingStrategy();

    /**
     * Calculate price based on strategy type
     * Demonstrates strategy pattern with runtime selection
     *
     * @param basePrice Base price of item
     * @param quantity Quantity of items
     * @param strategyType Type of pricing strategy (REGULAR, BULK, PREMIUM)
     * @return Calculated price
     */
    public BigDecimal calculatePrice(BigDecimal basePrice, int quantity, String strategyType) {
        logger.info("Calculating price with strategy: {} for quantity: {}", strategyType, quantity);

        PricingStrategy strategy = selectStrategy(strategyType);
        BigDecimal finalPrice = strategy.calculatePrice(basePrice, quantity);

        logger.info("Final price calculated: {} using strategy: {}", finalPrice, strategy.getStrategyName());
        return finalPrice;
    }

    /**
     * Select appropriate strategy based on type
     * Demonstrates strategy selection logic
     */
    private PricingStrategy selectStrategy(String strategyType) {
        switch (strategyType.toUpperCase()) {
            case "BULK":
                return bulkStrategy;
            case "PREMIUM":
                return premiumStrategy;
            case "REGULAR":
            default:
                return regularStrategy;
        }
    }

    /**
     * Calculate with auto-selection based on quantity
     * Low quantities use regular pricing
     * 5-10 quantities use bulk pricing
     * > 10 quantities use bulk pricing with higher discount
     */
    public BigDecimal calculatePriceAuto(BigDecimal basePrice, int quantity) {
        logger.info("Auto-calculating price for quantity: {}", quantity);

        String strategyType;
        if (quantity >= 5) {
            strategyType = "BULK";
        } else {
            strategyType = "REGULAR";
        }

        return calculatePrice(basePrice, quantity, strategyType);
    }

    /**
     * Calculate discounted price for VIP customers
     */
    public BigDecimal calculateVIPPrice(BigDecimal basePrice, int quantity) {
        logger.info("Calculating VIP price for quantity: {}", quantity);
        return calculatePrice(basePrice, quantity, "PREMIUM");
    }
}

