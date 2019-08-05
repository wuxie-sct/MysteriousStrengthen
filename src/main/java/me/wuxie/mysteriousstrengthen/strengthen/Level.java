package me.wuxie.mysteriousstrengthen.strengthen;

import lombok.Getter;

import java.util.List;
import java.util.Map;

public class Level {
    @Getter
    private int level;
    @Getter
    private double needMoney;
    @Getter
    private double probability;
    @Getter
    private boolean bbreak;
    @Getter
    private int downLevel;
    @Getter
    private String message;
    @Getter
    private String command;
    @Getter
    private Map<String,Integer> needMaterials;
    @Getter
    private List<String> attribute;
    public Level(int level, double needMoney, double probability, boolean bbreak, int downLevel, String message, String command, Map<String,Integer> needMaterials, List<String> attribute){
        this.attribute = attribute;
        this.bbreak = bbreak;
        this.command = command;
        this.downLevel = downLevel;
        this.message = message;
        this.probability = probability;
        this.level = level;
        this.needMaterials = needMaterials;
        this.needMoney = needMoney;
    }
}
