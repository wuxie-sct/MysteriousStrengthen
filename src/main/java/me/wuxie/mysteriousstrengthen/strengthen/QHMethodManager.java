package me.wuxie.mysteriousstrengthen.strengthen;

import lombok.Getter;
import org.bukkit.Bukkit;
import me.wuxie.mysteriousstrengthen.MysteriousStrengthen;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class QHMethodManager {
    private File qhMethoodsFile = new File("plugins/MysteriousStrengthen/方案");
    private File[] qhMethoodsFiles = new File[0];
    @Getter
    private static List<QHMethod> qhMethods;
    private MysteriousStrengthen plugin;
    public QHMethodManager(MysteriousStrengthen plugin){
        this.plugin =plugin;
        if(!qhMethoodsFile.exists()){
            plugin.saveResource("方案/测试强化方案.yml",true);
        }
        loadQHMethood();
    }
    public void loadQHMethood(){
        qhMethods = new ArrayList<>();
        if(qhMethoodsFile.isDirectory()&&qhMethoodsFile.exists()){
            try {
                qhMethoodsFiles = qhMethoodsFile.listFiles();
            }catch (Exception ignored){
                Bukkit.getConsoleSender().sendMessage("§4§lGuiManager 发生错误！");
            }
            if(qhMethoodsFiles==null||qhMethoodsFiles.length==0){
                qhMethoodsFiles=new File[0];
            }
            int a=0;
            for(File file:qhMethoodsFiles){
                ++a;
                qhMethods.add(new QHMethod(file));
            }
            Bukkit.getConsoleSender().sendMessage("§c§l共加载了 §a§l"+a+" §c§l个方案文件！");
        }else{
            Bukkit.getConsoleSender().sendMessage("§4§lQHMethodManager 发生错误！路径错误！文件不存在！");
        }
    }
    public static QHMethod getQHMethood(String qhmethood){
        for(QHMethod qhMethod : qhMethods){
            if(qhMethod.getFile().getName().equalsIgnoreCase(qhmethood+".yml")){
                return qhMethod;
            }
        }
        return null;
    }
}
