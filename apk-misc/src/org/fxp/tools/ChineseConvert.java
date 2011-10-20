package org.fxp.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class ChineseConvert {
	StringBuffer fantiBuffer;
	StringBuffer jiantiBuffer;
	File file;
	BufferedReader bufferReader;
	FileReader fileReader;
	String line;
	private static String SIMPLE_CHN_LIB="SimplizedChineseLib";
	private static String TRAD_CHN_LIB="TraditionalChineseLib";
	public HashMap T2S = new HashMap();
	public HashMap S2T = new HashMap();

	/**
	 * @param args
	 * @throws IOException 
	 */
	public ChineseConvert() throws IOException {
		formMap(TRAD_CHN_LIB, SIMPLE_CHN_LIB);
	};

	public StringBuffer getDictionary(String path) throws IOException {
		StringBuffer readAll = new StringBuffer();
			file = new File(path);
			fileReader = new FileReader(file);
			bufferReader = new BufferedReader(new FileReader(file));
			while ((line = bufferReader.readLine()) != null) {
				readAll.append(line);
			}
		return readAll;
	}

	public void formMap(String pathOfFanti,String pathOfJianti) throws IOException{
		jiantiBuffer=getDictionary(pathOfJianti);
		fantiBuffer=getDictionary(pathOfFanti);
		int k=jiantiBuffer.length();
		Character fan=null;
		Character jian=null;
		for(int i=0;i< fantiBuffer.length();i++){
		fan=fantiBuffer.charAt(i);
		jian=jiantiBuffer.charAt(i);
		T2S.put(fan, jian);
		S2T.put(jian, fan);
		}
	}

	public void translate(StringBuffer from, String type) {
		int i = from.length();
		char come;
		if (type.equals("fan2Jian")) {
			for (int k = 0; k < i; k++) {
				come = from.charAt(k);
				if (T2S.containsKey(come)) {
					from.setCharAt(k, (Character) T2S.get(come));
				}
			}
		} else {
			for (int k = 0; k < i; k++) {
				come = from.charAt(k);
				if (S2T.containsKey(come)) {
					from.setCharAt(k, (Character) S2T.get(come));
				}
			}
		}
	}

	public static void main(String[] args) {
/*		String str1="澳大利亞";
		ChineseConvert tran = new ChineseConvert();
		StringBuffer from = new StringBuffer();
		from.append(str1);
		tran.translate(from, "fan2Jian");
		if(str1.equals(from.toString()))
			System.out.println("YES");
		System.out.println("NO");
*/	}
}