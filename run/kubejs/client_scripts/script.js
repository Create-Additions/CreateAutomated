// priority: 0

console.info('Hello, World! (You will see this line every time client resources reload)')

onEvent('jei.hide.items', event => {
	// Hide items in JEI here
	// event.hide('minecraft:cobblestone')
})

onEvent('partial.registry.drillhead', event => {
    event.create("kubejs:test_item", "kubejs:test_item_partial")
})
