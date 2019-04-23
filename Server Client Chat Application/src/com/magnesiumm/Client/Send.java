package com.magnesiumm.Client;

import java.io.PrintStream;
import java.util.Scanner;

public class Send extends Thread {
	
	public void run() {
		try {
			
			Client.printStream = new PrintStream(Client.socket.getOutputStream());
			Client.printStream.println(Client.username + "," + Client.password);
			String output = "";
				while(true) {
					Scanner scan = new Scanner(System.in);
					output = scan.nextLine();
					send(output);
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void send(String message) {
		Client.printStream.println(message);
	}
}
