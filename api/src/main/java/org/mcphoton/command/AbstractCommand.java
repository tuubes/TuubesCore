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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.mcphoton.messaging.Messageable;

/**
 * Base class for commands.
 *
 * @author TheElectronWill
 */
public abstract class AbstractCommand implements Command {

	protected final String name, description, usage;
	protected final Options options;

	public AbstractCommand(String name, String description, String usage, Options options) {
		this.name = name;
		this.description = description;
		this.usage = usage;
		this.options = options;
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getDescription() {
		return description;
	}
	
	@Override
	public String getUsage() {
		return usage;
	}
	
	@Override
	public void execute(Messageable source, String[] args) {
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine cmdLine = parser.parse(options, args);
			execute(source, cmdLine);
		} catch (ParseException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Executes this command.
	 *
	 * @param source the person or thing that executes this command
	 * @param cmd the CommandLine objet that contains the parsed arguments of this command
	 */
	public abstract void execute(Messageable source, CommandLine cmd);

}
