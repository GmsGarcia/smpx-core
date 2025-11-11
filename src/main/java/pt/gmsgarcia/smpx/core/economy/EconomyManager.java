package pt.gmsgarcia.smpx.core.economy;

import net.milkbowl.vault2.economy.EconomyResponse;
import pt.gmsgarcia.smpx.core.SmpxCore;
import pt.gmsgarcia.smpx.core.user.User;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public class EconomyManager {
    public boolean isEnabled() {
        return SmpxCore.instance().isEnabled();
    }

    public String getName() {
        return SmpxCore.instance().getName();
    }

    public int fractionalDigits() {
        return 0;
    }

    public String format(BigDecimal amount) {
        return "$" + amount;
    }

    public String currency() {
        return "dollar";
    }

    public String currencyNamePlural() {
        return "Dollars";
    }

    public String currencyNameSingular() {
        return "Dollar";
    }

    @SuppressWarnings("deprecation")
    public net.milkbowl.vault.economy.EconomyResponse withdrawPlayer(UUID uuid, double amount) {
        User user = SmpxCore.users().get(uuid);
        if (user == null) {
            return new net.milkbowl.vault.economy.EconomyResponse(amount, 0.0, net.milkbowl.vault.economy.EconomyResponse.ResponseType.FAILURE, "User does not exist.");
        }

        if (!user.canAfford(BigDecimal.valueOf(amount))) {
            return new net.milkbowl.vault.economy.EconomyResponse(amount, user.balance().doubleValue(), net.milkbowl.vault.economy.EconomyResponse.ResponseType.FAILURE, "User cannot afford this transaction.");
        }

        user.removeBalance(BigDecimal.valueOf(amount));

        return new net.milkbowl.vault.economy.EconomyResponse(amount, user.balance().doubleValue(), net.milkbowl.vault.economy.EconomyResponse.ResponseType.SUCCESS, null);
    }

    public EconomyResponse withdrawPlayerUnlocked(UUID uuid, BigDecimal amount) {
        User user = SmpxCore.users().get(uuid);
        if (user == null) {
            return new EconomyResponse(amount, BigDecimal.ZERO, EconomyResponse.ResponseType.FAILURE, "User does not exist.");
        }

        if (!user.canAfford(amount)) {
            return new EconomyResponse(amount, user.balance(), EconomyResponse.ResponseType.FAILURE, "User cannot afford this transaction.");
        }

        user.removeBalance(amount);

        return new EconomyResponse(amount, user.balance(), EconomyResponse.ResponseType.SUCCESS, "");
    }

    @SuppressWarnings("deprecation")
    public net.milkbowl.vault.economy.EconomyResponse depositPlayer(UUID uuid, double amount) {
        User user = SmpxCore.users().get(uuid);
        if (user == null) {
            return new net.milkbowl.vault.economy.EconomyResponse(amount, 0.0, net.milkbowl.vault.economy.EconomyResponse.ResponseType.FAILURE, "User does not exist.");
        }

        user.addBalance(BigDecimal.valueOf(amount));

        return new net.milkbowl.vault.economy.EconomyResponse(amount, user.balance().doubleValue(), net.milkbowl.vault.economy.EconomyResponse.ResponseType.SUCCESS, null);
    }

    public EconomyResponse depositPlayerUnlocked(UUID uuid, BigDecimal amount) {
        User user = SmpxCore.users().get(uuid);
        if (user == null) {
            return new EconomyResponse(amount, BigDecimal.ZERO, EconomyResponse.ResponseType.FAILURE, "User does not exist.");
        }

        user.addBalance(amount);

        return new EconomyResponse(amount, user.balance(), EconomyResponse.ResponseType.SUCCESS, "");
    }

    public boolean createPlayerAccount(UUID uuid, String name) {
        return this.createPlayerAccount(uuid, name, false);
    }

    public boolean createPlayerAccount(UUID uuid, String name, boolean isPlayer) {
        // TODO: check if player has played before...
        if (SmpxCore.users().exists(uuid)) {
            return false;
        }
        SmpxCore.users().create(uuid, name, isPlayer);
        return true;
    }

    public boolean hasAccount(UUID uuid) {
        return SmpxCore.users().exists(uuid);
    }

    public BigDecimal getBalance(UUID uuid) {
        User user = SmpxCore.users().get(uuid);
        if (user == null) {
            return BigDecimal.ZERO;
        }

        return user.balance();
    }

    public boolean has(UUID uuid, double amount) {
        User user = SmpxCore.users().get(uuid);
        if (user == null) {
            return false;
        }

        return user.canAfford(BigDecimal.valueOf(amount));
    }

    public Optional<String> getAccountName(UUID uuid) {
        User user = SmpxCore.users().get(uuid);
        if (user == null) {
            return Optional.empty();
        }

        return Optional.of(user.name());
    }
}