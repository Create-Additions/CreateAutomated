package com.kotakotik.createautomated.content.processing.picker;

import com.kotakotik.createautomated.content.processing.picker.recipe.PickingRecipe;
import com.kotakotik.createautomated.register.ModRecipeTypes;
import com.kotakotik.createautomated.register.config.ModServerConfig;
import com.simibubi.create.content.contraptions.components.deployer.DeployerFakePlayer;
import com.simibubi.create.content.curiosities.tools.SandPaperItem;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SSpawnParticlePacket;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Optional;

@Mod.EventBusSubscriber
public class PickerItem extends Item {
	public PickerItem(Properties p_i48487_1_) {
		super(p_i48487_1_);
	}

	@Override
	public int getMaxDamage(ItemStack stack) {
		return ModServerConfig.Picker.durability.get();
	}

	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity plr, Hand hand) {
		ItemStack stack = plr.getItemInHand(hand);
		CompoundNBT tag = stack.getOrCreateTag();
		if (tag.contains("Picking")) {
			plr.startUsingItem(hand);
			return new ActionResult<>(ActionResultType.PASS, stack);
		} else {
			Hand otherHand = hand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
			Optional<PickingRecipe> pickingRecipe = getRecipe(world, plr.getItemInHand(otherHand));
			if (pickingRecipe.isPresent()) {
				ItemStack item = plr.getItemInHand(otherHand).copy();
				ItemStack toPick = item.split(1);
				plr.startUsingItem(hand);
				tag.put("Picking", toPick.serializeNBT());
				plr.setItemInHand(otherHand, item);
				return new ActionResult(ActionResultType.SUCCESS, stack);
			}
			AxisAlignedBB bb;
			if (plr instanceof DeployerFakePlayer) {
				bb = plr.getBoundingBox().move(-.5, -.5, -.5).inflate(.3, 0, .3);
				bb = new AxisAlignedBB(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY - .8, bb.maxZ);
			} else {
				RayTraceResult raytraceresult = getPlayerPOVHitResult(world, plr, RayTraceContext.FluidMode.NONE);
				if (!(raytraceresult instanceof BlockRayTraceResult))
					return new ActionResult<>(ActionResultType.FAIL, stack);
				BlockRayTraceResult ray = (BlockRayTraceResult) raytraceresult;
				Vector3d hitVec = ray.getLocation();

				bb = new AxisAlignedBB(hitVec, hitVec).inflate(1f);
			}
			ItemEntity pickUp = null;
			for (ItemEntity itemEntity : world.getEntitiesOfClass(ItemEntity.class, bb)) {
				if (!itemEntity.isAlive())
					continue;
				if (itemEntity.position()
						.distanceTo(plr.position()) > 3)
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

			plr.startUsingItem(hand);

			if (!world.isClientSide) {
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
	public UseAction getUseAnimation(ItemStack p_77661_1_) {
		return UseAction.EAT;
	}

	@Override
	public void releaseUsing(ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
		if (!(entityLiving instanceof PlayerEntity))
			return;
		PlayerEntity player = (PlayerEntity) entityLiving;
		CompoundNBT tag = stack.getOrCreateTag();
		if (tag.contains("Picking")) {
			ItemStack toPick = ItemStack.of(tag.getCompound("Picking"));
			player.inventory.placeItemBackInInventory(worldIn, toPick);
			tag.remove("Picking");
		}
	}

	public static BlockPos getDeployerClickingPos(DeployerFakePlayer deployer) {
		return new BlockPos(deployer.getX(), deployer.getY(), deployer.getZ());
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, World world, LivingEntity entity) {
		if (!(entity instanceof PlayerEntity))
			return stack; // im not sure this can even happen but since create is doing it, i will too
		PlayerEntity plr = (PlayerEntity) entity;
		CompoundNBT tag = stack.getOrCreateTag();

		if (tag.contains("Picking")) {
			ItemStack toPick = ItemStack.of(tag.getCompound("Picking"));
			List<ItemStack> outputs = getRecipe(world, toPick).get().generateOutputs();

			if (plr instanceof DeployerFakePlayer && !world.isClientSide) {
				Vector3d motion = VecHelper.offsetRandomly(Vector3d.ZERO, world.random, 0.125F);
				BlockPos p = getDeployerClickingPos((DeployerFakePlayer) plr);
				SSpawnParticlePacket packet = new SSpawnParticlePacket(new ItemParticleData(ParticleTypes.ITEM, toPick), false,
						p.getX() + .5, p.getY() + .5, p.getZ() + .5, (float) motion.x, (float) motion.y, (float) motion.z, .2f, 20);
				// pain
				// so much pain
				ServerWorld serverWorld = (ServerWorld) world;
				serverWorld.getEntitiesOfClass(ServerPlayerEntity.class, new AxisAlignedBB(p, p).inflate(10)).stream().forEach(pp -> {
					pp.connection.send(packet);
				});
//				DeployerTileEntity deployerTile = (DeployerTileEntity) world.getTileEntity(new BlockPos(Math.floor(plr.getX()), Math.floor(plr.getY()), Math.floor(plr.getZ())));
//				CompoundNBT nbt = deployerTile.serializeNBT();
//				nbt.put("Particle", toPick.serializeNBT());
//				deployerTile.fromTag(deployerTile.getBlockState(), nbt);
			} else {
				SandPaperItem.spawnParticles(entity.getEyePosition(1)
								.add(entity.getLookAngle()
										.scale(.5f)),
						toPick, world);
			}

			if (world.isClientSide) {
				return stack;
			}

			if (plr instanceof FakePlayer) {
				outputs.forEach(o -> plr.drop(o, false, false));
			} else {
				outputs.forEach(o -> plr.inventory.placeItemBackInInventory(world, o));
			}
			tag.remove("Picking");
			stack.hurtAndBreak(1, entity, p -> p.broadcastBreakEvent(p.getUsedItemHand()));
		}

		return stack;
	}

	public static Optional<PickingRecipe> getRecipe(World world, ItemStack toPick) {
		return world.getRecipeManager().getRecipeFor(ModRecipeTypes.PICKING, new PickingRecipe.PickingInventory(toPick), world);
	}

	@Override
	public int getUseDuration(ItemStack p_77626_1_) {
		return ModServerConfig.Picker.useTime.get();
	}
}
