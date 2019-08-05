package me.wuxie.mysteriousstrengthen.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;

/**
 *  进阶选择
 */
public class JJSGui extends Gui {
    JJSGui(File guiFile) {
        super(guiFile);
    }

    @Override
    public Inventory getGui(Player player) {
        Inventory inv = Bukkit.createInventory(new Holder(player,"JJS"),SIZE,TITLE);
        inv.setContents(gui.getContents());
        return inv;
    }
    @Override
    public Inventory loadGui(){
        this.TITLE = guiYaml.getString("GUI名称").replaceAll("&","§");
        this.SIZE = guiYaml.getInt("GUI大小");
        Inventory inv = Bukkit.createInventory(null,SIZE,TITLE);
        for(String key:guiYaml.getConfigurationSection("GUI内容").getKeys(false)){
            int x =guiYaml.getInt("GUI内容."+key+".GUI位置.x轴");
            int y =guiYaml.getInt("GUI内容."+key+".GUI位置.y轴");
            int slot = getLoc(x,y);
            int id= guiYaml.getInt("GUI内容." + key + ".物品.id");
            int data= guiYaml.getInt("GUI内容." + key + ".物品.data");
            String itemName = guiYaml.getString("GUI内容." + key + ".物品.名称").replace("&","§");
            List<String> lore = getlore(guiYaml.getStringList("GUI内容." + key + ".物品.lore"));
            boolean isEnchant= guiYaml.getBoolean("GUI内容." + key + ".物品.附魔");
            ItemStack item = createItem(itemName,id,data,lore,isEnchant);
            inv.setItem(slot,item);
            normalItemSlotList.add(slot);
        }
        loadCanClickSlots();
        return inv;
    }
    @Override
    public void loadCanClickSlots(){}
}
