package com.kotakotik.createautomated.compat.kubejs.item.drillHead.item;

import com.kotakotik.createautomated.api.IDrillHead;
import dev.latvian.kubejs.item.ItemJS;

public class DrillHeadItemJS extends ItemJS implements IDrillHead {
	public DrillHeadItemJS(DrillHeadBuilderJS p) {
		super(p);
	}

	@Override
	public int getDurability() {
		return ((DrillHeadBuilderJS) properties).durability;
	}
}
