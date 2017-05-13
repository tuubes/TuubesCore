package org.mcphoton.messaging;

/**
 * Interface for objects that can receive messages.
 *
 * @author TheElectronWill
 */
public interface Messageable {
	/**
	 * Sends a plain text message to this object.
	 *
	 * @param msg the message to send
	 */
	void sendMessage(CharSequence msg);

	/**
	 * Sends a formatted message to this object.
	 *
	 * @param msg the messafe to send.
	 */
	void sendMessage(ChatMessage msg);
}