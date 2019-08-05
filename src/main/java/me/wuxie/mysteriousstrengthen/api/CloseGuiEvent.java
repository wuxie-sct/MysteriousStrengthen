package me.wuxie.mysteriousstrengthen.api;

import lombok.Getter;
import me.wuxie.mysteriousstrengthen.gui.Gui;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;

public class CloseGuiEvent extends Event {
    @Getter
    private Gui gui;
    @Getter
    Inventory inventory;
    public CloseGuiEvent(Inventory inventory, Gui gui) {
        this.inventory = inventory;
        this.gui = gui;
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
