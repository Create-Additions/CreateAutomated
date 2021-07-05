package com.kotakotik.createautomated.content.items;

import com.kotakotik.createautomated.content.base.IDrillHead;
import net.minecraft.item.Item;

public class DrillHead extends Item implements IDrillHead {
    public DrillHead(Properties p_i48487_1_) {
        super(p_i48487_1_);
    }

    @Override
    public int getDurability() {
        return 200;
    }
}
