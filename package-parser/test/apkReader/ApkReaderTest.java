package apkReader;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

public class ApkReaderTest {

	public static String APK_FOLDER = "";
	public static String SUCCESS_FOLDER = "";
	public static String FAILED_FOLDER = "";
	public static String TODO_FOLDER = "";

	public static Hashtable<Integer, List<String>> errorApks = new Hashtable<Integer, List<String>>();

	public int testApk(String apkPath) {
		ApkReader apkReader = new ApkReader();
		ApkInfo apkInfo = new ApkInfo();
		String ret = "";
		int errCode = apkReader.read(apkPath, apkInfo);
		for(ArrayList<String> values:apkInfo.resStrings.values()){
			for(String str:values){
				if (!str.equals("0"))
					try {
						ret += new String(intToByteArray(str.hashCode()),
								"utf8");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}						
			}
		}
		
		apkInfo=null;
		apkReader=null;
		return errCode;
	}

	public static byte[] intToByteArray(int value) {
		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			int offset = (b.length - 1 - i);
			b[i] = (byte) ((value >>> offset) & 0xff);
		}
		return b;
	}
	public static void main(String[] args) throws IOException {
		APK_FOLDER = "d:\\apktest";

		for (int i = 0; i < 20; i++) {
			errorApks.put(i, new ArrayList<String>());
		}

		ApkReaderTest test = new ApkReaderTest();

		File[] files = (new File(APK_FOLDER)).listFiles();
		int count = 0;
		int errCode;
		for (File file : files) {
			String fileName = file.getAbsolutePath();
			System.out.println((count++) + "." + fileName);

			errCode = test.testApk(fileName);

			errorApks.get(errCode).add(file.getAbsolutePath());
			if (ApkInfo.FINE != errCode) {
//				boolean success = file.renameTo(new File(SUCCESS_FOLDER
//						+ file.getName() + file.getName().hashCode()));
//				if (!success) {
					System.err.println(fileName);
//				}
			}
		}
		printResult();
	}

	public static void printResult() {
		Set<Integer> errs = errorApks.keySet();
		for (int err : errs) {
			List<String> apks = errorApks.get(err);
			System.out.println(err + "(" + apks.size() + "):");
			for (String apk : apks) {
				System.out.println(apk);
			}
			System.out.println();
		}
	}

}
