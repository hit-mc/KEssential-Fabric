package com.keuin.kessentialfabric.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;

/**
 * Vanilla EntityTrackerEntry.sendPackets emits lots of annoying log messages as
 * "Fetching packet for removed entity ..." when some big machines are running.
 * This class wipe out this log entirely.
 */
@Mixin(EntityTrackerEntry.class)
public abstract class EntitySendPacketsMixin {

    @Shadow @Final private static Logger LOGGER;
    @Shadow @Final Entity entity;
    @Shadow private Vec3d field_18278; // velocity
    @Shadow private int lastHeadPitch;
    @Shadow @Final private boolean alwaysUpdateVelocity;

    /**
     * (Copied from vanilla Minecraft 1.14.4 sendPackets method in net.minecraft.server.network.EntityTrackerEntry, mapped yarn build16 v2)
     *
     * @reason to remove the LOGGER.warn invoke.
     * @author Keuin
     */
    @Overwrite
    public void sendPackets(Consumer<Packet<?>> sender) {
//        if (this.entity.removed) {
//            LOGGER.warn("Fetching packet for removed entity " + this.entity);
//        }

        Packet<?> packet = this.entity.createSpawnPacket();
        this.lastHeadPitch = MathHelper.floor(this.entity.getHeadYaw() * 256.0F / 360.0F);
        sender.accept(packet);
        if (!this.entity.getDataTracker().isEmpty()) {
            sender.accept(new EntityTrackerUpdateS2CPacket(this.entity.getEntityId(), this.entity.getDataTracker(), true));
        }

        boolean bl = this.alwaysUpdateVelocity;
        if (this.entity instanceof LivingEntity) {
            EntityAttributeContainer entityAttributeContainer = (EntityAttributeContainer)((LivingEntity)this.entity).getAttributes();
            Collection<EntityAttributeInstance> collection = entityAttributeContainer.buildTrackedAttributesCollection();
            if (!collection.isEmpty()) {
                sender.accept(new EntityAttributesS2CPacket(this.entity.getEntityId(), collection));
            }

            if (((LivingEntity)this.entity).isFallFlying()) {
                bl = true;
            }
        }

        this.field_18278 = this.entity.getVelocity();
        if (bl && !(packet instanceof MobSpawnS2CPacket)) {
            sender.accept(new EntityVelocityUpdateS2CPacket(this.entity.getEntityId(), this.field_18278));
        }

        if (this.entity instanceof LivingEntity) {
            EquipmentSlot[] var9 = EquipmentSlot.values();
            int var11 = var9.length;

            for(int var6 = 0; var6 < var11; ++var6) {
                EquipmentSlot equipmentSlot = var9[var6];
                ItemStack itemStack = ((LivingEntity)this.entity).getEquippedStack(equipmentSlot);
                if (!itemStack.isEmpty()) {
                    sender.accept(new EntityEquipmentUpdateS2CPacket(this.entity.getEntityId(), equipmentSlot, itemStack));
                }
            }
        }

        if (this.entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity)this.entity;
            Iterator var12 = livingEntity.getStatusEffects().iterator();

            while(var12.hasNext()) {
                StatusEffectInstance statusEffectInstance = (StatusEffectInstance)var12.next();
                sender.accept(new EntityStatusEffectS2CPacket(this.entity.getEntityId(), statusEffectInstance));
            }
        }

        if (!this.entity.getPassengerList().isEmpty()) {
            sender.accept(new EntityPassengersSetS2CPacket(this.entity));
        }

        if (this.entity.hasVehicle()) {
            sender.accept(new EntityPassengersSetS2CPacket(this.entity.getVehicle()));
        }
    }


}
