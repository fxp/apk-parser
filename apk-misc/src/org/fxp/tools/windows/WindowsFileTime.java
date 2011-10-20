package org.fxp.tools.windows;
import java.io.*;
import java.nio.*;
import java.util.Date;

// Java Native Access: http://jna.dev.java.net
// Test with jna-3.2.7
import com.sun.jna.*;
import com.sun.jna.ptr.*;
import com.sun.jna.win32.*;
import com.sun.jna.platform.win32.*;

// http://blog.csdn.net/lovetide
// 2010-07-23

public class WindowsFileTime
{
	public static final int GENERIC_READ = 0x80000000;
	//public static final int GENERIC_WRITE = 0x40000000;	// defined in com.sun.jna.platform.win32.WinNT
	public static final int GENERIC_EXECUTE = 0x20000000;
	public static final int GENERIC_ALL = 0x10000000;

	// defined in com.sun.jna.platform.win32.WinNT
	//public static final int CREATE_NEW = 1;
	//public static final int CREATE_ALWAYS = 2;
	//public static final int OPEN_EXISTING = 3;
	//public static final int OPEN_ALWAYS = 4;
	//public static final int TRUNCATE_EXISTING = 5;

	public interface MoreKernel32 extends Kernel32
	{
		static final MoreKernel32 instance = (MoreKernel32)Native.loadLibrary ("kernel32", MoreKernel32.class, W32APIOptions.DEFAULT_OPTIONS);
		boolean GetFileTime (WinNT.HANDLE hFile, WinBase.FILETIME lpCreationTime, WinBase.FILETIME lpLastAccessTime, WinBase.FILETIME lpLastWriteTime);
		boolean SetFileTime (WinNT.HANDLE hFile, final WinBase.FILETIME lpCreationTime, final WinBase.FILETIME lpLastAccessTime, final WinBase.FILETIME lpLastWriteTime);
	}

	static MoreKernel32 win32 = MoreKernel32.instance;
	//static Kernel32 _win32 = (Kernel32)win32;

	static WinBase.FILETIME _creationTime = new WinBase.FILETIME ();
	static WinBase.FILETIME _lastWriteTime = new WinBase.FILETIME ();
	static WinBase.FILETIME _lastAccessTime = new WinBase.FILETIME ();

	static boolean GetFileTime (String sFileName, Date creationTime, Date lastWriteTime, Date lastAccessTime)
	{
		WinNT.HANDLE hFile = OpenFile (sFileName, GENERIC_READ);	// may be WinNT.GENERIC_READ in future jna version.
		if (hFile == WinBase.INVALID_HANDLE_VALUE) return false;

		boolean rc = win32.GetFileTime (hFile, _creationTime, _lastAccessTime, _lastWriteTime);
		if (rc)
		{
			if (creationTime != null) creationTime.setTime (_creationTime.toLong());
			if (lastAccessTime != null) lastAccessTime.setTime (_lastAccessTime.toLong());
			if (lastWriteTime != null) lastWriteTime.setTime (_lastWriteTime.toLong());
		}
		else
		{
			int iLastError = win32.GetLastError();
			System.out.print ("获取文件时间失败，错误码：" + iLastError + " " + GetWindowsSystemErrorMessage (iLastError));
		}
		win32.CloseHandle (hFile);
		return rc;
	}
	static boolean SetFileTime (String sFileName, final Date creationTime, final Date lastWriteTime, final Date lastAccessTime)
	{
		WinNT.HANDLE hFile = OpenFile (sFileName, WinNT.GENERIC_WRITE);
		if (hFile == WinBase.INVALID_HANDLE_VALUE) return false;

		ConvertDateToFILETIME (creationTime, _creationTime);
		ConvertDateToFILETIME (lastWriteTime, _lastWriteTime);
		ConvertDateToFILETIME (lastAccessTime, _lastAccessTime);

		//System.out.println ("creationTime: " + creationTime);
		//System.out.println ("lastWriteTime: " + lastWriteTime);
		//System.out.println ("lastAccessTime: " + lastAccessTime);

		//System.out.println ("_creationTime: " + _creationTime);
		//System.out.println ("_lastWriteTime: " + _lastWriteTime);
		//System.out.println ("_lastAccessTime: " + _lastAccessTime);

		boolean rc = win32.SetFileTime (hFile, creationTime==null?null:_creationTime, lastAccessTime==null?null:_lastAccessTime, lastWriteTime==null?null:_lastWriteTime);
		if (! rc)
		{
			int iLastError = win32.GetLastError();
			System.out.print ("设置文件时间失败，错误码：" + iLastError + " " + GetWindowsSystemErrorMessage (iLastError));
		}
		win32.CloseHandle (hFile);
		return rc;
	}
	static void ConvertDateToFILETIME (Date date, WinBase.FILETIME ft)
	{
		if (ft != null)
		{
			long iFileTime = 0;
			if (date != null)
			{
				iFileTime = WinBase.FILETIME.dateToFileTime (date);
				ft.dwHighDateTime = (int)((iFileTime >> 32) & 0xFFFFFFFFL);
				ft.dwLowDateTime = (int)(iFileTime & 0xFFFFFFFFL);
			}
			else
			{
				ft.dwHighDateTime = 0;
				ft.dwLowDateTime = 0;
			}
		}
	}

	static WinNT.HANDLE OpenFile (String sFileName, int dwDesiredAccess)
	{
		WinNT.HANDLE hFile = win32.CreateFile (
			sFileName,
			dwDesiredAccess,
			0,
			null,
			WinNT.OPEN_EXISTING,
			0,
			null
			);
		if (hFile == WinBase.INVALID_HANDLE_VALUE)
		{
			int iLastError = win32.GetLastError();
			System.out.print ("	打开文件失败，错误码：" + iLastError + " " + GetWindowsSystemErrorMessage (iLastError));
		}
		return hFile;
	}
	static String GetWindowsSystemErrorMessage (int iError)
	{
		char[] buf = new char[255];
		CharBuffer bb = CharBuffer.wrap (buf);
		//bb.clear ();
		//PointerByReference pMsgBuf = new PointerByReference ();
		int iChar = win32.FormatMessage (
				WinBase.FORMAT_MESSAGE_FROM_SYSTEM
					//| WinBase.FORMAT_MESSAGE_IGNORE_INSERTS
					//|WinBase.FORMAT_MESSAGE_ALLOCATE_BUFFER
					,
				null,
				iError,
				0x0804,
				bb, buf.length,
				//pMsgBuf, 0,
				null
			);
		//for (int i=0; i<iChar; i++)
		//{
		//	System.out.print (" ");
		//	System.out.print (String.format("%02X", buf[i]&0xFFFF));
		//}
		bb.limit (iChar);
		//System.out.print (bb);
		//System.out.print (pMsgBuf.getValue().getString(0));
		//win32.LocalFree (pMsgBuf.getValue());
		return bb.toString ();
	}

	public static void main (String[] args) throws Exception
	{
		if (args.length == 0)
		{
			System.out.println ("获取 Windows 的文件时间（创建时间、最后修改时间、最后访问时间）");
			System.out.println ("用法：");
			System.out.println ("	java -cp .;..;jna.jar;platform.jar WindowsFileTime [文件名1] [文件名2]...");
			return;
		}

		boolean rc;
		java.sql.Timestamp ct = new java.sql.Timestamp(0);
		java.sql.Timestamp wt = new java.sql.Timestamp(0);
		java.sql.Timestamp at = new java.sql.Timestamp(0);

		for (String sFileName : args)
		{
			System.out.println ("文件 " + sFileName);

			rc = GetFileTime (sFileName, ct, wt, at);
			if (rc)
			{
				System.out.println ("	创建时间：" + ct);
				System.out.println ("	修改时间：" + wt);
				System.out.println ("	访问时间：" + at);
			}
			else
			{
				//System.out.println ("GetFileTime 失败");
			}


			//wt.setTime (System.currentTimeMillis());
			wt = java.sql.Timestamp.valueOf("2010-07-23 00:00:00");
			rc = SetFileTime (sFileName, null, wt, null);
			if (rc)
			{
				System.out.println ("SetFileTime (最后修改时间) 成功");
			}
			else
			{
				//System.out.println ("SetFileTime 失败");
			}
		}
	}
}