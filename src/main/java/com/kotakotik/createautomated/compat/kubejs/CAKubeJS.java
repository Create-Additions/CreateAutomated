package com.kotakotik.createautomated.compat.kubejs;

import com.jozufozu.flywheel.core.PartialModel;
import com.kotakotik.createautomated.CreateAutomated;
import com.kotakotik.createautomated.compat.kubejs.item.drillHead.item.DrillHeadBuilderJS;
import com.kotakotik.createautomated.compat.kubejs.item.drillHead.item.DrillHeadItemJS;
import com.kotakotik.createautomated.compat.kubejs.item.drillHead.item.DrillHeadRegistryEventJS;
import com.kotakotik.createautomated.compat.kubejs.item.drillHead.partial.DrillPartialRegistryEventJS;
import com.kotakotik.createautomated.compat.kubejs.recipe.ExtractingJS;
import com.kotakotik.createautomated.compat.kubejs.recipe.PickingJS;
import dev.latvian.kubejs.script.ScriptType;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.util.LinkedHashMap;
import java.util.Map;

import static dev.latvian.kubejs.recipe.RegisterRecipeHandlersEvent.EVENT;

public class CAKubeJS {
	public static final Map<ResourceLocation, DrillHeadBuilderJS> DRILL_HEADS = new LinkedHashMap<>();
	public static final Map<ResourceLocation, ResourceLocation> DRILL_PARTIALS = new LinkedHashMap<>();

	public CAKubeJS() {
		EVENT.register(event -> {
			event.register(CreateAutomated.asResource("extracting"), ExtractingJS::new);
			event.register(CreateAutomated.asResource("picking"), PickingJS::new);
		});

		FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(Item.class, this::registerDrillHeads);

		DeferredWorkQueue.runLater(() -> {
			new DrillHeadRegistryEventJS().post(ScriptType.STARTUP, "item.registry.drillhead");
			new DrillPartialRegistryEventJS().post(ScriptType.CLIENT, "partial.registry.drillhead");
		});
	}

	public void registerDrillHeads(final RegistryEvent.Register<Item> event) {
		DRILL_HEADS.forEach((id, builder) -> {
			DrillHeadItemJS item = new DrillHeadItemJS(builder);
			item.setRegistryName(id);
			event.getRegistry().register(item);
		});
	}

	public static final Map<ResourceLocation, PartialModel> BUILD_DRILL_PARTIALS = new LinkedHashMap<>();
}
