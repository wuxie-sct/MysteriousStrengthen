package me.wuxie.mysteriousstrengthen.util;

import me.wuxie.mysteriousstrengthen.gui.Gui;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import me.wuxie.mysteriousstrengthen.MysteriousStrengthen;
import me.wuxie.mysteriousstrengthen.jinjie.JinJie;
import me.wuxie.mysteriousstrengthen.jinjie.JinJieManager;
import me.wuxie.mysteriousstrengthen.listener.InventoryListener;
import me.wuxie.mysteriousstrengthen.strengthen.ItemSet;
import me.wuxie.mysteriousstrengthen.strengthen.ItemSetManager;
import me.wuxie.mysteriousstrengthen.strengthen.QHMethood;

public class Command implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if(args.length>0){
            if(args[0].equalsIgnoreCase("open")&&sender instanceof Player &&sender.hasPermission("strengthen.open")){
                Player p = (Player)sender;
                ItemStack itemStack = p.getInventory().getItemInMainHand();
                if(itemStack==null||itemStack.getType().equals(Material.AIR)){
                    Message.send(p,Message.path24);
                    return true;
                }
                if(itemStack.getAmount()>1){
                    Message.send(p,Message.path25);
                    return true;
                }
                Gui gui;
                ItemSet is;
                QHMethood qhm;
                if(itemStack.hasItemMeta()&&itemStack.getItemMeta().hasDisplayName()){
                    is = ItemSetManager.getItemSet(itemStack.getItemMeta().getDisplayName());
                    if(is==null){
                        Message.send(p,Message.path26);
                        return true;
                    }
                    qhm = is.getQhMethood();
                    gui = qhm.getGui();
                }else{
                    Message.send(p,Message.path26);
                    return true;
                }
                if(gui!=null){
                    Inventory inv = gui.getGui(p);
                    for(int a:gui.getMainItemSlotList()){
                        inv.setItem(a,itemStack.clone());
                    }
                    p.getInventory().setItemInMainHand(null);
                    p.openInventory(inv);
                    InventoryListener.openGuiPlayers.put(p,gui.getFileName());
                    return true;
                }else{
                    Message.send(p,Message.path28);
                    return true;
                }
            }else if(args[0].equalsIgnoreCase("jinjie")&&sender instanceof Player &&sender.hasPermission("strengthen.jinjie")){
                Player p = (Player)sender;
                ItemStack itemStack = p.getInventory().getItemInMainHand();
                if(itemStack==null||itemStack.getType().equals(Material.AIR)){
                    Message.send(p,Message.path24);
                    return true;
                }
                if(itemStack.getAmount()>1){
                    Message.send(p,Message.path25);
                    return true;
                }
                Gui gui;
                JinJie jj;
                if(itemStack.hasItemMeta()&&itemStack.getItemMeta().hasDisplayName()){
                    jj = JinJieManager.getJinJie(itemStack);
                    if(jj==null){
                        Message.send(p,Message.path27);
                        return true;
                    }
                    gui = jj.getJjgui();
                }else{
                    Message.send(p,Message.path27);
                    return true;
                }
                if(gui!=null){
                    Inventory inv = gui.getGui(p);
                    for(int a:gui.getMainItemSlotList()){
                        inv.setItem(a,itemStack.clone());
                    }
                    p.getInventory().setItemInMainHand(null);
                    p.openInventory(inv);
                    InventoryListener.openGuiPlayers.put(p,gui.getFileName());
                    return true;
                }else{
                    Message.send(p,Message.path28);
                    return true;
                }
            }else if(args[0].equalsIgnoreCase("savejinjie")&&sender.hasPermission("strengthen.savejinjie")&&sender instanceof Player){
                if(args.length>1){
                    String key = args[1];
                    Player p = (Player)sender;
                    if(JinJieManager.getJjItems().containsKey(key)){
                        p.sendMessage("§7该存储键值 "+key+" 已经存在，请换一个！");
                        return true;
                    }
                    ItemStack itemStack = p.getInventory().getItemInMainHand();
                    if(itemStack==null||itemStack.getType().equals(Material.AIR)){
                        Message.send(p,Message.path24);
                        return true;
                    }
                    if(itemStack.getAmount()>1){
                        Message.send(p,Message.path25);
                        return true;
                    }
                    JinJieManager.getJjItems().put(key,itemStack.clone());
                    JinJieManager.getJjiy().set(key,itemStack.clone());
                    JinJieManager.savejji();
                    p.sendMessage("§a存储物品到键值 "+key+" 成功！");
                }
            }
            else if(args[0].equalsIgnoreCase("reload")&&sender.hasPermission("strengthen.admin")){
                MysteriousStrengthen.getGuiManager().loadGuis();
                MysteriousStrengthen.getItemSetManager().loadItemName();
                MysteriousStrengthen.getQhMethoodManager().loadQHMethood();
                MysteriousStrengthen.getItemSetManager().loadItemSet();
                MysteriousStrengthen.getMsg().loadMessage();
                MysteriousStrengthen.getJinJieManager().loadJJ();
                Message.send(sender,Message.path30);
                return true;
            }else{
                Message.send(sender,Message.path29);
                return true;
            }
        }
        sender.sendMessage("§b§l§m-=-=-=-=-=-=-=-=-§e§l[§c§l强化§e§l]§b§l§m-=-=-=-=-=-=-=-=");
        if(sender.hasPermission("strengthen.open"))sender.sendMessage("§a§l/wqh open  §c§l手持装备输入，打开强化界面！");
        if(sender.hasPermission("strengthen.jinjie"))sender.sendMessage("§a§l/wqh jinjie  §c§l手持装备输入，打开进阶界面！");
        if(sender.hasPermission("strengthen.savejinjie"))sender.sendMessage("§a§l/wqh savejinjie [key] §c§l手持装备输入，一键存储进阶物品！");
        if(sender.hasPermission("strengthen.admin"))sender.sendMessage("§a§l/wqh reload §c§l重载插件！");
        sender.sendMessage("§b§l§m-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
        return true;
    }
}
