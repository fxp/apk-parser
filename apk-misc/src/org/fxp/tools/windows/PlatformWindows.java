package org.fxp.tools.windows;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.StringTokenizer;

public class PlatformWindows {

	public static String getFileCreateDate(File _file) {
		File file = _file;
		try {
			Process ls_proc = Runtime.getRuntime().exec(
					"cmd.exe /c dir \"" + file.getAbsolutePath() + "\" /tc");
			DataInputStream in = new DataInputStream(ls_proc.getInputStream());
			for (int i = 0; i < 5; i++) {
				in.readLine();
			}
			String stuff = in.readLine();
			StringTokenizer st = new StringTokenizer(stuff);
			String dateC = st.nextToken();
			String time = st.nextToken();
			in.close();
			return dateC;
		} catch (Exception e) {
			return null;
		}
	}

	static Runtime ru = Runtime.getRuntime();

	public static String executeFile(String exeFilePath) throws IOException,
			InterruptedException {
		String tmp = null;
		String tmp2 = null;
		String[] envp = { "LANG=zh_CN.UTF-8" };
		Process p1 = ru.exec(exeFilePath, envp);
		BufferedReader br = new BufferedReader(new InputStreamReader(
				p1.getInputStream()));
		while ((tmp = br.readLine()) != null)
			tmp2 += tmp;
		br.close();
		p1.waitFor();
		return tmp2;
	}

}
