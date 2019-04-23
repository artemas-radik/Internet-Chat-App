package com.magnesiumm.Server;

import java.io.PrintStream;
import java.net.Socket;

public class Account {
	
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