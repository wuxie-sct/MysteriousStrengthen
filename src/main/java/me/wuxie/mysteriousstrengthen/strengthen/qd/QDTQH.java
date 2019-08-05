package me.wuxie.mysteriousstrengthen.strengthen.qd;

import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QDTQH extends QH{
    @Getter
    private int level;
    @Getter
    private String mainString;
    @Getter
    private String prefix;
    @Getter
    private String suffix;
    @Getter
    private String colors;
    @Getter
    private String backgroundcolor;
    @Getter
    private Map<Integer,String> levels;
    public QDTQH(String qdSign,int level,String mainString,String prefix,String suffix,String colors,String backgroundcolor){
        this.qdSign = qdSign;
        this.level = level;
        this.mainString = mainString;
        this.prefix = prefix;
        this.suffix = suffix;
        this.colors = colors;
        this.backgroundcolor = backgroundcolor;
        setLevels();
    }
    @Override
    public void setLevels(){
        levels = new HashMap<>();
        ArrayList<Character> qdt = new ArrayList<>();
        char charColor[] = colors.toCharArray();
        for(int qd =0;qd<level;qd++){
            qdt.add(mainString.toCharArray()[0]);
        }
        for(int qd =0;qd<=level;qd++){
            StringBuilder sb = new StringBuilder();
            sb.append(qdSign);
            sb.append(prefix);
            if(qd==0){
                sb.append("§");
                sb.append(backgroundcolor);
                sb.append(qdt.toString().replace("[","").replace("]","").replace(", ","").replace(" ",""));
            }else{
                sb.append("§");
                sb.append(charColor[qd-1]);
                ArrayList<Character> qdtc = new ArrayList<>(qdt);
                if(qd<level) {
                    qdtc.add(qd, '§');
                    qdtc.add(qd+1, backgroundcolor.toCharArray()[0]);
                }
                sb.append(qdtc.toString().replace("[","").replace("]","").replace(", ","").replace(" ",""));
            }
            sb.append(suffix);
            levels.put(qd,sb.toString().replace("&","§"));
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
