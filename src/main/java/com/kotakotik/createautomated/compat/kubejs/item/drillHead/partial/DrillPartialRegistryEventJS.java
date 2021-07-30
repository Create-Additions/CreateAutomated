package com.kotakotik.createautomated.compat.kubejs.item.drillHead.partial;

import com.jozufozu.flywheel.core.PartialModel;
import com.kotakotik.createautomated.api.DrillPartialIndex;
import com.kotakotik.createautomated.compat.kubejs.CAKubeJS;
import dev.latvian.kubejs.event.EventJS;
import net.minecraft.util.ResourceLocation;

public class DrillPartialRegistryEventJS extends EventJS {
	public DrillPartialRegistryEventJS create(ResourceLocation item, ResourceLocation partial) {
		CAKubeJS.DRILL_PARTIALS.put(item, partial);
		PartialModel model = new PartialModel(partial);
		CAKubeJS.BUILD_DRILL_PARTIALS.put(item, model);
		DrillPartialIndex.add(item, model);
		return this;
	}

	public DrillPartialRegistryEventJS create(String itemNamespace, String itemId, String partialNamespace, String partialId) {
		return create(new ResourceLocation(itemNamespace, itemId), new ResourceLocation(partialNamespace, partialId));
	}
}
