package com.derp.derpymod.arena;

import com.derp.derpymod.arena.permanentskilltree.SkillTreeType;
import com.derp.derpymod.capabilities.CurrencyDataProvider;
import com.derp.derpymod.capabilities.UpgradeDataProvider;
import com.derp.derpymod.network.PacketHandler;
import com.derp.derpymod.packets.CSyncUpgradePacket;
import com.derp.derpymod.packets.CurrencySyncPacket;
import com.derp.derpymod.packets.ScreenType;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Upgrade {
    private String id;
    private String name;
    private String description;
    private int level;
    private boolean isPermanent;
    private List<Upgrade> prerequisites;
    private int x;
    private int y;
    private SkillTreeType type;
    private boolean isSwordDamage = false;

    public Upgrade(int x, int y) {
        setLevel(0);
        setPermanent(false);
        setDescription(null);
        this.prerequisites = new ArrayList<>();
        this.x = x;
        this.y = y;
        this.type = SkillTreeType.NONE;
    }

    public boolean hasEnoughCurrency(Player player) {
        AtomicBoolean hasEnoughCurrency = new AtomicBoolean(false);
        if (isPermanent()) {
            player.getCapability(CurrencyDataProvider.CURRENCY_DATA).ifPresent(currencyData -> {
                hasEnoughCurrency.set(currencyData.getPermanentCurrency() >= calculateEffectiveCost());
            });
        } else {
            player.getCapability(CurrencyDataProvider.CURRENCY_DATA).ifPresent(currencyData -> {
                hasEnoughCurrency.set(currencyData.getCurrency() >= calculateEffectiveCost());
            });
        }
        return hasEnoughCurrency.get();
    }

    public abstract void executeUpgrade(Player player);

    public void save(CompoundTag nbt) {
        nbt.putInt("Level", getLevel());
    }

    public void load(CompoundTag nbt) {
        setLevel(nbt.getInt("Level"));
    }

    public void payUpgrade(Player player) {
        if (isPermanent()) {
            player.getCapability(CurrencyDataProvider.CURRENCY_DATA).ifPresent(currencyData -> {
                currencyData.subtractPermanentCurrency(calculateEffectiveCost());
            });
        } else {
            player.getCapability(CurrencyDataProvider.CURRENCY_DATA).ifPresent(currencyData -> {
                currencyData.subtractCurrency(calculateEffectiveCost());
            });
        }
    }

    public void resetUpgrade(Player player) {
        setLevel(-1);
        executeUpgrade(player);
    }

    public int getLevel() {
        return level;
    }

    public boolean isPermanent() {
        return isPermanent;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setPermanent(boolean permanent) {
        isPermanent = permanent;
    }

    public double calculateEffectiveCost() {
        if (level == -1) {
            return 0;
        }
        else if (level == 1 && isPermanent()) {
            return 0;
        }
        return calculateCost();
    }

    public void confirmationMessage(Player player) {
        if (getLevel() != 0) {
            player.sendSystemMessage(Component.literal("Successfully upgraded!").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.GREEN));
        }
    }

    public abstract double calculateCost();

    public boolean alreadyUnlocked(Player player) {
        if (getLevel() == 1) {
            alreadyUnlockedMessage(player);
            return true;
        } else {
            return false;
        }
    }

//    public boolean alreadyMaxed(int max) {
//        return getLevel() == max;
//    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Upgrade> getPrerequisites() {
        return prerequisites;
    }

    public void addPrerequisite(Upgrade upgrade) {
        this.prerequisites.add(upgrade);
    }

    public void setPrerequisites(List<Upgrade> prerequisites) {
        this.prerequisites = prerequisites;
    }

    public boolean hasPrerequisites(Player player) {
        if (getLevel() != -1) {
            AtomicBoolean hasPrerequisites = new AtomicBoolean(true);
            player.getCapability(UpgradeDataProvider.UPGRADE_DATA).ifPresent(upgradeData -> {
                for (var prerequisite : prerequisites) {
                    if (upgradeData.getUpgrade(prerequisite.id).getLevel() != 0) {
                        hasPrerequisites.set(true);
                    } else {
                        hasPrerequisites.set(false);
                        break;
                    }
                }
            });
            if (!hasPrerequisites.get()) {
                player.sendSystemMessage(Component.literal("You do not have the requirements to unlock this!").withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.BOLD));
            }
            return hasPrerequisites.get();
        }
        return true;
    }

    public void alreadyUnlockedMessage(Player player) {
        player.sendSystemMessage(Component.literal("You have already unlocked this upgrade!").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.AQUA));
    }

    private void sendUpgradeDataToPlayer(ServerPlayer player, ScreenType screenType) {
        player.getCapability(UpgradeDataProvider.UPGRADE_DATA).ifPresent(upgradeData -> {
            CompoundTag upgradeDataTag = new CompoundTag();
            upgradeData.saveNBTData(upgradeDataTag);
            CSyncUpgradePacket packet = new CSyncUpgradePacket(upgradeDataTag, screenType);
            PacketHandler.sendToPlayer(packet, player);
        });
    }

    private void sendCurrencyDataToPlayer(ServerPlayer player) {
        player.getCapability(CurrencyDataProvider.CURRENCY_DATA).ifPresent(currencyData -> {
            CompoundTag currencyDataTag = new CompoundTag();
            currencyData.saveNBTData(currencyDataTag);
            CurrencySyncPacket packet = new CurrencySyncPacket(currencyDataTag);
            PacketHandler.sendToPlayer(packet, player);
        });
    }

    public void syncData(ServerPlayer player) {
        sendUpgradeDataToPlayer(player, ScreenType.NONE);
        sendCurrencyDataToPlayer(player);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public SkillTreeType getType() {
        return type;
    }

    public void setType(SkillTreeType type) {
        this.type = type;
    }

    public boolean isSwordDamage() {
        return isSwordDamage;
    }

    public void setSwordDamage(boolean swordDamage) {
        isSwordDamage = swordDamage;
    }
}
