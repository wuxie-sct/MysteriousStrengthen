package me.wuxie.mysteriousstrengthen.strengthen.qd;

import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WZQH extends QH{
    @Getter
    private int level ;
    @Getter
    private String wzlevels;
    @Getter
    private Map<Integer,String> levels = new HashMap<>();
    public WZQH(String qdSign,int level,String wzlevels){
        this.qdSign=qdSign;
        this.level=level;
        this.wzlevels=wzlevels;
        setLevels();
    }

    @Override
    public void setLevels(){
        levels = new HashMap<>();
        String[] levelss = wzlevels.replace("&","§").split(",");
        for(int qd=0;qd<=level;qd++){
            String sb = qdSign + levelss[qd];
            levels.put(qd, sb.replace("&","§"));
        }
    }
    @Override
    public int getQHLevel(List<String> lore){
        for(String l:lore){
            if(l.contains(qdSign)){
                for(Map.Entry<Integer,String> mp:levels.entrySet()){
                    if(mp.getValue().equalsIgnoreCase(l)||mp.getValue().contains(l)){
                        return mp.getKey();
                    }
                }
            }
        }
        return 0;
    }
    @Override
    public String getQHLevel(int qd,String lore){
        return levels.get(qd);
    }

    @Override
    public String getLevelLore(List<String> lore,int level) {
        return getQHLevel(level,null);
    }
}
