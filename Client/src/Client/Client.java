package Client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client implements Runnable {

	static Socket commsSocket = null;
	static Socket transportSocket = null;
	static BufferedReader serverInputStream = null;
	static PrintStream serverOutputStream = null;
	static BufferedReader keyboardInput = null;
	static DataOutputStream dataOutStream = null;
	static DataInputStream dataInStream = null;
	static String input;
	
	
	public static void main(String[] args) {
		
		try {
			commsSocket = new Socket("localhost", 7272);
			transportSocket = new Socket("localhost", 7373);
			serverInputStream = new BufferedReader(new InputStreamReader(commsSocket.getInputStream()));
			serverOutputStream = new PrintStream(commsSocket.getOutputStream());
			keyboardInput = new BufferedReader(new InputStreamReader(System.in));
			dataInStream = new DataInputStream(transportSocket.getInputStream());
			dataOutStream = new DataOutputStream(transportSocket.getOutputStream());
			new Thread(new Client()).start();
			while(true) {
				input = serverInputStream.readLine();
				System.out.println(input);
				if (input.equals("Dovidjenja")) {
					break;
				}
			}
			receiveFile("Uplata.txt");
			dataInStream.close();
			dataOutStream.close();
			transportSocket.close();
			commsSocket.close();
		} catch (UnknownHostException e) {
			System.out.println("Nepoznat host!");
		} catch (IOException e) {
			System.out.println("Server je pao!");
		}
		
		
	}

	
	public void run() {
		String poruka;

		while (true) {

			try {
				poruka = keyboardInput.readLine();
				
				serverOutputStream.println(poruka);

				if (poruka.equals("4")) {
					break;
				}
			} catch (IOException e) {
				System.out.println("Greska pri unosu!");
			}
		}
		
	}
	
	public static void receiveFile(String fileName){
	        int bytes = 0;
	        FileOutputStream fileOutputStream=null;
			try {
				fileOutputStream = new FileOutputStream(fileName);
				 long size = dataInStream.readLong(); // read file size
			        byte[] buffer = new byte[4 * 1024];
			        while (size > 0 && (bytes = dataInStream.read(buffer, 0,(int)Math.min(buffer.length, size)))!= -1) {
			            // Here we write the file using write method
			            fileOutputStream.write(buffer, 0, bytes);
			            size -= bytes; // read upto file size
			        }
			        // Here we received file
			        System.out.println("Fajl je primljen!");
			        fileOutputStream.close();
			} catch (FileNotFoundException e) {
				System.out.println("Fajl nije pronadjen!-klijent");
			} catch (IOException e) { 
				e.printStackTrace();
			}
	 
	       
	    }

}
