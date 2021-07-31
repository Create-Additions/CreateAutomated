package com.kotakotik.createautomated.register.config;

import com.kotakotik.createautomated.CreateAutomated;
import com.simibubi.create.content.contraptions.base.IRotate;
import com.simibubi.create.foundation.block.BlockStressDefaults;
import com.simibubi.create.foundation.block.BlockStressValues;
import com.simibubi.create.foundation.config.CKinetics;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashMap;
import java.util.Map;

public class ModServerConfig extends com.kotakotik.createautomated.register.config.ModConfig.Config {
	protected static class Comments {
		public static final String inventoryAbility = "Hoppers and arms allows mechanical arms and any block of the hopper sort (funnel, chute, etc), hoppers only allows only blocks of the hopper sort, none allows none";
	}

	public static class Extractor extends com.kotakotik.createautomated.register.config.ModConfig.Config {
		//		public ConfigBool allowArmDrillInsertion = b(true, "allowArmDrillInsertion", "Whether or not mechanical arms can insert drills into ore extractors");
//		public ConfigBool allowArmOutputExtraction = b(false, "allowArmOutputExtraction", "Whether or not mechanical arms can extract ore pieces from ore extractors");
//		public ConfigBool allowDrillInsertion = b(true, "allowDrillInsertion", "Whether or not things like hoppers and funnels can insert drills into ore extractors", "Note: This will also disable the ability to insert drills using mechanical arms");
//		public ConfigBool allowOutputExtraction = b(true, "allowOutputExtraction", "Whether or not things like hoppers and funnels can extract ore pieces from ore extractors", "Note: This will also disable the ability to extract items using mechanical arms");
		public ConfigEnum<InventoryAbility> extractionAbility = e(InventoryAbility.HOPPERS_ONLY, "extractionAbility", "How ore pieces can be extracted", Comments.inventoryAbility);
		public ConfigEnum<InventoryAbility> insertionAbility = e(InventoryAbility.HOPPERS_AND_ARMS, "insertionAbility", "How drills can be inserted", Comments.inventoryAbility, "Deployers will always be able to insert drills no matter what :)");
		public ConfigBool allowTogglingWithRedstone = b(true, "allowTogglingWithRedstone", "Whether or not the extractor can be toggled with redstone");
		public ConfigBool unbreakableDrills = b(false, "unbreakableDrills", "Makes drill heads not loose durability", "(why would you use this??)");
		public ConfigInt drillDurability = i(300, 0, "drillDurability", "The durability of the default drill head");
		public ConfigEnum<IRotate.SpeedLevel> requiredSpeed = e(IRotate.SpeedLevel.FAST, "requiredSpeed", "The speed requirement for the extractor. The exact number has to be configured in Create's config");
		public ConfigEnum<MiningAbility> miningAbility = e(MiningAbility.NONE, "miningAbility", "What types of blocks the extractor can mine. NONE if you want it to only be able to break blocks, ORES for ores only, and ANY for any block");
		public ConfigEnum<CKinetics.SpawnerMovementSetting> nodeMovement = e(CKinetics.SpawnerMovementSetting.UNMOVABLE, "nodeMovement", "Configure how node blocks can be moved by contraptions");
		public ConfigBool nodesDrop = b(false, "nodesDrop", "If enabled, nodes drop themselves, otherwise they drop nothing");

		public enum MiningAbility {
			NONE,
			ORES,
			ANY
		}

		public enum InventoryAbility {
			HOPPERS_AND_ARMS,
			HOPPERS_ONLY,
			NONE;

			public boolean canHopperInteract() {
				return canArmInteract() || this == HOPPERS_ONLY;
			}

			public boolean canArmInteract() {
				return this == HOPPERS_AND_ARMS;
			}
		}
	}

	public static class Picker extends com.kotakotik.createautomated.register.config.ModConfig.Config {
		public ConfigInt useTime = i(16, 0, "useTime", "[in ticks]", "How long it takes to pick");
		public ConfigInt durability = i(32, 0, "durability", "How many items you can pick before the picker breaks");
	}

	public static class Stress extends ModConfig.Config implements BlockStressValues.IStressValueProvider {
		protected Map<ResourceLocation, ForgeConfigSpec.ConfigValue<Double>> impacts = new HashMap();

		@Override
		protected void registerAll(ForgeConfigSpec.Builder builder) {
			builder.comment(new String[]{"", " [in Stress Units]", "Configure the individual stress impact of mechanical blocks. Note that this cost is doubled for every speed increase it receives"}).push("impact");
			BlockStressDefaults.DEFAULT_IMPACTS.forEach((r, i) -> {
				if (r.getNamespace().equals(CreateAutomated.MODID)) {
					impacts.put(r, builder.define(r.getPath(), i));
				}
			});
			builder.pop();
			BlockStressValues.registerProvider(CreateAutomated.MODID, this);
			super.registerAll(builder);
		}

		@Override
		public double getCapacity(Block block) {
			return 0;
		}

		@Override
		public boolean hasImpact(Block block) {
			return impacts.containsKey(block.getRegistryName());
		}

		@Override
		public double getImpact(Block block) {
			ResourceLocation key = block.getRegistryName();
			ForgeConfigSpec.ConfigValue<Double> value = impacts.get(key);
			return value != null ? value.get() : 0.0D;
		}

		@Override
		public boolean hasCapacity(Block block) {
			return false;
		}
	}

	public static class Machines extends com.kotakotik.createautomated.register.config.ModConfig.Config {
		public Picker picker = nested(1, Picker::new);
		public Extractor extractor = nested(1, Extractor::new);
		public Stress stress = nested(1, Stress::new);
	}

	public Machines machines = nested(0, Machines::new);
}
