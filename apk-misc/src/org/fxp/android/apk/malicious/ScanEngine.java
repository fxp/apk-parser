package org.fxp.android.apk.malicious;

import java.util.ArrayList;

public class ScanEngine {
	ArrayList<Virus> virusList=new ArrayList<Virus>();
	
	public void init(){
		virusList.add(new VirusGenimi());
	}
	
	public void scan(String apkPath){
		for(Virus virus:virusList){
			if(virus.isExist(apkPath)){
				System.out.println(virus.virusName+"\t"+apkPath);
			}
		}
	}
}
