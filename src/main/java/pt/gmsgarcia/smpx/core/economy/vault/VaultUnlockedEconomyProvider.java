package pt.gmsgarcia.smpx.core.economy.vault;

import net.milkbowl.vault2.economy.AccountPermission;
import net.milkbowl.vault2.economy.Economy;
import net.milkbowl.vault2.economy.EconomyResponse;
import org.jetbrains.annotations.NotNull;
import pt.gmsgarcia.smpx.core.SmpxCore;

import java.math.BigDecimal;
import java.util.*;

public class VaultUnlockedEconomyProvider implements Economy {
    @Override
    public boolean isEnabled() {
        return SmpxCore.economy().isEnabled();
    }

    @Override
    public @NotNull String getName() {
        return SmpxCore.economy().getName();
    }

    @Override
    public boolean hasSharedAccountSupport() {
        return false;
    }

    @Override
    public boolean hasMultiCurrencySupport() {
        return false;
    }

    @Override
    public int fractionalDigits(@NotNull String pluginName) {
        return SmpxCore.economy().fractionalDigits();
    }

    @Override
    public @NotNull String format(@NotNull BigDecimal amount) {
        return SmpxCore.economy().format(amount);
    }

    @Override
    public @NotNull String format(@NotNull String pluginName, @NotNull BigDecimal amount) {
        return SmpxCore.economy().format(amount);
    }

    @Override
    public @NotNull String format(@NotNull BigDecimal amount, @NotNull String currency) {
        return SmpxCore.economy().format(amount);
    }

    @Override
    public @NotNull String format(@NotNull String pluginName, @NotNull BigDecimal amount, @NotNull String currency) {
        return SmpxCore.economy().format(amount);
    }

    @Override
    public boolean hasCurrency(@NotNull String currency) {
        return currency.equalsIgnoreCase(SmpxCore.economy().currency());
    }

    @Override
    public @NotNull String getDefaultCurrency(@NotNull String pluginName) {
        return SmpxCore.economy().currency();
    }

    @Override
    public @NotNull String defaultCurrencyNamePlural(@NotNull String pluginName) {
        return SmpxCore.economy().currencyNamePlural();
    }

    @Override
    public @NotNull String defaultCurrencyNameSingular(@NotNull String pluginName) {
        return SmpxCore.economy().currencyNameSingular();
    }

    @Override
    public @NotNull Collection<String> currencies() {
        return List.of(SmpxCore.economy().currency());
    }

    @Override
    public boolean createAccount(@NotNull UUID accountID, @NotNull String name) {
        return SmpxCore.economy().createPlayerAccount(accountID, name);
    }

    @Override
    public boolean createAccount(@NotNull UUID accountID, @NotNull String name, boolean player) {
        return SmpxCore.economy().createPlayerAccount(accountID, name);
    }

    @Override
    public boolean createAccount(@NotNull UUID accountID, @NotNull String name, @NotNull String worldName) {
        return SmpxCore.economy().createPlayerAccount(accountID, name);
    }

    @Override
    public boolean createAccount(@NotNull UUID accountID, @NotNull String name, @NotNull String worldName, boolean player) {
        return SmpxCore.economy().createPlayerAccount(accountID, name);
    }

    // TODO: get all accounts's UUID ?
    @Override
    public @NotNull Map<UUID, String> getUUIDNameMap() {
        return Map.of();
    }

    @Override
    public Optional<String> getAccountName(@NotNull UUID accountID) {
        return SmpxCore.economy().getAccountName(accountID);
    }

    @Override
    public boolean hasAccount(@NotNull UUID accountID) {
        return SmpxCore.economy().hasAccount(accountID);
    }

    @Override
    public boolean hasAccount(@NotNull UUID accountID, @NotNull String worldName) {
        return SmpxCore.economy().hasAccount(accountID);
    }

    @Override
    public boolean renameAccount(@NotNull UUID accountID, @NotNull String name) {
        return false;
    }

    @Override
    public boolean renameAccount(@NotNull String plugin, @NotNull UUID accountID, @NotNull String name) {
        return false;
    }

    @Override
    public boolean deleteAccount(@NotNull String plugin, @NotNull UUID accountID) {
        return false;
    }

    @Override
    public boolean accountSupportsCurrency(@NotNull String plugin, @NotNull UUID accountID, @NotNull String currency) {
        return currency.equalsIgnoreCase(SmpxCore.economy().currency());
    }

    @Override
    public boolean accountSupportsCurrency(@NotNull String plugin, @NotNull UUID accountID, @NotNull String currency, @NotNull String world) {
        return currency.equalsIgnoreCase(SmpxCore.economy().currency());
    }

    @Override
    public @NotNull BigDecimal getBalance(@NotNull String pluginName, @NotNull UUID accountID) {
        return SmpxCore.economy().getBalance(accountID);
    }

    @Override
    public @NotNull BigDecimal getBalance(@NotNull String pluginName, @NotNull UUID accountID, @NotNull String world) {
        return SmpxCore.economy().getBalance(accountID);
    }

    @Override
    public @NotNull BigDecimal getBalance(@NotNull String pluginName, @NotNull UUID accountID, @NotNull String world, @NotNull String currency) {
        return SmpxCore.economy().getBalance(accountID);
    }

    @Override
    public boolean has(@NotNull String pluginName, @NotNull UUID accountID, @NotNull BigDecimal amount) {
        return SmpxCore.economy().has(accountID, amount.doubleValue());
    }

    @Override
    public boolean has(@NotNull String pluginName, @NotNull UUID accountID, @NotNull String worldName, @NotNull BigDecimal amount) {
        return SmpxCore.economy().has(accountID, amount.doubleValue());
    }

    @Override
    public boolean has(@NotNull String pluginName, @NotNull UUID accountID, @NotNull String worldName, @NotNull String currency, @NotNull BigDecimal amount) {
        return SmpxCore.economy().has(accountID, amount.doubleValue());
    }

    @Override
    public @NotNull EconomyResponse withdraw(@NotNull String pluginName, @NotNull UUID accountID, @NotNull BigDecimal amount) {
        return SmpxCore.economy().withdrawPlayerUnlocked(accountID, amount);
    }

    @Override
    public @NotNull EconomyResponse withdraw(@NotNull String pluginName, @NotNull UUID accountID, @NotNull String worldName, @NotNull BigDecimal amount) {
        return SmpxCore.economy().withdrawPlayerUnlocked(accountID, amount);
    }

    @Override
    public @NotNull EconomyResponse withdraw(@NotNull String pluginName, @NotNull UUID accountID, @NotNull String worldName, @NotNull String currency, @NotNull BigDecimal amount) {
        return SmpxCore.economy().withdrawPlayerUnlocked(accountID, amount);
    }

    @Override
    public @NotNull EconomyResponse deposit(@NotNull String pluginName, @NotNull UUID accountID, @NotNull BigDecimal amount) {
        return SmpxCore.economy().depositPlayerUnlocked(accountID, amount);
    }

    @Override
    public @NotNull EconomyResponse deposit(@NotNull String pluginName, @NotNull UUID accountID, @NotNull String worldName, @NotNull BigDecimal amount) {
        return SmpxCore.economy().depositPlayerUnlocked(accountID, amount);
    }

    @Override
    public @NotNull EconomyResponse deposit(@NotNull String pluginName, @NotNull UUID accountID, @NotNull String worldName, @NotNull String currency, @NotNull BigDecimal amount) {
        return SmpxCore.economy().depositPlayerUnlocked(accountID, amount);
    }

    // auto-generated methods...
    @Override
    public boolean createSharedAccount(@NotNull String pluginName, @NotNull UUID accountID, @NotNull String name, @NotNull UUID owner) {
        return false;
    }

    @Override
    public boolean isAccountOwner(@NotNull String pluginName, @NotNull UUID accountID, @NotNull UUID uuid) {
        return false;
    }

    @Override
    public boolean setOwner(@NotNull String pluginName, @NotNull UUID accountID, @NotNull UUID uuid) {
        return false;
    }

    @Override
    public boolean isAccountMember(@NotNull String pluginName, @NotNull UUID accountID, @NotNull UUID uuid) {
        return false;
    }

    @Override
    public boolean addAccountMember(@NotNull String pluginName, @NotNull UUID accountID, @NotNull UUID uuid) {
        return false;
    }

    @Override
    public boolean addAccountMember(@NotNull String pluginName, @NotNull UUID accountID, @NotNull UUID uuid, @NotNull AccountPermission... initialPermissions) {
        return false;
    }

    @Override
    public boolean removeAccountMember(@NotNull String pluginName, @NotNull UUID accountID, @NotNull UUID uuid) {
        return false;
    }

    @Override
    public boolean hasAccountPermission(@NotNull String pluginName, @NotNull UUID accountID, @NotNull UUID uuid, @NotNull AccountPermission permission) {
        return false;
    }

    @Override
    public boolean updateAccountPermission(@NotNull String pluginName, @NotNull UUID accountID, @NotNull UUID uuid, @NotNull AccountPermission permission, boolean value) {
        return false;
    }
}