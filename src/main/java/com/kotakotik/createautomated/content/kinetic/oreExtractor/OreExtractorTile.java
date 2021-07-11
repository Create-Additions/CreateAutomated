package com.kotakotik.createautomated.content.kinetic.oreExtractor;

import com.kotakotik.createautomated.content.base.IDrillHead;
import com.kotakotik.createautomated.content.base.IExtractable;
import com.kotakotik.createautomated.content.base.IOreExtractorBlock;
import com.kotakotik.createautomated.register.ModTags;
import com.simibubi.create.content.contraptions.components.actors.BlockBreakingKineticTileEntity;
import com.simibubi.create.content.logistics.block.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.OreBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.commons.lang3.RandomUtils;

import javax.annotation.Nonnull;

public class OreExtractorTile extends BlockBreakingKineticTileEntity {
	public OreExtractorTile(TileEntityType<?> typeIn) {
		super(typeIn);
	}

	public final ItemStackHandler inventory = new Inv();
	//    private NonNullList<ItemStack> inventory = NonNullList.withSize(1, ItemStack.EMPTY);
	public int extractProgress = 0;
	public int durability = 0;
	protected LazyOptional<IItemHandler> invHandler = LazyOptional.of(() -> this.inventory);

	public int maxDurability;

	@Override
	public BlockPos getBreakingPos() {
		return getPos().down(2);
	}

	public boolean isBreakableOre(BlockPos pos) {
		return getBlockToMine() instanceof OreBlock;
	}

	public boolean isExtractable(BlockPos pos) {
		return getBlockToMine() instanceof IExtractable || IExtractable.getRecipe(world, getBreakingPos()).isPresent();
	}

	@Override
	public boolean shouldRun() {
		return false && super.shouldRun() && isBreakableOre(getBreakingPos()) && isSpeedRequirementFulfilled();
	}

	public boolean shouldRunExtracting() {
		return isExtractable(getBreakingPos()) && isSpeedRequirementFulfilled() && durability > 0;
	}

	public Block getBlockToMine() {
		return world.getBlockState(getBreakingPos()).getBlock();
	}

	@Override
	protected float getBreakSpeed() {
		return super.getBreakSpeed() * 3;
	}

	@Override
	public void tick() {
		assert world != null;
		if (world.isRemote && !world.isAirBlock(getBreakingPos())) {
			particles();
		}

		super.tick();

		if (shouldRunExtracting()) {
			Block below = getBlockToMine();
			BlockPos belowBlock = getBreakingPos();
			IExtractable.tryExtract(this);
		} else if (extractProgress != 0) {
			extractProgress = 0;
			notifyUpdate();
		}
	}

	public void updateDurability() {
		// dunno if any of the libraries have a clamp method so
		if (durability < 0) {
			durability = 0;
		} else if (durability > maxDurability) {
			durability = maxDurability;
		}
	}

	public void updateDurability(int value) {
		durability = value;
		updateDurability();
	}

	@Override
	public void write(CompoundNBT compound, boolean clientPacket) {
		super.write(compound, clientPacket);
//        ItemStackHelper.saveAllItems(compound, inventory);
		compound.put("Inventory", inventory.serializeNBT());
		compound.putInt("ExtractProgress", extractProgress);
		compound.putInt("Durability", durability);
		compound.putInt("MaxDurability", maxDurability);
	}

	@Override
	protected void fromTag(BlockState state, CompoundNBT compound, boolean clientPacket) {
		super.fromTag(state, compound, clientPacket);
//        inventory = NonNullList.withSize(1, ItemStack.EMPTY);
//        ItemStackHelper.loadAllItems(compound, inventory);
		inventory.deserializeNBT(compound.getCompound("Inventory"));
		extractProgress = compound.getInt("ExtractProgress");
		durability = compound.getInt("Durability");
		maxDurability = compound.getInt("MaxDurability");
	}

	@Override
	public void remove() {
		super.remove();
		invHandler.invalidate();
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, Direction facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return invHandler.cast();
		}

		return super.getCapability(capability, facing);
	}

	public class Inv extends ItemStackHandler {
		public Inv() {
			super(1);
		}

		@Override
		public boolean isItemValid(int slot, ItemStack stack) {
			return stack.getItem().getTags().contains(ModTags.Items.ORE_PIECES.getId());
		}

		@Nonnull
		@Override
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
			if (stack.getItem() instanceof IDrillHead && durability == 0) {
				IDrillHead d = (IDrillHead) stack.getItem();
				maxDurability = d.getDurability();
				durability = maxDurability;
				stack.setCount(0);
			}
			return stack;
		}
	}

	public void particle(IParticleData data) {
		float angle = world.rand.nextFloat() * 360;
		Vector3d offset = new Vector3d(0, 0, 0.25f);
		offset = VecHelper.rotate(offset, angle, Direction.Axis.Y);
		Vector3d target = VecHelper.rotate(offset, getSpeed() > 0 ? 25 : -25, Direction.Axis.Y)
				.add(0, .25f, 0);
		Vector3d center = offset.add(VecHelper.getCenterOf(pos));
		target = VecHelper.offsetRandomly(target.subtract(offset), world.rand, 1 / 128f);
		world.addParticle(data, center.x, center.y - 1.75f, center.z, target.x, target.y, target.z);
	}

	public void particles(int amount, ItemStack stack) {
		for (int i = 0; i < amount; i++) {
			particle(new ItemParticleData(ParticleTypes.ITEM, stack));
		}
	}

	int timeToParticle = 5;
	int lastRenderDurability = durability;

	public void particles() {
		if (durability > 0) {
			float s = Math.abs(getSpeed());
			int t = 0;
			if (s >= 192) {
				t = 9;
			} else if (s >= 128) {
				t = 6;
			} else if (s >= 64) {
				t = 3;
			} else if (s >= 16) {
				t = 2;
			}
			timeToParticle -= t;
			if (timeToParticle <= 0) {
				timeToParticle = RandomUtils.nextInt(15, 17);
				particles(RandomUtils.nextInt(3, 8), new ItemStack(getBlockToMine()));
			}
		} else {
			if (lastRenderDurability != durability && durability == 0) {
				// TODO: subtitle shows item breaking instead of drill breaking
				// TODO: probably just make a custom sound for this
				world.playSound(Minecraft.getInstance().player, pos, SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.BLOCKS, 1, 10);
				particles(RandomUtils.nextInt(30, 50), new ItemStack(Blocks.IRON_BLOCK)); // SUMMON **ALL** THE PARTICLES!!!
			}
		}
		lastRenderDurability = durability;
	}

	public static int getDefaultStress() {
		return 64;
	}

	static {
		ArmInteractionPoint.addPoint(new OreExtractorInteractionPoint(), OreExtractorInteractionPoint::new);
	}

	public static class OreExtractorInteractionPoint extends ArmInteractionPoint {
		@Override
		protected boolean isValid(IBlockReader iBlockReader, BlockPos blockPos, BlockState blockState) {
			return blockState.getBlock() instanceof IOreExtractorBlock;
		}

		@Override
		protected ItemStack insert(World world, ItemStack stack, boolean simulate) {
			if (!(stack.getItem() instanceof IDrillHead)) return stack;
			TileEntity t = world.getTileEntity(pos);
			OreExtractorTile tile;
			if (t instanceof OreExtractorTile) tile = (OreExtractorTile) t;
			else {
				tile = (OreExtractorTile) world.getTileEntity(pos.up());
			}
			if (tile == null) return stack;
			if (tile.durability == 0) {
				if (!simulate) {
					tile.maxDurability = ((IDrillHead) stack.getItem()).getDurability();
					tile.durability = tile.maxDurability;
					tile.sendData();
				}
				ItemStack copy = stack.copy();
				copy.shrink(1);
				return copy;
			}
			return stack;
		}
	}
}
