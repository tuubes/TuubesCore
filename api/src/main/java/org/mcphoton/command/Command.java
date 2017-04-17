/*
 * Copyright (c) 2016 MCPhoton <http://mcphoton.org> and contributors.
 *
 * This file is part of the Photon API <https://github.com/mcphoton/Photon-API>.
 *
 * The Photon API is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Photon API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
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
	 * @param source the person or thing that executes this command
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
	 * @param args the array that contains the command's arguments
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
