package generators;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class Utilities {

	public static class Data {
		
		String name;
		int value;
		
		public Data (String s, int i) {
			this.name = s;
			this.value = i;
		}
		
		public String toString() {
			return name;
		}
		
		public int getValue() { return value; }
	}
	
	public static ArrayList<Data> loadFile(File f) {
		ArrayList<Data> temp = new ArrayList<>();
		
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(new DataInputStream(
					new FileInputStream(f))));

			String strLine;

			while ((strLine = br.readLine()) != null) {
				
				String[] arr = strLine.split(",");

				temp.add(new Data(arr[0], Integer.parseInt(arr[1].trim())));
				
			}

			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return temp;
	}
	
	public static ArrayList<Data> loadFile(URL f) {
		ArrayList<Data> temp = new ArrayList<>();
		
		BufferedReader br;
		try {
			br = new BufferedReader(
					new InputStreamReader(
							f.openStream()));

			String strLine;

			while ((strLine = br.readLine()) != null) {
				
				if(strLine.equals("")) continue;
				String[] arr = strLine.split(",");

				temp.add(new Data(arr[0], Integer.parseInt(arr[1].trim())));
				
			}

			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return temp;
	}
	
	public static ArrayList<String> loadFileNoArray(URL f) {
		ArrayList<String> temp = new ArrayList<>();
		
		BufferedReader br;
		try {
			br = new BufferedReader(
					new InputStreamReader(
							f.openStream()));

			String strLine;

			while ((strLine = br.readLine()) != null) {
				
				if(strLine.equals("")) continue;

				temp.add(strLine);
				
			}

			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return temp;
	}

}
