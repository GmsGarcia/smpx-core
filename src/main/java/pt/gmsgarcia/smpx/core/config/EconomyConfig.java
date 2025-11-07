package pt.gmsgarcia.smpx.core.config;

import org.bukkit.configuration.file.YamlConfiguration;

import java.math.BigDecimal;

public class EconomyConfig {
    private final BigDecimal initialBalance;
    private final BigDecimal maxBalance;

    public EconomyConfig(YamlConfiguration config) {
        this.initialBalance = BigDecimal.valueOf(config.getInt("economy.initial-balance"));
        this.maxBalance = BigDecimal.valueOf(config.getInt("economy.max-balance"));
    }

    public BigDecimal initialBalance() {
        return this.initialBalance;
    }

    public BigDecimal maxBalance() {
        return this.maxBalance;
    }
}