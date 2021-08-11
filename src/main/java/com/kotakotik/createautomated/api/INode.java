package com.kotakotik.createautomated.api;

import com.kotakotik.createautomated.content.processing.oreExtractor.OreExtractorTile;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.extensions.IForgeBlock;

public interface INode extends IForgeBlock {
	void takeCount(OreExtractorTile tile, BlockPos pos, int oreAdded);

	default NodeInfo.Entry getNodeInfo() {
		return NodeInfo.info.get(getBlock().getRegistryName());
	}
}
