package com.kotakotik.createautomated.content.simple.node;

import com.kotakotik.createautomated.register.config.ModServerConfig;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.LazyValue;

public class NodeTile extends TileEntity {
	public LazyValue<Integer> count = new LazyValue<>(() -> getConfig().getCount());

	public void setCount(int c) {
		count = new LazyValue<>(() -> c);
	}

	public NodeTile(TileEntityType<?> p_i48289_1_) {
		super(p_i48289_1_);
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		nbt.putInt("count", count.get());
		return super.save(nbt);
	}

	@Override
	public void load(BlockState p_230337_1_, CompoundNBT nbt) {
		setCount(nbt.getInt("count"));
		super.load(p_230337_1_, nbt);
	}

	public ModServerConfig.Extractor.Nodes.Node getConfig() {
		return ((NodeBlock) getBlockState().getBlock()).getConfig();
	}

	public void takeCount(int amount) {
		setCount(count.get() - amount * (getConfig().randomizeDamage() ? getLevel().random.nextInt(2) + 1 : 1));
		if (count.get() < 1) {
			getLevel().destroyBlock(getBlockPos(), true);
		}
	}
}
