package com.magnesiumm.Client;

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