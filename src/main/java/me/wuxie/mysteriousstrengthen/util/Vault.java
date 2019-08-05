package me.wuxie.mysteriousstrengthen.util;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.RegisteredServiceProvider;

/**
 * 经济类
 *
 * @author wuxie
 * @date 2018/11/14
 */
public class Vault {
    private static Economy economy = null;

    public static void setup() throws NullPointerException {
        RegisteredServiceProvider<Economy> registeredServiceProvider = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (registeredServiceProvider == null) {throw new NullPointerException();}
        economy = registeredServiceProvider.getProvider();
    }

    public static double get(OfflinePlayer player) {
        return economy.getBalance(player);
    }

    public static boolean has(OfflinePlayer player, double money) {
        return money <= get(player);
    }

    public static void give(OfflinePlayer player, double money) {
        economy.depositPlayer(player, money);
    }

    public static void take(OfflinePlayer player, double money) {
        economy.withdrawPlayer(player, money);
    }
}
