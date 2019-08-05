package me.wuxie.mysteriousstrengthen.util;

import lombok.Getter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import me.wuxie.mysteriousstrengthen.MysteriousStrengthen;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class Message {
    public static String path1 = "物品消息.材料不够";
    public static String path2 = "物品消息.金钱不够";
    public static String path4 = "物品消息.获取装备出错";
    public static String path5 = "物品消息.达到满级";
    public static String path5_1 = "物品消息.装备信息";

    public static String path6 = "强化消息.成功";
    public static String path7 = "强化消息.失败.损坏";
    public static String path8 = "强化消息.失败.防止了损坏降级";
    public static String path9 = "强化消息.失败.防止了损坏不降级";
    public static String path10 = "强化消息.失败.防止了损坏防止了降级";
    public static String path11 = "强化消息.失败.不损坏不降级";
    public static String path12 = "强化消息.失败.不损坏降级";
    public static String path13 = "强化消息.失败.不损坏防止了降级";

    public static String path14 = "进阶消息.材料不够";
    public static String path15 = "进阶消息.金钱不够";
    public static String path16 = "进阶消息.强度不够";
    public static String path17 = "进阶消息.装备信息";
    public static String path19 = "进阶消息.成功.1";
    public static String path20 = "进阶消息.成功.2";

    public static String path21 = "进阶消息.失败.损坏";
    public static String path22 = "进阶消息.失败.防止了损坏";
    public static String path23 = "进阶消息.失败.不损坏";

    public static String path24 = "命令消息.空手";
    public static String path25 = "命令消息.叠加";
    public static String path26 = "命令消息.不支持强化";
    public static String path27 = "命令消息.不支持进阶";
    public static String path28 = "命令消息.GUI不存在";
    public static String path29 = "命令消息.权限";
    public static String path30 = "命令消息.重载";


    private static File msgFile = new File("plugins/MysteriousStrengthen/消息.yml");
    private MysteriousStrengthen plugin;
    public Message(MysteriousStrengthen plugin){
        this.plugin =plugin;
        loadMessage();
    }
    @Getter
    private static YamlConfiguration message;
    public void loadMessage(){
        if(!msgFile.exists()){
            plugin.saveResource("消息.yml",true);
        }
        message = YamlConfiguration.loadConfiguration(msgFile);
        Bukkit.getConsoleSender().sendMessage("§amessage is loaded!");
    }
    public static String getMsg(String loc,String... args) {
        String raw = message.getString(loc);
        if (raw == null)raw = loc.replace("&", "§");
        if (args == null) {
            return raw.replace("&", "§");
        } else {
            for (int i = 0; i < args.length; ++i) {
                raw = raw.replace("{" + i + "}", args[i] == null ? "null" : args[i]);
            }
            return raw.replace("&", "§");
        }
    }
    public static void sends(Object entity, String loc, Object...args){
        Object[] o = (Object[]) args[0];
        String[] p = (String[]) args[1];
        String msg = getMsg(loc);
        if(msg.contains("[TEXT]")){
            Message.sendT(entity,loc,o);
        }else{
            Message.send(entity,loc,p);
        }
    }
    /**
     * 转换消息为TextComponent
     *
     * @param message    String
     * @param command    String
     * @param stringList List
     * @return TextComponent
     */
    public static TextComponent getTextComponent(String message, String command, List<String> stringList) {
        TextComponent tcMessage = new TextComponent(message);
        if (stringList != null && stringList.size() > 0) {
            ComponentBuilder bc = new ComponentBuilder("§7" + stringList.get(0).replace("&", "§"));
            IntStream.range(1, stringList.size()).mapToObj(i -> "\n§7" + stringList.get(i).replace("&", "§")).forEach(bc::append);
            tcMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, bc.create()));
        }
        if (command != null) {
            tcMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        }
        return tcMessage;
    }
    public static TextComponent getItemShow(ItemStack item){
        if(item.hasItemMeta()){
            ItemMeta meta = item.getItemMeta();
            List<String> lore = new ArrayList<>();
            String name;
            if(meta.hasDisplayName())name = meta.getDisplayName();else name = item.getType().toString();
            if(meta.hasLore())lore = meta.getLore();
            TextComponent tcMessage;
            lore.add(0, name);
            tcMessage = getTextComponent("§e["+ name+"§e]",null,lore);
            return tcMessage;
        }
        return new TextComponent();
    }
    public static TextComponent getMsg(String loc,Object... args) {
        String msg = message.getString(loc);
        if (msg == null)msg = loc;
        msg = msg.replace("&", "§").replace("[TEXT]","");
        if (args == null) {
            return new TextComponent(msg);
        } else {
            for (int i = 0; i < args.length; ++i) {
                msg = msg.replace("{" + i + "}", args[i] == null ? "null" : "<"+i+">");
            }
            TextComponent tc = new TextComponent();
            for (int y = 0; y < args.length; ++y) {
                if(msg.contains("<"+y+">")){
                    String[] msfs = msg.split("<"+y+">");
                    tc.addExtra(msfs[0]);
                    if(args[y] instanceof TextComponent){
                        tc.addExtra((TextComponent)args[y]);
                    }else{
                        tc.addExtra("§a"+args[y].toString());
                    }
                    msg = msg.replace(msfs[0],"");
                    msg = msg.replace("<"+y+">","");
                }
            }
            tc.addExtra(msg);
            return tc;
        }
    }

    public static void send(Object entity, String loc, String... args) {
        if (entity instanceof Player) {
            Player player = (Player) entity;
            String message = getMsg(loc, args);
            if (message.contains("[ACTIONBAR]")) {
                message = message.replace("[ACTIONBAR]", "");
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
            } else if (message.contains("[TITLE]")) {
                message = message.replace("[TITLE]", "");
                if (message.contains(":")) {
                    String title = message.split(":")[0];
                    String subTitle = message.split(":")[1];
                    player.sendTitle(title, subTitle);
                } else {
                    player.sendTitle(message, null);
                }
            } else {
                player.sendMessage(message);
            }
        } else {
            Bukkit.getConsoleSender().sendMessage(getMsg(loc, args));
        }
    }
    public static void sendT(Object entity, String loc, Object... args) {
        if (entity instanceof Player) {
            Player player = (Player) entity;
            TextComponent message = getMsg(loc, args);
            player.spigot().sendMessage(message);
        }
    }
}
