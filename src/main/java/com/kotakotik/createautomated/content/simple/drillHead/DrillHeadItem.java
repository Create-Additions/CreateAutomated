package com.kotakotik.createautomated.content.simple.drillHead;

import com.kotakotik.createautomated.api.IDrillHead;
import com.kotakotik.createautomated.register.config.ModConfig;
import net.minecraft.item.Item;

public class DrillHeadItem extends Item implements IDrillHead {
	public DrillHeadItem(Properties p_i48487_1_) {
		super(p_i48487_1_);
	}

	@Override
	public int getDurability() {
		return ModConfig.SERVER.machines.extractor.drillDurability.get();
	}
}
