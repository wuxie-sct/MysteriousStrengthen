package me.wuxie.mysteriousstrengthen.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.io.File;

/**
 * 强化
 */
public class QHGui extends Gui {
    QHGui(File guiFile) {
        super(guiFile);
    }
    public Inventory getGui(Player player){
        Inventory inv = Bukkit.createInventory(new Holder(player,"QH"),SIZE,TITLE);
        inv.setContents(gui.getContents());
        return inv;
    }
}
