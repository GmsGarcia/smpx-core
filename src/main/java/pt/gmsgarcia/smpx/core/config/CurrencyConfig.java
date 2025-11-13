package pt.gmsgarcia.smpx.core.config;

import org.bukkit.configuration.ConfigurationSection;

import java.math.BigDecimal;

public class CurrencyConfig {
    private final String id;
    private final String namePlural;
    private final String nameSingular;
    private final String symbol;
    private final BigDecimal initialBalance;
    private final BigDecimal maxBalance;
    private final boolean integer;
    private final BigDecimal minTransactionAmount;

    public CurrencyConfig(ConfigurationSection section) {
        this.id = section.getString("id", null);
        this.namePlural = section.getString("name-plural", "undefined");
        this.nameSingular = section.getString("name-singular", "undefined");
        this.symbol = section.getString("symbol", "$");
        this.initialBalance = BigDecimal.valueOf(section.getDouble("initial-balance", 0));
        this.maxBalance = BigDecimal.valueOf(section.getDouble("max-balance", 0));
        this.integer = section.getBoolean("integer", false);
        this.minTransactionAmount = BigDecimal.valueOf(section.getDouble("min-transaction-amount", 0));
    }

    public String id() {
        return this.id;
    }

    public String namePlural() {
        return this.namePlural;
    }

    public String nameSingular() {
        return this.nameSingular;
    }

    public String symbol() {
        return this.symbol;
    }

    public BigDecimal initialBalance() {
        return this.initialBalance;
    }

    public BigDecimal maxBalance() {
        return this.maxBalance;
    }

    public boolean hasMaxBalance() {
        return this.maxBalance.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean integer() {
        return this.integer;
    }

    public BigDecimal minTransactionAmount() {
        return this.minTransactionAmount;
    }

    public boolean hasMinTransactionAmount() {
        return this.minTransactionAmount.compareTo(BigDecimal.ZERO) > 0;
    }
}