package com.kotakotik.createautomated.mixin;

import com.kotakotik.createautomated.CALocalization;
import com.kotakotik.createautomated.content.simple.node.NodeBlock;
import com.kotakotik.createautomated.register.config.ModConfig;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.contraptions.components.structureMovement.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.Contraption;
import com.simibubi.create.content.contraptions.components.structureMovement.OrientedContraptionEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.mounted.MinecartContraptionItem;
import com.simibubi.create.foundation.config.CKinetics;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

// mixin abuse? 😩
@Mixin(MinecartContraptionItem.class)
public class WrenchPickupMixin {
	@Inject(method = "wrenchCanBeUsedToPickUpMinecartContraptions", at = @At("HEAD"), cancellable = true, remap = false)
	private static void event(PlayerInteractEvent.EntityInteract event, CallbackInfo ci) {
		if (ModConfig.SERVER.machines.extractor.nodeMovement.get() != CKinetics.SpawnerMovementSetting.NO_PICKUP)
			return;
		// pain?
		Entity entity = event.getTarget();
		PlayerEntity player = event.getPlayer();
		if (player == null || entity == null) {
			ci.cancel();
			return;
		}

		ItemStack wrench = player.getItemInHand(event.getHand());
		if (!AllItems.WRENCH.isIn(wrench)) {
			ci.cancel();
			return;
		}
		if (entity instanceof AbstractContraptionEntity)
			entity = entity.getVehicle();
		if (!(entity instanceof AbstractMinecartEntity)) {
			ci.cancel();
			return;
		}
		if (!entity.isAlive()) {
			ci.cancel();
			return;
		}
		AbstractMinecartEntity cart = (AbstractMinecartEntity) entity;
		AbstractMinecartEntity.Type type = cart.getMinecartType();
		if (type != AbstractMinecartEntity.Type.RIDEABLE && type != AbstractMinecartEntity.Type.FURNACE && type != AbstractMinecartEntity.Type.CHEST) {
			ci.cancel();
			return;
		}
		List<Entity> passengers = cart.getPassengers();
		if (passengers.isEmpty() || !(passengers.get(0) instanceof OrientedContraptionEntity)) {
			ci.cancel();
			return;
		}
		OrientedContraptionEntity contraption = (OrientedContraptionEntity) passengers.get(0);

		Contraption blocks = contraption.getContraption();
		if (blocks != null && blocks.getBlocks().values().stream()
				.anyMatch(i -> i.state.getBlock() instanceof NodeBlock)) {
			player.displayClientMessage(Lang.translate(CALocalization.noPickupNodeKey)
					.withStyle(TextFormatting.RED), true);
			ci.cancel();
		}
	}
}
