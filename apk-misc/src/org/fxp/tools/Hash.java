package org.fxp.tools;

import java.io.*;
import java.net.*;
import java.util.*;
import java.security.*;

public class Hash {
    
    
    public void Hash() {
        // Construct.
    }
    public static void main(String args[]) {
    	
    }
    
    public static MessageDigest createFileDigest(String filename, String method) {
        try {
            try {
                InputStream fis =  new FileInputStream(filename);
                
                byte[] buffer = new byte[1024];
                MessageDigest complete = MessageDigest.getInstance(method );
                int numRead = 0;
                while (numRead != -1) {
                    numRead = fis.read(buffer);
                    if (numRead > 0) {
                        complete.update(buffer, 0, numRead);
                    }
                }
                fis.close();
                return complete;
            }
            catch(NoSuchAlgorithmException nsae) {
                return null;
            }
            // Do nothing for it.
        }
        catch(IOException e ) {
            return null;
        }
        // Do nothing for it.
        
    }
    
    
    public static String createFileHash(String filename, String method) {
        try {
            try {
                InputStream fis =  new FileInputStream(filename);
                
                byte[] buffer = new byte[1024];
                MessageDigest complete = MessageDigest.getInstance(method );
                int numRead = 0;
                while (numRead != -1) {
                    numRead = fis.read(buffer);
                    if (numRead > 0) {
                        complete.update(buffer, 0, numRead);
                    }
                }
                fis.close();
                return asHex(complete.digest());
            }
            catch(NoSuchAlgorithmException nsae) {
                return null;
            }
            // Do nothing for it.
        }
        catch(IOException e ) {
            return null;
        }
        // Do nothing for it.
        
    }
    
    public static byte[] createHash(String text, String method) {
        try {
            byte[] b = text.getBytes();
            MessageDigest algorithm = MessageDigest.getInstance(method );
            algorithm.reset();
            algorithm.update(b);
            byte messageDigest[] = algorithm.digest();
            return messageDigest;
        }
        catch(NoSuchAlgorithmException nsae) {
            return null;
        }
        // Do nothing for it.
        
    }
    
    
    
    public static String getFileHash(String filename) {
        try {
            byte[] b = createFileDigest(filename, "SHA-1").digest();
            return asHex(b);
        }
        catch(Exception e) {
            return null;
            //Don't do anything else.
        }
    }
    
    public static String getHash(String text, String digestAlgorithm)throws NoSuchAlgorithmException {
        try {
            byte[] b = createHash(text, digestAlgorithm);
            return asHex(b);
        }
        catch(Exception e) {
            return null;
            //Don't do anything else.
        }
    }
    public static String getHash(byte[] data, String digestAlgorithm) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(digestAlgorithm);
        return asHex(md.digest(data));
    }
    

/**
* asHex
* @param bytes to change to hexidecimal. Did you know there is a faster version of this somewhere?
* http://www.rgagnon.com/javadetails/java-0596.html (In case you're board.)
*/
    public static String asHex(byte[] b) {
        String result = "";
        for (int i=0; i < b.length; i++) {
            result +=
            Integer.toString(( b[i] & 0xff ) + 0x100, 16).substring(1 );
        }
        return result;
    }
    
}
