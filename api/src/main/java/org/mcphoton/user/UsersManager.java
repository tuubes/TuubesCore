package org.mcphoton.user;

import java.util.Optional;
import java.util.UUID;

/**
 * Manages the informations that the Photon server has about its (previously or currently connected) users.
 *
 * @author TheElectronWill
 */
public interface UsersManager {
	/**
	 * Gets the user with the specified account id.
	 *
	 * @param accountId the user's account id.
	 * @return the user with the specified account id.
	 */
	Optional<User> getUser(UUID accountId);

	/**
	 * Gets the user with the specified name.
	 *
	 * @param name the user's name.
	 * @return the user with the specified name.
	 */
	Optional<User> getUser(String name);

	/**
	 * Removes all informations about the specified user.
	 *
	 * @param user the user to forget.
	 */
	void forgetUser(User user);

	/**
	 * Removes all informations about the specified user.
	 *
	 * @param accountId the user's account id.
	 */
	void forgetUser(UUID accountId);

	/**
	 * Checks if the Photon server has any information about the specified user.
	 *
	 * @param accountId the user's account id.
	 * @return {@code true} if this user is known, {@code false} if he/she is unknown.
	 */
	boolean isKnownUser(UUID accountId);

	/**
	 * Saves the data of the specified user.
	 *
	 * @param user the user to save.
	 */
	void saveUserData(User user);

	/**
	 * Saves the data of the specified user.
	 *
	 * @param accountId the user's account id.
	 */
	default void saveUserData(UUID accountId) {
		saveUserData(getUser(accountId).get());
	}
}
