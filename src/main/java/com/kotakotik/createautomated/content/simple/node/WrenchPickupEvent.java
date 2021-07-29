package com.kotakotik.createautomated.content.simple.node;

import com.kotakotik.createautomated.CALocalization;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.contraptions.components.structureMovement.AbstractContraptionEntity;
import com.simibubi.create.content.contraptions.components.structureMovement.Contraption;
import com.simibubi.create.content.contraptions.components.structureMovement.OrientedContraptionEntity;
import com.simibubi.create.foundation.config.AllConfigs;
import com.simibubi.create.foundation.config.CKinetics;
import com.simibubi.create.foundation.utility.Lang;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber
public class WrenchPickupEvent {
	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void event(PlayerInteractEvent.EntityInteract event) {
		if (AllConfigs.SERVER.kinetics.spawnerMovement.get() != CKinetics.SpawnerMovementSetting.NO_PICKUP) return;
		// pain?
		Entity entity = event.getTarget();
		PlayerEntity player = event.getPlayer();
		if (player == null || entity == null) {
			event.setCanceled(true);
			return;
		}

		ItemStack wrench = player.getItemInHand(event.getHand());
		if (!AllItems.WRENCH.isIn(wrench)) {
			event.setCanceled(true);
			return;
		}
		if (entity instanceof AbstractContraptionEntity)
			entity = entity.getVehicle();
		if (!(entity instanceof AbstractMinecartEntity)) {
			event.setCanceled(true);
			return;
		}
		if (!entity.isAlive()) {
			event.setCanceled(true);
			return;
		}
		AbstractMinecartEntity cart = (AbstractMinecartEntity) entity;
		AbstractMinecartEntity.Type type = cart.getMinecartType();
		if (type != AbstractMinecartEntity.Type.RIDEABLE && type != AbstractMinecartEntity.Type.FURNACE && type != AbstractMinecartEntity.Type.CHEST) {
			event.setCanceled(true);
			return;
		}
		List<Entity> passengers = cart.getPassengers();
		if (passengers.isEmpty() || !(passengers.get(0) instanceof OrientedContraptionEntity)) {
			event.setCanceled(true);
			return;
		}
		OrientedContraptionEntity contraption = (OrientedContraptionEntity) passengers.get(0);

		Contraption blocks = contraption.getContraption();
		if (blocks != null && blocks.getBlocks().values().stream()
				.anyMatch(i -> i.state.getBlock() instanceof NodeBlock)) {
			player.displayClientMessage(Lang.translate(CALocalization.noPickupNodeKey)
					.withStyle(TextFormatting.RED), true);
			event.setCanceled(true);
		}
	}
}
