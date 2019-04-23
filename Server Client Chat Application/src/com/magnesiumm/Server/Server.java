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