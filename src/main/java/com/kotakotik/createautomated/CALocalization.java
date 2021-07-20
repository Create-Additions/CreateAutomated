package com.kotakotik.createautomated;

import com.simibubi.create.foundation.data.CreateRegistrate;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;

public enum CALocalization {
	JEI_ORE_EXTRACTOR_BETWEEN("jei.ore_extractor", "between", "From %d to %d"),
	JEI_ORE_EXTRACTOR_TITLE("jei.ore_extractor", "title", "Ore Extraction"),
	JEI_ORE_EXTRACTOR_DRILL_DAMAGE("jei.ore_extractor", "drill_damage", "Drill damage: %d"),
	JEI_ORE_EXTRACTOR_TIME("jei.ore_extractor", "time", "%f seconds at 128 RPM"),
	JEI_PICKER_TITLE("jei.picker", "title", "Picking"),

	CONFIG_TITLE("config", "title", "Create: Automated");

	public final String key;
	public final String english;

	static void register(CreateRegistrate reg) {
		for (CALocalization loc : values()) {
			reg.addRawLang(loc.key, loc.english);
		}
	}

	CALocalization(String key, String english) {
		this.key = key;
		this.english = english;
	}

	CALocalization(String type, String id, String suffix, String localizedName) {
		this(Util.makeDescriptionId(type, CreateAutomated.asResource(id)) + "." + suffix, localizedName);
	}

	CALocalization(String type, String id, String localizedName) {
		this(Util.makeDescriptionId(type, CreateAutomated.asResource(id)), localizedName);
	}

	public TranslationTextComponent getComponent(Object... args) {
		return new TranslationTextComponent(key, args);
	}

	public String translate(Object... args) {
		return getComponent(args).getString();
	}
}
