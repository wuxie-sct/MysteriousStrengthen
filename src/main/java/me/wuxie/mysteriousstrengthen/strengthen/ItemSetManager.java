package me.wuxie.mysteriousstrengthen.strengthen;

import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;
import me.wuxie.mysteriousstrengthen.MysteriousStrengthen;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ItemSetManager {
    private static File itemSetFile = new File("plugins/MysteriousStrengthen/可强化装备设定.yml");
    private static File itemsNameFile = new File("plugins/MysteriousStrengthen/强化材料设定.yml");
    @Getter
    private static YamlConfiguration itemSetYaml;
    @Getter
    private static YamlConfiguration itemsNameYaml;
    private MysteriousStrengthen plugin;
    @Getter
    private static List<ItemSet> itemSetList;
    public ItemSetManager(MysteriousStrengthen plugin){
        this.plugin = plugin;
        loadItemName();
    }
    public void loadItemSet(){
        itemSetList = new ArrayList<>();
        if(!itemSetFile.exists()){
            plugin.saveResource("可强化装备设定.yml",true);
            itemSetYaml = YamlConfiguration.loadConfiguration(itemSetFile);
        }else{
            itemSetYaml = YamlConfiguration.loadConfiguration(itemSetFile);
        }
        getItemSets();
    }
    public void loadItemName(){
        itemsNameYaml = new YamlConfiguration();
        if(!itemsNameFile.exists()){
            plugin.saveResource("强化材料设定.yml",true);
            itemsNameYaml = YamlConfiguration.loadConfiguration(itemsNameFile);
        }else{
            itemsNameYaml = YamlConfiguration.loadConfiguration(itemsNameFile);
        }
    }
    private void getItemSets(){
        for(String key:itemSetYaml.getKeys(false)){
            ItemSet is = new ItemSet(itemSetYaml.getString(key+".装备名称"),itemSetYaml.getString(key+".强化方案"));
            itemSetList.add(is);
        }
    }
    public static ItemSet getItemSet(String name){
        for(ItemSet is:itemSetList){
            if(is.nameEquals(name)){
                return is;
            }
        }
        return null;
    }
    public static String getItemName(String key){
        return itemsNameYaml.getString(key);
    }
}
