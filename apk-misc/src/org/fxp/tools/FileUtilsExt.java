package org.fxp.tools;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class FileUtilsExt {
	public static List<File> getAllFiles(String fatherDir, FilenameFilter filter) {
		File dirFile=new File(fatherDir);
		ArrayList<File> fileArray = new ArrayList<File>();
		if (dirFile.isDirectory()) {
			File[] specFiles = null;
			File[] allFiles = dirFile.listFiles();
			if (filter != null)
				specFiles = dirFile.listFiles(filter);
			else
				specFiles = dirFile.listFiles();
			for (File file : specFiles)
				fileArray.add(file);

			for (File file : allFiles) {
				if (file.isDirectory()) {
					File[] subFiles = getAllFiles(file, filter);
					for (File subFile : subFiles)
						fileArray.add(subFile);
				}
			}
		} else
			return new ArrayList<File>();
		return fileArray;
	}
	public static File[] getAllFiles(File fatherDir, FilenameFilter filter) {
		ArrayList<File> fileArray = new ArrayList<File>();
		if (fatherDir.isDirectory()) {
			File[] specFiles = null;
			File[] allFiles = fatherDir.listFiles();
			if (filter != null)
				specFiles = fatherDir.listFiles(filter);
			else
				specFiles = fatherDir.listFiles();
			for (File file : specFiles){
				if(file.isFile())
					fileArray.add(file);
			}

			for (File file : allFiles) {
				if (file.isDirectory()) {
					File[] subFiles = getAllFiles(file, filter);
					for (File subFile : subFiles){
						if(subFile.isFile())
							fileArray.add(subFile);
					}
				}
			}
		} else
			return null;
		return fileArray.toArray(new File[fileArray.size()]);
	}

	public static boolean forceDelete(File f) {
		boolean result = false;
		int tryCount = 0;
		while (!result && tryCount++ < 10) {
			System.gc();
			result = f.delete();
		}
		return result;
	}

	public static boolean movefile(String srFile, String dtFile) {
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

	public static void copyfile(String srFile, String dtFile) {
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

	public static final void copyInputStream(InputStream in, OutputStream out)
			throws IOException {
		byte[] buffer = new byte[1024];
		int len;
		while ((len = in.read(buffer)) >= 0)
			out.write(buffer, 0, len);

		in.close();
		out.close();
	}

	public static boolean compareFile(String srcFile, String dstFile) {
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

	public static byte[] createChecksum(String filename) throws Exception {
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

	public static byte[] readFileToBytes(String file) {
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
	 * @created 2002-05-02 by Alexander Feldman
	 * 
	 */
	public static void writeToFile(String fileName, InputStream iStream)
			throws IOException {
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
	 * @refactored 2002-05-02 by Alexander Feldman - moved from OMDBlob.
	 * 
	 */
	public static void writeToFile(String fileName, InputStream iStream,
			boolean createDir) throws IOException {
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
	protected static void close(InputStream iStream, OutputStream oStream)
			throws IOException {
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
}
