package pt.gmsgarcia.smpx.core.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import pt.gmsgarcia.smpx.core.SmpxCore;

import java.math.BigDecimal;
import java.util.HashMap;

public class EconomyConfig {
    private final boolean allowDebt;
    private final BigDecimal maxDebt;
    private final HashMap<String, CurrencyConfig> currencies;

    public EconomyConfig(YamlConfiguration config) {
        this.allowDebt = config.getBoolean("economy.allow-debt");
        this.maxDebt = BigDecimal.valueOf(config.getInt("economy.max-debt"));

        this.currencies = new HashMap<>();

        ConfigurationSection section = config.getConfigurationSection("economy.currencies.default");
        if (section == null) {
            SmpxCore.logger().severe("Failed to load default currency! Please check your config file!");
            return;
        }

        this.currencies.put("default", new CurrencyConfig(section));
        this.loadCustomCurrencies(config);
    }

    private void loadCustomCurrencies(YamlConfiguration config) {
        ConfigurationSection list = config.getConfigurationSection("economy.currencies.custom");
        if (list != null) {
            for (String key : list.getKeys(false)) {
                ConfigurationSection section = config.getConfigurationSection("economy.currencies.custom." + key);
                if (section == null) {
                    SmpxCore.logger().severe("Failed to load currency: '" + key + "'!");
                    continue;
                }
                this.currencies.put(key, new CurrencyConfig(section));
            }
        }
    }

    public boolean allowDebt() {
        return this.allowDebt;
    }

    public BigDecimal maxDebt() {
        return this.maxDebt;
    }

    public boolean hasMaxDebt() {
        return this.maxDebt.compareTo(BigDecimal.ZERO) > 0;
    }

    public CurrencyConfig currency() {
        return this.currencies.get("default");
    }

    public CurrencyConfig currency(String currency) {
        return this.currencies.get(currency);
    }

    public HashMap<String, CurrencyConfig> currencies() {
        return this.currencies;
    }
}