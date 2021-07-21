package com.kotakotik.createautomated.register;

import com.kotakotik.createautomated.CreateAutomated;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.utility.Pair;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static com.simibubi.create.repack.registrate.providers.RegistrateLangProvider.toEnglishName;

public class ModTooltips {
	public static CreateRegistrate reg;
	public final String key;
	public final String content;

	public static void register(CreateRegistrate registrate) {
		reg = registrate;

		// stealing from create's lang lol
		register(ModItems.PICKER.get(),
				"A string that can be used to _pick tiny bits from materials_. Can be automatically applied using the Deployer.",
				"When used",
				"Picks from items held in the _offhand_ or lying on the _floor_ when _looking at them_");

		registerSpongeSail(ModBlocks.WET_SPONGE_SAIL.get(), "wash", "washing");
		registerSpongeSail(ModBlocks.LAVA_SPONGE_SAIL.get(), "blast", "blasting");
		register(ModBlocks.SPONGE_SAIL.get(), "A boring dry sponge. Try _splashing some water on it_!");

		register(RecipeItems.DRILL_HEAD.item.get(),
				"The most important component for _extracting_");

		onRegister.forEach(c -> c.accept(reg));
	}

	private static List<ModTooltips> registerSpongeSail(Block item, String type, String type_ing) {
		return register(item,
				"A sail that can be used to _" + type + " without a fluid_",
				"When placed in front of a fan",
				"Converts the _flow_ to _" + type_ing + "_");
	}

	// here comes the boilerplate
	protected static String getTooltipKeyStart(String id, boolean isItem) {
		return (isItem ? "item." : "block.") + CreateAutomated.MODID + "." + id + ".tooltip";
	}

	protected static String getTooltipKeyStart(Item item) {
		return getTooltipKeyStart(Objects.requireNonNull(item.getRegistryName()).getPath(), true);
	}

	protected static String getTooltipKeyStart(Block block) {
		return getTooltipKeyStart(Objects.requireNonNull(block.getRegistryName()).getPath(), false);
	}

	protected static ModTooltips registerTooltip(Item item, String suffix, String content) {
		return new ModTooltips(getTooltipKeyStart(item) + "." + suffix, content);
	}

	protected static ModTooltips registerTooltip(Block block, String suffix, String content) {
		return new ModTooltips(getTooltipKeyStart(block) + "." + suffix, content);
	}

	public TranslationTextComponent getComponent() {
		return new TranslationTextComponent(key);
	}

	public ModTooltips(String key, String content, boolean genLang) {
		if (genLang) {
			reg.addRawLang(key, content);
		}
		this.key = key;
		this.content = content;
	}

	public ModTooltips(String key, String content) {
		this(key, content, true);
	}

	public static ModTooltips registerMain(Item item, String name) {
		return new ModTooltips(getTooltipKeyStart(item), name.toUpperCase());
	}

	public static ModTooltips registerMain(Item item) {
		return registerMain(item, toEnglishName(item.getRegistryName().getPath()));
	}

	public static ModTooltips registerMain(Block item, String name) {
		return new ModTooltips(getTooltipKeyStart(item), name.toUpperCase());
	}

	public static ModTooltips registerMain(Block item) {
		return registerMain(item, toEnglishName(item.getRegistryName().getPath()));
	}

	public static ModTooltips registerSummary(Item item, String content) {
		return registerTooltip(item, "summary", content);
	}

	public static ModTooltips registerSummary(Block item, String content) {
		return registerTooltip(item, "summary", content);
	}

	protected static HashMap<ResourceLocation, Integer> behaviors = new HashMap<>();
	protected static HashMap<ResourceLocation, Integer> conditions = new HashMap<>();

	public static ModTooltips registerBehavior(Item item, String content) {
		int b = behaviors.getOrDefault(item.getRegistryName(), 0) + 1;
		behaviors.put(item.getRegistryName(), b);
		return registerTooltip(item, "behaviour" + b, content);
	}

	public static ModTooltips registerCondition(Item item, String content) {
		int b = conditions.getOrDefault(item.getRegistryName(), 0) + 1;
		conditions.put(item.getRegistryName(), b);
		return registerTooltip(item, "condition" + b, content);
	}

	public static ModTooltips registerBehavior(Block item, String content) {
		int b = behaviors.getOrDefault(item.getRegistryName(), 0) + 1;
		behaviors.put(item.getRegistryName(), b);
		return registerTooltip(item, "behaviour" + b, content);
	}

	public static ModTooltips registerCondition(Block item, String content) {
		int b = conditions.getOrDefault(item.getRegistryName(), 0) + 1;
		conditions.put(item.getRegistryName(), b);
		return registerTooltip(item, "condition" + b, content);
	}


	@SafeVarargs
	public static List<ModTooltips> register(Item item, String summary, Pair<String, String>... behaviors) {
		ArrayList<ModTooltips> list = new ArrayList<>();
		list.add(registerMain(item));
		list.add(registerSummary(item, summary));
		for (Pair<String, String> b : behaviors) {
			list.add(registerCondition(item, b.getFirst()));
			list.add(registerBehavior(item, b.getSecond()));
		}
		return list;
	}

	public static List<ModTooltips> register(Item item, String summary, String condition, String behavior) {
		return register(item, summary, Pair.of(condition, behavior));
	}

	@SafeVarargs
	public static List<ModTooltips> register(Block item, String summary, Pair<String, String>... behaviors) {
		ArrayList<ModTooltips> list = new ArrayList<>();
		list.add(registerMain(item));
		list.add(registerSummary(item, summary));
		for (Pair<String, String> b : behaviors) {
			list.add(registerCondition(item, b.getFirst()));
			list.add(registerBehavior(item, b.getSecond()));
		}
		return list;
	}

	public static List<ModTooltips> register(Block item, String summary, String condition, String behavior) {
		return register(item, summary, Pair.of(condition, behavior));
	}

	protected static ArrayList<Consumer<CreateRegistrate>> onRegister = new ArrayList<>();

	public static void onRegister(Consumer<CreateRegistrate> consumer) {
		onRegister.add(consumer);
	}
}
