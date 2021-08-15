package com.kotakotik.createautomated.content.worldgen;

import com.kotakotik.createautomated.register.ModConfigAnnotations;
import com.kotakotik.createautomated.register.config.ModConfig;
import com.simibubi.create.foundation.worldgen.ConfigDrivenDecorator;
import com.simibubi.create.foundation.worldgen.ConfigDrivenFeatureEntry;
import com.simibubi.create.foundation.worldgen.ConfigDrivenOreFeature;
import com.simibubi.create.foundation.worldgen.ConfigDrivenOreFeatureConfig;
import com.simibubi.create.repack.registrate.util.nullness.NonNullSupplier;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.template.BlockMatchRuleTest;
import net.minecraft.world.gen.feature.template.IRuleTestType;
import net.minecraft.world.gen.feature.template.RuleTest;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.world.BiomeLoadingEvent;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

public class DimensionalConfigDrivenFeatureEntry extends ConfigDrivenFeatureEntry {
	private final Function<ForgeConfigSpec.Builder, FilterConfig> defaultBiomes;
	private final Function<ForgeConfigSpec.Builder, FilterConfig> defaultBlocks;
	private final Function<ForgeConfigSpec.Builder, FilterConfig> defaultCategories;

	public static class FilterConfig extends ModConfig.Config {
		protected final String name;
		protected final Predicate<String> validator;
		protected final boolean blacklistEnabledByDefault;
		protected final String listName;

		public FilterConfig(String name, Predicate<String> validator, boolean blacklistEnabledByDefault, String listName, List<String> defaults, String... description) {
			this.name = name;
			this.validator = validator;
			this.blacklistEnabledByDefault = blacklistEnabledByDefault;
			this.listName = listName;
			blacklist = b(blacklistEnabledByDefault, "blacklist", "Whether or not this filter is in blacklist mode", ModConfigAnnotations.hide());
			this.defaults = defaults;
			listDescription = description;
		}

		public ConfigBool blacklist;
		private final List<String> defaults;
		private final String[] listDescription;
		public ForgeConfigSpec.ConfigValue<List<? extends String>> list;

		@Override
		protected void registerAll(ForgeConfigSpec.Builder builder) {
			// why are javas arrays so stupid
			String[] comments = new String[listDescription.length + 1];
			comments[0] = ModConfigAnnotations.hide();
			System.arraycopy(listDescription, 0, comments, 1, listDescription.length);
			builder.comment(comments);
			list = builder.defineList(listName, defaults, (o) -> o instanceof String && validator.test((String) o));
			super.registerAll(builder);
		}

		@Override
		public String getName() {
			return name;
		}
	}

	public DimensionalConfigDrivenFeatureEntry(String id, NonNullSupplier<? extends Block> block, int clusterSize, float frequency,
											   Function<ForgeConfigSpec.Builder, FilterConfig> defaultBiomes,
											   Function<ForgeConfigSpec.Builder, FilterConfig> defaultBlocks,
											   Function<ForgeConfigSpec.Builder, FilterConfig> defaultCategories) {
		super(id, block, clusterSize, frequency);
		this.defaultBiomes = defaultBiomes;
		this.defaultBlocks = defaultBlocks;
		this.defaultCategories = defaultCategories;
	}

//	public static final Predicate<BiomeLoadingEvent> isNether = e -> e.getCategory().equals(Biome.Category.THEEND);
//	public static final Predicate<BiomeLoadingEvent> isEnd = e -> e.getName() == Biomes.THE_END.getRegistryName();
//	public static final Predicate<BiomeLoadingEvent> isOverworld = e -> !isNether.test(e) && !isEnd.test(e);

	public static DimensionalConfigDrivenFeatureEntry nether(String id, NonNullSupplier<? extends Block> block, int clusterSize, float frequency) {
		return new DimensionalConfigDrivenFeatureEntry(id, block, clusterSize, frequency,
				b -> biomeConfig(true, Collections.emptyList()),
				b -> blockConfig(false, Collections.singletonList("#" + Tags.Blocks.NETHERRACK.getName())),
				b -> categoryConfig(false, Collections.singletonList(Biome.Category.NETHER.getName())));
	}

	public static DimensionalConfigDrivenFeatureEntry overworld(String id, NonNullSupplier<? extends Block> block, int clusterSize, float frequency) {
		return new DimensionalConfigDrivenFeatureEntry(id, block, clusterSize, frequency,
				b -> biomeConfig(true, Collections.emptyList()),
				b -> blockConfig(false, Collections.singletonList("#" + Tags.Blocks.DIRT.getName())),
				b -> categoryConfig(true, Arrays.asList(Biome.Category.NETHER.getName(), Biome.Category.THEEND.getName())));
	}

	Optional<ConfiguredFeature<?, ?>> cachedFeature = Optional.empty();

	//	public FilterConfig biomes = nested(0, () -> new FilterConfig("biomes", $ -> true, true, "biomes"), ModConfigAnnotations.screenUnsupported("lists"));
	public FilterConfig biomes;
	public FilterConfig blocks;
	public FilterConfig categories;

	// pain intensifies
	public ConfiguredFeature<?, ?> getFeature() {
		if (!this.cachedFeature.isPresent()) {
			this.cachedFeature = Optional.of(this.createFeature());
		}

		return this.cachedFeature.get();
	}

	public boolean blockMatches(ResourceLocation id, Set<ResourceLocation> tags) {
		return blocks.list.get().stream().anyMatch(l -> {
			if (l.startsWith("#")) {
				return tags.contains(new ResourceLocation(l.replace("#", "")));
			}
			return new ResourceLocation(l).equals(id);
		}) != blocks.blacklist.get();
	}

	public RuleTest createTest() {
		return new BlockMatchRuleTest(Blocks.AIR) {
			@Override
			public boolean test(BlockState state, Random random) {
				return blockMatches(state.getBlock().getRegistryName(), state.getBlock().getTags());
			}

			@Override
			protected IRuleTestType<?> getType() {
				return IRuleTestType.BLOCK_TEST;
			}
		};
	}

	protected ConfiguredFeature<?, ?> createFeature() {
		ConfigDrivenOreFeatureConfig config = new ConfigDrivenOreFeatureConfig(createTest(), this.block.get().defaultBlockState(), this.id) {
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

	public static FilterConfig biomeConfig(boolean blacklistEnabled, List<String> conf) {
		return new FilterConfig("biomes", $ -> true, blacklistEnabled, "biomes", conf);
	}

	public static FilterConfig blockConfig(boolean blacklistEnabled, List<String> conf) {
		return new FilterConfig("blocks", $ -> true, blacklistEnabled, "blocks", conf);
	}

	public static FilterConfig categoryConfig(boolean blacklistEnabled, List<String> conf) {
		return new FilterConfig("categories", $ -> true, blacklistEnabled, "categories", conf);
	}

	@Override
	protected void registerAll(ForgeConfigSpec.Builder builder) {
		builder.comment(ModConfigAnnotations.screenUnsupported("lists"));
		biomes = defaultBiomes.apply(builder);
		builder.push(biomes.getName());
		biomes.registerAll(builder);
		builder.pop();
		builder.comment(ModConfigAnnotations.screenUnsupported("lists"), "Start with # if is a tag");
		blocks = defaultBlocks.apply(builder);
		builder.push(blocks.getName());
		blocks.registerAll(builder);
		builder.pop();
		builder.comment(ModConfigAnnotations.screenUnsupported("lists"));
		categories = defaultCategories.apply(builder);
		builder.push(categories.getName());
		categories.registerAll(builder);
		builder.pop();
		super.registerAll(builder);
	}

	public boolean biomeMatches(ResourceLocation name) {
		return biomes.list.get().stream().anyMatch(l -> new ResourceLocation(l).equals(name)) != biomes.blacklist.get();
	}

	public boolean categoryMatches(String name) {
		return categories.list.get().stream().anyMatch(s -> s.equals(name)) != biomes.blacklist.get();
	}

	public boolean shouldRegister(BiomeLoadingEvent event) {
		return biomeMatches(event.getName()) && categoryMatches(event.getCategory().getName());
	}
}
