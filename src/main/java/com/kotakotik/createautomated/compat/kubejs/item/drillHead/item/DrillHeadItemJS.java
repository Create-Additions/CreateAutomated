package com.kotakotik.createautomated.compat.kubejs.item.drillHead.item;

import com.kotakotik.createautomated.api.IDrillHead;
import net.minecraft.item.Item;

public class DrillHeadItemJS extends Item implements IDrillHead {
	public final DrillHeadBuilderJS properties;

	public DrillHeadItemJS(DrillHeadBuilderJS p) {
		super(p.createItemProperties());
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
