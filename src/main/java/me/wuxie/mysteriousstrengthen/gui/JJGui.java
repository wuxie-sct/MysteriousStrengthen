package me.wuxie.mysteriousstrengthen.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.io.File;
/**
 * 进阶
 */
public class JJGui extends Gui{
    JJGui(File guiFile) {
        super(guiFile);
    }

    public Inventory getGui(Player player){
        Inventory inv = Bukkit.createInventory(new Holder(player,"JJ"),SIZE,TITLE);
        inv.setContents(gui.getContents());
        return inv;
    }
    @Override
    public void loadCanClickSlots(){
        canClickSlotList.clear();
        canClickSlotList.addAll(qhsSlotList);
        canClickSlotList.addAll(bhs_BreakSlotList);
        canClickSlotList.addAll(zfclSlotList);
    }
}
