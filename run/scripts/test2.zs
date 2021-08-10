#loader contenttweaker

import mods.contenttweaker.item.ItemBuilder;
import mods.createautomated.item.DrillHeadBuilder;

new ItemBuilder()
    .withType<DrillHeadBuilder>()
    .durability(999999)
    .partial("block/my_drill")
    .build("diamond_drill_head");
