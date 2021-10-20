package com.kotakotik.createautomated.compat.kubejs.item.drillHead.item;

import com.kotakotik.createautomated.api.IDrillHead;
import dev.latvian.kubejs.item.ItemJS;

public class DrillHeadItemJS extends ItemJS implements IDrillHead {
	public final DrillHeadBuilderJS properties;

	public DrillHeadItemJS(DrillHeadBuilderJS p) {
		super(p);
		properties = p;
	}

	@Override
	public int getDurability() {
		return properties.durability;
	}

	@Override
	public boolean shouldIgnoreDamage() {
		return IDrillHead.super.shouldIgnoreDamage() || properties.ignoreDamage;
	}
}
