package org.tud.schunk.gripper;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.tud.log.Logger;
import org.tud.schunk.gripper.SchunkGripperAcknowledge.StatusCode;
import org.tud.schunk.gripper.SchunkGripperServerListener.SchunkGripperServerState;

class SchunkSocket {

	
	private static final String IP = "172.31.1.146";
	private static final int PORT = 1000;
	private Socket socket;
	private DataOutputStream writer;
	private DataInputStream reader;
	private SchunkGripperCommand currentCommand;
	private SchunkGripperAcknowledge currentAcknowlegement;
	private static final long sendSyncTimeoutInMs = 200;

	private List<SchunkGripperListener> listeners;
	private List<SchunkGripperServerListener> serverListeners;
	
	private static final SchunkSocket instance = new SchunkSocket();
	
	private SchunkSocket() {		
		listeners = new CopyOnWriteArrayList<SchunkGripperListener>();
		serverListeners = new CopyOnWriteArrayList<SchunkGripperServerListener>();
		
	}

	public static SchunkSocket getInstance() {
		return instance;
	}
	public boolean isConnected() {
		try {
			return socket != null && socket.isConnected() && socket.getInputStream() != null && socket.getOutputStream() != null;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	/* listeners */
	public void addServerListener(SchunkGripperServerListener listener) {
		serverListeners.add(listener);
	}

	public void removeServerListener(SchunkGripperServerListener listener) {
		serverListeners.remove(listener);
	}

	public void addListener(SchunkGripperListener listener) {
		listeners.add(listener);
	}

	public void removeListener(SchunkGripperListener listener) {
		listeners.remove(listener);
	}
	/* end listeners */
	
	public synchronized SchunkGripperAcknowledge sendCommandSync(
			SchunkGripperCommand command) {
		if (!isConnected())
			return null;
		try {
			currentCommand = command;
			currentAcknowlegement = null;
			sendCommand(command);			
			long time = new Date().getTime();
			command.setTimestamp(time);
			while (time - command.getTimestamp() < sendSyncTimeoutInMs) {
				try {
					synchronized(currentCommand) {
						currentCommand.wait(sendSyncTimeoutInMs/2);
					}
				} catch (InterruptedException e) {
					// TODO Automatisch generierter Erfassungsblock
					e.printStackTrace();
				}
				if (currentAcknowlegement != null) {
					return currentAcknowlegement;
				}
				time = new Date().getTime();
			}
			//Logger.error("no ack received for command:"+currentCommand.toString());
		}
		catch (Exception e) {
			Logger.error("Gripper Write Error! " + e.getMessage());
			e.printStackTrace();
			return null;
		}
		return null;	
	}
	
	public void sendCommand(SchunkGripperCommand command) {
		if(!isConnected()) {
			connect();
		}
		int[] c = command.toArray();
		for (int i = 0; i < c.length; i++) {
			try {
				writer.write(c[i]);
			} catch (IOException e) {
				
			}
		}
		try {
			writer.flush();
		} catch (IOException e) {
			// TODO Automatisch generierter Erfassungsblock
			e.printStackTrace();
		}
		//Logger.trace("send-->" + Arrays.toString(c));
		//Logger.info("send-->" + command.toString());
	}

	private int incommingDataPosition = 0;
	private int totalDataLength = 0;
	private int[] incommingBuffer = new int[1024];

	/***
	 * Liest entweder Blockierend = true, was bedeutet er liest bis wieder einer nachricht da ist, oder er liest
	 * nicht blockierend, was bedeutet er liest eine maxReadingTime in ms und wenn er in der Zeit keine komplette
	 * Nachricht empfangen hat wird er das lesen beenden und null zur�ckgeben oder wenn er eine Nachricht empfangen hat
	 * gibt er die nachricht zur�ck.
	 * @param blocking
	 * @param maxReadingTime
	 * @return
	 */
	private SchunkGripperAcknowledge readData(boolean blocking, long maxReadingTime) {
		long startTime = new Date().getTime();
		SchunkGripperAcknowledge ack = null;
		while (ack == null) {
			int value = 0;
			try {
				if (blocking) {
					value = reader.read();
				}
				else
				{
					if (reader.available() == 0)
					{
						if (new Date().getTime() - startTime > maxReadingTime)
							return null;
						try {
							Thread.sleep(250);
						} catch (InterruptedException e) {
							// TODO Automatisch generierter Erfassungsblock
							e.printStackTrace();
						}
						continue;
					}
					else {
						value = reader.read();
					}
				}
				if (value < 0)	{
					return null;
				}
				// java intepretiert die bytes falsch
				value = value & 0xff;
			} catch (IOException e) {
				// wenn eine io Exception fliegt, dann ist der Inputstream geschlossen
				return null;
			}
			if (incommingDataPosition < 3) {
				if (value == 0xaa) {
					incommingBuffer[incommingDataPosition] = value;
					incommingDataPosition++;
				} else {
					incommingBuffer = new int[1024];
					incommingDataPosition = 0;
					totalDataLength = 0;
					ack = null;
					currentAcknowlegement = null;
				}
			} else {
				incommingBuffer[incommingDataPosition] = value;
				incommingDataPosition++;
			}
			// jetzt ist die Nachricht ohne Payload und status code da
			if (incommingDataPosition == 6) {
				// Auslesen der noch l�nge der Antwort
				totalDataLength = incommingBuffer[5];
				totalDataLength <<= 8;
				totalDataLength |= incommingBuffer[4];
				totalDataLength += 2; // crcCode
				totalDataLength += 6; // command
			}
			// 2 zeichen crc 6 Zeichen commando
			if (incommingDataPosition == totalDataLength && incommingDataPosition != 0) {
				// Eine komplette Nachricht ist angekommen, jetzt auswerten der
				// Nachricht
				ack = new SchunkGripperAcknowledge(incommingBuffer[3]);
				ack.setSize(totalDataLength - 8);
				int statusCode = 0;
				statusCode = incommingBuffer[7];
				statusCode <<= 8;
				statusCode |= incommingBuffer[6];
				ack.setStatusCode(StatusCode.fromInt(statusCode));
				// 2 abziehen f�r den statuscode
				int[] parameter = new int[ack.getSize() - 2];
				for (int i = 0; i < parameter.length; i++) {
					parameter[i] = incommingBuffer[i + 8];
				}
				ack.setParameter(parameter);
				ack.setCrc(new int[] { incommingBuffer[totalDataLength - 2],
						incommingBuffer[totalDataLength - 1] });
				if (!ack.isValid()) {
					System.out.println("ERROR :crc not valid");
					ack.setStatusCode(StatusCode.E_CHECKSUM_ERROR);
				}
				incommingBuffer = new int[1024];
				incommingDataPosition = 0;
				totalDataLength = 0;
			}
		}
		return ack;
	}

	public boolean connect() {
		if (!isConnected()) {
			try {
				socket = new Socket(IP, PORT);
			} catch (UnknownHostException e) {
				Logger.error("Gripper Socket not found! cable connected/power on?");
				return false;
			} catch (IOException e) {
				Logger.error("Schunk Gripper Socket not found! cable connected/power on?");
				return false;
			}
			try {
				writer = new DataOutputStream(socket.getOutputStream());
				reader = new DataInputStream(socket.getInputStream());
				//socket.setSoTimeout(1000);
			} catch (IOException e) {
				Logger.error("Schunk Gripper Socket not found! cable connected/power on?");
				return false;
			}
			if (isConnected()) {
				notifyServerListeners(SchunkGripperServerState.CONNECTED);
				Logger.info("connected to Gripper socket");
				new SchunkSocketThread().start();

				return true;
			} else {
				Logger.error("not connected to Gripper Socket");
				notifyServerListeners(SchunkGripperServerState.ERROR);
				return false;
			}
		}
		return true;
	}


	private void notifyListeners(SchunkGripperAcknowledge ack) {
		for (SchunkGripperListener l : listeners) {
			l.notify(ack);
		}
	}
	private void notifyServerListeners(SchunkGripperServerState state) {
		for (SchunkGripperServerListener l : serverListeners) {
			l.notify(state);
		}
	}

	public void close() {
		try {
			reader.close();	
		} catch (Exception e) {

		}
		try {
			writer.close();
		} catch (Exception e) {

		}
		try {
			socket.close();
		} catch (Exception e) {
		}
		socket = null;
		Logger.info("disconnected!");
	}
	
	private class SchunkSocketThread extends Thread {
		private boolean running = true;
		
		public SchunkSocketThread() {
			super("SchunkSocketThread");
		}
		@Override
		public void run() {
			Logger.info("Gripper Socket Thread started!");
			notifyServerListeners(SchunkGripperServerState.RUNNING);
			while (running) {
				SchunkGripperAcknowledge ack = readData(true, 0);
					if(ack != null) {
						//Logger.info("receive-->" + ack.toString());
						if (ack.matches(currentCommand.getCommandId())) {
							currentAcknowlegement = ack;
							synchronized (currentCommand) {
								currentCommand.notify();
							}
						}
						notifyListeners(ack);
					} else {
						//Wenn er hier rein geht ist der Stream Beendet also muss der Socket geschlossen werden!					
						//close();
						running = false;
						socket = null;
					}
				}
			Logger.info("Gripper Socket Thread stopped!");
			notifyServerListeners(SchunkGripperServerState.STOPPING);
		}

	}
}

