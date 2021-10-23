package com.kotakotik.createautomated.api;

import com.kotakotik.createautomated.content.processing.oreExtractor.OreExtractorTile;
import com.kotakotik.createautomated.register.config.ModConfig;
import com.simibubi.create.foundation.config.ContraptionMovementSetting;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.extensions.IForgeBlock;

public interface INode extends IForgeBlock, ContraptionMovementSetting.IMovementSettingProvider {
	void takeCount(OreExtractorTile tile, BlockPos pos, int oreAdded);

	default NodeInfo.Entry getNodeInfo() {
		return NodeInfo.info.get(getBlock().getRegistryName());
	}

	@Override
	default ContraptionMovementSetting getContraptionMovementSetting() {
		return ModConfig.SERVER.machines.extractor.nodeMovement.get();
	}
}
