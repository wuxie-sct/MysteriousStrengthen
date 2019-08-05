package me.wuxie.mysteriousstrengthen.listener;

import lombok.Getter;
import me.wuxie.mysteriousequipment.itemstack.ItemStackManager;
import me.wuxie.mysteriousstrengthen.gui.Gui;
import me.wuxie.mysteriousstrengthen.gui.GuiManager;
import me.wuxie.mysteriousstrengthen.jinjie.JinJie;
import me.wuxie.mysteriousstrengthen.jinjie.JinJieManager;
import me.wuxie.mysteriousstrengthen.strengthen.Level;
import me.wuxie.mysteriousstrengthen.strengthen.qd.QH;
import me.wuxie.mysteriousstrengthen.util.Message;
import me.wuxie.mysteriousstrengthen.util.Vault;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import me.wuxie.mysteriousstrengthen.api.ClickGuiEvent;
import me.wuxie.mysteriousstrengthen.api.CloseGuiEvent;
import me.wuxie.mysteriousstrengthen.api.OpenGuiEvent;
import me.wuxie.mysteriousstrengthen.gui.Holder;
import me.wuxie.mysteriousstrengthen.strengthen.ItemSet;
import me.wuxie.mysteriousstrengthen.strengthen.ItemSetManager;
import me.wuxie.mysteriousstrengthen.strengthen.QHMethod;

import java.util.*;

public class InventoryListener implements Listener {
    @Getter
    public static Map<Player,String> openGuiPlayers = new HashMap<>();
    @Getter
    public static final Random r = new Random();
    @EventHandler
    public void onOpen(InventoryOpenEvent e){
        if(e.getInventory()==null)return;
        Player p = (Player) e.getPlayer();
        if (p==null)return;
        if(openGuiPlayers.containsKey(p)){
            Gui gui = GuiManager.getGui(openGuiPlayers.get(p));
            new OpenGuiEvent(gui,e);
        }
    }
    @EventHandler
    public void onClick(InventoryClickEvent e){
        if(e.getClickedInventory()==null)return;
        if(e.getInventory()==null)return;
        Player p = (Player) e.getWhoClicked();
        if (p==null)return;
        InventoryHolder holder = e.getClickedInventory().getHolder();
        if(openGuiPlayers.containsKey(p) && holder instanceof Holder){
            // 强化
            if(((Holder) holder).getType().equalsIgnoreCase("QH")){
                e.setCancelled(true);
                QH(e, p);
            // 进阶
            }else if(((Holder) holder).getType().equalsIgnoreCase("JJ")){
                e.setCancelled(true);
                JJ(e, p);
            }else if(((Holder) holder).getType().equalsIgnoreCase("JJS")){
                e.setCancelled(true);
                Gui gui = GuiManager.getGui(openGuiPlayers.get(p));
                assert gui != null;
                if(e.getClickedInventory().getType().equals(InventoryType.CHEST)){
                    List<Integer> o = ((Holder) holder).getOther();
                    if(o.contains(e.getSlot())){
                        ItemStack i = e.getClickedInventory().getItem(e.getSlot());
                        if(i!=null&&!i.getType().equals(Material.AIR)){
                            PlayerInventory pi = p.getInventory();
                            if(pi.firstEmpty()<0){
                                p.getWorld().dropItem(p.getLocation(),i);
                            }else p.getInventory().addItem(i);
                            Message.sends(p,Message.path20,new Object[]{Message.getItemShow(i)},new String[]{i.getItemMeta().getDisplayName()});
                            openGuiPlayers.remove(p);
                            p.closeInventory();
                        }
                    }
                }
            }else if(!e.getInventory().getType().equals(InventoryType.CHEST))openGuiPlayers.remove(p);
        }
    }
    @EventHandler
    public void cc(InventoryClickEvent e){
        if(e.getClickedInventory()==null)return;
        if(e.getInventory()==null)return;
        Player p = (Player) e.getWhoClicked();
        if (p==null)return;
        InventoryHolder holder = e.getInventory().getHolder();
        if(holder==null|| !(holder instanceof Holder))return;
        if(((Holder) holder).getType().equalsIgnoreCase("JJS")){
            e.setCancelled(true);
        }
    }
    //进阶
    private void JJ(InventoryClickEvent e, Player p) {
        Inventory inv = e.getClickedInventory();
        Gui gui = GuiManager.getGui(openGuiPlayers.get(p));
        new ClickGuiEvent(gui,e);
        assert gui != null;
        List<Integer> messageSlotList = gui.getMessageSlotList();
        List<Integer> buttonSlotList = gui.getButtonSlotList();
        List<Integer> canClickSlotList = gui.getCanClickSlotList();
        if((canClickSlotList.contains(e.getSlot()) ||e.getClickedInventory().getType().equals(InventoryType.PLAYER))){
            if(!e.isShiftClick())e.setCancelled(false);
        }
        if(buttonSlotList.contains(e.getSlot())&&e.getClickedInventory().getType().equals(InventoryType.CHEST)){
            ItemStack item = inv.getItem(gui.getMainItemSlotList().get(0));
            JinJie jj = JinJieManager.getJinJie(item);
            if(jj!=null){
                double money = jj.getMoney();
                if(!Vault.has(p,money)){
                    Message.send(p,Message.path15,money+"");
                }
                int needl = jj.getNeed_level();
                ItemMeta meta = item.getItemMeta();
                int qhlevel = getQh(Objects.requireNonNull(ItemSetManager.getItemSet(item.getItemMeta().getDisplayName())).getQhMethod()).
                        getQHLevel(meta.getLore()==null?new ArrayList<>():meta.getLore());
                if(qhlevel>=needl){
                    double lp = jj.getLevel_probability();
                    double rp = jj.getProbability();
                    Map<String,Integer> needMaterials = jj.getNeedMaterials();
                    List<ItemStack> qhitems = getItems(inv,gui.getQhsSlotList());
                    if(contains(needMaterials,qhitems)){
                        Vault.take(p,money);
                        removeItem(inv,needMaterials,gui.getQhsSlotList());
                        double additon = 0D;
                        List<ItemStack> zflist = getItems(inv,gui.getZfclSlotList());
                        if(contain(jj.getZfcl(),zflist)&&rp+lp*(qhlevel-needl)<100D){
                            String sign = jj.getZfcl_sign();
                            for(ItemStack zfitem:zflist){
                                if(zfitem.hasItemMeta()&&zfitem.getItemMeta().hasLore()&&zfitem.getItemMeta().hasDisplayName()){
                                    if(ItemSet.nameEquals(jj.getZfcl(),zfitem.getItemMeta().getDisplayName())) {
                                        additon += QHMethod.getAddition(zfitem.getItemMeta().getLore(), sign);
                                        removeItem(inv, zfitem);
                                        if (additon + rp+lp*(qhlevel-needl) >= 100D) {
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        if(isSuccess(rp+lp*(qhlevel-needl),additon)){
                            Gui jjsGui = jj.getJjsgui();
                            Inventory jjsinv = jjsGui.getGui(p);
                            List<String> jjis = jj.getJjitems();
                            List<ItemStack> jjisss = new ArrayList<>();
                            String msg = jj.getMsg();
                            String cmd = jj.getCmd();
                            sendAllMessage(msg,p,item,qhlevel);
                            playerRunCommand(p,cmd);
                            for(String s:jjis){
                                if(s.contains("meq:")){
                                    String[] ss = s.replace("meq:","").split(" ");
                                    int quality = Integer.parseInt(ss[1]);
                                    ItemStack i = ItemStackManager.getRandomIrem(ss[0],quality);
                                    if(i!=null){
                                        jjisss.add(i);
                                    }
                                }else {
                                    ItemStack i = JinJieManager.getJjItems().get(s);
                                    if(i!=null){
                                        i=i.clone();
                                        i.setAmount(1);
                                        jjisss.add(i);
                                    }
                                }
                            }
                            Holder h = (Holder)jjsinv.getHolder();
                            List<Integer> o = new ArrayList<>();
                            for(ItemStack i:jjisss){
                                if(jjsinv.firstEmpty()>=0){
                                    o.add(jjsinv.firstEmpty());
                                    jjsinv.setItem(jjsinv.firstEmpty(),i);
                                }
                            }
                            h.setOther(o);
                            openGuiPlayers.remove(p);
                            for(int slot:gui.getCanClickSlotList()){
                                ItemStack i = inv.getItem(slot);
                                if(i!=null&&!i.getType().equals(Material.AIR)){
                                    p.getInventory().addItem(i);
                                }
                            }
                            p.openInventory(jjsinv);
                            openGuiPlayers.put(p,jjsGui.fileName);
                            Message.send(p,Message.path19);
                        }else {
                            boolean breakk = jj.isBreakk();
                            int qk = 0;
                            // 0 不损坏
                            // 1 损坏
                            // 2 防止了损坏
                            // 3 没有防止损坏
                            if(breakk){
                                List<ItemStack> bhblist = getItems(inv,gui.getBhs_BreakSlotList());
                                if(contain(jj.getBhcl(),bhblist)){
                                    double prob = 0D;
                                    String signd = jj.getBhcl_sign();
                                    for(ItemStack bhitem:bhblist){
                                        if(bhitem.hasItemMeta()&&bhitem.getItemMeta().hasLore()&&bhitem.getItemMeta().hasDisplayName()){
                                            if(ItemSet.nameEquals(jj.getBhcl(),bhitem.getItemMeta().getDisplayName())) {
                                                prob += QHMethod.getAddition(bhitem.getItemMeta().getLore(), signd);
                                                removeItem(inv, bhitem);
                                                if (prob >= 100D) {
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    if(isSuccess(prob,0)){
                                        qk = 2;
                                    }else {
                                        qk = 3;
                                    }
                                }else qk = 1;
                            }
                            switch (qk){
                                case 0:{
                                    Message.sends(p,Message.path23,new Object[]{Message.getItemShow(item)},new String[]{item.getItemMeta().getDisplayName()});
                                }
                                case 1:{
                                    inv.remove(item);
                                    Message.sends(p,Message.path21,new Object[]{Message.getItemShow(item)},new String[]{item.getItemMeta().getDisplayName()});
                                }
                                case 2:{
                                    Message.sends(p,Message.path22,new Object[]{Message.getItemShow(item)},new String[]{item.getItemMeta().getDisplayName()});
                                }
                                case 3:{
                                    inv.remove(item);
                                    Message.sends(p,Message.path21,new Object[]{Message.getItemShow(item)},new String[]{item.getItemMeta().getDisplayName()});
                                }
                            }
                        }
                    }else {
                        Message.sends(p,Message.path14,new Object[]{Message.getItemShow(item)},new String[]{item.getItemMeta().getDisplayName()});
                    }
                }else {
                    Message.send(p,Message.path16,needl+"");
                }
            }else {
                Message.send(p,Message.path27);
            }
        }else if(messageSlotList.contains(e.getSlot())&&e.getClickedInventory().getType().equals(InventoryType.CHEST)){
            ItemStack item = inv.getItem(gui.getMainItemSlotList().get(0));
            JinJie jj = JinJieManager.getJinJie(item);
            ItemMeta meta = item.getItemMeta();
            // 装备名
            String zbname = meta.getDisplayName();
            //  需要强度
            int needlevel = jj.getNeed_level();
            // 需要钱数
            double money = jj.getMoney();
            // 是否损坏
            String breakk = jj.isBreakk()?"损坏":"不损坏";
            // 保护材料名称
            String sbs = jj.getBhcl().replace("%p%","?");
            // 祝福材料名称
            String zfs = jj.getZfcl().replace("%p%","?");
            // 装备强度等级
            int level = getQh(ItemSetManager.getItemSet(item.getItemMeta().getDisplayName()).getQhMethod()).
                    getQHLevel(meta.getLore()==null?new ArrayList<>():meta.getLore());
            // 成功几率
            double probability = jj.getProbability();
            // 额外强度增加的成功几率
            double qdp = (level-needlevel)*jj.getLevel_probability();
            StringBuilder sb = new StringBuilder();
            for(Map.Entry<String,Integer> nd:jj.getNeedMaterials().entrySet()){
                sb.append(nd.getKey().replace("%p%","?"));
                sb.append("*");
                sb.append(nd.getValue());
                sb.append(" ");
            }
            // 材料需求
            String needs = sb.toString();
            // 损坏保护几率
            double breakp = 0D;
            String signd = jj.getBhcl_sign();
            List<ItemStack> bhblist = getItems(inv,gui.getBhs_BreakSlotList());
            if(contain(jj.getBhcl(),bhblist)){
                for(ItemStack bhitem:bhblist){
                    if(bhitem.hasItemMeta()&&bhitem.getItemMeta().hasLore()&&bhitem.getItemMeta().hasDisplayName()){
                        if(ItemSet.nameEquals(jj.getBhcl(),bhitem.getItemMeta().getDisplayName())) {
                            breakp += QHMethod.getAddition(bhitem.getItemMeta().getLore(), signd);
                            if (breakp >= 100D) {
                                breakp = 100D;
                                break;
                            }
                        }
                    }
                }
            }
            // 成功几率加成
            double additon = 0D;
            List<ItemStack> zflist = getItems(inv,gui.getZfclSlotList());
            if(contain(jj.getZfcl(),zflist)){
                String sign = jj.getZfcl_sign();
                for(ItemStack zfitem:zflist){
                    if(zfitem.hasItemMeta()&&zfitem.getItemMeta().hasLore()&&zfitem.getItemMeta().hasDisplayName()){
                        if(ItemSet.nameEquals(jj.getZfcl(),zfitem.getItemMeta().getDisplayName())) {
                            additon += QHMethod.getAddition(zfitem.getItemMeta().getLore(), sign);
                        }
                    }
                }
            }
            List<String> msgLore = gui.getMsgLore();
            List<String> nmsglore = new ArrayList<>();
            for(String ml:msgLore){
                if(ml.contains("%zb_name%"))nmsglore.add(ml.replace("%zb_name%",zbname));
                else if(ml.contains("%zb_level%"))nmsglore.add(ml.replace("%zb_level%",level+""));
                else if(ml.contains("%zb_probability%"))nmsglore.add(ml.replace("%zb_probability%",probability+""));
                else if(ml.contains("%zb_money%"))nmsglore.add(ml.replace("%zb_money%",money+""));
                else if(ml.contains("%zb_break%"))nmsglore.add(ml.replace("%zb_break%",breakk));
                else if(ml.contains("%zb_needs%"))nmsglore.add(ml.replace("%zb_needs%",needs));
                else if(ml.contains("%zb_bbhs%"))nmsglore.add(ml.replace("%zb_bbhs%",sbs));
                else if(ml.contains("%zb_zfcl%"))nmsglore.add(ml.replace("%zb_zfcl%",zfs));
                else if(ml.contains("%zb_strength%"))nmsglore.add(ml.replace("%zb_strength%",needlevel+""));
                else if(ml.contains("%zb_qd_probability%"))nmsglore.add(ml.replace("%zb_qd_probability%",qdp+""));
                else if(ml.contains("%zb_bbhs_probability%"))nmsglore.add(ml.replace("%zb_bbhs_probability%",breakp+""));
                else if(ml.contains("%zb_zfcl_probability%"))nmsglore.add(ml.replace("%zb_zfcl_probability%",additon+""));
                else nmsglore.add(ml);
            }
            ItemStack msgItem = gui.getMsgItem(nmsglore);
            for(int slot:gui.getMessageSlotList()){
                inv.setItem(slot,msgItem);
            }
            Message.send(p,Message.path17);
        }
    }
    private void QH(InventoryClickEvent e, Player p) {
        Inventory inv = e.getClickedInventory();
        Gui gui = GuiManager.getGui(openGuiPlayers.get(p));
        new ClickGuiEvent(gui,e);
        assert gui != null;
        List<Integer> messageSlotList = gui.getMessageSlotList();
        List<Integer> buttonSlotList = gui.getButtonSlotList();
        List<Integer> canClickSlotList = gui.getCanClickSlotList();
        if((canClickSlotList.contains(e.getSlot()) ||e.getClickedInventory().getType().equals(InventoryType.PLAYER))){
            if(!e.isShiftClick())e.setCancelled(false);
        } else if(buttonSlotList.contains(e.getSlot())&&e.getClickedInventory().getType().equals(InventoryType.CHEST)){
            qh(inv,gui,e);
        }else if(messageSlotList.contains(e.getSlot())&&e.getClickedInventory().getType().equals(InventoryType.CHEST)){
            ItemStack item = inv.getItem(gui.getMainItemSlotList().get(0));
            String zbname;
            int zblevel;
            double probability;
            double needmoney;
            boolean isBreak;
            int downLevel;
            String needs;
            String jjbhf;
            String bbbhf;
            String zfcl;
            double jjbhf_jl=0D;
            double bbbhf_jl=0D;
            double zfcl_jl=0D;
            ItemSet is;
            QHMethod qhm;
            QH qh;
            List<String> lore;
            if(item.hasItemMeta()&&item.getItemMeta().hasDisplayName()){
                ItemMeta meta = item.getItemMeta();
                zbname = meta.getDisplayName();
                lore = meta.hasLore()?meta.getLore():new ArrayList<>();
                is = ItemSetManager.getItemSet(zbname);
                if(is!=null){
                    qhm = is.getQhMethod();
                    qh = getQh(qhm);
                    zblevel = qh.getQHLevel(lore);
                    if(zblevel+1>qhm.getMaxLevel()){
                        Message.send(p,Message.getMsg(Message.path5));
                        return;
                    }
                    Level nextLevel = qhm.getLevels().get(zblevel+1);
                    isBreak = nextLevel.isBbreak();
                    downLevel = nextLevel.getDownLevel();
                    needmoney = nextLevel.getNeedMoney();
                    probability = nextLevel.getProbability();
                    StringBuilder sb = new StringBuilder();
                    for(Map.Entry<String,Integer> nd:nextLevel.getNeedMaterials().entrySet()){
                        sb.append(nd.getKey().replace("%p%","?"));
                        sb.append("*");
                        sb.append(nd.getValue());
                        sb.append(" ");
                    }
                    needs = sb.toString();
                    jjbhf = qhm.getQhbhf_level_name();
                    bbbhf = qhm.getQhbhf_break_name();
                    zfcl = qhm.getZfcl_name();
                    String jjbhfsign = qhm.getQhbhf_level_sign();
                    String bbbhfsign = qhm.getQhbhf_break_sign();
                    String zfclsign = qhm.getZfcl_sign();
                    List<ItemStack> jjs = getItems(inv,gui.getBhs_LevelSlotList());
                    List<ItemStack> bbs = getItems(inv,gui.getBhs_BreakSlotList());
                    List<ItemStack> zfs = getItems(inv,gui.getZfclSlotList());
                    jjbhf_jl=ct(jjbhf, jjbhf_jl, jjbhfsign, jjs);
                    bbbhf_jl=ct(bbbhf, bbbhf_jl, bbbhfsign, bbs);
                    zfcl_jl=ct(zfcl, zfcl_jl, zfclsign, zfs);
                    List<String> msgLore = gui.getMsgLore();
                    List<String> nmsglore = new ArrayList<>();
                    for(String ml:msgLore){
                        if(ml.contains("%zb_name%"))nmsglore.add(ml.replace("%zb_name%",zbname));
                        else if(ml.contains("%zb_level%"))nmsglore.add(ml.replace("%zb_level%",zblevel+""));
                        else if(ml.contains("%zb_probability%"))nmsglore.add(ml.replace("%zb_probability%",probability+""));
                        else if(ml.contains("%zb_money%"))nmsglore.add(ml.replace("%zb_money%",needmoney+""));
                        else if(ml.contains("%zb_break%"))nmsglore.add(ml.replace("%zb_break%",isBreak?"损坏":"不损坏"));
                        else if(ml.contains("%zb_downlevel%"))nmsglore.add(ml.replace("%zb_downlevel%",downLevel<=0?"不降级":downLevel+""));
                        else if(ml.contains("%zb_needs%"))nmsglore.add(ml.replace("%zb_needs%",needs+""));
                        else if(ml.contains("%zb_lbhs%"))nmsglore.add(ml.replace("%zb_lbhs%",jjbhf.replace("%p%","?")));
                        else if(ml.contains("%zb_bbhs%"))nmsglore.add(ml.replace("%zb_bbhs%",bbbhf.replace("%p%","?")));
                        else if(ml.contains("%zb_zfcl%"))nmsglore.add(ml.replace("%zb_zfcl%",zfcl.replace("%p%","?")));
                        else if(ml.contains("%zb_lbhs_probability%"))nmsglore.add(ml.replace("%zb_lbhs_probability%",jjbhf_jl+""));
                        else if(ml.contains("%zb_bbhs_probability%"))nmsglore.add(ml.replace("%zb_bbhs_probability%",bbbhf_jl+""));
                        else if(ml.contains("%zb_zfcl_probability%"))nmsglore.add(ml.replace("%zb_zfcl_probability%",zfcl_jl+""));
                        else nmsglore.add(ml);
                    }
                    ItemStack msgItem = gui.getMsgItem(nmsglore);
                    for(int slot:gui.getMessageSlotList()){
                        inv.setItem(slot,msgItem);
                    }
                    Message.send(p,Message.path5_1);
                }
            }
        }
    }
    private double ct(String cl, double jl, String sign, List<ItemStack> items) {
        if(contain(cl,items)){
            for(ItemStack i:items){
                if(i.hasItemMeta()&&i.getItemMeta().hasLore()&&i.getItemMeta().hasDisplayName()){
                    if(ItemSet.nameEquals(cl,i.getItemMeta().getDisplayName())) {
                        jl += QHMethod.getAddition(i.getItemMeta().getLore(), sign);
                    }
                }
            }
        }
        return jl;
    }
    private void qh(Inventory inv,Gui gui,InventoryClickEvent e){
        Player p = (Player) e.getWhoClicked();
        ItemStack item = inv.getItem(gui.getMainItemSlotList().get(0));
        if(item==null||item.getType().equals(Material.AIR)){
            Message.send(p,Message.path4);
            return;
        }
        ItemMeta meta = item.getItemMeta();
        if(!meta.hasDisplayName()){
            return;
        }
        String name = meta.getDisplayName();
        ItemSet is = ItemSetManager.getItemSet(name);
        if(is==null){
            return;
        }
        QHMethod qhm = is.getQhMethod();
        QH qh = getQh(qhm);
        int level = qh.getQHLevel(meta.getLore()==null?new ArrayList<>():meta.getLore());
        if(level+1>qhm.getMaxLevel()){
            Message.send(p,Message.path5);
            return;
        }
        String thislevelLore = qh.getLevelLore(meta.getLore()==null? Collections.singletonList(qh.getQdSign() + level) :meta.getLore(),level);
        String nextlevelLore = qh.getQHLevel(level+1,thislevelLore);
        List<String> thisAttribute = level==0?new ArrayList<>() :qhm.getLevels().get(level).getAttribute();
        Level nextLevel = qhm.getLevels().get(level+1);
        List<String> nextAttribute = nextLevel.getAttribute();
        double probability = nextLevel.getProbability();
        int downlevel = nextLevel.getDownLevel();
        String cmd = nextLevel.getCommand();
        String msg = nextLevel.getMessage();
        Map<String,Integer> needs = nextLevel.getNeedMaterials();
        double needMoney = nextLevel.getNeedMoney();
        boolean isBreak = nextLevel.isBbreak();
        String addlore = qhm.getWriteLore();
        List<ItemStack> qhitems = getItems(inv,gui.getQhsSlotList());
        if(contains(needs,qhitems)){
            if(Vault.has((OfflinePlayer)e.getWhoClicked(),needMoney)){
                Vault.take((OfflinePlayer) e.getWhoClicked(),needMoney);
                removeItem(inv,needs,gui.getQhsSlotList());
                List<ItemStack> zflist = getItems(inv,gui.getZfclSlotList());
                double additon = 0D;
                if(contain(qhm.getZfcl_name(),zflist)&&probability<100D){
                    String sign = qhm.getZfcl_sign();
                    for(ItemStack zfitem:zflist){
                        if(zfitem.hasItemMeta()&&zfitem.getItemMeta().hasLore()&&zfitem.getItemMeta().hasDisplayName()){
                            if(ItemSet.nameEquals(qhm.getZfcl_name(),zfitem.getItemMeta().getDisplayName())) {
                                additon += QHMethod.getAddition(zfitem.getItemMeta().getLore(), sign);
                                removeItem(inv, zfitem);
                                if (additon + probability >= 100D) {
                                    break;
                                }
                            }
                        }
                    }
                }
                // 强化成功！
                if(isSuccess(probability,additon)){
                    List<String> lore = meta.hasLore()?meta.getLore():new ArrayList<>();
                    if(!lore.contains(thislevelLore)){
                        lore.add(nextlevelLore); }else{
                        lore.set(lore.indexOf(thislevelLore),nextlevelLore); }
                    if(!lore.contains(addlore)){lore.add(addlore);}
                    lore.removeAll(thisAttribute);
                    lore.addAll(lore.indexOf(addlore)+1,nextAttribute);
                    meta.setLore(lore);
                    item.setItemMeta(meta);
                    for(int slot:gui.getMainItemSlotList()){
                        inv.setItem(slot,item);
                    }
                    playerRunCommand(p,cmd);
                    sendAllMessage(msg,p,item,level);
                    Message.sends(p,Message.path6, new Object[]{Message.getItemShow(item),level+1},new String[]{name,(level+1)+""});
                }else{
                    int qk; // 1 损坏
                            // 2 防止了损坏 但是降级
                            // 3 防止了损坏，本身不降级
                            // 4 防止了损坏，防止了降级
                            // 5 本身不降级，本身不损坏
                            // 6 本身不损坏，降级
                            // 7 本身不损坏，防止了降级
                    if(isBreak){
                        List<ItemStack> bhblist = getItems(inv,gui.getBhs_BreakSlotList());
                        if(contain(qhm.getQhbhf_break_name(),bhblist)){
                            double randd = 0D;
                            String signd = qhm.getQhbhf_break_sign();
                            for(ItemStack bhitem:bhblist){
                                if(bhitem.hasItemMeta()&&bhitem.getItemMeta().hasLore()&&bhitem.getItemMeta().hasDisplayName()){
                                    if(ItemSet.nameEquals(qhm.getQhbhf_break_name(),bhitem.getItemMeta().getDisplayName())) {
                                        randd += QHMethod.getAddition(bhitem.getItemMeta().getLore(), signd);
                                        removeItem(inv, bhitem);
                                        if (randd >= 100D) {
                                            break;
                                        }
                                    }
                                }
                            }
                            if(isSuccess(randd,0)){
                                //保护损坏成功
                                if(downlevel>0) {
                                    List<ItemStack> bhllist = getItems(inv, gui.getBhs_LevelSlotList());
                                    if (contain(qhm.getQhbhf_level_name(), bhllist)) {
                                    double randl = 0D;
                                    randl = getRandl(inv, qhm, bhllist, randl);
                                    if (isSuccess(randl, 0)) {
                                        qk = 4;
                                    } else {
                                        //保护降级失败
                                        qk = 2;
                                    }
                                }else qk = 2;
                                }else{
                                    qk = 3;
                                }
                            }else{
                                qk = 1;
                            }
                        }else qk = 1;
                    }else{
                        if(downlevel>0) {
                            List<ItemStack> bhllist = getItems(inv,gui.getBhs_LevelSlotList());
                            double randl = 0D;
                            if (contain(qhm.getQhbhf_level_name(), bhllist)) {
                                randl = getRandl(inv, qhm, bhllist, randl);
                                if(isSuccess(randl,0)){
                                    // 本身不损坏，防止了降级
                                    qk = 7;
                                }else{
                                    //本身不损坏，保护降级失败
                                    qk = 6;
                                }
                            }else{
                                qk = 6;
                            }
                        }else{
                            qk = 5;
                        }
                    }
                    qhsb(p,qk,level,downlevel,addlore,thislevelLore,qh,qhm,inv,item);
                }
            }else{
                //no monry
                Message.send(p,Message.path2,needMoney+"");
            }
        }else{
            //no material
            Message.send(p,Message.path1);
        }
    }
    private double getRandl(Inventory inv, QHMethod qhm, List<ItemStack> bhllist, double randl) {
        if(contain(qhm.getQhbhf_level_name(),bhllist)){
            String signl = qhm.getQhbhf_level_sign();
            for(ItemStack bhitem:bhllist){
                if(bhitem.hasItemMeta()&&bhitem.getItemMeta().hasLore()&&bhitem.getItemMeta().hasDisplayName()){
                    if(ItemSet.nameEquals(qhm.getQhbhf_level_name(),bhitem.getItemMeta().getDisplayName())){
                        randl += QHMethod.getAddition(bhitem.getItemMeta().getLore(),signl);
                        removeItem(inv,bhitem);
                        if(randl>=100D){
                            break;
                        }
                    }
                }
            }
        }
        return randl;
    }
    private void qhsb(Player p, int qk, int level, int downLevel, String addlore, String levellore, QH qh, QHMethod qhm, Inventory inv, ItemStack item){
        String name = item.getItemMeta().getDisplayName();
        switch (qk){
            case 1://损坏
                inv.remove(item);
                Message.sends(p,Message.path7, new Object[]{Message.getItemShow(item),level+1},new String[]{name,(level+1)+""});
                break;
            case 2://防止了损坏，降级
                jj(qhm,level,downLevel,item,addlore,levellore,qh,inv);
                Message.sends(p,Message.path8, new Object[]{Message.getItemShow(item),level+1,level-downLevel<0?0:level-downLevel},new String[]{name,(level+1)+"",level-downLevel<0?"0":(level-downLevel)+""});
                break;
            case 3://防止了损坏，本身不降级
                Message.sends(p,Message.path9, new Object[]{Message.getItemShow(item),level+1},new String[]{name,(level+1)+""});
                break;
            case 4://防止了损坏，防止了降级
                Message.sends(p,Message.path10, new Object[]{Message.getItemShow(item),level+1},new String[]{name,(level+1)+""});
                break;
            case 5://本身不降级，本身不损坏
                Message.sends(p,Message.path11, new Object[]{Message.getItemShow(item),level+1},new String[]{name,(level+1)+""});
                break;
            case 6://本身不损坏，但没有防止降级
                jj(qhm,level,downLevel,item,addlore,levellore,qh,inv);
                Message.sends(p,Message.path12, new Object[]{Message.getItemShow(item),level-downLevel<0?0:level-downLevel},new String[]{name,level-downLevel<0?"0":(level-downLevel)+""});
                break;
            case 7://本身不损坏，防止了降级
                Message.sends(p,Message.path13, new Object[]{Message.getItemShow(item),level-downLevel<0?0:level-downLevel},new String[]{name,level-downLevel<0?"0":(level-downLevel)+""});
                break;
        }
    }
    private void jj(QHMethod qhm, int level, int downLevel, ItemStack item, String addlore, String levellore, QH qh, Inventory inv){
        List<String> thisAttribute;
        if(level<=0){
            thisAttribute = new ArrayList<>();
        }else thisAttribute = qhm.getLevels().get(level).getAttribute();
        int per = level-downLevel<0?0:level-downLevel;
        List<String> perAttribute;
        if(per>0){
            perAttribute = qhm.getLevels().get(per).getAttribute();
        }else{
            perAttribute = new ArrayList<>();
        }
        if(item.hasItemMeta()&&item.getItemMeta().hasLore()){
            ItemMeta meta = item.getItemMeta();
            List<String> lore = meta.getLore();
            lore.removeAll(thisAttribute);
            lore.addAll(lore.indexOf(addlore)+1,perAttribute);
            String perLevelLore = qh.getLevelLore(lore,per);
            lore.set(lore.indexOf(levellore),perLevelLore);
            meta.setLore(lore);
            item.setItemMeta(meta);
            for(int slot:qhm.getGui().getMainItemSlotList()){
                inv.setItem(slot,item);
            }
        }
    }
    private QH getQh(QHMethod qhm){
        QH qh;
        if(qhm.isQDT()){
            qh = qhm.getQdtqh();
        }else if(qhm.isWZ()){
            qh = qhm.getWzqh();
        }else{
            qh = qhm.getNumqh();
        }
        return qh;
    }
    private static void removeItem(Inventory inv,ItemStack itemStack){
        ItemStack itemClone = itemStack.clone();
        itemClone.setAmount(1);
        inv.removeItem(itemClone);
    }
    private List<ItemStack> getItems(Inventory inv,List<Integer> slots){
        List<ItemStack> items = new ArrayList<>();
        for(int slot:slots){
            ItemStack item = inv.getItem(slot);
            if(item!=null&&!item.getType().equals(Material.AIR)){
                items.add(item);
            }
        }
        return items;
    }
    private void removeItem(Inventory inv,Map<String,Integer> needs,List<Integer> slots){
        for(Map.Entry<String,Integer> nd:needs.entrySet()){
            int amount =0;
            int takeamount = nd.getValue();
            for(int slot:slots){
                ItemStack item = inv.getItem(slot);
                if(item!=null&&item.hasItemMeta()&&item.getItemMeta().hasDisplayName()){
                    String name = item.getItemMeta().getDisplayName();
                    if(ItemSet.nameEquals(nd.getKey(),name)){
                        if(amount+item.getAmount()<takeamount){
                            inv.remove(item);
                            amount+=item.getAmount();
                        }else{
                            item.setAmount((amount+item.getAmount())-takeamount);
                            break;
                        }
                    }
                }
            }
        }
    }
    private boolean isSuccess(double num,double additon){ return r.nextInt(101)<=num+additon; }
    private boolean contains(Map<String,Integer> needs,List<ItemStack> items){
        for(Map.Entry<String,Integer> nd:needs.entrySet()){
            int amount =0;
            for(ItemStack item:items){
                if(item.hasItemMeta()){
                    ItemMeta meta = item.getItemMeta();
                    if(meta.hasDisplayName()){
                        String name = meta.getDisplayName();
                        if(ItemSet.nameEquals(nd.getKey(),name)){
                            amount+=item.getAmount();
                            if(amount>=nd.getValue())break;
                        }
                    }
                }
            }
            if(amount<nd.getValue())return false;
        }
        return true;
    }
    private boolean contain(String itemSetName,List<ItemStack> items){
        for(ItemStack item:items){
            if(item.hasItemMeta()){
                ItemMeta meta = item.getItemMeta();
                if(meta.hasDisplayName()){
                    String name = meta.getDisplayName();
                    if(ItemSet.nameEquals(itemSetName,name)){
                        return true;
                    }
                }
            }
        }
        return false;
    }
    @EventHandler
    public void onClose(InventoryCloseEvent e){
        Player p = (Player) e.getPlayer();
        Inventory inv = e.getInventory();
        if(openGuiPlayers.containsKey(p)&&inv.getHolder() instanceof Holder){
            if(((Holder) inv.getHolder()).getType().equalsIgnoreCase("JJS")){
                Gui gui = GuiManager.getGui(openGuiPlayers.get(p));
                openGuiPlayers.remove(p);
                new CloseGuiEvent(e.getInventory(), gui);
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
                    Gui gui = GuiManager.getGui(openGuiPlayers.get(p));
                    openGuiPlayers.remove(p);
                    new CloseGuiEvent(e.getInventory(), gui);
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
                } else openGuiPlayers.remove(p);
            }
        }
    }
    private void playerRunCommand(Player player,String cmd){
        if(cmd==null||cmd.contains("[NOCMD]"))return;
        if(cmd.contains("%player%"))cmd=cmd.replace("%player%",player.getName());
        if(cmd.contains("[OP]")){
            cmd = cmd.replace("[OP]","");
            boolean isOp=player.isOp();
            try {
                player.setOp(true);
                Bukkit.getServer().dispatchCommand(player,cmd);
                player.setOp(isOp);
            } catch(Exception ignored){
            } finally {
                player.setOp(isOp);
            }
        }else if(cmd.contains("[SERVER]")){
            cmd = cmd.replace("[SERVER]","");
            Bukkit.getServer().dispatchCommand(Bukkit.getServer().getConsoleSender(), cmd);
        }else Bukkit.getServer().dispatchCommand(player, cmd);
    }
    private void sendAllMessage(String msg,Player player,ItemStack item,int level){
        if(!msg.contains("[NOMSG]")){
            msg = msg.replace("[NOMSG]","");
            for(Player p:Bukkit.getOnlinePlayers()){
                Message.sends(p,msg, new Object[]{player.getName(),Message.getItemShow(item),level+1},new String[]{player.getName(),item.getItemMeta().getDisplayName(),(level+1)+""});
            }
        }
    }
}
