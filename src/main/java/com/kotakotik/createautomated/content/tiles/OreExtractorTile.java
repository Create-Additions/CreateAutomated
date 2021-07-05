package com.kotakotik.createautomated.content.tiles;

import com.kotakotik.createautomated.content.base.IExtractable;
import com.kotakotik.createautomated.content.base.INode;
import com.kotakotik.createautomated.register.ModTags;
import com.simibubi.create.content.contraptions.components.actors.BlockBreakingKineticTileEntity;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.OreBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.commons.lang3.RandomUtils;

import javax.annotation.Nonnull;
import java.util.Random;

public class OreExtractorTile extends BlockBreakingKineticTileEntity {
    public OreExtractorTile(TileEntityType<?> typeIn) {
        super(typeIn);
    }

    public final ItemStackHandler inventory = new Inv();
    //    private NonNullList<ItemStack> inventory = NonNullList.withSize(1, ItemStack.EMPTY);
    public int extractProgress = 0;
    protected LazyOptional<IItemHandler> invHandler = LazyOptional.of(() -> this.inventory);

    @Override
    public BlockPos getBreakingPos() {
        return getPos().down(2);
    }

    public boolean isBreakableOre(BlockPos pos) {
        return getBlockToMine() instanceof OreBlock;
    }

    public boolean isExtractable(BlockPos pos) {
        return getBlockToMine() instanceof IExtractable;
    }

    @Override
    public boolean shouldRun() {
        return super.shouldRun() && isBreakableOre(getBreakingPos()) && isSpeedRequirementFulfilled();
    }

    public boolean shouldRunExtracting() {
        return isExtractable(getBreakingPos()) && isSpeedRequirementFulfilled();
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
        if (!world.isAirBlock(getBreakingPos())) {
            particles();
        }

        super.tick();

        if (shouldRunExtracting()) {
            Block below = getBlockToMine();
            BlockPos belowBlock = getBreakingPos();
            ((IExtractable) below).extractTick(this);
            if (below instanceof INode) {
                INode node = (INode) below;
                int progress = this.extractProgress + node.getProgressToAdd(world, belowBlock, this.getPos(), (int) getSpeed());
                if (progress >= node.getRequiredProgress(world, belowBlock, this.getPos())) {
                    progress = 0;
                    ItemStack stack = inventory.getStackInSlot(0);
                    ItemStack toAdd = node.getOrePieceStack(world, belowBlock, this.getPos(), new Random());
                    if (stack.getItem().getRegistryName().equals(toAdd.getItem().getRegistryName())) {
                        stack.setCount(Math.min(stack.getMaxStackSize(), stack.getCount() + toAdd.getCount()));
                    } else if (stack.isEmpty()) {
                        inventory.setStackInSlot(0, toAdd);
                    }
                }
                extractProgress = progress;
            }
        } else {
            extractProgress = 0;
        }
    }

    @Override
    public void write(CompoundNBT compound, boolean clientPacket) {
        super.write(compound, clientPacket);
//        ItemStackHelper.saveAllItems(compound, inventory);
        compound.put("Inventory", inventory.serializeNBT());
        compound.putInt("ExtractProgress", extractProgress);
    }

    @Override
    protected void fromTag(BlockState state, CompoundNBT compound, boolean clientPacket) {
        super.fromTag(state, compound, clientPacket);
//        inventory = NonNullList.withSize(1, ItemStack.EMPTY);
//        ItemStackHelper.loadAllItems(compound, inventory);
        inventory.deserializeNBT(compound.getCompound("Inventory"));
        extractProgress = compound.getInt("ExtractProgress");
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
        ;
    }

    int timeToParticle = 5;

    public void particles() {
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
    }
}
