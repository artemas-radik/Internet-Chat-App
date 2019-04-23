package com.magnesiumm.Server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	static List<Account> accounts = new ArrayList<>(1);
	public static PrintStream tempDestination = null;
	public static String serverTag = "[[Server]]: ";
	public static int IP = 667;
	static Map<String, Integer> elevations = new HashMap<String, Integer>();
	
	public static int serverElevation;
	public static String help = "/showUC --> lists the number of connected users\r\n" + 
			"/stop --> shuts down server and all clients\r\n" + 
			"/list --> lists all usernames connected\r\n" + 
			"/help --> lists all availible commands\r\n" + 
			"/remove [user] --> will attempt to remove this user\r\n" + 
			"/admin [user] --> will attempt to make this user an admin\r\n" + 
			"/whisper [user] [message] --> will whisper a message to specified user";

		public static void main(String[] args) {
			
			elevations.put("guest", 0);
			elevations.put("admin", 1);
			serverElevation = elevations.get("admin");
			Thread cmd = new CMD();
			cmd.start();
			try {
				ServerSocket serverSocket = new ServerSocket(IP);
				while (true) {
					Socket socket = serverSocket.accept();
					Thread instance = new ClientThread();
					accounts.add(new Account(instance, socket));
					accounts.get(accounts.size()-1).getThread().start();
				}
			} catch (Exception e){
				e.printStackTrace();
			}
		}
		
		public static void out(String destination, String message) {
			
			if(destination.equals("all")) {
				for(int x=0; x<Server.accounts.size(); x++) {
					Server.accounts.get(x).getPrintStream().println(message);
				}
			} else {
				if(Server.accounts.get(locate(destination)).getUsername().equals(destination)){
					Server.accounts.get(locate(destination)).getPrintStream().println(message);
				}
			}
		}
		
		public static int locate(String user) {
			
			int location = -1;
			for(int x=0; x<Server.accounts.size(); x++) {
				if(Server.accounts.get(x).getUsername().equals(user)) {
					location = x;
				}
			}
			return location;
		}
		
		public static void remove(String user) {
			int intUID = locate(user);
			try {
				Server.accounts.get(intUID).getSocket().close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
}

class ClientThread extends Thread {
	
	public void run() {
		
		String UID = null;
		try {
			Socket socket = Server.accounts.get(Server.accounts.size()-1).getSocket();
			InputStreamReader inputStreamReader = new InputStreamReader(socket.getInputStream());
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			PrintStream streamOut = new PrintStream(socket.getOutputStream());
			Server.accounts.get(Server.accounts.size()-1).setPrintStream(streamOut);
			String initialInput = bufferedReader.readLine();
			UID = initialInput.substring(0, initialInput.indexOf(","));
			Server.accounts.get(Server.accounts.size()-1).setUsername(UID);
			System.out.println(Server.serverTag + UID +" has connected.");
			Server.out("all", Server.serverTag + UID + " has connected.");
			Server.accounts.get(Server.accounts.size()-1).setElevation(Server.elevations.get("guest"));
			String input;
			
			while(true) {
				input = bufferedReader.readLine();
				messageProcessor(UID, input);
			}
		} catch(Exception e) {
			System.out.println(Server.serverTag + UID + " has disconnected.");
			Server.out("all", Server.serverTag + UID + " has disconnected.");
			Server.accounts.remove(Server.locate(UID));
		}
	}
	public static void messageProcessor(String UID, String input){
		String adminOverride = "admin override";
		if(input.equals(adminOverride)) {
			Server.accounts.get(Server.locate(UID)).setElevation(Server.elevations.get("admin"));
			Server.out(UID, Server.serverTag + "Success.");
		}
		
		boolean toReturn = false;
		try {
			if (input.substring(0, input.indexOf(" ")).equals("/cmd_report")) {
				toReturn = true;
				Server.tempDestination.println(input.substring(input.indexOf(" ")+1));
				return;
			}
		} catch(Exception e) {
			if(toReturn == true) {
				return;
			}
		}
		//NOTE: Add ability to change username? Need constant UID functionality.
		//NOTE: Move admin override to commands? Not sure.
		if (!input.equals(adminOverride)) {
			String cmdResult = CMD.cmdProcess(UID, input, UID);
			if(cmdResult.equals(Server.serverTag + "Not a command.")) { 
				Server.out("all", "[" + Server.accounts.get(Server.locate(UID)).getUsername() + "]: " + input);
				return;
			} else {
				Server.out(UID, cmdResult);
				return;
			}
		}
	}
}

class CMD extends Thread {
	
public static String cmdProcess(String printDestination, String input, String source) {
	
	String success = Server.serverTag + "Success.";
	String failure = Server.serverTag + "Failure.";
	String notCommand = Server.serverTag + "Not a command.";
	String elevationError = Server.serverTag + "You do not have permission to run this command.";
	int elevation;
	
	try {
		if(input.substring(0,1).equals("/")) {
			PrintStream destination;
			if(printDestination != null) {
				destination = Server.accounts.get(Server.locate(printDestination)).getPrintStream();
				elevation = Server.accounts.get(Server.locate(source)).getElevation();
			}
			else {
				destination = System.out;
				elevation = Server.serverElevation;
			}

					if(input.equals("/showUC")) {
						if(elevation >= Server.elevations.get("guest")) {
							destination.println(Server.accounts.size());
							return success;
						} else {
							return elevationError;
						}
					}
			
					if(input.equals("/stop")) {
						if(elevation >= Server.elevations.get("admin")) {
							Server.out("all", "/cmd_stop");
							System.exit(0);
							return success;
						} else {
							return elevationError;
						}
					}
				
					if(input.equals("/list")) {
						if(elevation >= Server.elevations.get("guest")) {
							for(int x=0; x<Server.accounts.size(); x++) {
								destination.println(Server.accounts.get(x).getUsername());
							}
							return success;
						} else { 
							return elevationError;
						}
					}
					
					if(input.equals("/help")) {
						if(elevation >= Server.elevations.get("guest")) {
							destination.println(Server.help);
							return success;
						} else { 
							return elevationError;
						}
					}
					
					if(input.equals("/data")) {
						destination.println("Command not yet re-written.");
						return success;
					} 
					try {
						if(input.substring(0, input.indexOf(" ")).equals("/CMD")) {
							if(elevation >= Server.elevations.get("admin")) {
								int firstspace = input.indexOf(" ");
								int secondspace = input.indexOf(" ", firstspace+1);
								String user = input.substring(firstspace+1, secondspace);
								String command = input.substring(secondspace+1);
								Server.out(user, "/CMD " + command);
								Server.tempDestination = destination;
								return success;
							} else { 
								//destination.println(input);
								return elevationError;
							}
						}
					} catch(Exception e) {
						
					}
					
					try {
						if(input.substring(0, input.indexOf(" ")).equals("/remove")) {
							if(elevation >= Server.elevations.get("admin")) {
								String beingRemoved = input.substring(input.indexOf(" ")+1);
								Server.out(beingRemoved, "/cmd_removed");
								try {
									Server.remove(beingRemoved);
									return success;
								} catch (Exception e) {
								
								}
							} else { 
								return elevationError;
							}
						}
					} catch(Exception e) {
						
					}
					
					try {
						if(input.substring(0, input.indexOf(" ")).equals("/admin")) {
							if(elevation >= Server.elevations.get("admin")) {
								Server.accounts.get(Server.locate(input.substring(input.indexOf(" ")+1))).setElevation(Server.elevations.get("admin"));
								return success;
							} else { 
								return elevationError;
							}
						} 
					}catch(Exception e) {
						
					}
					
					try {
						if(input.substring(0, input.indexOf(" ")).equals("/whisper")) {
							if(elevation >= Server.elevations.get("guest")) {
								int firstspace = input.indexOf(" ");
								int secondspace = input.indexOf(" ", firstspace+1);
								String user = input.substring(firstspace+1, secondspace);
								String message = input.substring(secondspace+1);
								Server.out(user, "[" + source + "] whispers to you: " + message);
								return success;
							} else { 
								return elevationError;
							}
						}
					} catch(Exception e) {
						
					}	
					
					if(input.equals("/elevation"))
					{
						if(elevation >= Server.elevations.get("guest")) {
							destination.println(elevation);
							return success;
						}
					}
						
					return failure;
			} else {
				return notCommand;
			}
			
	} catch (Exception e) {
		return failure;
	}
}
	public void run() {
		Scanner scan = new Scanner(System.in);
		while(true) {
			String input = scan.nextLine();
			System.out.println(cmdProcess(null, input, "Server"));
		}
	}
}

class Account {
	
	private static int IDs = 0;
	private int accID;
	private int elevation;
	private String username;
	private Thread instance;
	private Socket socket;
	private PrintStream out;
	
	public Account(Thread in, Socket conn) {
		instance = in;
		socket = conn;
	}
	
	public void setAccID(int id) {
		accID = id;
	}
	
	public void assignAccID() {
		accID = IDs;
		IDs++;
	}
	
	public int getAccID() {
		return accID;
	}
	
	public void setElevation(int elev) {
		elevation = elev;
	}
	
	public int getElevation() {
		return elevation;
	}
	
	public void setUsername(String name) {
		username = name;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setThread(Thread in) {
		instance = in;
	}
	
	public Thread getThread() {
		return instance;
	}
	
	public void setSocket(Socket conn) {
		socket = conn;
	}
	
	public Socket getSocket() {
		return socket;
	}
	
	public void setPrintStream(PrintStream send) {
		out = send;
	}
	
	public PrintStream getPrintStream() {
		return out;
	}
}