package pt.gmsgarcia.smpx.core.economy;

import net.milkbowl.vault2.economy.EconomyResponse;
import pt.gmsgarcia.smpx.core.SmpxCore;
import pt.gmsgarcia.smpx.core.account.Account;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class EconomyBridge {
    public boolean isEnabled() {
        return SmpxCore.instance().isEnabled();
    }

    public String getName() {
        return SmpxCore.instance().getName();
    }

    // TODO: return according to the currency specified
    public int fractionalDigits() {
        return 0;
    }

    // TODO: format according to the currency specified
    public String format(BigDecimal amount) {
        return "$" + amount;
    }

    public String currency(String currency) {
        return SmpxCore.config().economy().currency(currency).id();
    }

    public ArrayList<String> currencies() {
        return new ArrayList<>(SmpxCore.config().economy().currencies().keySet());
    }

    public String currencyNamePlural(String currency) {
        return SmpxCore.config().economy().currency(currency).namePlural();
    }

    public String currencyNameSingular(String currency) {
        return SmpxCore.config().economy().currency(currency).nameSingular();
    }

    public boolean hasCurrency(String currency) {
        return SmpxCore.config().economy().currencies().containsKey(currency);
    }

    public String getDefaultCurrency(String currency) {
        return "default";
    }

    @SuppressWarnings("deprecation")
    public net.milkbowl.vault.economy.EconomyResponse withdrawAccount(UUID uuid, double amount) {
        HashMap<String, Account> accounts = SmpxCore.accounts().get(uuid);
        if (accounts == null) {
            return new net.milkbowl.vault.economy.EconomyResponse(amount, 0, net.milkbowl.vault.economy.EconomyResponse.ResponseType.FAILURE, "Accounts do not exist.");
        }

        Account acc = accounts.get("default");
        if (acc == null) {
            return new net.milkbowl.vault.economy.EconomyResponse(amount, 0, net.milkbowl.vault.economy.EconomyResponse.ResponseType.FAILURE, "Account does not exist.");
        }

        if (!acc.canAfford(BigDecimal.valueOf(amount))) {
            return new net.milkbowl.vault.economy.EconomyResponse(amount, acc.balance().doubleValue(), net.milkbowl.vault.economy.EconomyResponse.ResponseType.FAILURE, "This account cannot afford this transaction.");
        }

        acc.withdraw(BigDecimal.valueOf(amount));
        return new net.milkbowl.vault.economy.EconomyResponse(amount, acc.balance().doubleValue(), net.milkbowl.vault.economy.EconomyResponse.ResponseType.SUCCESS, "");
    }

    public EconomyResponse withdrawAccountUnlocked(UUID uuid, BigDecimal amount) {
        return this.withdrawAccountUnlocked(uuid, "default", amount);
    }

    public EconomyResponse withdrawAccountUnlocked(UUID uuid, String currency, BigDecimal amount) {
        HashMap<String, Account> accounts = SmpxCore.accounts().get(uuid);
        if (accounts == null) {
            return new EconomyResponse(amount, BigDecimal.ZERO, EconomyResponse.ResponseType.FAILURE, "Accounts do not exist.");
        }

        Account acc = accounts.get("default");
        if (acc == null) {
            return new EconomyResponse(amount, BigDecimal.ZERO, EconomyResponse.ResponseType.FAILURE, "Account does not exist.");
        }

        if (!acc.canAfford(amount)) {
            return new EconomyResponse(amount, acc.balance(), EconomyResponse.ResponseType.FAILURE, "This account cannot afford this transaction.");
        }

        acc.withdraw(amount);
        return new EconomyResponse(amount, acc.balance(), EconomyResponse.ResponseType.SUCCESS, "");
    }

    @SuppressWarnings("deprecation")
    public net.milkbowl.vault.economy.EconomyResponse depositAccount(UUID uuid, double amount) {
        HashMap<String, Account> accounts = SmpxCore.accounts().get(uuid);
        if (accounts == null) {
            return new net.milkbowl.vault.economy.EconomyResponse(amount, 0, net.milkbowl.vault.economy.EconomyResponse.ResponseType.FAILURE, "Accounts dos not exist.");
        }

        Account acc = accounts.get("default");
        if (acc == null) {
            return new net.milkbowl.vault.economy.EconomyResponse(amount, 0, net.milkbowl.vault.economy.EconomyResponse.ResponseType.FAILURE, "Account does not exist.");
        }

        acc.deposit(BigDecimal.valueOf(amount));
        return new net.milkbowl.vault.economy.EconomyResponse(amount, acc.balance().doubleValue(), net.milkbowl.vault.economy.EconomyResponse.ResponseType.SUCCESS, "");
    }

    public EconomyResponse depositAccountUnlocked(UUID uuid, BigDecimal amount) {
        return this.depositAccountUnlocked(uuid, "default", amount);
    }

    public EconomyResponse depositAccountUnlocked(UUID uuid, String currency, BigDecimal amount) {
        HashMap<String, Account> accounts = SmpxCore.accounts().get(uuid);
        if (accounts == null) {
            return new EconomyResponse(amount, BigDecimal.ZERO, EconomyResponse.ResponseType.FAILURE, "Accounts dos not exist.");
        }

        Account acc = accounts.get("default");
        if (acc == null) {
            return new EconomyResponse(amount, BigDecimal.ZERO, EconomyResponse.ResponseType.FAILURE, "Account does not exist.");
        }

        acc.deposit(amount);
        return new EconomyResponse(amount, acc.balance(), EconomyResponse.ResponseType.SUCCESS, "");
    }

    public boolean createAccount(UUID uuid, String name) {
        return this.createAccount(uuid, name, "default");
    }

    /**
     * By default, this method creates accounts for every registered currency.
     * So it will return false (without creating accounts) if a user has
     * an account for the currency X even though it does not have an account for the currency specified
     * <br>
     * <br>
     * <b>TODO</b>: implement a create method for single accounts (assign a new account for a "user" that already has some accounts...)
     */
    public boolean createAccount(UUID uuid, String name, String currency) {
        HashMap<String, Account> accounts = SmpxCore.accounts().get(uuid);
        if (accounts != null) {
            return false;
        }

        SmpxCore.accounts().create(uuid, name);

        return true;
    }

    public boolean hasAccount(UUID uuid) {
        return this.hasAccount(uuid, "default");
    }

    public boolean hasAccount(UUID uuid, String currency) {
        HashMap<String, Account> accounts = SmpxCore.accounts().get(uuid);
        if (accounts == null) return false;

        Account acc = SmpxCore.accounts().get(uuid).get(currency);
        return acc != null;
    }

    public BigDecimal getBalance(UUID uuid) {
        return this.getBalance(uuid, "default");
    }

    public BigDecimal getBalance(UUID uuid, String currency) {
        HashMap<String, Account> accounts = SmpxCore.accounts().get(uuid);
        if (accounts == null) return BigDecimal.ZERO;

        Account acc = SmpxCore.accounts().get(uuid).get(currency);
        if (acc == null) {
            return BigDecimal.ZERO;
        }

        return acc.balance();
    }

    public boolean has(UUID uuid, double amount) {
        return this.has(uuid, "default", amount);
    }

    public boolean has(UUID uuid, String currency, double amount) {
        HashMap<String, Account> accounts = SmpxCore.accounts().get(uuid);
        if (accounts == null) return false;

        Account acc = SmpxCore.accounts().get(uuid).get(currency);
        if (acc == null) {
            return false;
        }

        return acc.canAfford(BigDecimal.valueOf(amount));
    }

    public Optional<String> getAccountName(UUID uuid) {
        HashMap<String, Account> accounts = SmpxCore.accounts().get(uuid);
        if (accounts == null) {
            return Optional.empty();
        }

        Account acc = SmpxCore.accounts().get(uuid).get("default");
        return Optional.of(acc.name());
    }
}