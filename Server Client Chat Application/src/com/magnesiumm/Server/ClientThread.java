package com.magnesiumm.Server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class ClientThread extends Thread {
	
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

