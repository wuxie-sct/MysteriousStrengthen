package me.wuxie.mysteriousstrengthen.strengthen.qd;

import org.bukkit.Bukkit;
import me.wuxie.mysteriousstrengthen.strengthen.QHMethood;

import java.util.List;

public class NUMQH extends QH {
    public NUMQH(String qdSign) {
        this.qdSign = qdSign;
    }

    @Override
    public void setLevels() {

    }

    public int getQHLevel(List<String> lore) {
        int num = 0;
        for (String l : lore) {
            if (l.contains(qdSign)) {
                try {
                    double d = Double.valueOf(QHMethood.getNumber(l));
                    num = (int) d;
                } catch (Exception e) {
                    e.printStackTrace();
                    Bukkit.getConsoleSender().sendMessage("§c§l强化中获取装备强化度失败！");
                    Bukkit.getConsoleSender().sendMessage("§c§l可能是修改了强度显示的原因，此处为读取数字型强度！装备显示的可能是其它类型强度！");
                }
                return num < 0 ? 0 : num;
            }
        }
        return 0;
    }

    @Override
    public String getQHLevel(int qd, String lore) {
        String qd1 = QHMethood.getNumber(lore) + "";
        lore = lore.replace(qd1, qd + "");
        return lore;
    }

    @Override
    public String getLevelLore(List<String> lore,int level) {
        for (String l : lore) {
            if (l.contains(qdSign)) {
                return getQHLevel(level, l);
            }
        }
        return null;
    }
}