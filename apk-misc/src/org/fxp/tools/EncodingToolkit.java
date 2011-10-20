package org.fxp.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

public class EncodingToolkit {
	public static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
				|| ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
				|| ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
				|| ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
				|| ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}
		return false;
	}

	public static String convertStreamToString(InputStream is)
			throws IOException {
		if (is != null) {
			Writer writer = new StringWriter();

			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(new InputStreamReader(is,
						"UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} finally {
				is.close();
			}
			return writer.toString();
		} else {
			return "";
		}
	}
	
	public static String getEncoding(String str) {   
        String encode = "GB2312";   
       try {   
           if (str.equals(new String(str.getBytes(encode), encode))) {   
                String s = encode;   
               return s;   
            }   
        } catch (Exception exception) {   
        }   
        encode = "ISO-8859-1";   
       try {   
           if (str.equals(new String(str.getBytes(encode), encode))) {   
                String s1 = encode;   
               return s1;   
            }   
        } catch (Exception exception1) {   
        }   
        encode = "UTF-8";   
       try {   
           if (str.equals(new String(str.getBytes(encode), encode))) {   
                String s2 = encode;   
               return s2;   
            }   
        } catch (Exception exception2) {   
        }   
        encode = "GBK";   
       try {   
           if (str.equals(new String(str.getBytes(encode), encode))) {   
                String s3 = encode;   
               return s3;   
            }   
        } catch (Exception exception3) {   
        }   
       return "";   
    }  
	
}
