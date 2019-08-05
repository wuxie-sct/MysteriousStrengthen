package me.wuxie.mysteriousstrengthen.gui;

import lombok.Getter;
import org.bukkit.Bukkit;
import me.wuxie.mysteriousstrengthen.MysteriousStrengthen;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GuiManager {
    private File qhguiFile = new File("plugins/MysteriousStrengthen/GUI");
    private File jjguiFile = new File("plugins/MysteriousStrengthen/进阶/进阶GUI");
    private File jjsguiFile = new File("plugins/MysteriousStrengthen/进阶/进阶选择GUI");
    private File[] qhguiFiles = new File[0];
    private File[] jjguiFiles = new File[0];
    private File[] jjsguiFiles = new File[0];
    @Getter
    private static List<Gui> guis;
    private MysteriousStrengthen plugin;
    public GuiManager(MysteriousStrengthen plugin){
        this.plugin = plugin;
        if(!qhguiFile.exists()){
            plugin.saveResource("GUI/测试GUI.yml",true);
        }
        if(!jjguiFile.exists()){
            plugin.saveResource("进阶/进阶GUI/测试进阶GUI.yml",true);
        }
        if(!jjsguiFile.exists()){
            plugin.saveResource("进阶/进阶选择GUI/测试进阶选择GUI.yml",true);
        }
        loadGuis();
    }
    public void loadGuis(){
        guis = new ArrayList<>();
        int a=0;
        if(qhguiFile.isDirectory()&&qhguiFile.exists()){
            try {
                qhguiFiles = qhguiFile.listFiles();
            }catch (Exception ignored){
                Bukkit.getConsoleSender().sendMessage("§4§lGuiManager 发生错误！");
            }
            if(qhguiFiles==null||qhguiFiles.length==0){
                qhguiFiles=new File[0];
            }
            for(File file:qhguiFiles){
                ++a;
                guis.add(new QHGui(file));
            }
        }else{
            Bukkit.getConsoleSender().sendMessage("§4§lGuiManager 发生错误！路径错误！文件不存在！");
        }
        if(jjguiFile.isDirectory()&&jjguiFile.exists()){
            try {
                jjguiFiles = jjguiFile.listFiles();
            }catch (Exception ignored){
                Bukkit.getConsoleSender().sendMessage("§4§lGuiManager 发生错误！");
            }
            if(jjguiFiles==null||jjguiFiles.length==0){
                jjguiFiles=new File[0];
            }
            for(File file:jjguiFiles){
                ++a;
                guis.add(new JJGui(file));
            }
        }else{
            Bukkit.getConsoleSender().sendMessage("§4§lGuiManager 发生错误！路径错误！文件不存在！");
        }
        if(jjsguiFile.isDirectory()&&jjsguiFile.exists()){
            try {
                jjsguiFiles = jjsguiFile.listFiles();
            }catch (Exception ignored){
                Bukkit.getConsoleSender().sendMessage("§4§lGuiManager 发生错误！");
            }
            if(jjsguiFiles==null||jjsguiFiles.length==0){
                jjsguiFiles=new File[0];
            }
            for(File file:jjsguiFiles){
                ++a;
                guis.add(new JJSGui(file));
            }
        }else{
            Bukkit.getConsoleSender().sendMessage("§4§lGuiManager 发生错误！路径错误！文件不存在！");
        }
        Bukkit.getConsoleSender().sendMessage("§c§l共加载了 §a§l"+a+" §c§l个GUI文件！");
    }
    public static Gui getGui(String fileName){
        for(Gui gui:guis){
            if(gui.getFileName().equalsIgnoreCase(fileName)){
                return gui;
            }
        }
        return null;
    }
}
