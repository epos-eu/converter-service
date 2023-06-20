package org.epos.converter.app.plugin.managment;

/**
 * @author patk
 * 
 * Used to help dictate what constraints should be placed on the plugin system.
 *
 */
public enum PluginsMode { 
	/*
	 * Indicates support for plugins that require flexibility in how they 
	 * are structured (e.g. supports multiple RequestTypes)
	 */
	FLEXIBILE, 
	/*
	 *  Indicates system to work only with ICS-V1-compliant plugins. 
	 *  These support only a single RequestType ("icsc-default")
	 */
	ICSC_V1
}
