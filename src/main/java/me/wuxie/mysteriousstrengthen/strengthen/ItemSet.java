package me.wuxie.mysteriousstrengthen.strengthen;

import lombok.Getter;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.Iterator;

public class ItemSet {
    @Getter
    private String name;
    @Getter
    private QHMethood qhMethood;
    private String[] nameSplit;

    ItemSet(String name, String programme){
        this.name = name;
        nameSplit = name.split("%p%");
        qhMethood = QHMethoodManager.getQHMethood(programme);
    }
    private boolean isContain(){
        return false;
    }

    public boolean nameEquals(String name){
        if(ChatColor.getLastColors(this.name.replace("&","§")).equalsIgnoreCase("")){
            name = ChatColor.stripColor(name);
        }
        StringBuilder sb = new StringBuilder();
        Iterator<String> i = Arrays.asList(nameSplit).iterator();
        return isXt(name, sb, i) && sb.toString().equalsIgnoreCase(this.name);
    }

    /**
     * @param setName 有变量的名称
     * @param itemName 实例物品名称
     * @return 是否相同
     */
    public static boolean nameEquals(String setName,String itemName){
        if(ChatColor.getLastColors(setName.replace("&","§")).equalsIgnoreCase("")){
            itemName = ChatColor.stripColor(itemName);
        }
        StringBuilder sb = new StringBuilder();
        Iterator<String> i = Arrays.asList(setName.split("%p%")).iterator();
        return isXt(itemName, sb, i) && sb.toString().equalsIgnoreCase(setName);
    }

    private static boolean isXt(String itemName, StringBuilder sb, Iterator<String> i) {
        while (i.hasNext()){
            String str = i.next();
            if(!itemName.contains(str)){
                return false;
            }else{
                sb.append(str);
                if(i.hasNext())sb.append("%p%");
            }
        }
        return true;
    }
}
