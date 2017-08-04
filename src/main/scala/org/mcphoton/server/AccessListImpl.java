package org.mcphoton.server;

import com.electronwill.nightconfig.core.ConfigSpec;
import com.electronwill.nightconfig.core.io.ParsingException;
import com.electronwill.nightconfig.core.io.WritingException;
import com.electronwill.nightconfig.toml.TomlConfig;
import com.electronwill.nightconfig.toml.TomlParser;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;
import org.mcphoton.Photon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class for blacklist and whitelist managers, to avoid duplicated code.
 *
 * @author TheElectronWill
 */
final class AccessListImpl implements AccessList {
	private static final ConfigSpec CONFIG_SPEC = new ConfigSpec();

	static {
		CONFIG_SPEC.define("accounts", Collections.emptyList());
		CONFIG_SPEC.define("ips", Collections.emptyList());
	}

	private final String name;
	private final File file;
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final Set<UUID> accountSet = new ConcurrentSkipListSet<>();
	private final Set<InetAddress> addressSet = new ConcurrentSkipListSet<>();

	AccessListImpl(String name) {
		this.name = name;
		this.file = new File(Photon.getMainDirectory(), name + ".toml");
	}

	void load() {
		logger.info("Loading " + name + "...");
		try {
			boolean created = file.createNewFile();
			if (created) {// The file didn't exist before => Fill it with the basic structure
				Writer writer = new FileWriter(file);
				writer.write("accounts = []" + System.lineSeparator() + "ips = []");
				writer.close();
				return;// No need to parse the file
			}
		} catch (IOException | SecurityException e) {
			throw new RuntimeException("Failed to create the " + name + " file", e);
		}
		TomlConfig config;
		try {
			config = new TomlParser().parse(file);
		} catch (ParsingException e) {
			throw new StartupFailedException("Cannor parse the " + name, e);
		}
		int corrected = CONFIG_SPEC.correct(config);
		if (corrected > 0) {
			logger.info("Corrected {} entries in {}.", corrected, name);
			save();
		}
		if (!config.isEmpty()) {
			List<?> accounts = config.getValue("accounts");
			for (Object account : accounts) {
				try {
					UUID playerId = UUID.fromString((String)account);
					accountSet.add(playerId);
				} catch (IllegalArgumentException | ClassCastException e) {
					logger.warn("Invalid account id in {}: {}", name, account);
				}
			}
			List<?> ips = config.getValue("ips");
			for (Object ip : ips) {
				try {
					InetAddress address = InetAddress.getByName((String)ip);
					addressSet.add(address);
				} catch (IllegalArgumentException | UnknownHostException | ClassCastException e) {
					logger.warn("Invalid ip address in {}: {}", name, ip);
				}
			}
		}
	}

	void save() {
		TomlConfig config = new TomlConfig();
		config.setValue("accounts", accountSet);
		config.setValue("ips", addressSet);
		try {
			config.write(file);
		} catch (WritingException e) {
			logger.error("Cannot save the " + name, e);
		}
	}

	@Override
	public boolean contains(UUID accountId) {
		return accountSet.contains(accountId);
	}

	@Override
	public boolean contains(InetAddress ip) {
		return addressSet.contains(ip);
	}

	@Override
	public void add(UUID accountId) {
		accountSet.add(accountId);
	}

	@Override
	public void add(InetAddress ip) {
		addressSet.add(ip);
	}

	@Override
	public void remove(UUID accountId) {
		accountSet.remove(accountId);
	}

	@Override
	public void remove(InetAddress ip) {
		addressSet.remove(ip);
	}

	@Override
	public Collection<UUID> getAccounts() {
		return Collections.unmodifiableSet(accountSet);
	}

	@Override
	public Collection<InetAddress> getIPs() {
		return Collections.unmodifiableSet(addressSet);
	}
}