package com.derp.derpymod.block.custom;

import com.derp.derpymod.block.entity.PermanentSkillTreeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class PermanentSkillTreeBlock extends BaseEntityBlock {
    public static final VoxelShape SHAPE = PermanentSkillTreeBlock.box(0, 0, 0, 16, 16, 16);

    public PermanentSkillTreeBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState p_49232_) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof PermanentSkillTreeBlockEntity) {
                ((PermanentSkillTreeBlockEntity) blockEntity).drops();
            }
        }
        System.out.println("Block Removed");
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {

        return new PermanentSkillTreeBlockEntity(blockPos, blockState);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof PermanentSkillTreeBlockEntity) {
                NetworkHooks.openScreen((ServerPlayer) player, (MenuProvider) blockEntity, pos);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        System.out.println("Block placed!");
        super.onPlace(state, level, pos, oldState, isMoving);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        System.out.println("Block placed by player!");
        super.setPlacedBy(level, pos, state, placer, stack);
    }
}
