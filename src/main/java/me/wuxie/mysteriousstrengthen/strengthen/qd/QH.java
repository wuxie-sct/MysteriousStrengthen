package me.wuxie.mysteriousstrengthen.strengthen.qd;

import lombok.Getter;

import java.util.List;

public abstract class QH {
    @Getter
    String qdSign;
    public abstract void setLevels();
    public abstract int getQHLevel(List<String> lore);
    public abstract String getQHLevel(int qd,String lore);
    public abstract String getLevelLore(List<String> lore,int level);
}
