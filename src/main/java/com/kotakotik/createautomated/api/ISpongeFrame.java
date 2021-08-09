package com.kotakotik.createautomated.api;

import com.simibubi.create.content.contraptions.processing.InWorldProcessing;
import net.minecraftforge.common.extensions.IForgeBlock;

import javax.annotation.Nullable;

public interface ISpongeFrame extends IForgeBlock {
	@Nullable
	InWorldProcessing.Type getType();
}
