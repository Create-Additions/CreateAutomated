package com.kotakotik.createautomated.compat.kubejs;

import dev.latvian.kubejs.KubeJSObjects;
import dev.latvian.kubejs.item.ItemRegistryEventJS;

public class DrillHeadRegistryEventJS extends ItemRegistryEventJS {
	@Override
	public DrillHeadBuilderJS create(String name) {
		DrillHeadBuilderJS builder = new DrillHeadBuilderJS(name);
		CAKubeJS.DRILL_HEADS.put(builder.id, builder);
		KubeJSObjects.ALL.add(builder);
		return builder;
	}
}
