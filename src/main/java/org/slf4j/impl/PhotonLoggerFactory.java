package org.slf4j.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public class PhotonLoggerFactory implements ILoggerFactory {

	private final ConcurrentMap<String, Logger> loggerMap = new ConcurrentHashMap<>();

	@Override
	public Logger getLogger(String name) {
		Logger logger = loggerMap.get(name);
		if (logger != null) {
			return logger;
		} else {
			Logger newInstance = new PhotonLogger(name);
			Logger oldInstance = loggerMap.putIfAbsent(name, newInstance);
			return oldInstance == null ? newInstance : oldInstance;
		}
	}

}
