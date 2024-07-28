package com.example;

import com.example.registry.ModEvent;
import com.example.registry.ModItems;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EndDragonMOD implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MOD_ID = "enddragon";
    public static final Logger LOGGER = LoggerFactory.getLogger("enddragon-mod");

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		ModItems.main_registerItem();
		ModEvent.main_registerEvent();
		LOGGER.info("Hello Fabric world!");
	}
}