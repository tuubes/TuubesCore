package org.mcphoton.plugin;

/**
 * A ClassSharer allows to share java classes across the different plugins.
 * <h2>How class sharing works</h2>
 * <p>
 * A SharedClassLoader is a plugin's ClassLoader with an additional public method
 * {@link SharedClassLoader#findClass(String, boolean)}. It is associated with a ClassSharer. When
 * asked for a class by the plugin, a SharedClassFinder asks the ClassSharer via
 * {@link #getClass(String)}. The {@link #getClass(String)} method loops though all
 * SharedClassFinder to find the corresponding class. In that way, when a plugin needs a class,
 * it may be located in another plugin. Provided that the two plugins' ClassLoaders are
 * SharedClassFinder and have the same ClassSharer, and were added to it with
 * {@link #addClassLoader(SharedClassLoader)}.
 * </p>
 *
 * @author TheElectronWill
 */
public interface ClassSharer {
	/**
	 * Gets a class by name.
	 *
	 * @param name the full class name, for instance "java.lang.String".
	 */
	Class<?> getClass(String name);

	/**
	 * Adds a SharedClassLoader to this ClassSharer.
	 */
	void addClassLoader(SharedClassLoader classLoader);

	/**
	 * Removes a SharedClassLoader from this ClassSharer. The class loader is removed even if its
	 * use count is
	 * greater than zero.
	 */
	void removeClassLoader(SharedClassLoader classLoader);

	/**
	 * Removes a SharedClassLoader from this ClassSharer. The class loader is removed if and only if
	 * its use
	 * count is less than or equal to zero.
	 */
	void removeUselessClassLoader(SharedClassLoader classLoader);
}