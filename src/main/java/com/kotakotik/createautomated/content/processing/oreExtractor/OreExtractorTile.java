package com.kotakotik.createautomated.content.processing.oreExtractor;

import com.kotakotik.createautomated.CreateAutomated;
import com.kotakotik.createautomated.api.IDrillHead;
import com.kotakotik.createautomated.api.IExtractable;
import com.kotakotik.createautomated.content.base.IOreExtractorBlock;
import com.kotakotik.createautomated.content.simple.drillHead.DrillHeadItem;
import com.kotakotik.createautomated.register.ModTags;
import com.kotakotik.createautomated.register.config.ModConfig;
import com.simibubi.create.content.contraptions.components.actors.BlockBreakingKineticTileEntity;
import com.simibubi.create.content.logistics.block.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.OreBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
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
	public ResourceLocation drillId;

	public OreExtractorTile(TileEntityType<?> typeIn) {
		super(typeIn);
	}

	public final ItemStackHandler inventory = new Inv();
	//    private NonNullList<ItemStack> inventory = NonNullList.withSize(1, ItemStack.EMPTY);
	public int extractProgress = 0;
	public int durability = 0;
	public float drillPos = 0;
	protected LazyOptional<IItemHandler> invHandler = LazyOptional.of(() -> this.inventory);

	public int maxDurability;

	@Override
	public BlockPos getBreakingPos() {
		return getBlockPos().below(2);
	}

	public boolean isBreakableOre(BlockPos pos) {
		MiningAbility miningAbility = getMiningAbility();
		return (miningAbility == MiningAbility.ORES && getBlockToMine() instanceof OreBlock) || (miningAbility == MiningAbility.ANY && !isExtractable(null)) && !level.isEmptyBlock(getBreakingPos());
	}

	public boolean isExtractable(BlockPos pos) {
		return getBlockToMine() instanceof IExtractable || IExtractable.getRecipe(level, getBreakingPos()).isPresent();
	}

	public boolean isDrillLowEnough() {
		return drillPos < .05 || !ModConfig.SERVER.machines.extractor.extractorAllowToggleRedstone.get();
	}

	@Override
	public boolean shouldRun() {
		return super.shouldRun() && isBreakableOre(getBreakingPos()) && isSpeedRequirementFulfilled() && isDrillLowEnough();
	}

	public boolean shouldRunExtracting() {
		return isExtractable(getBreakingPos()) && isSpeedRequirementFulfilled() && durability > 0 && isDrillLowEnough();
	}

	public Block getBlockToMine() {
		return level.getBlockState(getBreakingPos()).getBlock();
	}

	@Override
	protected float getBreakSpeed() {
		return super.getBreakSpeed() * 3;
	}

	@Override
	public void tick() {
		assert level != null;
		if (level.isClientSide && (shouldRunExtracting() || shouldRun())) {
			particles();
		}

		super.tick();
		if (!level.isClientSide) {
			doRedstoneStuff();
		}

		if (shouldRunExtracting()) {
			IExtractable.tryExtract(this);
		} else if (extractProgress != 0) {
			extractProgress = 0;
			notifyUpdate();
		}
	}

	public boolean isRedstonePowered() {
		return level.getBlockState(worldPosition.below()).getOptionalValue(BlockStateProperties.POWERED).orElse(false) || getBlockState().getValue(BlockStateProperties.POWERED);
	}

	protected void doRedstoneStuff() {
		if (!ModConfig.SERVER.machines.extractor.extractorAllowToggleRedstone.get()) return;
		float toSet = drillPos;
		if (isRedstonePowered()) {
			toSet += .03f;
		} else {
			toSet -= .03f;
		}
		toSet = MathHelper.clamp(toSet, 0, .4f);
		if (toSet != drillPos) {
			drillPos = toSet;
			sendData();
		}
	}

	public void updateDurability() {
		if (ModConfig.SERVER.machines.extractor.unbreakableDrills.get()) {
			durability = maxDurability;
		} else {
			durability = MathHelper.clamp(durability, 0, maxDurability);
		}
	}

	public void updateDurability(int value) {
		durability = value;
		updateDurability();
	}

	public static MiningAbility getMiningAbility() {
		return ModConfig.SERVER.machines.extractor.miningAbility.get();
	}

	@Override
	public void write(CompoundNBT compound, boolean clientPacket) {
		super.write(compound, clientPacket);
//        ItemStackHelper.saveAllItems(compound, inventory);
		compound.put("Inventory", inventory.serializeNBT());
		compound.putInt("ExtractProgress", extractProgress);
		compound.putInt("Durability", durability);
		compound.putInt("MaxDurability", maxDurability);
		compound.putFloat("DrillPos", drillPos);
		if (drillId != null && durability != 0) {
			compound.putString("DrillId", drillId == null ? CreateAutomated.MODID + ":block/ore_extractor/drill" : drillId.toString());
		}
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
		drillPos = compound.getFloat("DrillPos");
		String drillIdTempt = compound.getString("DrillId");
		if (!drillIdTempt.equals("")) {
			drillId = new ResourceLocation(drillIdTempt);
		}
	}

	public void setDrill(int durability, ResourceLocation id) {
		this.maxDurability = durability;
		this.durability = durability;
		this.drillId = id;
	}

	public void setDrill(IDrillHead head, ResourceLocation id) {
		setDrill(head.getDurability(), id);
	}

	public void setDrill(DrillHeadItem head) {
		setDrill(head, head.getRegistryName());
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
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

	@Override
	public World getWorld() {
		return level;
	}

	public class Inv extends ItemStackHandler {
		public Inv() {
			super(1);
		}

		@Override
		public boolean isItemValid(int slot, ItemStack stack) {
			return stack.getItem().getTags().contains(ModTags.Items.ORE_PIECES.getName());
		}

		@Nonnull
		@Override
		public ItemStack extractItem(int slot, int amount, boolean simulate) {
			if (!ModConfig.SERVER.machines.extractor.allowExtractOrePieces.get()) return ItemStack.EMPTY;
			return super.extractItem(slot, amount, simulate);
		}

		@Nonnull
		@Override
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
			if (ModConfig.SERVER.machines.extractor.allowInsertDrills.get() && stack.getItem() instanceof IDrillHead && durability == 0) {
				IDrillHead d = (IDrillHead) stack.getItem();
				setDrill((DrillHeadItem) stack.getItem());
				stack.setCount(0);
			}
			return stack;
		}
	}

	public void particle(IParticleData data) {
		float angle = level.random.nextFloat() * 360;
		Vector3d offset = new Vector3d(0, 0, 0.25f);
		offset = VecHelper.rotate(offset, angle, Direction.Axis.Y);
		Vector3d target = VecHelper.rotate(offset, getSpeed() > 0 ? 25 : -25, Direction.Axis.Y)
				.add(0, .25f, 0);
		Vector3d center = offset.add(VecHelper.getCenterOf(worldPosition));
		target = VecHelper.offsetRandomly(target.subtract(offset), level.random, 1 / 128f);
		level.addParticle(data, center.x, center.y - 1.75f, center.z, target.x, target.y, target.z);
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
				level.playSound(null, worldPosition, SoundEvents.ITEM_BREAK, SoundCategory.BLOCKS, 1, 10);
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
		protected boolean armCanInsertDrills() {
			if (ModConfig.SERVER.machines.extractor.armCanInsertDrills != null)
				return ModConfig.SERVER.machines.extractor.armCanInsertDrills.get();
			return true;
		}

		protected boolean armCanExtractOrePieces() {
			if (ModConfig.SERVER.machines.extractor.armCanExtractOrePieces != null)
				return ModConfig.SERVER.machines.extractor.armCanExtractOrePieces.get();
			return false;
		}

		public OreExtractorInteractionPoint() {

		}

		@Override
		protected boolean isValid(IBlockReader iBlockReader, BlockPos blockPos, BlockState blockState) {
			return (armCanExtractOrePieces() || armCanInsertDrills()) && blockState.getBlock() instanceof IOreExtractorBlock;
		}

		@Override
		protected ItemStack insert(World world, ItemStack stack, boolean simulate) {
			if (!armCanInsertDrills()) return stack;
			if (!(stack.getItem() instanceof IDrillHead)) return stack;
			TileEntity t = world.getBlockEntity(pos);
			OreExtractorTile tile;
			if (t instanceof OreExtractorTile) tile = (OreExtractorTile) t;
			else {
				tile = (OreExtractorTile) world.getBlockEntity(pos.above());
			}
			if (tile == null) return stack;
			if (tile.durability == 0) {
				if (!simulate) {
					tile.setDrill((DrillHeadItem) stack.getItem());
					tile.sendData();
				}
				ItemStack copy = stack.copy();
				copy.shrink(1);
				return copy;
			}
			return stack;
		}

		@Override
		protected ItemStack extract(World world, int slot, int amount, boolean simulate) {
			return armCanExtractOrePieces() ? super.extract(world, slot, amount, simulate) : ItemStack.EMPTY;
		}

		@Override
		protected void cycleMode() {
			if (armCanInsertDrills() && armCanExtractOrePieces()) {
				super.cycleMode();
			} else if (armCanInsertDrills() && !armCanExtractOrePieces()) {
				mode = Mode.DEPOSIT;
			} else if (armCanExtractOrePieces() && !armCanInsertDrills()) {
				mode = Mode.TAKE;
			}
		}
	}

	public enum MiningAbility {
		NONE,
		ORES,
		ANY
	}
}
