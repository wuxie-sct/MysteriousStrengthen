package me.wuxie.mysteriousstrengthen.strengthen;

import lombok.Getter;
import me.wuxie.mysteriousstrengthen.gui.Gui;
import me.wuxie.mysteriousstrengthen.gui.GuiManager;
import me.wuxie.mysteriousstrengthen.strengthen.qd.NUMQH;
import me.wuxie.mysteriousstrengthen.strengthen.qd.QDTQH;
import me.wuxie.mysteriousstrengthen.strengthen.qd.WZQH;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QHMethood {
    @Getter
    private Gui gui;
    @Getter
    private boolean QDT = false;
    @Getter
    private boolean WZ = false;
    @Getter
    private int maxLevel;
    @Getter
    private String writeLore;
    @Getter
    private String qdSign;
    @Getter
    private String qhbhf_break_name;
    @Getter
    private String qhbhf_break_sign;
    @Getter
    private String qhbhf_level_sign;
    @Getter
    private String qhbhf_level_name;
    @Getter
    private String zfcl_name;
    @Getter
    private String zfcl_sign;
    @Getter
    private QDTQH qdtqh;
    @Getter
    private WZQH wzqh;
    @Getter
    private NUMQH numqh;
    @Getter
    private YamlConfiguration qhMethoodYaml;
    @Getter
    private File file;
    @Getter
    private Map<Integer, Level> levels;
    public QHMethood(File file){
        this.file = file;
        Bukkit.getConsoleSender().sendMessage("§aload Method §c"+file.getName());
        loadMethood();
    }
    public void loadMethood(){
        qhMethoodYaml = YamlConfiguration.loadConfiguration(file);
        writeLore = qhMethoodYaml.getString("强化属性书写在的lore下").replace("&","§");
        qdSign = qhMethoodYaml.getString("强度.获取强度").replace("&","§");
        maxLevel = qhMethoodYaml.getInt("强度.满级");
        QDT = qhMethoodYaml.get("强度.强度条") != null && qhMethoodYaml.getBoolean("强度.强度条.开启");
        WZ = qhMethoodYaml.get("强度.文字强度") != null && qhMethoodYaml.getBoolean("强度.文字强度.开启");
        gui = GuiManager.getGui(qhMethoodYaml.getString("使用的GUI"));
        qhbhf_break_name =ItemSetManager.getItemName(qhMethoodYaml.getString("保护材料.损坏保护石.物品名编号")).replace("&","§");;
        qhbhf_level_name =ItemSetManager.getItemName(qhMethoodYaml.getString("保护材料.降级保护石.物品名编号")).replace("&","§");
        zfcl_name =ItemSetManager.getItemName(qhMethoodYaml.getString("保护材料.提升几率石.物品名编号")).replace("&","§");
        qhbhf_break_sign = qhMethoodYaml.getString("保护材料.损坏保护石.几率.获取lore");
        qhbhf_level_sign = qhMethoodYaml.getString("保护材料.降级保护石.几率.获取lore");
        zfcl_sign = qhMethoodYaml.getString("保护材料.提升几率石.几率.获取lore");
        if(QDT) loadQDTQH();else if(WZ) loadWZQH();else loadNUMQH();
        loadAllLevels();
    }

    private void loadQDTQH(){
        Object obj = qhMethoodYaml.get("强度.强度条");
        String mainString = obj==null?"X":qhMethoodYaml.getString("强度.强度条.符号");
        String prefix = obj==null?"[null]":qhMethoodYaml.getString("强度.强度条.前缀");
        String suffix = obj==null?"[null]":qhMethoodYaml.getString("强度.强度条.后缀");
        String colors = obj==null?"44444444444444444444":qhMethoodYaml.getString("强度.强度条.强化颜色");
        String backgroundColor = obj==null?"8":qhMethoodYaml.getString("强度.强度条.背景颜色");
        qdtqh = new QDTQH(qdSign,maxLevel,mainString,prefix,suffix,colors,backgroundColor);
    }
    private void loadWZQH(){
        Object obj = qhMethoodYaml.get("强度.文字强度");
        String wzLevel =obj==null?"n,u,l,l,j,k,n,m,g,c,d,e,y,f":qhMethoodYaml.getString("强度.文字强度.文字");
        wzqh = new WZQH(qdSign,maxLevel,wzLevel);
    }
    private void loadNUMQH(){
        numqh = new NUMQH(qdSign);
    }
    private static List<String> getlore(List<String> lore){
        List<String> list = new ArrayList<>();
        for(String s:lore){
            list.add(s.replaceAll("&","§"));
        }
        return list;
    }
    private void loadAllLevels(){
        levels = new HashMap<>();
        int a =0;
        for(String key:qhMethoodYaml.getConfigurationSection("等级").getKeys(false)){
            ++a;
            int level = Integer.parseInt(key);
            double needmoney = qhMethoodYaml.getDouble("等级."+key+".需要钱数");
            double probaby = qhMethoodYaml.getDouble("等级."+key+".成功几率");
            boolean isbreak = qhMethoodYaml.getBoolean("等级."+key+".失败损坏");
            int downlevel = qhMethoodYaml.getInt("等级."+key+".失败降级数");
            String msg = qhMethoodYaml.getString("等级."+key+".全服消息");
            String cmd = qhMethoodYaml.getString("等级."+key+".成功命令");
            Map<String,Integer> needs = new HashMap<>();
            for(String key1:qhMethoodYaml.getConfigurationSection("等级."+key+".强化材料").getKeys(false)){
                needs.put(ItemSetManager.getItemName(key1),qhMethoodYaml.getInt("等级."+key+".强化材料."+key1));
            }
            List<String> attributes = getlore(qhMethoodYaml.getStringList("等级."+key+".强化属性"));
            Level l= new Level(level,needmoney,probaby,isbreak,downlevel,msg,cmd,needs,attributes);
            levels.put(level,l);
        }
        if(a<maxLevel){
            Bukkit.getConsoleSender().sendMessage("§4强化方案配置文件 "+file.getName()+" 有很大的问题!");
            Bukkit.getConsoleSender().sendMessage("§4定义的最大等级大于已分配的等级方案！既已分配方案不足以支撑强化到最终结果！");
        }
    }

    public static double getAddition(List<String> lore,String sign){
        double num=0D;
        for(String l:lore){
            if(l.contains(sign)){
                num+=Double.valueOf(getNumber(l));
            }
        }
        return num>100?100D:num;
    }
    /**
     * 获取属性值
     *
     * @param lore String
     * @return String
     */
    public static String getNumber(String lore) {
        String str = lore.replaceAll("§+[a-z0-9]", "").replaceAll("[^-0-9.]", "");
        return str.length() == 0 || str.replaceAll("[^.]", "").length() > 1 ? "0" : str;
    }
}
