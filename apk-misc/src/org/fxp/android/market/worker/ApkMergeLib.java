package org.fxp.android.market.worker;

import java.io.File;
import java.io.IOException;

import org.fxp.tools.FileUtilsExt;

public class ApkMergeLib {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		File srcLib = new File("d:\\apkTemp");
		File dstLib = new File("d:\\apkErr");

		File[] files = FileUtilsExt.getAllFiles(srcLib, null);
		for (File file : files) {
			File dstFile = new File(dstLib.getPath() + "\\" + file.getName());
			if (dstFile.isFile()) {
				// Check whether they are same
				if (FileUtilsExt.compareFile(dstFile.getAbsolutePath(),
						file.getAbsolutePath())) {
					System.out.println("SAMEFILE "+file.getAbsolutePath());
					FileUtilsExt.forceDelete(file);
				} else {
					int count=0;
					String filename;
					do{
						filename=dstFile.getAbsoluteFile()+".CONFLICT."+count;
						count++;
					}while((new File(filename)).isFile());
					System.out.println("CONFLICT "+filename);
					FileUtilsExt.movefile(file.getAbsolutePath(),filename);						
				}
			}else{
				System.out.println("NEWFILE "+file.getName());
				FileUtilsExt.movefile(file.getAbsolutePath(), dstLib.getPath()
					+ "\\" + file.getName());
			}
		}
	}

}
