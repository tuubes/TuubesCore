package org.mcphoton.permissions;

import java.util.Collection;
import java.util.List;

/**
 * A PermissionGroup is a group of Permissible objects that share common permission settings.
 *
 * @author TheElectronWill
 */
public interface PermissionGroup extends Permissible {
	/**
	 * @return the group's name.
	 */
	String getName();

	/**
	 * @return the group's members.
	 */
	Collection<Permissible> getMembers();

	/**
	 * Adds a member to the group.
	 *
	 * @param member the member to add.
	 */
	void addMember(Permissible member);

	/**
	 * Removes a member from the group.
	 *
	 * @param member the member to remove.
	 */
	void removeMember(Permissible member);

	/**
	 * Checks if the given Permissible is a member of the group.
	 *
	 * @param p the permissible.
	 * @return {@code true} if it is a member of the group, else {@code false}.
	 */
	boolean hasMember(Permissible p);

	/**
	 * Gets the parent groups of this group. The order is important because, to resolve permissions, the
	 * parent groups are checked in order.
	 *
	 * @return the parent groups.
	 */
	List<PermissionGroup> getParents();
}
