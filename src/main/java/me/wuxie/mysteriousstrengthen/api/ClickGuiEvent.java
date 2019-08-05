package me.wuxie.mysteriousstrengthen.api;

import lombok.Getter;
import me.wuxie.mysteriousstrengthen.gui.Gui;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class ClickGuiEvent extends Event {
    @Getter
    private Gui gui;
    @Getter
    private InventoryClickEvent inventoryClickEvent;
    public ClickGuiEvent(Gui gui, InventoryClickEvent inventoryClickEvent){
        this.gui = gui;
        this.inventoryClickEvent = inventoryClickEvent;
        Bukkit.getPluginManager().callEvent(this);
    }
    private static final HandlerList handlers = new HandlerList();
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public void setCancel(boolean toCancel){
        inventoryClickEvent.setCancelled(toCancel);
    }
    public Inventory getInventory(){
        return inventoryClickEvent.getInventory();
    }
    public HumanEntity getWhoClicked(){
        return inventoryClickEvent.getWhoClicked();
    }
    public Inventory getClickedInventory(){
        return inventoryClickEvent.getClickedInventory();
    }
    public int getSlot() {
        return  inventoryClickEvent.getSlot();
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
