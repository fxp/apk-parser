package org.fxp.android.market.worker.frame.master;

import java.io.*;

/**
 * Class to represent a File object that can be sent
 * and recreated on another system
 * Will actually do the creating for you
 **/
public class FilePacket implements Serializable {

	// the file name I represent
	private String name;
	// the data in my file
  private byte[] data;

	/**
		* Make a file packet that represents a given filename
		* @param name The filename this represents
		**/
  public FilePacket( String name ){
  	this.name = name;
  }

	/**
	 * Get the name associated with this file
	 * @return The name
	 **/
  public String getName(){
  	return name;
  }

	/**
	 * Have the filepacket read iteself in from the
	 * file it represents in name
	 **/
  public void readIn(){
  	try{
    	File file = new File( name );
     	data = new byte[ (int)(file.length()) ];
         (new FileInputStream( file )).read( data );
    }catch( Exception e ){
    	e.printStackTrace();
   	}
	}

	/**
	 * Have the file packet recreate itself, used
	 * after sending it to another location
	 * file will have same name and contents
	 * @param out The outputStream to write itself to
	 **/
	public void writeTo( OutputStream out  ){
  	try{
    	out.write( data );
  	}catch( Exception e ){
   		e.printStackTrace();
   	}
	}
}