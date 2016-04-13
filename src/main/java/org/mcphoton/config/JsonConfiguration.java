package org.mcphoton.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import com.electronwill.json.Json;
import java.io.InputStreamReader;

public class JsonConfiguration extends BaseConfiguration {

	public JsonConfiguration() {
		super();
	}

	public JsonConfiguration(Map<String, Object> map) {
		super(map);
	}

	@Override
	public synchronized void readFrom(InputStream in) throws IOException {
		map = Json.readObject(new InputStreamReader(in, StandardCharsets.UTF_8));
	}

	@Override
	public synchronized void writeTo(OutputStream out) throws IOException {
		Json.write(map, new OutputStreamWriter(out, StandardCharsets.UTF_8), true);
	}

	public synchronized String writeToString() throws IOException {
		return Json.writeToString(map, false);
	}

}
