package org.mcphoton.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that must be applied on plugin classes, so that we can get the plugin's basic
 * informations without creating an instance of it.
 *
 * @author TheElectronWill
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PluginDescription {
	String name();

	String version();

	String author();

	String[] requiredDependencies() default "";

	String[] optionalDependencies() default "";
}