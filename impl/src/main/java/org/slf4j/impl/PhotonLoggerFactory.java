package org.slf4j.impl;

import com.electronwill.utils.StringUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public class PhotonLoggerFactory implements ILoggerFactory {

	private final ConcurrentMap<String, Logger> loggerMap = new ConcurrentHashMap<>();

	@Override
	public Logger getLogger(String name) {
		if(name.startsWith("org.mcphoton")){
			List<String> parts = new ArrayList<>();
			StringUtils.split(name, '.', parts);
			name = parts.get(parts.size()-1);//uses the last part
		}
		final String loggerName = name;//final String for the lambda expression
		return loggerMap.computeIfAbsent(name, (k) -> new PhotonLogger(loggerName));
	}

}
