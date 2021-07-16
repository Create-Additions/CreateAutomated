package com.kotakotik.createautomated.content.processing.picker;

import com.kotakotik.createautomated.content.processing.picker.recipe.PickingRecipe;
import com.kotakotik.createautomated.register.ModPackets;
import com.kotakotik.createautomated.register.ModRecipeTypes;
import com.simibubi.create.content.contraptions.components.deployer.DeployerFakePlayer;
import com.simibubi.create.content.contraptions.components.deployer.DeployerHandler;
import com.simibubi.create.content.contraptions.components.deployer.DeployerTileEntity;
import com.simibubi.create.content.curiosities.tools.SandPaperItem;
import com.simibubi.create.content.curiosities.tools.SandPaperPolishingRecipe;
import com.simibubi.create.foundation.networking.AllPackets;
import com.simibubi.create.foundation.utility.ServerSpeedProvider;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SSpawnParticlePacket;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
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
				bb = plr.getBoundingBox().offset(-.5,-.5,-.5).grow(.3);
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

	public static BlockPos getDeployerClickingPos(DeployerFakePlayer deployer) {
		return new BlockPos(deployer.getX(), deployer.getY(), deployer.getZ());
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World world, LivingEntity entity) {
		if(!(entity instanceof PlayerEntity)) return stack; // im not sure this can even happen but since create is doing it, i will too
		PlayerEntity plr = (PlayerEntity) entity;
		CompoundNBT tag = stack.getOrCreateTag();

		if (tag.contains("Picking")) {
			ItemStack toPick = ItemStack.read(tag.getCompound("Picking"));
			List<ItemStack> outputs = getRecipe(world, toPick).get().generateOutputs();

			if(plr instanceof DeployerFakePlayer && !world.isRemote) {
				Vector3d motion = VecHelper.offsetRandomly(Vector3d.ZERO, world.rand, 0.125F);
				BlockPos p = getDeployerClickingPos((DeployerFakePlayer) plr);
				SSpawnParticlePacket packet = new SSpawnParticlePacket(new ItemParticleData(ParticleTypes.ITEM, toPick), false,
						p.getX() + .5, p.getY() + .5, p.getZ() + .5, (float) motion.x, (float) motion.y, (float) motion.z, .2f, 20);
				// pain
				// so much pain
				ServerWorld serverWorld = (ServerWorld) world;
				serverWorld.getEntitiesWithinAABB(ServerPlayerEntity.class, new AxisAlignedBB(p, p).grow(10)).stream().forEach(pp -> {
					pp.connection.sendPacket(packet);
				});
//				DeployerTileEntity deployerTile = (DeployerTileEntity) world.getTileEntity(new BlockPos(Math.floor(plr.getX()), Math.floor(plr.getY()), Math.floor(plr.getZ())));
//				CompoundNBT nbt = deployerTile.serializeNBT();
//				nbt.put("Particle", toPick.serializeNBT());
//				deployerTile.fromTag(deployerTile.getBlockState(), nbt);
			} else {
				SandPaperItem.spawnParticles(entity.getEyePosition(1)
								.add(entity.getLookVec()
										.scale(.5f)),
						toPick, world);
			}

			if (world.isRemote) {
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
