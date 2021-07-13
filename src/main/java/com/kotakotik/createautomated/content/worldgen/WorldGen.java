package com.kotakotik.createautomated.content.worldgen;

import com.kotakotik.createautomated.CreateAutomated;
import com.kotakotik.createautomated.register.config.ModConfig;
import com.simibubi.create.repack.registrate.util.nullness.NonNullSupplier;
import net.minecraft.block.Block;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.template.RuleTest;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WorldGen {
	//    public static ConfiguredFeature<?, ?> LAPIS_NODE;
	public static List<ConfiguredFeature<?, ?>> NODES = new ArrayList<>();
	public static List<ConfiguredFeature<?, ?>> NETHER_NODES = new ArrayList<>();
	protected static HashMap<String, FeatureToRegister> toReg = new HashMap<>();

	public static class FeatureToRegister {
		public final NonNullSupplier<Block> block;
		public final int veinSize;
		public final int minHeight;
		public final int maxHeight;
		public final int frequency;
		public final RuleTest test;
		public final NodeDimension dim;
		public final String name;
		public ConfiguredFeature<?, ?> registered;

		public FeatureToRegister(NonNullSupplier<Block> block, int veinSize, int minHeight, int maxHeight, int frequency, RuleTest test, NodeDimension dim, String name) {
			this.block = block;
			this.veinSize = veinSize;
			this.minHeight = minHeight;
			this.maxHeight = maxHeight;
			this.frequency = frequency;
			this.test = test;
			this.dim = dim;
			this.name = name;
		}

		public ConfiguredFeature<?, ?> create() {
			ConfiguredFeature<?, ?> f = Feature.ORE.configure(new OreFeatureConfig(
					test, block.get().getDefaultState(), ModConfig.worldGenVeinSizes.get(name).get()))
					.decorate(Placement.RANGE.configure(new TopSolidRangeConfig(ModConfig.worldGenMinHeights.get(name).get(), 0, ModConfig.worldGenMaxHeights.get(name).get())))
					.spreadHorizontally()
					.repeat(ModConfig.worldGenFrequencies.get(name).get());

			this.registered = f;
			return f;
		}
	}

	public static void register() {
	}

	public static void gen(BiomeLoadingEvent e) {
		BiomeGenerationSettingsBuilder gen = e.getGeneration();
		if (e.getCategory() == Biome.Category.NETHER) {
			NETHER_NODES.forEach(n -> gen.feature(GenerationStage.Decoration.SURFACE_STRUCTURES, n));
		} else if (e.getName() != Biomes.THE_END.getRegistryName()) {
			NODES.forEach(n -> gen.feature(GenerationStage.Decoration.SURFACE_STRUCTURES, n));
		}
	}

	public static void reg(FMLCommonSetupEvent e) {
		toReg.forEach((name, toReg) -> {
			if (ModConfig.worldGenEnabled.get(name).get()) {
				;
				register(name, toReg.create(), toReg.dim);
			}
		});
	}

	public static FeatureToRegister add(String name, NonNullSupplier<Block> block, int veinSize, int minHeight, int maxHeight, int frequency, RuleTest test, NodeDimension dim) {
		FeatureToRegister f = new FeatureToRegister(block, veinSize, minHeight, maxHeight, frequency, test, dim, name);
		toReg.put(name, f);
		return f;
	}

	public static <F extends IFeatureConfig> ConfiguredFeature<F, ?> register(String name, ConfiguredFeature<F, ?> feature, NodeDimension dim) {
		switch (dim) {
			case OVERWORLD:
				NODES.add(feature);
			case NETHER:
				NETHER_NODES.add(feature);
		}
		return Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, CreateAutomated.modid + ":" + name, feature);
	}

	public enum NodeDimension {
		NETHER,
		OVERWORLD,
		END
	}
}
