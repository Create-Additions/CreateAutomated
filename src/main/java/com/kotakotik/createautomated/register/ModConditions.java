package com.kotakotik.createautomated.register;

import com.kotakotik.createautomated.CreateAutomated;
import com.kotakotik.createautomated.content.conditions.ConfigEnabledCondition;
import com.kotakotik.createautomated.register.config.ModConfig;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.registry.Registry;

public class ModConditions {
	public static LootConditionType CONFIG_NODES_DROP;

	public static void register() {
		CONFIG_NODES_DROP = register("config_nodes_drop", new ConfigEnabledCondition(ModConfig.SERVER.machines.extractor.nodesDrop).serializer);
	}

	protected static LootConditionType register(String id, ILootSerializer<? extends ILootCondition> serializer) {
		return Registry.register(Registry.LOOT_CONDITION_TYPE, CreateAutomated.asResource(id), new LootConditionType(serializer));
	}
}
