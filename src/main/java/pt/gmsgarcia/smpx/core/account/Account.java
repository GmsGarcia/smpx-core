package pt.gmsgarcia.smpx.core.account;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import pt.gmsgarcia.smpx.core.SmpxCore;
import pt.gmsgarcia.smpx.core.config.CurrencyConfig;
import pt.gmsgarcia.smpx.core.config.EconomyConfig;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * This class represents an account. It contains all the
 * information that is stored for a player or a system account,
 * such as their UUID, name, balance, joinDate, lastSeen, and previousNames.
 */
@SerializableAs("Account")
public class Account implements ConfigurationSerializable {
    private final UUID uuid;
    private final String name;
    private final String currency;
    private BigDecimal balance;

    private final EconomyConfig econ = SmpxCore.config().economy();
    private final CurrencyConfig config;

    public Account(UUID uuid, String name, String currency, BigDecimal balance) {
        this.uuid = uuid;
        this.name = name;
        this.currency = currency;
        this.balance = balance;
        this.config = SmpxCore.config().economy().currency(currency);
    }

    /**
     * Returns the account {@link UUID}.
     */
    public UUID uuid() {
        return this.uuid;
    }

    /**
     * Returns the account name.
     */
    public String name() {
        return this.name;
    }

    /**
     * Returns the account currency.
     */
    public String currency() {
        return this.currency;
    }

    /**
     * Returns the account balance.
     */
    public BigDecimal balance() {
        return this.balance;
    }

    /**
     * Checks if this account can afford a transaction with the given amount.
     */
    public boolean canAfford(BigDecimal amount) {
        BigDecimal newBalance = this.balance.subtract(amount);

        // check if the newBalance is valid (positive)
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            if (!"default".equals(this.currency)) return false;

            if (!econ.allowDebt()) return false;

            // check if the newBalance is lower than the maxDebt limit
            if (econ.hasMaxDebt()) {
                BigDecimal maxDebt = econ.maxDebt();
                return newBalance.compareTo(maxDebt) >= 0;
            }
        }

        return true;
    }

    /**
     * Sets the account balance to the given amount.
     */
    public void setBalance(BigDecimal amount) {
        this.balance = amount;
    }

    /**
     * Deposits the given amount.
     */
    public void deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) return; // invalid amount...

        BigDecimal newBalance = this.balance.add(amount);

        // check if the newBalance is higher than the maxBalance limit
        // and snap back to the maxBalance limit if the newBalance is higher than that...
        if (config.hasMaxBalance()) {
            BigDecimal maxBalance = config.maxBalance();
            if (newBalance.compareTo(maxBalance) > 0) {
                newBalance = maxBalance;
            }
        }

        this.balance = newBalance;
    }

    /**
     * Withdraws the given amount.
     */
    public void withdraw(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) return; // invalid amount...

        // check for minimum transaction amount
        if (config.hasMinTransactionAmount()) {
            BigDecimal minAmount = config.minTransactionAmount();
            if (amount.compareTo(minAmount) < 0) return;
        }

        if (!this.canAfford(amount)) {
            return;
        }

        this.balance = this.balance.subtract(amount);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("uuid", uuid.toString());
        map.put("name", name);
        map.put("currency", currency);
        map.put("balance", balance.toString());
        return map;
    }

    public static Account deserialize(Map<String, Object> map) {
        UUID uuid = UUID.fromString((String) map.get("uuid"));
        String name = (String) map.get("name");
        String currency = (String) map.get("currency");
        BigDecimal balance = new BigDecimal((String) map.get("balance"));
        return new Account(uuid, name, currency, balance);
    }
}