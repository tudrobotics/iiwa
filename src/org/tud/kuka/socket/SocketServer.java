package org.tud.kuka.socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.tud.kuka.socket.SocketListerner.SocketServerNotification;
import org.tud.log.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

/**
 * es kann sich nur 1 Klient mit dem Server verbinden
 * @author Jan
 *
 */
public class SocketServer extends Thread {

	private static final SocketServer instance = new SocketServer();
	
	private static final int minPORT = 30000;
	private static final int maxPORT = 30010;
	private static int port = minPORT;
	
	// 0 = unendlich
	private static final int maxConnections = 1;
	private List<SocketConnection> openConnections;
	
	private Set<SocketListerner> listeners;
	
	private ServerSocket serverSocket;
	
	private SocketServer() {
		listeners = new CopyOnWriteArraySet<SocketListerner>();
		openConnections = new CopyOnWriteArrayList<SocketConnection>();
		start();
	}
	public static SocketServer getInstance() {
		return instance;
	}
	public boolean isRunning() {
		if(serverSocket == null) return false;
		return !serverSocket.isClosed();
	}
	public int getLocalPort() {
		if(serverSocket == null) return -1;
		return  serverSocket.getLocalPort();
	}
	public int getMaxConnections() {
		return maxConnections;
	}
	public int getOpenConnections() {
		return openConnections.size();
	}
	public String getServerStatusMessage() {
		if(isRunning()) {
			return "Server running port:"+getLocalPort()+", "+getOpenConnections()+" of "+getMaxConnections()+" connections";
		} else {
			return "Server not running";
		}
	}
	@Override
	public void start() {
		if(serverSocket != null && isRunning()) return;
		for(int i = minPORT ; i< maxPORT+1; i++) {
			if(portAvailable(i)) {
				port = i;
				break;
			}
		}
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			// TODO Automatisch generierter Erfassungsblock
			e.printStackTrace();
		}
        Logger.info("Waiting for client on port " +serverSocket.getLocalPort() + "...");
        super.start();
	}
	public void stopServer(){
		if(serverSocket != null) {
			Logger.info("try to close "+openConnections.size()+" connections");
			for(SocketConnection c:openConnections) {
				c.close();
			}
			try {
				serverSocket.close();
			} catch (IOException e) {
				// TODO Automatisch generierter Erfassungsblock
				e.printStackTrace();
			}
		}
	}
	public void addListener(SocketListerner listener) {
		listeners.add(listener);
	}
	public void removeListener(SocketListerner listener) {
		listeners.remove(listener);
	}
	protected Set<SocketListerner> getListeners() {
		return listeners;
	}
	private void notifyListeners(SocketServerNotification notification) {
		for(SocketListerner l:getListeners()) {
			l.notifyServerStatus(notification);
		}
	}
	public void send(SocketMessage message) {
		for(SocketConnection connection:openConnections) {
			connection.addSendMessage(message);
		}
	}
	@Override
	public void run() {
		notifyListeners(SocketServerNotification.RUNNING);
		while(true) {
			if(openConnections.size() < maxConnections || maxConnections == 0) {
				try {
					Socket socket = serverSocket.accept();
					SocketConnection connection = new SocketConnection(this, socket);
					openConnections.add(connection);
					connection.start();
					notifyListeners(SocketServerNotification.NEW_CONNECTION);
					//Logger.info("new SocketConnection");
				} catch (IOException e) {
					//e.printStackTrace();
					// der socket server ist geschlossen beende den Thread
					break;
				}
			} else {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Automatisch generierter Erfassungsblock
					e.printStackTrace();
				}
			}
		}
		notifyListeners(SocketServerNotification.CLOSED);
		Logger.info("server closed");
	}
	
	protected void closeConnection(SocketConnection connection) {
		openConnections.remove(connection);
		notifyListeners(SocketServerNotification.CONNECTION_CLOSED);
		//Logger.info("closing connection");
	}
	private static boolean portAvailable(int port) {
	    Socket s = null;
	    try {
	        s = new Socket("localhost", port);

	        // If the code makes it this far without an exception it means
	        // something is using the port and has responded.
	        return false;
	    } catch (IOException e) {
	        return true;
	    } finally {
	        if( s != null){
	            try {
	                s.close();
	            } catch (IOException e) {
	                return false;
	            }
	        }
	    }
	}
	private class SocketConnection extends Thread {
		
		private final JsonParser parser = new JsonParser();
		private final Gson gson = new Gson();
		private SocketServer server;
		
		public SocketConnection(SocketServer server, Socket socket) {
			this.server = server;
			this.socket = socket;
			try {
				reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				writer  = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));        
			} catch (IOException e) {
				// TODO Automatisch generierter Erfassungsblock
				e.printStackTrace();
			}
			synd = new MessageSyndication("MessageSyndication"+socket.toString(), writer);
			synd.start();
		}
		
		private Socket socket;
		private BufferedReader reader;
		private BufferedWriter writer;
		private String line;
		
		private MessageSyndication synd;
		
		@Override
		public void run() {
			try {
				receive();
			} catch (IOException e1) {
				Logger.info("socket closed?");
			}
			close();
			
		}
		private void receive() throws IOException {
			AbstractRobotCommandMessage message = null;
			LedCommandMessage ledMessage = null;
			JsonElement jsonElement;
			JsonObject jsonObject;
			String messageType;
			while((line = reader.readLine()) != null) {
		     	//Logger.info("receive.." + line);
				try {
					jsonElement = parser.parse(line);
					jsonObject = jsonElement.getAsJsonObject();
					messageType = jsonObject.get("type").getAsString();
				} catch (Exception ex) {
					continue;
				}
				if(messageType.toLowerCase().equals(RobotCommandCartesianMessage.class.getSimpleName().toLowerCase())) {
					try {
						message = gson.fromJson(line, RobotCommandCartesianMessage.class);
			     		execute(message);
			     		continue;
			     	} catch (JsonSyntaxException e) {
			     		//Logger.error("json syntax error");
			     	}
				}
				if(messageType.toLowerCase().equals(RobotCommandRotationMessage.class.getSimpleName().toLowerCase())) {
					try {
						message = gson.fromJson(line, RobotCommandRotationMessage.class);
			     		execute(message);
			     		continue;
			     	} catch (JsonSyntaxException e) {
			     		//Logger.error("json syntax error");
			     	}
				}
				if(messageType.toLowerCase().equals(RobotCommandJointMessage.class.getSimpleName().toLowerCase())) {
					try {
						message = gson.fromJson(line, RobotCommandJointMessage.class);
			     		execute(message);
			     		continue;
			     	} catch (JsonSyntaxException e) {
			     		//Logger.error("json syntax error");
			     	}
				}
				if(messageType.toLowerCase().equals(LedCommandMessage.class.getSimpleName().toLowerCase()))
			     	try {
			     		ledMessage = gson.fromJson(line, LedCommandMessage.class);
			     		execute(ledMessage);
			     		continue;
			     	} catch (JsonSyntaxException e) {
			     		//Logger.error("json syntax error");
			     	}
			}
		}
		private void execute(AbstractRobotCommandMessage message) {
			for(SocketListerner l:server.getListeners()) {
				l.handleRobotCommand(message);
			}
		}
		private void execute(LedCommandMessage message) {
			for(SocketListerner l:server.getListeners()) {
				l.handleLedCommand(message);
			}
		}
		public void addSendMessage(SocketMessage message) {
			synd.send(gson.toJson(message));
		}
		
		public void close() {
			if(!synd.isInterrupted()) {
				synd.interrupt();
				Logger.info("syndication is interrupted");
			}
//			try {
//				reader.close();
//				Logger.info("reader is closed");
//			} catch (IOException e) {
//				// TODO Automatisch generierter Erfassungsblock
//				e.printStackTrace();
//			}
//			try {
//				writer.close();
//				Logger.info("writer is closed");
//			} catch (IOException e) {
//				// TODO Automatisch generierter Erfassungsblock
//				e.printStackTrace();
//			}
			if(!socket.isClosed()) {
				try {
					socket.close();
					Logger.info("socket is closed");
				} catch (IOException e) {
					// TODO Automatisch generierter Erfassungsblock
					e.printStackTrace();
				}
				server.closeConnection(this);
			}
		}
	}
	
	private class MessageSyndication extends Thread {

		private static final long TIMEOUT = 100;

		private BlockingQueue<String> dataQueue;
		private BufferedWriter writer;
		
		public MessageSyndication(String name, BufferedWriter writer) {
			super(name + " Synd");
			this.writer = writer;
			dataQueue = new LinkedBlockingQueue<String>();
		}

		@Override
		public void run() {
			while (!isInterrupted()) {
				try {
					String next = dataQueue.poll(TIMEOUT, TimeUnit.MILLISECONDS);
					if (next != null) {
						//Logger.info("sending.. "+next);
						writer.write(next + "\n");
						writer.flush();
					}
				} catch (Exception e) {
					// ich unterbreche den thread wenn die connection zu gemacht wird
					break;
				}
			}
		}

		public void send(String data) {
			dataQueue.add(data);
		}
	}
}
