package com.derp.derpymod.arena.permanentskilltree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Called during mod init
public class SkillTreeRegistry {
    private static final Map<String,SkillTree> TREES = new HashMap<>();

    public static void register(String category, SkillTree tree) {
        TREES.put(category, tree);
    }

    public static SkillTree get(String category) {
        return TREES.get(category);
    }

    public static java.util.Set<String> categories() {
        return TREES.keySet();
    }

    // Example registration
    public static void init() {
        // Defense tree
        register("defense", new SkillTree(List.of(
                //     id           x,   y,    prereqs
                new SkillNode("perm_armour_1", 77, 19, Map.of()),                      // root
                new SkillNode("perm_max_health_1", 37, 51, Map.of("perm_armour_1", 3))
        )));
        // Melee tree
        register("melee", new SkillTree(List.of(
        )));
    }
}

