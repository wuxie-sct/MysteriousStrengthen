package me.wuxie.mysteriousstrengthen.jinjie;

import lombok.Getter;
import me.wuxie.mysteriousstrengthen.gui.Gui;
import me.wuxie.mysteriousstrengthen.gui.GuiManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import me.wuxie.mysteriousstrengthen.strengthen.ItemSetManager;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JinJie {
    @Getter private double money;
    @Getter private int need_level;
    @Getter private String zb_name;
    @Getter private Gui jjgui;
    @Getter private Gui jjsgui;
    @Getter private boolean breakk;
    @Getter private double level_probability;
    @Getter private double probability;
    @Getter private Map<String,Integer> needMaterials;
    @Getter private String zfcl;
    @Getter private String zfcl_sign;
    @Getter private String bhcl;
    @Getter private String bhcl_sign;
    @Getter private String cmd;
    @Getter private String msg;
    @Getter private List<String> jjitems;
    @Getter private YamlConfiguration jY;
    public JinJie(File file){
        jY = YamlConfiguration.loadConfiguration(file);
        jjgui = GuiManager.getGui(jY.getString("使用的GUI"));
        jjsgui = GuiManager.getGui(jY.getString("进阶选择GUI"));
        money = jY.getDouble("需求金币");
        need_level = jY.getInt("需求强度等级");
        zb_name = jY.getString("装备名称");
        probability = jY.getDouble("成功几率");
        breakk = jY.getBoolean("失败损坏");
        level_probability = jY.getDouble("等级增加成功几率");
        needMaterials = new HashMap<>();
        for(String key:jY.getConfigurationSection("进阶材料").getKeys(false)){
            needMaterials.put(ItemSetManager.getItemName(key),jY.getInt("进阶材料."+key));
        }
        zfcl = jY.getString("成功几率石.名称");
        zfcl_sign = jY.getString("成功几率石.几率获取lore");
        bhcl = jY.getString("损坏保护石.名称");
        bhcl_sign = jY.getString("损坏保护石.几率获取lore");
        cmd = jY.getString("成功命令");
        msg = jY.getString("全服消息");
        jjitems = jY.getStringList("进阶成功后的物品");
        Bukkit.getConsoleSender().sendMessage("§aload JinJie §c"+file.getName());
    }
}
