package me.wuxie.mysteriousstrengthen;

import lombok.Getter;
import me.wuxie.mysteriousstrengthen.gui.Gui;
import me.wuxie.mysteriousstrengthen.gui.GuiManager;
import me.wuxie.mysteriousstrengthen.jinjie.JinJieManager;
import me.wuxie.mysteriousstrengthen.listener.InventoryListener;
import me.wuxie.mysteriousstrengthen.strengthen.ItemSetManager;
import me.wuxie.mysteriousstrengthen.strengthen.QHMethoodManager;
import me.wuxie.mysteriousstrengthen.util.Command;
import me.wuxie.mysteriousstrengthen.util.Message;
import me.wuxie.mysteriousstrengthen.util.Metrics;
import me.wuxie.mysteriousstrengthen.util.Vault;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import me.wuxie.mysteriousstrengthen.gui.Holder;

public final class MysteriousStrengthen extends JavaPlugin {
    @Getter
    private static ItemSetManager itemSetManager;
    @Getter
    private static QHMethoodManager qhMethoodManager;
    @Getter
    private static GuiManager guiManager;
    @Getter
    private static JinJieManager jinJieManager;
    @Getter
    private static Message msg;
    @Override
    public void onEnable() {
        new Metrics(this);
        guiManager = new GuiManager(this);
        itemSetManager = new ItemSetManager(this);
        qhMethoodManager = new QHMethoodManager(this);
        itemSetManager.loadItemSet();
        jinJieManager = new JinJieManager(this);
        msg = new Message(this);
        Bukkit.getPluginCommand("wqh").setExecutor(new Command());
        Bukkit.getPluginManager().registerEvents(new InventoryListener(),this);

        if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            try {
                Vault.setup();
                Bukkit.getConsoleSender().sendMessage("Find Vault");
            } catch (NullPointerException e) {
                Bukkit.getConsoleSender().sendMessage("§cNo Find Vault-Economy!");
            }
        } else {
            Bukkit.getConsoleSender().sendMessage("§cNo Find Vault!");
        }
    }
    @Override
    public void onDisable() {
        for(Player p:Bukkit.getOnlinePlayers()){
            InventoryView view = p.getOpenInventory();
            if(view!=null){
                Inventory inv = view.getTopInventory();
                if(InventoryListener.openGuiPlayers.containsKey(p)&&inv.getHolder() instanceof Holder){
                    if(((Holder) inv.getHolder()).getType().equalsIgnoreCase("JJS")){
                        Gui gui = GuiManager.getGui(InventoryListener.openGuiPlayers.get(p));
                        InventoryListener.openGuiPlayers.remove(p);
                        assert gui != null;
                        for(int a:((Holder) inv.getHolder()).getOther()){
                            ItemStack i = inv.getItem(a);
                            if(i!=null&&!i.getType().equals(Material.AIR)){
                                PlayerInventory pi = p.getInventory();
                                if(pi.firstEmpty()<0){
                                    p.getWorld().dropItem(p.getLocation(),i);
                                }else p.getInventory().addItem(i);
                                Message.sends(p,Message.path20,new Object[]{Message.getItemShow(i)},new String[]{i.getItemMeta().getDisplayName()});
                                break;
                            }
                        }
                    }else {
                        if (inv.getType().equals(InventoryType.CHEST)) {
                            Gui gui = GuiManager.getGui(InventoryListener.openGuiPlayers.get(p));
                            InventoryListener.openGuiPlayers.remove(p);
                            assert gui != null;
                            for (int slot : gui.getCanClickSlotList()) {
                                ItemStack item = inv.getItem(slot);
                                if (item != null && !item.getType().equals(Material.AIR)) {
                                    p.getInventory().addItem(item);
                                }
                            }
                            ItemStack mainItem = inv.getItem(gui.getMainItemSlotList().get(0));
                            if (mainItem != null && !mainItem.getType().equals(Material.AIR))
                                p.getInventory().addItem(mainItem);
                        } else InventoryListener.openGuiPlayers.remove(p);
                    }
                }
                view.close();
            }
        }
    }
}
