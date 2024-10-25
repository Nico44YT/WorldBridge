package net.letscode.worldbridge.block.custom;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.letscode.worldbridge.WorldBridgeConfig;
import net.letscode.worldbridge.networking.packets.RequestStoredEntitiesC2S;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Colors;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SoulExplorerBlock extends BlockWithEntity implements BlockEntityProvider {
    public SoulExplorerBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SoulExplorerBlockEntity(pos, state);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if(state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if(blockEntity instanceof SoulExplorerBlockEntity) {
                world.updateComparators(pos, this);
            }
        }
        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if(world.isClient) {
            ClientPlayNetworking.send(new RequestStoredEntitiesC2S());
            return ActionResult.CONSUME;
        }

        if(!WorldBridgeConfig.getConfigHolder().enable_soul_explorer) return ActionResult.PASS;

        NamedScreenHandlerFactory screenHandlerFactory = (SoulExplorerBlockEntity)world.getBlockEntity(pos);
        if(screenHandlerFactory != null) player.openHandledScreen(screenHandlerFactory);

        return ActionResult.SUCCESS;
    }

    @Override
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType options) {
        if(!WorldBridgeConfig.getConfigHolder().enable_soul_explorer) tooltip.add(1, Text.translatable("tooltip.worldbridge.disabled_feature").withColor(Colors.LIGHT_RED));
    }
}
