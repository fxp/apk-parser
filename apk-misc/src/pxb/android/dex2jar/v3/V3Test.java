package pxb.android.dex2jar.v3;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.fxp.android.apk.ApkBean;
import org.fxp.tools.FileUtilsExt;

import com.sun.jna.platform.FileUtils;

public class V3Test {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		File[] folders = (new File("/home/fxp/apktest")).listFiles();
		for (File folder : folders) {
			if (!folder.isDirectory())
				continue;
//			File[] files = (new File(folder.getAbsolutePath())).listFiles();
			File[] files=FileUtilsExt.getAllFiles(folder, null);
			for (File file : files) {
				FileWriter filewriter = new FileWriter(new File(args[0]
						+ folder.getName() + ".url"), true);

				ApkBean apk = new ApkBean();
				if(file.isFile()){
					try{
					Dex2Jar.doApk(file.getAbsolutePath(), apk);
					for (String className : apk.classNames) {
						filewriter.write(className + "\r\n");
					}					
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				filewriter.close();
			}
		}
	}

}
