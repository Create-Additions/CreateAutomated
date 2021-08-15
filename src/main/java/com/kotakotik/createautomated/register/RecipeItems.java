package com.kotakotik.createautomated.register;

import com.kotakotik.createautomated.CreateAutomated;
import com.kotakotik.createautomated.content.base.IOreExtractorBlock;
import com.kotakotik.createautomated.content.conditions.ConfigEnabledCondition;
import com.kotakotik.createautomated.content.processing.oreExtractor.TopOreExtractorBlock;
import com.kotakotik.createautomated.content.processing.oreExtractor.recipe.ExtractingRecipeGen;
import com.kotakotik.createautomated.content.processing.picker.recipe.PickingRecipeGen;
import com.kotakotik.createautomated.content.simple.drillHead.DrillHeadItem;
import com.kotakotik.createautomated.content.simple.node.NodeBlock;
import com.kotakotik.createautomated.content.worldgen.DimensionalConfigDrivenFeatureEntry;
import com.kotakotik.createautomated.content.worldgen.WorldGen;
import com.kotakotik.createautomated.register.config.ModServerConfig;
import com.kotakotik.createautomated.register.recipes.ModCrushingRecipes;
import com.kotakotik.createautomated.register.recipes.ModDeployingRecipes;
import com.kotakotik.createautomated.register.recipes.ModMixingRecipes;
import com.kotakotik.createautomated.register.recipes.ModSplashingRecipes;
import com.simibubi.create.AllItems;
import com.simibubi.create.content.AllSections;
import com.simibubi.create.content.contraptions.processing.HeatCondition;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.worldgen.ConfigDrivenFeatureEntry;
import com.simibubi.create.repack.registrate.builders.BlockBuilder;
import com.simibubi.create.repack.registrate.builders.FluidBuilder;
import com.simibubi.create.repack.registrate.builders.ItemBuilder;
import com.simibubi.create.repack.registrate.providers.DataGenContext;
import com.simibubi.create.repack.registrate.providers.RegistrateLangProvider;
import com.simibubi.create.repack.registrate.providers.RegistrateRecipeProvider;
import com.simibubi.create.repack.registrate.util.entry.BlockEntry;
import com.simibubi.create.repack.registrate.util.entry.FluidEntry;
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
import net.minecraft.item.*;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.loot.ConstantRange;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.conditions.SurvivesExplosion;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.*;

import static com.kotakotik.createautomated.CreateAutomated.modEventBus;

public class RecipeItems extends ModFluids {
	public static class ExtractableResource {
		public final String name;
		public CreateRegistrate reg;

		public BlockEntry<NodeBlock> NODE;
		public ItemEntry<Item> ORE_PIECE;

		public final Tags.IOptionalNamedTag<Block> NODE_TAG;
		public final Tags.IOptionalNamedTag<Item> ORE_PIECE_TAG;

		List<BiConsumer<RegistrateRecipeProvider, ExtractableResource>> recipeGen = new ArrayList<>();
		public Function<BlockBuilder<Block, CreateRegistrate>, BlockBuilder<Block, CreateRegistrate>> nodeConf = c -> c;

		public ExtractableResource(String name, CreateRegistrate reg, Function<ItemBuilder<Item, CreateRegistrate>, ItemBuilder<Item, CreateRegistrate>> orePieceConf) {
			EXTRACTABLES.add(this);

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

		public ExtractableResource oreGen(int veinSize, int minHeight, int maxHeight, int frequency, boolean nether) {
			ConfigDrivenFeatureEntry entry;
			if (nether) {
				entry = DimensionalConfigDrivenFeatureEntry.nether(name + "_node", NODE, veinSize, frequency);
			} else {
				entry = DimensionalConfigDrivenFeatureEntry.overworld(name + "_node", NODE, veinSize, frequency);
			}
			WorldGen.register(entry.between(minHeight, maxHeight));
			return this;
		}

		public ExtractableResource oreGen(int veinSize, int frequency, boolean nether) {
			return oreGen(veinSize, 40, 256, frequency, nether);
		}

		public ExtractableResource node(int minOre, int maxOre, Function<TopOreExtractorBlock.ExtractorProgressBuilder, Integer> progress, Function<BlockBuilder<NodeBlock, CreateRegistrate>, BlockBuilder<NodeBlock, CreateRegistrate>> conf, int drillDamage, boolean tooltip, int count) {
			String nodeName = name + "_node";
			NODE = conf.apply(reg.block(nodeName, NodeBlock::new).properties(p -> p.strength(0.5F)).recipe((ctx, prov) -> {
				EXTRACTING.add(name, e -> e.output(ORE_PIECE).node(ctx.get()).ore(minOre, maxOre).requiredProgress(progress.apply(new IOreExtractorBlock.ExtractorProgressBuilder())).drillDamage(drillDamage));
			})
					.blockstate((ctx, prov) -> prov.simpleBlock(ctx.get(), prov.models().cubeAll(ctx.getName(), prov.modLoc("block/nodes/" + name)))).tag(ModTags.Blocks.NODES, NODE_TAG).loot((p, b) ->
							p.add(b, LootTable.lootTable().withPool(LootPool.lootPool().when(((ConfigEnabledCondition.Serializer) ModConditions.CONFIG_NODES_DROP.getSerializer()).get()).when(SurvivesExplosion.survivesExplosion()).setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(b))))).simpleItem()).register();
			if (tooltip) {
				ModTooltips.onRegister(reg -> {
					ModTooltips.register(NODE.get(), "Some dirt with ore in it, but _how much?_",
							"When mined by an extractor",
							(minOre == maxOre) ? "Gives _" + minOre + " " + getOrePieceName() + "_" : "Gives _" + minOre + "_ to _" + maxOre + " " + getOrePieceName() + "_");
				});
			}
			if (count > 0) {
				ModServerConfig.Extractor.Nodes.reg(() -> new ModServerConfig.Extractor.Nodes.Node(new ResourceLocation(CreateAutomated.MODID, nodeName), true, count, false));
			}
			return this;
		}

		public String getOrePieceName() {
			return RegistrateLangProvider.toEnglishName(ORE_PIECE.getId().getPath());
		}

		public ExtractableResource node(int minOre, int maxOre, Function<TopOreExtractorBlock.ExtractorProgressBuilder, Integer> progress, Function<BlockBuilder<NodeBlock, CreateRegistrate>, BlockBuilder<NodeBlock, CreateRegistrate>> conf, int drillDamage, int count) {
			return node(minOre, maxOre, progress, conf, drillDamage, false, count);
		}

		public ExtractableResource node(int ore, Function<TopOreExtractorBlock.ExtractorProgressBuilder, Integer> progress, Function<BlockBuilder<NodeBlock, CreateRegistrate>, BlockBuilder<NodeBlock, CreateRegistrate>> conf, int drillDamage, int count) {
			return node(ore, ore, progress, conf, drillDamage, count);
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
			itemTag = ItemTags.createOptional(new ResourceLocation(tag.getName().getNamespace(), tag.getName().getPath() + "/" + s));
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
			return configureProperties(p -> p.stacksTo(1));
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

	public static class RenewableGem {
		public final String name;
		protected final CreateRegistrate registrate;
		protected Supplier<Ingredient> start;
		public Supplier<Item> gem;

		protected String bitName() {
			return name + "_bit";
		}

		protected String fluidName() {
			return "molten_" + name;
		}

		public RenewableGem(String name,
							CreateRegistrate registrate,
							Supplier<Item> gem,
							Supplier<Ingredient> start,
							float pickingChance) {
			this.name = name;
			this.gem = gem;
			this.registrate = registrate;
			this.start = start;
			// CRUSHED_PRISMARINE.generalTag
			BIT = RecipeItem.createBasic(bitName(), registrate)
					.quickTag("bits", name)
					.recipe((ctx, prov) -> {
						PICKING.add(bitName(), b -> b.require(start.get()).output(pickingChance, ctx.get()));
						MIXING.add(name + "_from_bits", b -> b.require(BIT.itemTag).require(FLUID.get(), 800).output(this.gem.get()));
						CRUSHING.add(bitName(), b -> b.require(gem.get()).output(ctx.get()));
					})
					.register();
			FLUID_BUILDER = registrate.fluid(fluidName(), still(fluidName()), flow(fluidName()), NoColorFluidAttributes::new)
					.attributes(b -> b.viscosity(500)
							.density(1400))
					.properties(p -> p.levelDecreasePerBlock(2)
							.tickRate(25)
							.slopeFindDistance(3)
							.explosionResistance(100f))
					.removeTag(FluidTags.WATER)
					.tag(FluidTags.LAVA);
		}

		public RenewableGem bit(Consumer<RecipeItem<Item>> cons) {
			cons.accept(BIT);
			return this;
		}

		public RenewableGem fluid(Consumer<FluidBuilder<ForgeFlowingFluid.Flowing, CreateRegistrate>> cons) {
			cons.accept(FLUID_BUILDER);
			return this;
		}

		public RenewableGem bucket(Consumer<ItemBuilder<BucketItem, FluidBuilder<ForgeFlowingFluid.Flowing, CreateRegistrate>>> cons) {
			bucketTransform = bucketTransform.andThen(cons);
			return this;
		}

		public RenewableGem register() {
			BIT.register();
			ItemBuilder<BucketItem, FluidBuilder<ForgeFlowingFluid.Flowing, CreateRegistrate>> bucket = FLUID_BUILDER.bucket();
			bucketTransform.accept(bucket);
			bucket.build();
			FLUID = FLUID_BUILDER.register();
			return this;
		}

		public RenewableGem noModel() {
			CreateAutomated.LOGGER.warn("Renewable " + name + " using no models");
			return fluid(b -> b.block().blockstate(($, $$) -> {
			}).build()).bit(RecipeItem::noModel).bucket(b -> b.model(($, $$) -> {
			}));
		}

		protected FluidBuilder<ForgeFlowingFluid.Flowing, CreateRegistrate> FLUID_BUILDER;
		public FluidEntry<ForgeFlowingFluid.Flowing> FLUID;
		public RecipeItem<Item> BIT;

		public Consumer<ItemBuilder<BucketItem, FluidBuilder<ForgeFlowingFluid.Flowing, CreateRegistrate>>> bucketTransform = b2 -> b2.recipe((ctx, prov) -> {
			RecipeItems.MIXING.add(fluidName(), b -> {
				for (int i = 0; i < 3; i++) {
					b.require(BIT.itemTag);
				}
				return b.requiresHeat(HeatCondition.SUPERHEATED).output(FLUID.get(), 150);
			});
			RecipeItems.MIXING.add(fluidName() + "_from_ingot", b -> b.require(gem.get()).requiresHeat(HeatCondition.SUPERHEATED).output(FLUID.get(), 1000));
		})
				.properties(p -> p.stacksTo(1));
	}

	public static List<ExtractableResource> EXTRACTABLES = new ArrayList<>();

	public static ExtractableResource LAPIS_EXTRACTABLE;
	public static ExtractableResource IRON_EXTRACTABLE;
	public static ExtractableResource ZINC_EXTRACTABLE;
	public static ExtractableResource GOLD_EXTRACTABLE;
	public static ExtractableResource COPPER_EXTRACTABLE;
	public static ExtractableResource CINDER_FLOUR_EXTRACTABLE;

	//    public static ItemEntry<DrillHead> DRILL_HEAD;
	public static RecipeItem<DrillHeadItem> DRILL_HEAD;
	public static RecipeItem<Item> CRUSHED_PRISMARINE;
	public static RenewableGem DIAMOND_RENEWABLE;
	public static RenewableGem EMERALD_RENEWABLE;

	public static ItemGroup itemGroup = new ItemGroup(CreateAutomated.MODID + "_resources") {
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(IRON_EXTRACTABLE.ORE_PIECE.get());
		}
	};

	public static void register(CreateRegistrate registrate) {
		registrate.itemGroup(() -> itemGroup, "Create Automated resources");
		registrate.startSection(AllSections.MATERIALS);
//		registrate.addRawLang(((TranslationTextComponent) itemGroup.getTranslationKey()).getKey() , "Create Automated resources");
//		CreateRegistrate registrate = CreateAutomated.registrate.get();
		// TODO: theese are test counts, fix them!
		LAPIS_EXTRACTABLE = new GlueableExtractableResource("lapis", registrate, true, () -> Items.LAPIS_LAZULI, c -> c)
				.node(1, 4, (b) -> b.atSpeedOf(128).takesSeconds(10).build(), c -> c, 1, 5)
				.oreGen(10, 4, false);

		IRON_EXTRACTABLE = new IngotExtractableResource("iron", registrate, true, () -> Items.IRON_INGOT, c -> c, c -> c)
				.node(1, 2, (b) -> b.atSpeedOf(128).takesSeconds(40).build(), c -> c, 3, 4)
				.oreGen(4, 1, false);

		ZINC_EXTRACTABLE = new IngotExtractableResource("zinc", registrate, true, AllItems.ZINC_INGOT, c -> c, c -> c)
				.node(1, 2, (b) -> b.atSpeedOf(128).takesSeconds(20).build(), c -> c, 3, 3)
				.oreGen(9, 2, false);

		GOLD_EXTRACTABLE = new IngotExtractableResource("gold", registrate, true, () -> Items.GOLD_INGOT, c -> c, c -> c)
				.node(0, 2, (b) -> b.atSpeedOf(128).takesSeconds(35).build(), c -> c, 4, 2)
				.oreGen(6, 1, false);

		COPPER_EXTRACTABLE = new IngotExtractableResource("copper", registrate, true, AllItems.COPPER_INGOT, c -> c, c -> c)
				.node(1, 4, (b) -> b.atSpeedOf(128).takesSeconds(10).build(), c -> c, 3, 1)
				.oreGen(16, 2, false);

		CINDER_FLOUR_EXTRACTABLE = new ExtractableResource("cinder_flour", registrate, c -> c.lang("Cinder Dust"))
				.node(1, 3, b -> b.atSpeedOf(128).takesSeconds(5).build(), c -> c, 2, 6)
				.oreGen(16, 0, 256, 10, true)
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
				.recipe((ctx, prov) -> ShapedRecipeBuilder.shaped(ctx.get())
						.pattern("bbb")
						.pattern("rbr")
						.pattern(" r ")
						.define('b', Blocks.IRON_BLOCK)
						.define('r', Items.IRON_INGOT)
						.unlockedBy("has_extractor", RegistrateRecipeProvider.hasItem(ModBlocks.ORE_EXTRACTOR_BOTTOM.get()))
						.save(prov))
				.register();

		CRUSHED_PRISMARINE = RecipeItem.createBasic("crushed_prismarine", registrate)
				.quickTag("crushed_prismarine", "vanilla")
				.recipe((ctx, prov) -> CRUSHING.add("crushed_prismarine", b -> b.duration(150).require(Tags.Items.DUSTS_PRISMARINE).output(.3f, ctx.get(), 1).output(.1f, ctx.get(), 2)))
				.register();

		DIAMOND_RENEWABLE = new RenewableGem("diamond", registrate, () -> Items.DIAMOND, () -> Ingredient.of(CRUSHED_PRISMARINE.generalTag), .2f)
				.register();

		EMERALD_RENEWABLE = new RenewableGem("emerald", registrate, () -> Items.EMERALD, () -> Ingredient.of(Items.ENDER_PEARL), .4f)
				.register();

		modEventBus.addListener(RecipeItems::gatherData);
	}

	public static ModMixingRecipes MIXING;
	public static ModCrushingRecipes CRUSHING;
	public static ExtractingRecipeGen EXTRACTING;
	public static PickingRecipeGen PICKING;
	public static ModDeployingRecipes DEPLOYING;
	public static ModSplashingRecipes SPLASHING;

	public static void gatherData(GatherDataEvent event) {
		DataGenerator gen = event.getGenerator();
		MIXING = new ModMixingRecipes(gen);
		EXTRACTING = new ExtractingRecipeGen(gen);
		CRUSHING = new ModCrushingRecipes(gen);
		DEPLOYING = new ModDeployingRecipes(gen);
		PICKING = new PickingRecipeGen(gen);
		SPLASHING = new ModSplashingRecipes(gen);
		gen.addProvider(MIXING);
		gen.addProvider(CRUSHING);
		gen.addProvider(EXTRACTING);
		gen.addProvider(PICKING);
		gen.addProvider(DEPLOYING);
		gen.addProvider(SPLASHING);
	}
}
