package me.wuxie.mysteriousstrengthen.jinjie;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import me.wuxie.mysteriousstrengthen.MysteriousStrengthen;
import me.wuxie.mysteriousstrengthen.strengthen.ItemSet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JinJieManager {
    private static File jjFile = new File("plugins/MysteriousStrengthen/进阶/进阶方案");
    private static File jjiFile = new File("plugins/MysteriousStrengthen/进阶/进阶物品库.yml");
    @Getter
    private static YamlConfiguration jjiy;
    public static void savejji(){
        try {
            jjiy.save(jjiFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private MysteriousStrengthen plugin;
    @Getter
    private static List<JinJie> jJs;
    @Getter
    private static Map<String, ItemStack> jjItems;
    public JinJieManager(MysteriousStrengthen plugin){
        this.plugin = plugin;
        if(!jjFile.exists()){
            plugin.saveResource("进阶/进阶方案/测试进阶.yml",true);
        }
        if(!jjiFile.exists()){
            plugin.saveResource("进阶/进阶物品库.yml",true);
            Bukkit.getConsoleSender().sendMessage("§a保存 进阶物品库.yml");
        }
        loadJJ();
    }
    public void loadJJ(){
        jJs = new ArrayList<>();
        jjItems = new HashMap<>();
        if(jjFile.isDirectory()&&jjFile.exists()){
            File[] jjFiles = jjFile.listFiles();
            if(jjFiles ==null|| jjFiles.length==0){
                jjFiles =new File[0];
            }
            int a=0;
            for(File file: jjFiles){
                if(file.getName().contains(".yml")) {
                    ++a;
                    try {
                        jJs.add(new JinJie(file));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
            Bukkit.getConsoleSender().sendMessage("§c§l共加载了 §a§l"+a+" §c§l个进阶方案文件！");
        }
        if(jjiFile.exists()){
            jjiy = YamlConfiguration.loadConfiguration(jjiFile);
            for (String key:jjiy.getKeys(false)){
                jjItems.put(key,jjiy.getItemStack(key));
            }
        }
    }
    public static JinJie getJinJie(ItemStack is){
        if(is == null)return null;
        if(!is.hasItemMeta())return null;
        if(!is.getItemMeta().hasDisplayName())return null;
        String name = is.getItemMeta().getDisplayName();
        for(JinJie j:jJs){
            if(ItemSet.nameEquals(j.getZb_name(),name)){
                return j;
            }
        }
        return null;
    }
}
