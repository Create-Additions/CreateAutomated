package com.kotakotik.createautomated.content.simple.node;

import com.kotakotik.createautomated.register.config.ModConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.PushReaction;

public class NodeBlock extends Block {
	public NodeBlock(Properties p_i48440_1_) {
		super(p_i48440_1_);
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState p_149656_1_) {
		return ModConfig.SERVER.machines.extractor.allowMovingNodes.get() ? PushReaction.NORMAL : PushReaction.BLOCK;
	}
}
