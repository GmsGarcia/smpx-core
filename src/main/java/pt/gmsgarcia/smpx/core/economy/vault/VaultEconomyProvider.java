package pt.gmsgarcia.smpx.core.economy.vault;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import pt.gmsgarcia.smpx.core.SmpxCore;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class VaultEconomyProvider implements Economy {
    @Override
    public boolean isEnabled() {
        return SmpxCore.economy().isEnabled();
    }

    @Override
    public String getName() {
        return SmpxCore.economy().getName();
    }

    @Override
    public int fractionalDigits() {
        return SmpxCore.economy().fractionalDigits();
    }

    @Override
    public String format(double amount) {
        return SmpxCore.economy().format(BigDecimal.valueOf(amount));
    }

    @Override
    public String currencyNamePlural() {
        return SmpxCore.economy().currencyNamePlural();
    }

    @Override
    public String currencyNameSingular() {
        return SmpxCore.economy().currencyNameSingular();
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean hasAccount(String playerName) {
        UUID uuid = Bukkit.getPlayerUniqueId(playerName);
        return SmpxCore.economy().hasAccount(uuid);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        UUID uuid = player.getUniqueId();
        return SmpxCore.economy().hasAccount(uuid);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean hasAccount(String playerName, String worldName) {
        UUID uuid = Bukkit.getPlayerUniqueId(playerName);
        return SmpxCore.economy().hasAccount(uuid);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        UUID uuid = player.getUniqueId();
        return SmpxCore.economy().hasAccount(uuid);
    }

    @SuppressWarnings("deprecation")
    @Override
    public double getBalance(String playerName) {
        UUID uuid = Bukkit.getPlayerUniqueId(playerName);
        return SmpxCore.economy().getBalance(uuid).doubleValue();
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        UUID uuid = player.getUniqueId();
        return SmpxCore.economy().getBalance(uuid).doubleValue();
    }

    @SuppressWarnings("deprecation")
    @Override
    public double getBalance(String playerName, String world) {
        UUID uuid = Bukkit.getPlayerUniqueId(playerName);
        return SmpxCore.economy().getBalance(uuid).doubleValue();
    }

    @Override
    public double getBalance(OfflinePlayer player, String world) {
        UUID uuid = player.getUniqueId();
        return SmpxCore.economy().getBalance(uuid).doubleValue();
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean has(String playerName, double amount) {
        UUID uuid = Bukkit.getPlayerUniqueId(playerName);
        return SmpxCore.economy().has(uuid, amount);
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        UUID uuid = player.getUniqueId();
        return SmpxCore.economy().has(uuid, amount);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean has(String playerName, String worldName, double amount) {
        UUID uuid = Bukkit.getPlayerUniqueId(playerName);
        return SmpxCore.economy().has(uuid, amount);
    }

    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        UUID uuid = player.getUniqueId();
        return SmpxCore.economy().has(uuid, amount);
    }

    @SuppressWarnings("deprecation")
    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        UUID uuid = Bukkit.getPlayerUniqueId(playerName);
        return SmpxCore.economy().withdrawPlayer(uuid, amount);
    }

    @SuppressWarnings("deprecation")
    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        UUID uuid = player.getUniqueId();
        return SmpxCore.economy().withdrawPlayer(uuid, amount);
    }

    @SuppressWarnings("deprecation")
    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        UUID uuid = Bukkit.getPlayerUniqueId(playerName);
        return SmpxCore.economy().withdrawPlayer(uuid, amount);
    }

    @SuppressWarnings("deprecation")
    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        UUID uuid = player.getUniqueId();
        return SmpxCore.economy().withdrawPlayer(uuid, amount);
    }

    @SuppressWarnings("deprecation")
    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        UUID uuid = Bukkit.getPlayerUniqueId(playerName);
        return SmpxCore.economy().depositPlayer(uuid, amount);
    }

    @SuppressWarnings("deprecation")
    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        UUID uuid = player.getUniqueId();
        return SmpxCore.economy().depositPlayer(uuid, amount);
    }

    @SuppressWarnings("deprecation")
    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        UUID uuid = Bukkit.getPlayerUniqueId(playerName);
        return SmpxCore.economy().depositPlayer(uuid, amount);
    }

    @SuppressWarnings("deprecation")
    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        UUID uuid = player.getUniqueId();
        return SmpxCore.economy().depositPlayer(uuid, amount);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean createPlayerAccount(String playerName) {
        UUID uuid = Bukkit.getPlayerUniqueId(playerName);
        return SmpxCore.economy().createPlayerAccount(uuid, playerName);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        UUID uuid = player.getUniqueId();
        String playerName = player.getName();
        return SmpxCore.economy().createPlayerAccount(uuid, playerName);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        UUID uuid = Bukkit.getPlayerUniqueId(playerName);
        return SmpxCore.economy().createPlayerAccount(uuid, playerName);
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        UUID uuid = player.getUniqueId();
        String playerName = player.getName();
        return SmpxCore.economy().createPlayerAccount(uuid, playerName);
    }

    // banks - not implemented... for now?
    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public EconomyResponse createBank(String name, String player) {
        return null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        return null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public EconomyResponse deleteBank(String name) {
        return null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public EconomyResponse bankBalance(String name) {
        return null;
    }

    @Override
    @SuppressWarnings("deprecation")
    public EconomyResponse bankHas(String name, double amount) {
        return null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
        return null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        return null;
    }

    @Override
    public List<String> getBanks() {
        return List.of();
    }
}