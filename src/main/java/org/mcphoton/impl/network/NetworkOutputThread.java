package org.mcphoton.impl.network;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Collections;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.mcphoton.impl.server.Main;

/**
 * The thread that manages all the network output. It writes outcoming packets.
 *
 * @author TheElectronWill
 *
 */
public final class NetworkOutputThread extends Thread {

	private final BlockingQueue<PacketSending> queue = new ArrayBlockingQueue<>(500);
	private final PacketSending POISON = new PacketSending(null, Collections.emptyList());
	private final Selector selector;
	private final Object selectorLock;
	private volatile boolean run = true;

	/**
	 * Creates a new <code>NetworkOutputThread</code> that registers channels to the specified
	 * <code>Selector</code> in case of incomplete writing.
	 *
	 * @param selector the selector to use in case of incomplete writing
	 * @param selectorLock the objet to synchronize on when a selector's wakeup is needed. (normally it's the
	 * NetworkInputThread instance).
	 */
	public NetworkOutputThread(Selector selector, Object selectorLock) {
		super("network-output");
		this.selector = selector;
		this.selectorLock = selectorLock;
	}

	/**
	 * Stops this Thread nicely, as soon as possible but without any forcing.
	 */
	public void stopNicely() {
		run = false;
		queue.offer(POISON);
	}

	@Override
	public void run() {
		while (run) {
			try {
				PacketSending sending = queue.take();
				if (sending == POISON) {
					return;
				}
				Main.serverInstance.logger.debug("Take " + sending);
				for (PhotonClient client : sending.clients) {
					try {
						boolean completeAndImmediateWrite = client.messageWriter.writeASAP(sending.packet);
						if (!completeAndImmediateWrite) {
							synchronized (selectorLock) {
								selector.wakeup();
								client.channel.register(selector, SelectionKey.OP_WRITE, client);
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void enqueue(PacketSending ps) {
		Main.serverInstance.logger.debug("Put " + ps);
		try {
			queue.put(ps);
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}

}
