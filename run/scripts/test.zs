import mods.createautomated.RequiredProgress;

val e = new RequiredProgress();
e.takesSeconds(32);
e.build();

new RequiredProgress().atSpeedOf(32).takesSeconds(2).build();

<recipetype:createautomated:extracting>.addRecipe("diamonds_from_cobble", <item:minecraft:cobblestone>, <item:minecraft:diamond>, 3,
    new RequiredProgress().atSpeedOf(128).takesSeconds(2), 2, 4
);

<recipetype:createautomated:extracting>.addRecipe("diamonds_from_cobble2", <item:minecraft:cobblestone>, <item:minecraft:diamond>, 3, 128 * 40, 2, 4);

//<recipetype:createautomated:extracting>.addRecipe("diamonds_from_grass", <item:minecraft:cobblestone>, <item:minecraft:diamond>, 0, 1, 999);
// <recipetype:createautomated:picking>.addRecipe("not_op_at_all", <item:minecraft:dirt>, [<item:minecraft:diamond> % 100]);

// <recipetype:createautomated:extracting>.addRecipe("zinctest", <item:create:zinc_ore>, <item:createautomated:zinc_ore_piece>, 2, 500, 3);
// <recipetype:createautomated:extracting>.addRecipe("diamonds_from_cobble", <item:minecraft:cobblestone>, <item:minecraft:diamond>, 0, 1, 999);
