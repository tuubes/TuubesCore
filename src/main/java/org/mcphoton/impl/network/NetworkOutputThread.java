package org.mcphoton.impl.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * The thread that manages all the network output. It writes outcoming packets.
 * 
 * @author TheElectronWill
 * 		
 */
public final class NetworkOutputThread extends Thread {
	
	// TODO measurements to determine the best queue's capacity
	private final BlockingQueue<PacketSending> queue = new ArrayBlockingQueue<>(200, false);
	private final PacketSending POISON = new PacketSending(null, null);
	private final Selector selector;
	private final Object guard;
	private volatile boolean run = true;
	
	/**
	 * Creates a new <code>NetworkOutputThread</code> that registers channels to the specified <code>Selector</code> in
	 * case of incomplete writing.
	 * 
	 * @param selector the selector to use in case of incomplete writing
	 * @param the guard for wakeup (normally this is the NetworkInputTherad instance).
	 */
	public NetworkOutputThread(Selector selector, Object guard) {
		this.selector = selector;
		this.guard = guard;
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
				for (PhotonClient client : sending.clients) {
					try {
						BytesProtocolOutputStream out = new BytesProtocolOutputStream();
						sending.packet.writeTo(out);
						ByteBuffer data = out.asBuffer();
						ByteBuffer encodedData = client.encodeWithCodecs(data);
						boolean completeAndImmediateWrite = client.getMessageWriter().writeASAP(encodedData);
						if (!completeAndImmediateWrite) {
							synchronized (guard) {
								selector.wakeup();
								client.getChannel().register(selector, SelectionKey.OP_WRITE, client);
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
	
	/**
	 * Adds a <code>PacketSending</code> to the sending queue, waiting if necessary for space to become available.
	 */
	public void enqueue(PacketSending sending) {
		try {
			queue.put(sending);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Adds a <code>PacketSending</code> to the sending queue, waiting up to the specified wait time if necessary for
	 * space to become available.
	 */
	public void enqueue(PacketSending sending, long timeout, TimeUnit unit) {
		try {
			queue.offer(sending, timeout, unit);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
