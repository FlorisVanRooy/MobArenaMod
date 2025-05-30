package com.derp.derpymod.arena.permanentskilltree;

import java.util.Map;

public class SkillNode {
    public final String upgradeId;
    public final int x, y;                        // pixel coords on canvas
    public final Map<String,Integer> prereq;      // upgradeId → required level

    public SkillNode(String upgradeId, int x, int y, Map<String,Integer> prereq) {
        this.upgradeId = upgradeId;
        this.x = x;
        this.y = y;
        this.prereq = prereq;
    }
}