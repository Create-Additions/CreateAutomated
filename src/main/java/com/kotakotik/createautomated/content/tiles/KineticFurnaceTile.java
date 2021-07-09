package com.kotakotik.createautomated.content.tiles;

import com.kotakotik.createautomated.content.base.IDrillHead;
import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class KineticFurnaceTile extends KineticTileEntity {
    public final ItemStackHandler inventory = new KineticFurnaceTile.Inv();
    protected LazyOptional<IItemHandler> invHandler = LazyOptional.of(() -> this.inventory);

    public int progress = 0;

    public KineticFurnaceTile(TileEntityType<?> typeIn) {
        super(typeIn);
    }

    @Override
    public void tick() {
        super.tick();

        if(world.isRemote || getSpeed() == 0) return;
        int speed = (int) Math.abs(getSpeed());
        Optional<FurnaceRecipe> fR = getFurnaceRecipe();
        if(fR.isPresent()) {
            FurnaceRecipe recipe = fR.get();
            progress++;
            inventory.setStackInSlot(1, recipe.getCraftingResult(null));
            sendData();
        } else {
            if(progress != 0) {
                progress = 0;
                sendData();
            }
        }
    }

    @Override
    protected void write(CompoundNBT compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        compound.putInt("Progress", progress);
        inventory.deserializeNBT(compound);
    }

    @Override
    protected void fromTag(BlockState state, CompoundNBT compound, boolean clientPacket) {
        super.fromTag(state, compound, clientPacket);
        progress = compound.getInt("Progress");
        compound.put("Inventory", inventory.serializeNBT());
    }

    public Optional<FurnaceRecipe> getFurnaceRecipe(RecipeWrapper w) {
       return world.getRecipeManager().getRecipe(IRecipeType.SMELTING, w, world);
    }

    public Optional<FurnaceRecipe> getFurnaceRecipe() {
        return getFurnaceRecipe(new RecipeWrapper(inventory));
    }

    public class Inv extends ItemStackHandler {
        public Inv() {
            super(2);
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return slot == 0 && getFurnaceRecipe(new RecipeWrapper(inventory) {
                @Override
                public ItemStack getStackInSlot(int slot) {
                    return stack;
                }
            }).isPresent();
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            // vanilla already does this lol
//            if(isItemValid(slot, stack)) {
//                return super.insertItem(slot, stack, simulate);
//            }
            return super.insertItem(slot, stack, simulate);
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return slot == 1 ? super.extractItem(slot, amount, simulate) : ItemStack.EMPTY;
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return invHandler.cast();
        }
        return super.getCapability(cap, side);
    }
}
