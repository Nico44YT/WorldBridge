package net.letscode.worldbridge.item.custom;

import net.letscode.worldbridge.WorldBridge;
import net.letscode.worldbridge.WorldBridgeConfig;
import net.letscode.worldbridge.item.ModItemRegistry;
import net.letscode.worldbridge.sound.ModSoundRegistry;
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
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Colors;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class SoulCrystal extends Item {
    public SoulCrystal(Settings settings) {
        super(settings);
    }


    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity player, LivingEntity entity, Hand hand) {
        World world = player.getWorld();

        if(world.isClient) return ActionResult.CONSUME;
        if(entity.isPlayer() || stack.hasNbt()) return ActionResult.FAIL;

        if(!isAllowedEntity(entity)) {
            //.withColor(Colors.LIGHT_RED)
            player.sendMessage(Text.translatable("message.worldbridge.soul_crystal.disallowed_capture", entity.getName()).setStyle(Style.EMPTY.withColor(0xFFFF5555)));
            return ActionResult.FAIL;
        }

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
        if(world.isClient) {

            try {
                if(!EntityDataHolder.isValidNbt(stack.getNbt())) return ActionResult.FAIL;
                Random random = Random.create();

                for(int i = 0;i<25;i++){
                    world.addParticle(ParticleTypes.POOF, hitPos.x, hitPos.y, hitPos.z, random.nextBetween(-5, 5)/40f, random.nextBetween(0, 5)/40f, random.nextBetween(-5, 5)/40f);
                }

            } catch (Exception ignore) {}
            return ActionResult.CONSUME;
        }

        try{
            if(!EntityDataHolder.isValidNbt(stack.getNbt())) return ActionResult.FAIL;

            EntityDataHolder dataHolder = EntityDataHolder.fromNbt(stack.getNbt());
            Entity newEntity = dataHolder.createEntity(world);
            newEntity.setPosition(hitPos);

            if(!isAllowedEntity((LivingEntity)newEntity)) {
                //.withColor(Colors.LIGHT_RED)
                player.sendMessage(Text.translatable("message.worldbridge.soul_crystal.disallowed_release", newEntity.getName()).setStyle(Style.EMPTY.withColor(0xFFFF5555)));
                return ActionResult.FAIL;
            }

            world.spawnEntity(newEntity);

            world.playSound(null, new BlockPos((int)hitPos.x, (int)hitPos.y, (int)hitPos.z), ModSoundRegistry.RELEASE_SOUND, SoundCategory.PLAYERS, 1, 1);

            player.getStackInHand(hand).setNbt(new NbtCompound());
        }catch (Exception ignore){}

        return ActionResult.SUCCESS;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if(!stack.hasNbt()) {
            //.withColor(Colors.GRAY)
            tooltip.add(1, Text.translatable("tooltip.worldbridge.empty").setStyle(Style.EMPTY.withColor(0xFFAAAAAA)));
            return;
        }

        EntityDataHolder entityDataHolder = EntityDataHolder.fromNbt(stack.getNbt());

        tooltip.add(1, entityDataHolder.getName(world, Colors.GRAY));
        LivingEntity entity = (LivingEntity)EntityDataHolder.fromNbt(stack.getNbt()).getEntityType().create(world);
        if(!isAllowedEntity(entity)) {
            //.withColor(Colors.LIGHT_RED)
            tooltip.add(2, Text.translatable("tooltip.worldbridge.disallowed_entity").setStyle(Style.EMPTY.withColor(0xAAFF5555)));
        }
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return stack.hasNbt();
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }
    private boolean isAllowedEntity(LivingEntity entity) {
        if(WorldBridgeConfig.getConfigHolder().blacklisted_entities.contains(entity.getType().toString())) return false;

        return true;
    }
}

