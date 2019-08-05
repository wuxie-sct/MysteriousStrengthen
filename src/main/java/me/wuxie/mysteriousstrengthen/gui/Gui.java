package me.wuxie.mysteriousstrengthen.gui;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.util.*;

public abstract class Gui {
    @Getter
    public YamlConfiguration guiYaml;
    @Getter
    public List<Integer> qhsSlotList = new ArrayList<>();
    @Getter
    public List<Integer> bhs_LevelSlotList = new ArrayList<>();
    @Getter
    public List<Integer> bhs_BreakSlotList = new ArrayList<>();
    @Getter
    public List<Integer> zfclSlotList = new ArrayList<>();
    @Getter
    public List<Integer> messageSlotList = new ArrayList<>();
    @Getter
    public List<Integer> mainItemSlotList = new ArrayList<>();
    @Getter
    public List<Integer> normalItemSlotList = new ArrayList<>();
    @Getter
    public List<Integer> canClickSlotList = new ArrayList<>();
    @Getter
    public List<Integer> buttonSlotList = new ArrayList<>();
    @Getter
    public Inventory gui;
    @Getter
    public String TITLE;
    @Getter
    public int SIZE;
    @Getter
    public String fileName;
    Gui(File guiFile){
        fileName = guiFile.getName();
        guiYaml = YamlConfiguration.loadConfiguration(guiFile);
        gui = loadGui();
        Bukkit.getConsoleSender().sendMessage("§aload gui §c"+fileName);
    }
    public abstract Inventory getGui(Player player);
    public ItemStack getMsgItem(List<String> lore){
        String name =guiYaml.getString("GUI物品消息.名称").replaceAll("&","§");
        int id = guiYaml.getInt("GUI物品消息.id");
        int data = guiYaml.getInt("GUI物品消息.data");
        boolean isEnch = guiYaml.getBoolean("GUI物品消息.附魔");
        return createItem(name,id,data,lore,isEnch);
    }
    public List<String> getMsgLore(){
        return getlore(guiYaml.get("GUI物品消息.lore")==null?new ArrayList<>():guiYaml.getStringList("GUI物品消息.lore"));
    }

    public void loadCanClickSlots(){
        canClickSlotList.clear();
        canClickSlotList.addAll(qhsSlotList);
        canClickSlotList.addAll(bhs_LevelSlotList);
        canClickSlotList.addAll(bhs_BreakSlotList);
        canClickSlotList.addAll(zfclSlotList);
    }
    public Inventory loadGui(){
        this.TITLE = guiYaml.getString("GUI名称").replaceAll("&","§");

        this.SIZE = guiYaml.getInt("GUI大小");
        Inventory inv = Bukkit.createInventory(null,SIZE,TITLE);
        for(String key:guiYaml.getConfigurationSection("GUI内容").getKeys(false)){
            int x =guiYaml.getInt("GUI内容."+key+".GUI位置.x轴");
            int y =guiYaml.getInt("GUI内容."+key+".GUI位置.y轴");
            int slot = getLoc(x,y);
            String slotType = guiYaml.getString("GUI内容." + key + ".槽类型");
            //if(slotType==null)Bukkit.getConsoleSender().sendMessage("load gui error !"+"GUI内容." + key + ".槽类型");
            int id;
            int data;
            String itemName;
            List<String> lore;
            ItemStack item = new ItemStack(Material.AIR);
            boolean isEnchant;
            if (slotType.equalsIgnoreCase("normal")|| slotType.equalsIgnoreCase("message")|| slotType.equalsIgnoreCase("button")) {
                id = guiYaml.getInt("GUI内容." + key + ".物品.id");
                data = guiYaml.getInt("GUI内容." + key + ".物品.data");
                itemName = guiYaml.getString("GUI内容." + key + ".物品.名称").replace("&","§");
                lore = getlore(guiYaml.getStringList("GUI内容." + key + ".物品.lore"));
                isEnchant = guiYaml.getBoolean("GUI内容." + key + ".物品.附魔");
                item = createItem(itemName,id,data,lore,isEnchant);
            }
            switch (slotType) {
                case "NORMAL":
                    normalItemSlotList.add(slot);
                    inv.setItem(slot,item);
                    break;
                case "ITEM":
                    mainItemSlotList.add(slot);
                    break;
                case "QHS":
                    qhsSlotList.add(slot);
                    break;
                case "MESSAGE":
                    messageSlotList.add(slot);
                    inv.setItem(slot,item);
                    break;
                case "BUTTON":
                    buttonSlotList.add(slot);
                    inv.setItem(slot,item);
                    break;
                case "BHS_LEVEL":
                    bhs_LevelSlotList.add(slot);
                    break;
                case "BHS_BREAK":
                    bhs_BreakSlotList.add(slot);
                    break;
                case "ZFCL":
                    zfclSlotList.add(slot);
                    break;
            }
        }
        loadCanClickSlots();
        return inv;
    }
    static int getLoc(int x, int y){
        return (9*(y-1)-1)+x;
    }
    static ItemStack createItem(String name, int id, int data, List<String> lore, boolean enchantment){
        ItemStack item =new ItemStack(Material.getMaterial(id),1,(short)data);
        ItemMeta meta =item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        item.setItemMeta(meta);
        if(enchantment){
            item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL,1);
        }
        item.setAmount(1);
        return item;
    }
    static List<String> getlore(List<String> lore){
        List<String> list = new ArrayList<>();
        for(String s:lore){
            list.add(s.replaceAll("&","§"));
        }
        return list;
    }
}
