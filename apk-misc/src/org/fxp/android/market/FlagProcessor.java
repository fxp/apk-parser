package org.fxp.android.market;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class FlagProcessor {

	public static String convertFlagToString(int flag) {
		String ret="";
		if((flag&256)!=0){
//			System.out.println("Licence");
			ret+="L";
		}	if((flag&2)!=0){
//			System.out.println("Deleted");
			ret+="D";
		}
		return ret;
	}

	public static void main(String[] args) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(new File(
				"d:\\flags.txt")));
		String line = reader.readLine();
		int id;
		int flag;
		String strFlag="";

		do {
			String[] parts = line.split("\t");
			id = Integer.valueOf(parts[0]);
			flag = Integer.valueOf(parts[1]);
			strFlag=convertFlagToString(flag);
			System.out.println(id+"\t"+strFlag);
			line = reader.readLine();
		} while (line != null);
	}

}
