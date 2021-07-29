// priority: 0

settings.logAddedRecipes = true
settings.logRemovedRecipes = true
settings.logSkippedRecipes = false
settings.logErroringRecipes = true

console.info('Hello, World! (You will see this line every time server resources reload)')

onEvent('recipes', event => {
	// Change recipes here
	for(var i = 0 ; i < 20 ; i++) {
	    console.info("wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww" + i)
	    	event.recipes.createautomated.extracting("minecraft:obsidian", "minecraft:netherrack")
                .drillDamage(304 + i)
                .ore(30)
                .requiredProgressMinutes(128, 60)
	}

	const p = event.recipes.createautomated.picking("minecraft:diamond_block", Item.of("minecraft:diamond")
	    .withChance(0.5)
	    .withCount(64))
//	console.info(p)
//	console.info(event)
	p.addDeploying(event)

	event.recipes.createautomated.extracting("kubejs:floppa_node", "kubejs:floppa_bit")
	    .drillDamage(0)
	    .ore(2)
	    .requiredProgressSeconds(128, 3)
})

onEvent('item.tags', event => {
	// Get the #forge:cobblestone tag collection and add Diamond Ore to it
	// event.get('forge:cobblestone').add('minecraft:diamond_ore')

	// Get the #forge:cobblestone tag collection and remove Mossy Cobblestone from it
	// event.get('forge:cobblestone').remove('minecraft:mossy_cobblestone')
})
