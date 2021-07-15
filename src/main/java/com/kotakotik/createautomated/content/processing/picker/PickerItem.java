package com.kotakotik.createautomated.content.processing.picker;

import com.kotakotik.createautomated.content.processing.picker.recipe.PickingRecipe;
import com.kotakotik.createautomated.register.ModRecipeTypes;
import com.simibubi.create.content.contraptions.components.deployer.DeployerFakePlayer;
import com.simibubi.create.content.contraptions.components.deployer.DeployerHandler;
import com.simibubi.create.content.curiosities.tools.SandPaperItem;
import com.simibubi.create.content.curiosities.tools.SandPaperPolishingRecipe;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;
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

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, PlayerEntity plr, Hand hand) {
		ItemStack stack = plr.getHeldItem(hand);
		CompoundNBT tag = stack.getOrCreateTag();
		if(tag.contains("Picking")) {
			plr.setActiveHand(hand);
			return new ActionResult<>(ActionResultType.PASS, stack);
		} else {
			Hand otherHand = hand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
			Optional<PickingRecipe> pickingRecipe = getRecipe(world, plr.getHeldItem(otherHand));
			if(pickingRecipe.isPresent()) {
				ItemStack item = plr.getHeldItem(otherHand).copy();
				ItemStack toPick = item.split(1);
				plr.setActiveHand(hand);
				tag.put("Picking", toPick.serializeNBT());
				plr.setHeldItem(otherHand, item);
				return new ActionResult(ActionResultType.SUCCESS, stack);
			}
			AxisAlignedBB bb;
			if(plr instanceof DeployerFakePlayer) {
				bb = plr.getBoundingBox().offset(new BlockPos(0,0,0).offset(plr.getHorizontalFacing())).grow(1);
			} else {
				RayTraceResult raytraceresult = rayTrace(world, plr, RayTraceContext.FluidMode.NONE);
				if (!(raytraceresult instanceof BlockRayTraceResult))
					return new ActionResult<>(ActionResultType.FAIL, stack);
				BlockRayTraceResult ray = (BlockRayTraceResult) raytraceresult;
				Vector3d hitVec = ray.getHitVec();

				bb = new AxisAlignedBB(hitVec, hitVec).grow(1f);
			}
			ItemEntity pickUp = null;
			for (ItemEntity itemEntity : world.getEntitiesWithinAABB(ItemEntity.class, bb)) {
				if (!itemEntity.isAlive())
					continue;
				if (itemEntity.getPositionVec()
						.distanceTo(plr.getPositionVec()) > 3)
					continue;
				ItemStack toPick = itemEntity.getItem();
				if (!getRecipe(world, toPick).isPresent())
					continue;
				pickUp = itemEntity;
				break;
			}

			if (pickUp == null)
				return new ActionResult<>(ActionResultType.FAIL, stack);

			ItemStack item = pickUp.getItem()
					.copy();
			ItemStack toPick = item.split(1);

			plr.setActiveHand(hand);

			if (!world.isRemote) {
				tag.put("Picking", toPick.serializeNBT());
				if (item.isEmpty())
					pickUp.remove();
				else
					pickUp.setItem(item);
			}

			return new ActionResult<>(ActionResultType.SUCCESS, stack);
		}
	}

	@Override
	public UseAction getUseAction(ItemStack p_77661_1_) {
		return UseAction.EAT;
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
		if (!(entityLiving instanceof PlayerEntity))
			return;
		PlayerEntity player = (PlayerEntity) entityLiving;
		CompoundNBT tag = stack.getOrCreateTag();
		if (tag.contains("Picking")) {
			ItemStack toPick = ItemStack.read(tag.getCompound("Picking"));
			player.inventory.placeItemBackInInventory(worldIn, toPick);
			tag.remove("Picking");
		}
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World world, LivingEntity entity) {
		if(!(entity instanceof PlayerEntity)) return stack; // im not sure this can even happen but since create is doing it, i will too
		PlayerEntity plr = (PlayerEntity) entity;
		CompoundNBT tag = stack.getOrCreateTag();

		if (tag.contains("Picking")) {
			ItemStack toPick = ItemStack.read(tag.getCompound("Picking"));
			List<ItemStack> outputs = getRecipe(world, toPick).get().generateOutputs();

			if (world.isRemote) {
//				spawnParticles(entityLiving.getEyePosition(1)
//								.add(entityLiving.getLookVec()
//										.scale(.5f)),
//						toPick, worldIn);
				return stack;
			}

			if (plr instanceof FakePlayer) {
				outputs.forEach(o -> plr.dropItem(o, false, false));
			} else {
				outputs.forEach(o -> plr.inventory.placeItemBackInInventory(world, o));
			}
			tag.remove("Picking");
			stack.damageItem(1, entity, p -> p.sendBreakAnimation(p.getActiveHand()));
		}

		return stack;
	}

	public static Optional<PickingRecipe> getRecipe(World world, ItemStack toPick) {
		return  world.getRecipeManager().getRecipe(ModRecipeTypes.PICKING, new PickingRecipe.PickingInventory(toPick), world);
	}

	@Override
	public int getUseDuration(ItemStack p_77626_1_) {
		return 16;
	}
}
