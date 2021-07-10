package com.kotakotik.createautomated.util;

import com.kotakotik.createautomated.CreateAutomated;
import com.simibubi.create.foundation.config.StressConfigDefaults;
import com.simibubi.create.repack.registrate.builders.BlockBuilder;
import com.simibubi.create.repack.registrate.util.nullness.NonNullUnaryOperator;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;

public class AddonStressConfigDefaults {
	protected static Field registeredDefaultImpactsField = ObfuscationReflectionHelper.findField(StressConfigDefaults.class, "registeredDefaultImpacts");

	protected static Map<ResourceLocation, Double> getRegisteredDefaultImpacts() {
		try {
			return (Map<ResourceLocation, Double>) registeredDefaultImpactsField.get(null);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static <B extends Block, P> NonNullUnaryOperator<BlockBuilder<B, P>> setImpact(double impact) {
		return (b) -> {
			Objects.requireNonNull(getRegisteredDefaultImpacts()).put(new ResourceLocation(CreateAutomated.modid, b.getName()), impact);
			return b;
		};
	}
}
