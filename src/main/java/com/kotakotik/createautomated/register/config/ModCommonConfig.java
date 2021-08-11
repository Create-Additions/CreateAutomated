package com.kotakotik.createautomated.register.config;


import com.kotakotik.createautomated.api.NodeInfo;
import com.simibubi.create.foundation.worldgen.ConfigDrivenFeatureEntry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ModCommonConfig extends ModConfig.Config {
	public static class WorldGen extends ModConfig.Config {
		public ConfigBool enabled = b(true, "enabled", "Whether or not CreateAutomated worldgen is enabled");

		@Override
		protected void registerAll(ForgeConfigSpec.Builder builder) {
			super.registerAll(builder);
			for (ConfigDrivenFeatureEntry entry : com.kotakotik.createautomated.content.worldgen.WorldGen.entries.values()) {
				builder.push(entry.id);
				entry.addToConfig(builder);
				builder.pop();
			}
		}
	}

	public static class Extractor extends ModConfig.Config {
		public static class Nodes extends ModConfig.Config {
			public static class Node extends ModConfig.Config implements NodeInfo.Entry {

				public final ResourceLocation id;

				public Node(ResourceLocation id, boolean defaultInfinite, int defaultCount, boolean defaultRandomizeDamage) {
					depth = 2;
					this.id = id;
					isInfinite = b(defaultInfinite, "isInfinite", "Whether or not this node is infinite");
					count = i(defaultCount, 1, "count", "How many ore pieces can be extracted. Ignored if is infinite");
					randomizeDamage = b(defaultInfinite, "randomizeDamage", "Whether or not to randomize the amount of ore to remove each time it is extracted");
				}

				@Override
				public String getName() {
					// converts snake case to camel case
					return StringUtils.uncapitalize(
							Arrays.stream(id.getPath().split("_"))
									.map(StringUtils::capitalize).collect(Collectors.joining()));
				}

				public ConfigBool isInfinite;
				public ConfigInt count;
				public ConfigBool randomizeDamage;

				@Override
				public boolean isInfinite() {
					return isInfinite.get();
				}

				@Override
				public int getCount() {
					return count.get();
				}

				@Override
				public boolean randomizeDamage() {
					return randomizeDamage.get();
				}
			}

			protected static List<Supplier<Node>> toReg = new ArrayList<>();

			public static void reg(Supplier<Node> constructor) {
				toReg.add(constructor);
			}

			@Override
			protected void registerAll(ForgeConfigSpec.Builder builder) {
				super.registerAll(builder);
				for (Supplier<Node> constructor : toReg) {
					registerNow(constructor, builder);
				}
//				super.registerAll(builder);
			}

			public Node registerNow(Supplier<Node> constructor, ForgeConfigSpec.Builder builder) {
				Node node = constructor.get();
				builder.push(node.getName());
				node.registerAll(builder);
				builder.pop();
//				Node node = nested(2, constructor);
				all.put(node.id, node);
				NodeInfo.info.put(node.id, node);
				return node;
			}

			public static final HashMap<ResourceLocation, Node> all = new HashMap<>();
		}

		public Nodes nodes = nested(1, Nodes::new);
	}

	public WorldGen worldGen = nested(0, WorldGen::new);
	public Extractor extractor = nested(0, Extractor::new);
}
