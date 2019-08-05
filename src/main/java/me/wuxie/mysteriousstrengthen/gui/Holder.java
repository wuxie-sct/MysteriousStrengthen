package me.wuxie.mysteriousstrengthen.gui;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.List;

public class Holder implements InventoryHolder {
    @Getter
    private Player player;
    @Getter
    private String type;
    @Getter
    @Setter
    List<Integer> other;
    public Holder(Player player,String type){
        this.player = player;
        this.type = type;
    }
    @Override
    public Inventory getInventory() {
        return null;
    }
}
