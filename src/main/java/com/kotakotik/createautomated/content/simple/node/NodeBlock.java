package com.kotakotik.createautomated.content.simple.node;

import com.kotakotik.createautomated.api.INode;
import com.kotakotik.createautomated.content.processing.oreExtractor.OreExtractorTile;
import com.kotakotik.createautomated.register.ModTiles;
import com.kotakotik.createautomated.register.config.ModConfig;
import com.kotakotik.createautomated.register.config.ModServerConfig;
import com.simibubi.create.foundation.block.ITE;
import com.simibubi.create.foundation.config.CKinetics;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.PushReaction;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class NodeBlock extends Block implements INode, ITE<NodeTile> {
	public NodeBlock(Properties p_i48440_1_) {
		super(p_i48440_1_);
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState p_149656_1_) {
		return ModConfig.SERVER.machines.extractor.nodeMovement.get() == CKinetics.SpawnerMovementSetting.UNMOVABLE ? PushReaction.BLOCK : PushReaction.NORMAL;
	}

	public ModServerConfig.Extractor.Nodes.Node getConfig() {
		return ModServerConfig.Extractor.Nodes.all.get(getRegistryName());
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return !getConfig().isInfinite();
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return hasTileEntity(state) ? ModTiles.NODE.create() : null;
	}

	@Override
	public void takeCount(OreExtractorTile tile, BlockPos belowBlock, int a) {
		if (!getConfig().isInfinite()) {
			withTileEntityDo(tile.getLevel(), belowBlock, t -> t.takeCount(a));
		}
	}

	@Override
	public Class<NodeTile> getTileEntityClass() {
		return NodeTile.class;
	}
}
