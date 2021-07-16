package com.kotakotik.createautomated.register;

import com.kotakotik.createautomated.CreateAutomated;
import com.kotakotik.createautomated.content.base.IOreExtractorBlock;
import com.kotakotik.createautomated.content.processing.oreExtractor.recipe.ExtractingRecipeGen;
import com.kotakotik.createautomated.content.processing.oreExtractor.TopOreExtractorBlock;
import com.kotakotik.createautomated.content.processing.picker.recipe.PickingRecipeGen;
import com.kotakotik.createautomated.content.simple.drillHead.DrillHeadItem;
import com.kotakotik.createautomated.content.worldgen.WorldGen;
import com.kotakotik.createautomated.register.config.ModCommonConfig;
import com.kotakotik.createautomated.register.recipes.ModCrushingRecipes;
import com.kotakotik.createautomated.register.recipes.ModDeployingRecipes;
import com.kotakotik.createautomated.register.recipes.ModMixingRecipes;
import com.simibubi.create.AllItems;
import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.processing.HeatCondition;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.repack.registrate.builders.BlockBuilder;
import com.simibubi.create.repack.registrate.builders.ItemBuilder;
import com.simibubi.create.repack.registrate.providers.DataGenContext;
import com.simibubi.create.repack.registrate.providers.RegistrateRecipeProvider;
import com.simibubi.create.repack.registrate.util.entry.BlockEntry;
import com.simibubi.create.repack.registrate.util.entry.ItemEntry;
import com.simibubi.create.repack.registrate.util.nullness.NonNullBiConsumer;
import com.simibubi.create.repack.registrate.util.nullness.NonNullConsumer;
import com.simibubi.create.repack.registrate.util.nullness.NonNullFunction;
import com.simibubi.create.repack.registrate.util.nullness.NonNullSupplier;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.feature.template.TagMatchRuleTest;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static com.kotakotik.createautomated.CreateAutomated.modEventBus;

public class RecipeItems {
	public static class ExtractableResource {
		public final String name;
		public CreateRegistrate reg;

		public BlockEntry<Block> NODE;
		public ItemEntry<Item> ORE_PIECE;

		public final Tags.IOptionalNamedTag<Block> NODE_TAG;
		public final Tags.IOptionalNamedTag<Item> ORE_PIECE_TAG;

		List<BiConsumer<RegistrateRecipeProvider, ExtractableResource>> recipeGen = new ArrayList<>();
		public WorldGen.FeatureToRegister oreGenFeature;
		public Function<BlockBuilder<Block, CreateRegistrate>, BlockBuilder<Block, CreateRegistrate>> nodeConf = c -> c;

		public ExtractableResource(String name, CreateRegistrate reg, Function<ItemBuilder<Item, CreateRegistrate>, ItemBuilder<Item, CreateRegistrate>> orePieceConf) {
			this.name = name;
			this.reg = reg;

			NODE_TAG = ModTags.Blocks.tag("nodes/" + name);
			ORE_PIECE_TAG = ModTags.Items.tag("ore_pieces/" + name);

			ORE_PIECE = orePieceConf.apply(reg.item(name + "_ore_piece", Item::new).recipe((ctx, prov) -> recipeGen.forEach(r -> r.accept(prov, this))).tag(ModTags.Items.ORE_PIECES, ORE_PIECE_TAG).model((ctx, prov) -> {
				prov.singleTexture(
						ctx.getName(),
						prov.mcLoc("item/generated"),
						"layer0",
						prov.modLoc("item/ore_pieces/" + name));
			})).register();
		}

		public ExtractableResource oreGen(int veinSize, int minHeight, int maxHeight, int frequency, WorldGen.NodeDimension dim) {
			oreGenFeature = WorldGen.add(name + "_node", NODE::get, veinSize, minHeight, maxHeight, frequency, dim == WorldGen.NodeDimension.NETHER ? new TagMatchRuleTest(Tags.Blocks.NETHERRACK) : new TagMatchRuleTest(Tags.Blocks.DIRT), dim);
			ModCommonConfig.addWorldGen(oreGenFeature);
			return this;
//            WorldGen.register(name + "_node", f);
//            this.oreGenFeature = f;
//            return this;
		}

		public ExtractableResource oreGen(int veinSize, int frequency, WorldGen.NodeDimension dimension) {
			return oreGen(veinSize, 40, 256, frequency, dimension);
		}

		public ExtractableResource node(int minOre, int maxOre, Function<TopOreExtractorBlock.ExtractorProgressBuilder, Integer> progress, Function<BlockBuilder<Block, CreateRegistrate>, BlockBuilder<Block, CreateRegistrate>> conf, int drillDamage) {
			NODE = conf.apply(reg.block(name + "_node", Block::new).recipe((ctx, prov) -> {
				EXTRACTING.add(name, e -> e.output(ORE_PIECE).node(ctx.get()).ore(minOre, maxOre).requiredProgress(progress.apply(new IOreExtractorBlock.ExtractorProgressBuilder())).drillDamage(drillDamage));
			})
					.blockstate((ctx, prov) -> prov.simpleBlock(ctx.get(), prov.models().cubeAll(ctx.getName(), prov.modLoc("block/nodes/" + name)))).tag(ModTags.Blocks.NODES, NODE_TAG, ModTags.References.NON_MOVABLE).loot((p, b) -> {
						p.registerDropping(b, Items.AIR);
					}).simpleItem()).register();
			return this;
		}

		public ExtractableResource node(int ore, Function<TopOreExtractorBlock.ExtractorProgressBuilder, Integer> progress, Function<BlockBuilder<Block, CreateRegistrate>, BlockBuilder<Block, CreateRegistrate>> conf, int drillDamage) {
			return node(ore, ore, progress, conf, drillDamage);
		}

		public ExtractableResource recipe(BiConsumer<RegistrateRecipeProvider, ExtractableResource> consumer) {
			recipeGen.add(consumer);
			return this;
		}
	}

	public static class IngotExtractableResource extends ExtractableResource {
		public final boolean autoCreateRecipes;

		public IngotExtractableResource(String name, CreateRegistrate reg, boolean autoCreateRecipes, @Nullable NonNullSupplier<Item> ingot, Function<ItemBuilder<Item, CreateRegistrate>, ItemBuilder<Item, CreateRegistrate>> orePieceConf, Function<ItemBuilder<Item, CreateRegistrate>, ItemBuilder<Item, CreateRegistrate>> ingotPieceConf) {
			super(name, reg, orePieceConf);
			this.autoCreateRecipes = autoCreateRecipes;

			if (autoCreateRecipes) {
				recipe((provider, $) -> {
					MIXING.add(name + "_ingot_from_pieces", b -> {
						for (int i = 0; i < 9; i++) {
							b.require(ORE_PIECE_TAG);
						}
						return b.output(ingot.get()).requiresHeat(HeatCondition.SUPERHEATED);
					});
				});
			}
		}
	}

	public static class GlueableExtractableResource extends ExtractableResource {
		public GlueableExtractableResource(String name, CreateRegistrate reg, boolean autoCreateRecipes, NonNullSupplier<Item> output, Function<ItemBuilder<Item, CreateRegistrate>, ItemBuilder<Item, CreateRegistrate>> orePieceConf) {
			super(name, reg, orePieceConf);

			if (autoCreateRecipes) {
				recipe((provider, $) -> MIXING.add(name + "_gluing", b -> {
					for (int i = 0; i < 8; i++) {
						b.require(ORE_PIECE_TAG);
					}
					return b.require(Items.SLIME_BALL).output(output.get()).requiresHeat(HeatCondition.SUPERHEATED);
				}));
			}
		}
	}

	public static class RecipeItem<T extends Item> {
		public final String name;
		public final CreateRegistrate reg;
		public ItemEntry<T> item;

		ItemBuilder<T, CreateRegistrate> builder;

		@Nullable
		public Tags.IOptionalNamedTag itemTag;
		@Nullable
		public Tags.IOptionalNamedTag generalTag;

		protected List<UnaryOperator<Item.Properties>> propertiesConfig = new ArrayList<>();

		RecipeItem(String name, CreateRegistrate reg, NonNullFunction<Item.Properties, T> factory) {
			this.name = name;
			this.reg = reg;

			builder = reg.item(name, factory);
		}

		public static RecipeItem<Item> createBasic(String name, CreateRegistrate reg) {
			return new RecipeItem<>(name, reg, Item::new);
		}

		RecipeItem<T> recipe(NonNullBiConsumer<DataGenContext<Item, T>, RegistrateRecipeProvider> cons) {
			builder.recipe(cons);
			return this;
		}

		RecipeItem<T> quickTag(Tags.IOptionalNamedTag<Item> tag, String s) {
//            itemTag = ModTags.Items.tag(tag.getId().getPath() + "/" + s);
			itemTag = ItemTags.createOptional(new ResourceLocation(tag.getId().getNamespace(), tag.getId().getPath() + "/" + s));
			builder.tag(tag, itemTag);
			generalTag = tag;
			return this;
		}

		RecipeItem<T> quickTag(String tag, String s) {
			generalTag = ModTags.Items.tag(tag);
			builder.tag(generalTag);
			return quickTag(generalTag, s);
		}

		RecipeItem<T> quickTag(String tag) {
			generalTag = ModTags.Items.tag(tag);
			builder.tag(generalTag);
			return this;
		}

		RecipeItem<T> configureBuilder(NonNullConsumer<ItemBuilder<T, CreateRegistrate>> consumer) {
			consumer.accept(builder);
			return this;
		}

		RecipeItem<T> nonStackable() {
			return configureProperties(p -> p.maxStackSize(1));
		}

		RecipeItem<T> configureProperties(UnaryOperator<Item.Properties> f) {
			propertiesConfig.add(f);
			return this;
		}

		RecipeItem<T> noModel() {
			CreateAutomated.LOGGER.warn("Recipe item " + name + " has no model");
			return configureBuilder(b -> b.model(($, $$) -> {}));
		}

		RecipeItem<T> register() {
			item = builder.properties((p) -> {
				for (UnaryOperator<Item.Properties> c : propertiesConfig) {
					p = c.apply(p);
				}
				return p;
			}).register();
			return this;
		}
	}

	public static ExtractableResource LAPIS_EXTRACTABLE;
	public static ExtractableResource IRON_EXTRACTABLE;
	public static ExtractableResource ZINC_EXTRACTABLE;
	public static ExtractableResource GOLD_EXTRACTABLE;
	public static ExtractableResource COPPER_EXTRACTABLE;
	public static ExtractableResource CINDER_FLOUR_EXTRACTABLE;

	//    public static ItemEntry<DrillHead> DRILL_HEAD;
	public static RecipeItem<DrillHeadItem> DRILL_HEAD;
	public static RecipeItem<Item> CRUSHED_PRISMARINE;
	public static RecipeItem<Item> DIAMOND_BIT;
	public static RecipeItem<Item> IRON_BIT;

	public static ItemGroup itemGroup = new ItemGroup(CreateAutomated.modid + "_resources") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(IRON_EXTRACTABLE.ORE_PIECE.get());
		}
	};

	public static void register(CreateRegistrate registrate) {
		registrate.itemGroup(() -> itemGroup, "Create Automated resources");
//		registrate.addRawLang(((TranslationTextComponent) itemGroup.getTranslationKey()).getKey() , "Create Automated resources");
//		CreateRegistrate registrate = CreateAutomated.registrate.get();
		LAPIS_EXTRACTABLE = new GlueableExtractableResource("lapis", registrate, true, () -> Items.LAPIS_LAZULI, c -> c)
				.node(1, 4, (b) -> b.atSpeedOf(128).takesSeconds(10).build(), c -> c, 1)
				.oreGen(10, 4, WorldGen.NodeDimension.OVERWORLD);

		IRON_EXTRACTABLE = new IngotExtractableResource("iron", registrate, true, () -> Items.IRON_INGOT, c -> c, c -> c)
				.node(0, 2, (b) -> b.atSpeedOf(128).takesSeconds(40).build(), c -> c, 5)
				.oreGen(4, 1, WorldGen.NodeDimension.OVERWORLD);

		ZINC_EXTRACTABLE = new IngotExtractableResource("zinc", registrate, true, AllItems.ZINC_INGOT, c -> c, c -> c)
				.node(1, 2, (b) -> b.atSpeedOf(128).takesSeconds(20).build(), c -> c, 3)
				.oreGen(9, 2, WorldGen.NodeDimension.OVERWORLD);

		GOLD_EXTRACTABLE = new IngotExtractableResource("gold", registrate, true, () -> Items.GOLD_INGOT, c -> c, c -> c)
				.node(0, 2, (b) -> b.atSpeedOf(128).takesSeconds(35).build(), c -> c, 4)
				.oreGen(6, 1, WorldGen.NodeDimension.OVERWORLD);

		COPPER_EXTRACTABLE = new IngotExtractableResource("copper", registrate, true, AllItems.COPPER_INGOT, c -> c, c -> c)
				.node(1, 4, (b) -> b.atSpeedOf(128).takesSeconds(10).build(), c -> c, 3)
				.oreGen(16, 2, WorldGen.NodeDimension.OVERWORLD);

		CINDER_FLOUR_EXTRACTABLE = new ExtractableResource("cinder_flour", registrate, c -> c.lang("Cinder Dust"))
				.node(1, 3, b -> b.atSpeedOf(128).takesSeconds(5).build(), c -> c, 2)
				.oreGen(16, 0, 256, 10, WorldGen.NodeDimension.NETHER)
				.recipe((prov, r) -> {
					MIXING.add("cinder_flour_from_ore_pieces", b -> {
						for (int i = 0; i < 9; i++) {
							b.require(r.ORE_PIECE_TAG);
						}
						b.require(Fluids.WATER, 10);
						return b.requiresHeat(HeatCondition.NONE).output(AllItems.CINDER_FLOUR.get());
					});
					MIXING.add("netherrack_from_cinder_flour", b -> {
						for (int i = 0; i < 5; i++) {
							b.require(AllItems.CINDER_FLOUR.get());
						}
						b.require(Fluids.WATER, 100);
						return b.requiresHeat(HeatCondition.NONE).output(Blocks.NETHERRACK);
					});
				});

//        DRILL_HEAD = registrate.item("drill_head", DrillHead::new)
//                .tag(ModTags.Items.DRILL_HEADS)
//                .properties(p -> p.maxStackSize(1))
//                .recipe((ctx, prov) -> ShapedRecipeBuilder.shapedRecipe(ctx.get())
//                        .patternLine("bbb")
//                        .patternLine("rbr")
//                        .patternLine(" r ")
//                        .key('b', Blocks.IRON_BLOCK)
//                        .key('r', Items.IRON_INGOT)
//                        .addCriterion("has_extractor", RegistrateRecipeProvider.hasItem(ModBlocks.ORE_EXTRACTOR_BOTTOM.get()))
//                        .build(prov))
//                .register();

		DRILL_HEAD = new RecipeItem<>("drill_head", registrate, DrillHeadItem::new)
				.quickTag(ModTags.Items.DRILL_HEADS, "iron")
				.nonStackable()
				.recipe((ctx, prov) -> ShapedRecipeBuilder.shapedRecipe(ctx.get())
						.patternLine("bbb")
						.patternLine("rbr")
						.patternLine(" r ")
						.key('b', Blocks.IRON_BLOCK)
						.key('r', Items.IRON_INGOT)
						.addCriterion("has_extractor", RegistrateRecipeProvider.hasItem(ModBlocks.ORE_EXTRACTOR_BOTTOM.get()))
						.build(prov))
				.register();

		CRUSHED_PRISMARINE = RecipeItem.createBasic("crushed_prismarine", registrate)
				.quickTag("crushed_prismarine", "vanilla")
				.recipe((ctx, prov) -> CRUSHING.add("crushed_prismarine", b -> b.duration(150).require(Tags.Items.DUSTS_PRISMARINE).output(.3f, ctx.get(), 1).output(.1f, ctx.get(), 2)))
				.register();

		DIAMOND_BIT = RecipeItem.createBasic("diamond_bit", registrate)
				.quickTag("bits", "diamond")
				.recipe((ctx, prov) -> {
					PICKING.add("diamond_bit", b -> b.require(CRUSHED_PRISMARINE.generalTag).output(.2f, ctx.get()));
					MIXING.add("diamond", b -> b.require(DIAMOND_BIT.itemTag).require(ModFluids.MOLTEN_DIAMOND.get(), 800).output(Items.DIAMOND));
					CRUSHING.add("diamond_bit", b -> b.require(Tags.Items.GEMS_DIAMOND).output(ctx.get(), 3));
				})
				.register();

		IRON_BIT = RecipeItem.createBasic("iron_bit", registrate)
				.quickTag("bits", "iron")
				.recipe((ctx, prov) -> CRUSHING.add("iron_bit", b -> b.require(Items.IRON_NUGGET).output(ctx.get()).output(.3f, ctx.get(), 2)))
				.noModel()
				.register();

		modEventBus.addListener(RecipeItems::gatherData);
	}

	public static ModMixingRecipes MIXING;
	public static ModCrushingRecipes CRUSHING;
	public static ExtractingRecipeGen EXTRACTING;
	public static PickingRecipeGen PICKING;
	public static ModDeployingRecipes DEPLOYING;

	public static void gatherData(GatherDataEvent event) {
		DataGenerator gen = event.getGenerator();
		MIXING = new ModMixingRecipes(gen);
		EXTRACTING = new ExtractingRecipeGen(gen);
		CRUSHING = new ModCrushingRecipes(gen);
		DEPLOYING = new ModDeployingRecipes(gen);
		PICKING = new PickingRecipeGen(gen);
		gen.addProvider(MIXING);
		gen.addProvider(CRUSHING);
		gen.addProvider(EXTRACTING);
		gen.addProvider(PICKING);
		gen.addProvider(DEPLOYING);
	}
}
