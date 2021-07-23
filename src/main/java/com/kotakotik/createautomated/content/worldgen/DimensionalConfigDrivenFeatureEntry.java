package com.kotakotik.createautomated.content.worldgen;

import com.kotakotik.createautomated.register.config.ModConfig;
import com.simibubi.create.foundation.worldgen.ConfigDrivenDecorator;
import com.simibubi.create.foundation.worldgen.ConfigDrivenFeatureEntry;
import com.simibubi.create.foundation.worldgen.ConfigDrivenOreFeature;
import com.simibubi.create.foundation.worldgen.ConfigDrivenOreFeatureConfig;
import com.simibubi.create.repack.registrate.util.nullness.NonNullSupplier;
import net.minecraft.block.Block;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.template.RuleTest;
import net.minecraft.world.gen.feature.template.TagMatchRuleTest;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.world.BiomeLoadingEvent;

import java.util.Optional;
import java.util.function.Predicate;

public class DimensionalConfigDrivenFeatureEntry extends ConfigDrivenFeatureEntry {
	public final Predicate<BiomeLoadingEvent> shouldRegister;
	public final RuleTest target;

	public DimensionalConfigDrivenFeatureEntry(String id, NonNullSupplier<? extends Block> block, int clusterSize, float frequency, Predicate<BiomeLoadingEvent> shouldRegister, RuleTest target) {
		super(id, block, clusterSize, frequency);
		this.shouldRegister = shouldRegister;
		this.target = target;
	}

	public static final Predicate<BiomeLoadingEvent> isNether = e -> e.getCategory().equals(Biome.Category.NETHER);
	public static final Predicate<BiomeLoadingEvent> isEnd = e -> e.getName() == Biomes.THE_END.getRegistryName();
	public static final Predicate<BiomeLoadingEvent> isOverworld = e -> !isNether.test(e) && !isEnd.test(e);

	public static DimensionalConfigDrivenFeatureEntry nether(String id, NonNullSupplier<? extends Block> block, int clusterSize, float frequency) {
		return new DimensionalConfigDrivenFeatureEntry(id, block, clusterSize, frequency, isNether, new TagMatchRuleTest(Tags.Blocks.NETHERRACK));
	}

	public static DimensionalConfigDrivenFeatureEntry overworld(String id, NonNullSupplier<? extends Block> block, int clusterSize, float frequency) {
		return new DimensionalConfigDrivenFeatureEntry(id, block, clusterSize, frequency, isOverworld, new TagMatchRuleTest(Tags.Blocks.DIRT));
	}

	Optional<ConfiguredFeature<?, ?>> cachedFeature = Optional.empty();

	// pain intensities
	public ConfiguredFeature<?, ?> getFeature() {
		if (!this.cachedFeature.isPresent()) {
			this.cachedFeature = Optional.of(this.createFeature());
		}

		return this.cachedFeature.get();
	}

	protected ConfiguredFeature<?, ?> createFeature() {
		ConfigDrivenOreFeatureConfig config = new ConfigDrivenOreFeatureConfig(target, this.block.get().defaultBlockState(), this.id) {
			@Override
			protected ConfigDrivenFeatureEntry entry() {
				return DimensionalConfigDrivenFeatureEntry.this;
			}

			@Override
			public float getFrequency() {
				return ModConfig.COMMON.worldGen.enabled.get() ? frequency.getF() : 0;
			}
		};
		return ConfigDrivenOreFeature.INSTANCE.configured(config).decorated(ConfigDrivenDecorator.INSTANCE.configured(config));
	}


}
