package org.mcphoton.plugin;

/**
 * Interface that provides convenient methods to share classes between plugins.
 *
 * @author TheElectronWill
 * @see ClassSharer
 */
public interface SharedClassLoader {
	/**
	 * Tries to find a class.
	 *
	 * @param name        the full class name, for instance "java.lang.String"
	 * @param checkShared true to use the sharer to find the class, false to ignore it.
	 * @return the class
	 *
	 * @throws ClassNotFoundException if the class can't be found.
	 */
	Class<?> findClass(String name, boolean checkShared) throws ClassNotFoundException;

	/**
	 * Gets the ClassSharer.
	 */
	ClassSharer getSharer();

	/**
	 * Gets the use count. When the use count reach zero, the SharedClassLoader can be removed from
	 * the ClassSharer.
	 */
	int getUseCount();

	/**
	 * Increases and get the use count. When the use count reach zero, the SharedClassLoader can be
	 * removed from the ClassSharer.
	 *
	 * @return the count value, after the increase.
	 */
	int increaseUseCount();

	/**
	 * Decreases and get the use count. When the use count reach zero, the SharedClassLoader can be
	 * removed from the ClassSharer.
	 *
	 * @return the count value, after the decrease.
	 */
	int decreaseUseCount();
}