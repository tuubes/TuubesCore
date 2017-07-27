package org.mcphoton.messaging;

import java.util.Map;

@SuppressWarnings("unchecked")
public class TranslateChatMessage extends ChatMessage {
	public TranslateChatMessage(Map<String, Object> map) {
		super(map);
	}

	public TranslateChatMessage(String textToTranslate, String[] with) {
		map.put("translate", textToTranslate);
		map.put("with", with);
	}

	public String getTextToTranslate() {
		return (String)map.get("translate");
	}

	public String[] getWith() {
		return (String[])map.get("with");
	}

	public void setTextToTranslate(String text) {
		map.put("translate", text);
	}

	public void setWith(String... with) {
		map.put("with", with);
	}

	@Override
	public String toConsoleString() {
		return toString();
	}

	@Override
	public String toLegacyString() {
		return toString();
	}
}