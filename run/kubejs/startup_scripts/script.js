// priority: 0

console.info('Hello, World! (You will only see this line once in console, during startup)')

onEvent('item.registry', event => {
	// Register new items here
	// event.create('example_item').displayName('Example Item')
	 event.create('floppa_bit').displayName('Floppa Bit')
	 event.create("dripping_floppa").displayName("Dripping Floppa")
	    .tooltip("epic floppa!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
	    // i SAID MAIININING SPEED 9999
	 event.create("engineer_floppa").miningSpeed(999).displayName("Engineer Floppa").miningSpeed(999)
	            .tier("netherite")
                .attackDamage(999).miningSpeed(999)
                .attackSpeed(999).miningSpeed(999)
        	    .miningSpeed(999)
        	                    	    .maxDamage(9999999)
        	    	    .miningSpeed(999)
.miningSpeed(999)
        	    .unstackable().miningSpeed(999)

})

onEvent('block.registry', event => {
	// Register new blocks here
	// event.create('example_block').material('wood').hardness(1.0).displayName('Example Block')
	event.create('floppa_node').material('wood').hardness(1.0).displayName('Floppa Node')
})

onEvent("item.registry.drillhead", event => {
    event.create('floppa_drill').displayName('Floppa Drill Head').infinite()
})
