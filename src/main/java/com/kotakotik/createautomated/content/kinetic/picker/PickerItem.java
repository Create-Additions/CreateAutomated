package com.kotakotik.createautomated.content.kinetic.picker;

import com.kotakotik.createautomated.register.ModRecipeTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;

public class PickerItem extends Item {
	public PickerItem(Properties p_i48487_1_) {
		super(p_i48487_1_);
	}

	// TODO: make picking not instant
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity plr, Hand hand) {
		Hand otherHand = hand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
		Optional<PickingRecipe> recipe = world.getRecipeManager().getRecipe(ModRecipeTypes.PICKING, new PickingRecipe.PickingInventory(plr.getHeldItem(otherHand)), world);
		if (recipe.isPresent()) {
			PickingRecipe r = recipe.get();
			plr.getHeldItem(otherHand).shrink(1);
			List<ItemStack> outputs = r.generateOutputs();
			outputs.forEach(stack -> plr.inventory.placeItemBackInInventory(world, stack));
		}
		return super.onItemRightClick(world, plr, hand);
	}
}
