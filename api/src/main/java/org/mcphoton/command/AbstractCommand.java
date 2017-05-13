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
	 * @param cmd    the CommandLine objet that contains the parsed arguments of this command
	 */
	public abstract void execute(Messageable source, CommandLine cmd);
}