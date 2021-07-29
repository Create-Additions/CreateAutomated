// String name, IIngredient node, IItemStack output, int drillDamage, int speedOf, int takesTicks, int minOre, int maxOre
// takes 10 seconds at 128 rpm
<recipetype:createautomated:extracting>.addRecipe("tea_extraction", <item:contenttweaker:tea_node>, <item:create:builders_tea>, 0, 128, 200, 1, 1);


// forgive me crafttweaker masters, but im too tired to figure out how to spread these
craftingTable.addShapeless("tea_node", <item:contenttweaker:tea_node>, [<item:create:builders_tea>, <item:create:builders_tea>, <item:create:builders_tea>, <item:create:builders_tea>, <item:create:builders_tea>, <item:create:builders_tea>, <item:create:builders_tea> ,<item:create:builders_tea>, <item:create:builders_tea>]);
