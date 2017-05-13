package org.mcphoton.command;

import com.electronwill.utils.StringUtils;
import java.util.List;
import org.mcphoton.messaging.Messageable;

/**
 * A command that can be executed.
 *
 * @author TheElectronWill
 */
public interface Command {
	/**
	 * Executes this command.
	 *
	 * @param source    the person or thing that executes this command
	 * @param argString the string that contains the command's arguments
	 */
	default void execute(Messageable source, String argString) {
		List<String> parts = StringUtils.splitArguments(argString);
		String[] array = new String[parts.size()];
		execute(source, parts.toArray(array));
	}

	/**
	 * Executes this command.
	 *
	 * @param source the person or thing that executes this command
	 * @param args   the array that contains the command's arguments
	 */
	void execute(Messageable source, String[] args);

	/**
	 * @return the name of this command.
	 */
	String getName();

	/**
	 * @return the description of this command.
	 */
	String getDescription();

	/**
	 * @return the usage of this command.
	 */
	default String getUsage() {
		return '/' + getName();
	}

	/**
	 * @return the aliases of this command.
	 */
	default String[] getAliases() {
		return new String[0];
	}
}