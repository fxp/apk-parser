package org.fxp.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Lala{
	String a;
}

public class TinyStuffs {

	static ArrayList<Integer> numList = new ArrayList<Integer>();
	static int lineNum=0;

	public static void foo(String a,Lala b){
		a="test";
		b.a="test";
	}
	public static void main(String[] args) {
		String a="123";
		Lala b = new Lala();
		b.a="123";
		foo(a,b);
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					"doAllApkErrLog.txt"));
			String allText = null;
			String regex = "ID\\s(\\d+?)\\s";
			Pattern p = Pattern.compile(regex);
			int cursor = 10000;
			while ((allText = reader.readLine()) != null) {
				lineNum++;
				Matcher matcher = p.matcher(allText);
				if (matcher.find()) {
					while(cursor!=Integer.valueOf(matcher.group(1))){
						numList.add(cursor);
						cursor++;
					}
					cursor++;
					if (cursor > 20000)
						break;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(numList.size()+"/"+lineNum);
		try {
			BufferedWriter writer=new BufferedWriter(new FileWriter("ErrApkID"));
			for(int errNum:numList){
				System.out.println(errNum);
				writer.write(errNum+"\r\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
