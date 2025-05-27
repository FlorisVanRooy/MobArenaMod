package com.derp.derpymod.arena.upgrades;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class UpgradeRegistry {
    private static final Map<String, Supplier<IUpgrade>> FACTORIES = new HashMap<>();

    public static void register(String id, Supplier<IUpgrade> factory) {
        FACTORIES.put(id, factory);
    }

    public static IUpgrade create(String id) {
        Supplier<IUpgrade> f = FACTORIES.get(id);
        return (f != null) ? f.get() : null;
    }

    public static Set<String> ids() {
        return Collections.unmodifiableSet(FACTORIES.keySet());
    }
}

