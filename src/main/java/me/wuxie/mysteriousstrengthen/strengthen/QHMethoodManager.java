package me.wuxie.mysteriousstrengthen.strengthen;

import lombok.Getter;
import org.bukkit.Bukkit;
import me.wuxie.mysteriousstrengthen.MysteriousStrengthen;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class QHMethoodManager {
    private File qhMethoodsFile = new File("plugins/MysteriousStrengthen/方案");
    private File[] qhMethoodsFiles = new File[0];
    @Getter
    private static List<QHMethood> qhMethoods;
    private MysteriousStrengthen plugin;
    public QHMethoodManager(MysteriousStrengthen plugin){
        this.plugin =plugin;
        if(!qhMethoodsFile.exists()){
            plugin.saveResource("方案/测试强化方案.yml",true);
        }
        loadQHMethood();
    }
    public void loadQHMethood(){
        qhMethoods = new ArrayList<>();
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
                qhMethoods.add(new QHMethood(file));
            }
            Bukkit.getConsoleSender().sendMessage("§c§l共加载了 §a§l"+a+" §c§l个方案文件！");
        }else{
            Bukkit.getConsoleSender().sendMessage("§4§lQHMethodManager 发生错误！路径错误！文件不存在！");
        }
    }
    public static QHMethood getQHMethood(String qhmethood){
        for(QHMethood qhMethood:qhMethoods){
            if(qhMethood.getFile().getName().equalsIgnoreCase(qhmethood+".yml")){
                return qhMethood;
            }
        }
        return null;
    }
}
