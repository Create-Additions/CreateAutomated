package com.kotakotik.createautomated.content.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.kotakotik.createautomated.register.ModConditions;
import com.simibubi.create.foundation.config.ConfigBase;
import net.minecraft.loot.ILootSerializer;
import net.minecraft.loot.LootConditionType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;

public class ConfigEnabledCondition implements ILootCondition, ILootCondition.IBuilder {
	public final ConfigBase.ConfigBool config;
	public final Serializer serializer = new Serializer();

	public ConfigEnabledCondition(ConfigBase.ConfigBool config) {
		this.config = config;
	}

	@Override
	public LootConditionType getType() {
		return ModConditions.CONFIG_NODES_DROP;
	}

	@Override
	public boolean test(LootContext lootContext) {
		return config.get();
	}

	@Override
	public ILootCondition build() {
		return this; // wtf
	}

	public class Serializer implements ILootSerializer<ConfigEnabledCondition> {
		public void serialize(JsonObject json, ConfigEnabledCondition condition, JsonSerializationContext ctx) {
		}

		public ConfigEnabledCondition deserialize(JsonObject p_230423_1_, JsonDeserializationContext ctx) {
			return get();
		}

		public ConfigEnabledCondition get() {
			return ConfigEnabledCondition.this;
		}
	}
}
