package com.magnesiumm.Server;

import java.io.PrintStream;
import java.util.Scanner;

public class CMD extends Thread {
	
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