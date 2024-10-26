package net.letscode.worldbridge.item.custom;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.mixin.client.keybinding.KeyBindingAccessor;
import net.letscode.worldbridge.WorldBridgeConfig;
import net.letscode.worldbridge.block.custom.SoulExplorerBlock;
import net.letscode.worldbridge.block.custom.SoulExplorerBlockEntity;
import net.letscode.worldbridge.networking.packets.RequestStoredEntitiesC2S;
import net.letscode.worldbridge.util.nbt.NbtCompoundBuilder;
import net.letscode.worldbridge.util.nbt.NbtCompoundReader;
import net.minecraft.client.gui.screen.option.ControlsListWidget;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Colors;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SoulTablet extends Item {
    public SoulTablet(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if(!WorldBridgeConfig.getConfigHolder().enable_soul_tablet) return super.use(world, player, hand);

        if(player.isSneaking()) return TypedActionResult.pass(player.getStackInHand(hand));
        if(world.isClient) {
            ClientPlayNetworking.send(new RequestStoredEntitiesC2S());
            return TypedActionResult.pass(player.getStackInHand(hand));
        }

        NamedScreenHandlerFactory screenHandlerFactory = (SoulExplorerBlockEntity)world.getBlockEntity(NbtCompoundReader.create((player.getStackInHand(hand).getNbt())).getBlockPos("linked_pos"));
        if(screenHandlerFactory != null) {
            player.openHandledScreen(screenHandlerFactory);
            player.getStackInHand(hand).damage(1, player, p -> {
                p.sendToolBreakStatus(hand);
            });
        }



        return TypedActionResult.success(player.getStackInHand(hand), true);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if(!WorldBridgeConfig.getConfigHolder().enable_soul_tablet) return super.useOnBlock(context);
        return this.useOnBlock(context.getWorld(), context.getStack(), context.getPlayer(), context.getHand(), context.getBlockPos());
    }

    public ActionResult useOnBlock(World world, ItemStack stack, PlayerEntity player, Hand hand, BlockPos blockPos) {
        if(world.isClient || !player.isSneaking()) return ActionResult.PASS;

        if(world.getBlockState(blockPos).getBlock() instanceof SoulExplorerBlock soulExplorerBlock) {
            player.getStackInHand(hand).setNbt(NbtCompoundBuilder.create().putBlockPos("linked_pos", blockPos).build());
        }

        return ActionResult.CONSUME;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if(!WorldBridgeConfig.getConfigHolder().enable_soul_tablet) {
            //.withColor(Colors.LIGHT_RED)
            tooltip.add(1, Text.translatable("tooltip.worldbridge.disabled_feature").setStyle(Style.EMPTY.withColor(0xFFFF5555)));
            return;
        }

        if(!stack.hasNbt()) {
            //.withColor(Colors.LIGHT_GRAY)
            tooltip.add(1, Text.keybind("key.sneak").append(" + ").append(Text.keybind("key.use")).append(" -> ").append(Text.translatable("block.worldbridge.soul_explorer").setStyle(Style.EMPTY.withColor(0xFFAAAAAA))));
            return;
        }

        if(!stack.getNbt().contains("linked_pos")) {
            //.withColor(Colors.LIGHT_GRAY)
            tooltip.add(1, Text.keybind("key.sneak").append(" + ").append(Text.keybind("key.use")).append(" -> ").append(Text.translatable("block.worldbridge.soul_explorer").setStyle(Style.EMPTY.withColor(0xFFAAAAAA))));
            return;
        }

        if(stack.hasNbt()) {
            BlockPos pos = (NbtCompoundReader.create(stack.getNbt()).getBlockPos("linked_pos"));
            //.withColor(Colors.LIGHT_GRAY)
            tooltip.add(1, Text.translatable("tooltip.worldbridge.linked", pos.getX() + ", " + pos.getY() + ", " + pos.getZ()).setStyle(Style.EMPTY.withColor(0xFFAAAAAA)));
        }
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }
}
