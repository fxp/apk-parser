package com.iw.core.common;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;

import org.apache.commons.io.IOUtils;

public class FileUtil {
	public static String RUN_AS_JAR = "RUN_AS_JAR";

	public static Class myClass = FileUtil.class;

	public static String DEFAULT_ENCODING = "utf8";

	public synchronized static String getTempFile(String prefix, String postfix)
			throws IOException {
		if (prefix == null)
			prefix = "";
		if (postfix == null)
			postfix = "";

		File tmpFile = File.createTempFile(prefix, postfix);
		tmpFile.delete();

		return tmpFile.getPath();
	}

	public synchronized static String getCurrentAbsolutePath() {
		// if (System.getenv().get(RUN_AS_JAR) == null) {
		// return myClass.getResource("").getPath();
		// }
		return (new File("")).getAbsolutePath();
	}

	public static class OnlyExt implements FilenameFilter {
		String ext = "";

		public OnlyExt(String ext) {
			this.ext = ext;
		}

		@Override
		public boolean accept(File dir, String name) {
			return name.endsWith(ext);
		}

	}

	public synchronized static boolean forceDelete(File f) {
		boolean result = false;
		int tryCount = 0;
		while (!result && tryCount++ < 10) {
			System.gc();
			result = f.delete();
		}
		return result;
	}

	public synchronized static boolean movefile(String srFile, String dtFile) {
		copyfile(srFile, dtFile);
		File f = new File(dtFile);
		if (f.exists()) {
			f = new File(srFile);
			if (f.delete())
				return true;
			forceDelete(f);
		}
		return true;
	}

	public static void copyFile(File sourceFile, File destFile)
			throws IOException {
		if (!destFile.exists()) {
			destFile.createNewFile();
		}

		FileChannel source = null;
		FileChannel destination = null;
		try {
			source = new FileInputStream(sourceFile).getChannel();
			destination = new FileOutputStream(destFile).getChannel();
			destination.transferFrom(source, 0, source.size());
		} finally {
			if (source != null) {
				source.close();
			}
			if (destination != null) {
				destination.close();
			}
		}
	}

	public synchronized static void copyfile(String srFile, String dtFile) {
		try {
			File f1 = new File(srFile);
			File f2 = new File(dtFile);
			InputStream in = new FileInputStream(f1);

			// For Append the file.
			// OutputStream out = new FileOutputStream(f2,true);

			// For Overwrite the file.
			OutputStream out = new FileOutputStream(f2);

			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		} catch (FileNotFoundException ex) {
			System.out
					.println(ex.getMessage() + " in the specified directory.");
			System.exit(0);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public synchronized static final void copyInputStream(InputStream in,
			OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int len;
		while ((len = in.read(buffer)) >= 0)
			out.write(buffer, 0, len);

		in.close();
		out.close();
	}

	public synchronized static boolean compareFile(String srcFile,
			String dstFile) {
		boolean rb = false;
		try {
			byte[] chk1 = createChecksum(srcFile);
			byte[] chk2 = createChecksum(dstFile);
			if (new String(chk1).equals(new String(chk2)))
				rb = true;
		} catch (Exception e) {
			e.printStackTrace();
			rb = false;
		}
		return rb;
	}

	public synchronized static byte[] createChecksum(String filename)
			throws Exception {
		InputStream fis = new FileInputStream(filename);

		byte[] buffer = new byte[1024];
		MessageDigest complete = MessageDigest.getInstance("SHA1");
		int numRead;
		do {
			numRead = fis.read(buffer);
			if (numRead > 0) {
				complete.update(buffer, 0, numRead);
			}
		} while (numRead != -1);
		fis.close();
		return complete.digest();
	}

	public synchronized static byte[] readFileToBytes(String file) {
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);

			byte[] tmp = new byte[1024];
			byte[] data = null;
			int sz, len = 0;

			while ((sz = fis.read(tmp)) != -1) {
				if (data == null) {
					len = sz;
					data = tmp;
				} else {
					byte[] narr;
					int nlen;

					nlen = len + sz;
					narr = new byte[nlen];
					System.arraycopy(data, 0, narr, 0, len);
					System.arraycopy(tmp, 0, narr, len, sz);
					data = narr;
					len = nlen;
				}
			}
			if (len != data.length) {
				byte[] narr = new byte[len];

				System.arraycopy(data, 0, narr, 0, len);
				data = narr;
			}
			return data;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Calls writeToFile with createDir flag false.
	 * 
	 * @see writeToFile(String fileName, InputStream iStream, boolean createDir)
	 * 
	 */
	public synchronized static void writeToFile(String fileName,
			InputStream iStream) throws IOException {
		writeToFile(fileName, iStream, false);
	}

	/**
	 * Writes InputStream to a given <code>fileName<code>.
	 * And, if directory for this file does not exists,
	 * if createDir is true, creates it, otherwice throws OMDIOexception.
	 * 
	 * @param fileName
	 *            - filename save to.
	 * @param iStream
	 *            - InputStream with data to read from.
	 * @param createDir
	 *            (false by default)
	 * @throws IOException
	 *             in case of any error.
	 * 
	 */
	public synchronized static void writeToFile(String fileName,
			InputStream iStream, boolean createDir) throws IOException {
		String me = "FileUtils.WriteToFile";
		if (fileName == null) {
			throw new IOException(me + ": filename is null");
		}
		if (iStream == null) {
			throw new IOException(me + ": InputStream is null");
		}

		File theFile = new File(fileName);

		// Check if a file exists.
		if (theFile.exists()) {
			String msg = theFile.isDirectory() ? "directory" : (!theFile
					.canWrite() ? "not writable" : null);
			if (msg != null) {
				throw new IOException(me + ": file '" + fileName + "' is "
						+ msg);
			}
		}

		// Create directory for the file, if requested.
		if (createDir && theFile.getParentFile() != null) {
			theFile.getParentFile().mkdirs();
		}

		// Save InputStream to the file.
		BufferedOutputStream fOut = null;
		try {
			fOut = new BufferedOutputStream(new FileOutputStream(theFile));
			byte[] buffer = new byte[32 * 1024];
			int bytesRead = 0;
			while ((bytesRead = iStream.read(buffer)) != -1) {
				fOut.write(buffer, 0, bytesRead);
			}
		} catch (Exception e) {
			throw new IOException(me + " failed, got: " + e.toString());
		} finally {
			close(iStream, fOut);
		}
	}

	/**
	 * Closes InputStream and/or OutputStream. It makes sure that both streams
	 * tried to be closed, even first throws an exception.
	 * 
	 * @throw IOException if stream (is not null and) cannot be closed.
	 * 
	 */
	protected synchronized static void close(InputStream iStream,
			OutputStream oStream) throws IOException {
		try {
			if (iStream != null) {
				iStream.close();
			}
		} finally {
			if (oStream != null) {
				oStream.close();
			}
		}
	}

	public synchronized static String getFileContent(String filePath,
			String encode) {
		String ret = null;
		try {
			FileInputStream fstream = new FileInputStream(filePath);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			StringBuffer sb = new StringBuffer();
			String strLine;
			// Read File Line By Line
			while ((strLine = br.readLine()) != null) {
				sb.append(strLine + "\r\n");
			}
			in.close();
			ret = sb.toString();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
		return ret;
	}

	public static String getStreamString(InputStream in, String encode) {
		String ret = null;
		try {
			StringWriter writer = new StringWriter();
			if (encode != null)
				IOUtils.copy(in, writer, encode);
			else
				IOUtils.copy(in, writer, DEFAULT_ENCODING);
			ret = writer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
}
