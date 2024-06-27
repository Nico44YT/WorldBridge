package net.letscode.worldbridge.item.custom;

import net.letscode.worldbridge.WorldBridge;
import net.letscode.worldbridge.WorldBridgeConfig;
import net.letscode.worldbridge.util.EntityDataHolder;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Colors;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SoulCrystal extends Item {
    public SoulCrystal(Settings settings) {
        super(settings);
    }


    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
        World world = player.getWorld();

        if(world.isClient) return ActionResult.CONSUME;
        if(entity.isPlayer() || stack.hasNbt()) return ActionResult.FAIL;

        NbtCompound entityNbt = entity.writeNbt(new NbtCompound());
        if(entityNbt.contains("Items") && !WorldBridgeConfig.getConfigHolder().allow_inventory_transfer) {
            NbtList nbtList = entityNbt.getList("Items", NbtElement.COMPOUND_TYPE);

            for(int i = 0; i < nbtList.size(); ++i) {
                NbtCompound nbtCompound = nbtList.getCompound(i);
                entity.dropStack(ItemStack.fromNbt(nbtCompound));
            }

            entityNbt.remove("Items");
        }

        EntityDataHolder dataHolder = new EntityDataHolder(player.getUuid(), entity.getUuid(), entity.getType(), entityNbt, WorldBridge.syncedData.getLevelUUID());

        player.getStackInHand(hand).setNbt(dataHolder.toNbt());

        entity.discard();
        return ActionResult.SUCCESS;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        return this.useOnBlock(context.getWorld(), context.getStack(), context.getPlayer(), context.getHand(), context.getHitPos());
    }

    public ActionResult useOnBlock(World world, ItemStack stack, PlayerEntity player, Hand hand, Vec3d hitPos) {
        if(world.isClient) return ActionResult.CONSUME;
        if(!stack.hasNbt()) return ActionResult.FAIL;

        EntityDataHolder dataHolder = EntityDataHolder.fromNbt(stack.getNbt());
        Entity newEntity = dataHolder.createEntity(world);
        newEntity.setPosition(hitPos);

        world.spawnEntity(newEntity);

        player.getStackInHand(hand).setNbt(new NbtCompound());

        return ActionResult.SUCCESS;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if(!stack.hasNbt()) {
            tooltip.add(1, Text.translatable("tooltip.worldbridge.empty").withColor(Colors.GRAY));
            return;
        }

        tooltip.add(1, EntityDataHolder.fromNbt(stack.getNbt()).getName(world, Colors.GRAY));
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return stack.hasNbt();
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }
}

