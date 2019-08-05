package me.wuxie.mysteriousstrengthen.api;


import lombok.Getter;
import me.wuxie.mysteriousstrengthen.gui.Gui;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryOpenEvent;

public class OpenGuiEvent extends Event {
     @Getter
     private Gui gui;
     @Getter
    private InventoryOpenEvent inventoryOpenEvent;
    public OpenGuiEvent(Gui gui, InventoryOpenEvent inventoryOpenEvent){
        this.gui = gui;
        this.inventoryOpenEvent = inventoryOpenEvent;
        Bukkit.getPluginManager().callEvent(this);
    }
    private static final HandlerList handlers = new HandlerList();
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
