package org.fxp.android.market.worker.frame.master;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.fxp.android.apk.ApkBean;

public interface ApkDownloadService extends Remote {
	ApkBean[] getNextId(String marketName) throws RemoteException;
	
	void setId(String marketName, String[] ids, boolean isLocked)
			throws RemoteException;
	
	void resetAllLock(String marketName)throws RemoteException;

	/**
	 * Set the directory to upload files to
	 * @param directory The directory to place files in
	 * @exception RemoteException to remote object
	 **/
   public void setDirectory( String directory ) throws RemoteException;
   
   /**
    * Receive a file from a remote source
    * @param packet The file packet to receive
    * @exception RemoteException if something bad happens
    **/
   public void receiveFile( FilePacket packet ) throws RemoteException;
}