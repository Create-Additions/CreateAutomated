package com.kotakotik.createautomated.content.processing.picker;

import com.kotakotik.createautomated.content.processing.picker.recipe.PickingRecipe;
import com.kotakotik.createautomated.register.ModRecipeTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mod.EventBusSubscriber
public class PickerItem extends Item {
	public PickerItem(Properties p_i48487_1_) {
		super(p_i48487_1_);
	}

	// TODO: make picking not instant
	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity plr, Hand hand) {
		Hand otherHand = hand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
		pick(plr.getItemInHand(otherHand), world, plr.getItemInHand(hand), !plr.isCreative(), plr).forEach(plr::addItem);
		return super.use(world, plr, hand);
	}

	public static List<ItemStack> pick(ItemStack toPick, World world, ItemStack picker, boolean damage, PlayerEntity plr) {
		Optional<PickingRecipe> recipe = world.getRecipeManager().getRecipeFor(ModRecipeTypes.PICKING, new PickingRecipe.PickingInventory(toPick), world);
		if (recipe.isPresent()) {
			PickingRecipe r = recipe.get();
			toPick.shrink(1);
			if (damage) picker.hurtAndBreak(1, plr, LivingEntity::animateHurt);
			return r.generateOutputs();
		}
		return new ArrayList<>();
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		return super.getDurabilityForDisplay(stack);
	}

	// TODO: doesnt do particles or add motion to the item after its made, i hate this
	@SubscribeEvent
	public static void onEntityRightClicked(PlayerInteractEvent.EntityInteract event) {
		Entity entity = event.getTarget();
		if (entity instanceof ItemEntity) {
			ItemEntity item = (ItemEntity) entity;
			if (event.getItemStack().getItem() instanceof PickerItem) {
				pick(item.getItem(), event.getWorld(), event.getItemStack(), !event.getPlayer().isCreative(), event.getPlayer()).forEach(stack -> {
					World world = event.getWorld();
					ItemEntity output = new ItemEntity(world, item.getX(), item.getY(), item.getZ(), stack);
					world.addFreshEntity(output);
				});
				// ah yes, getItem().getItem()
				// amazing mappings lol
//				event.getItemStack().getItem().onItemRightClick(event.getWorld(), event.getPlayer(), event.getHand());
			}
		}
	}
}
