// priority: 0

console.info('Hello, World! (You will only see this line once in console, during startup)')

onEvent('item.registry', event => {
	// Register new items here
	// event.create('example_item').displayName('Example Item')
	 event.create('floppa_bit').displayName('Floppa Bit')
})

onEvent('block.registry', event => {
	// Register new blocks here
	// event.create('example_block').material('wood').hardness(1.0).displayName('Example Block')
	event.create('floppa_node').material('wood').hardness(1.0).displayName('Floppa Node')
})

onEvent('item.registry.drillhead', event => {
    event.create('test_item').displayName('Test Item').durability(3)
})

onEvent('partial.registry.drillhead', event => {
    event.create("kubejs:test_item", "kubejs:test_item_partial")
})
