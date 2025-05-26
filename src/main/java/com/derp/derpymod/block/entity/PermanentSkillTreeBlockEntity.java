package com.derp.derpymod.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public class PermanentSkillTreeBlockEntity extends BlockEntity {
    private final ItemStackHandler itemHandler = new ItemStackHandler(0);

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty() ;

    public PermanentSkillTreeBlockEntity(BlockPos p_155229_, BlockState p_155230_) {
        super(ModBlockEntities.PERMANENT_SKILL_TREE.get(), p_155229_, p_155230_);
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

//    @Nullable
//    @Override
//    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
////        return new PermanentSkillTreeMenu(containerId, inventory, this, this.data);
//        return new PermanentSkillTreeMenu(containerId, inventory, this, null);
//    }
}
