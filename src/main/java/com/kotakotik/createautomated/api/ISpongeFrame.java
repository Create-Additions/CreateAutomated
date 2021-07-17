package com.kotakotik.createautomated.api;

import com.simibubi.create.content.contraptions.processing.InWorldProcessing;

import javax.annotation.Nullable;

public interface ISpongeFrame {
	@Nullable
	InWorldProcessing.Type getType();
}
