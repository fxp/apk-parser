package org.fxp.android.market.worker.frame.master;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteServer;
import java.rmi.server.ServerNotActiveException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import org.fxp.android.apk.ApkBean;
import org.fxp.android.market.api.ApkDAO;

public class ApkDownloadImp implements ApkDownloadService, Serializable {

	private static final long serialVersionUID = -7390020974956854468L;
	ApkDAO apkDAO=ApkDAO.GetInstance();
	int fetchStep=10;

	public ApkDownloadImp() {
	}

	public static void main(String[] args) throws RemoteException {
		try {
			int port = 2222;
			String name = "ApkDownloadService";
			ApkDownloadImp server = new ApkDownloadImp();
			UnicastRemoteObject.exportObject(server, port);
			Registry registry = LocateRegistry.createRegistry(2221);
			registry.rebind(name, server);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public ApkBean[] getNextId(String marketName) throws RemoteException {
		ArrayList<ApkBean> apks = new ArrayList<ApkBean>();
		String ipAddr = null;
		try {
			ipAddr = InetAddress.getByName(RemoteServer.getClientHost())
					.getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (ServerNotActiveException e) {
			e.printStackTrace();
		}

/*		System.out.println("Input a id range");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		try {
			String inStr = in.readLine();
			length = Integer.valueOf(inStr);
		} catch (IOException e) {
			e.printStackTrace();
		}catch(Exception e1){
			return null;
		}*/
		String[] ids=apkDAO.getIds("gfan", fetchStep);
		
		for (String id:ids) {
			ApkBean apk = new ApkBean();
			apk.marketBean.marketPid = id;
			apk.misc = ipAddr;
			apks.add(apk);
		}

		return apks.toArray(new ApkBean[apks.size()]);
	}

	@Override
	public void setId(String marketName, String[] ids,boolean isLocked ) throws RemoteException {
		apkDAO.lockIds(marketName, ids, isLocked);
	}

	@Override
	public void resetAllLock(String marketName) throws RemoteException {
		apkDAO.resetLocks(marketName);
	}

	@Override
	public void setDirectory(String directory) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveFile(FilePacket packet) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

}
