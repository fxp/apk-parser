package com.yingyonghui.apkhub;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.AndroidDebugBridge.IClientChangeListener;
import com.android.ddmlib.AndroidDebugBridge.IDebugBridgeChangeListener;
import com.android.ddmlib.AndroidDebugBridge.IDeviceChangeListener;
import com.android.ddmlib.Client;
import com.android.ddmlib.IDevice;

public class ApkHub implements IDeviceChangeListener, IClientChangeListener,
		IDebugBridgeChangeListener, Runnable {
	// For managing all devices
	AndroidDebugBridge adb;

	Hashtable<IDevice, AnDeviceThread> devThreads = new Hashtable<IDevice, AnDeviceThread>();

	public static void main(String[] args) {
		ApkHub apkHub = new ApkHub();
		if (!apkHub.init())
			return;
		apkHub.run();
	}

	private void serv() {
		AndroidDebugBridge.addDeviceChangeListener(this);
		AndroidDebugBridge.addClientChangeListener(this);
		AndroidDebugBridge.addDebugBridgeChangeListener(this);
		Scanner scanner = new Scanner(System.in);
		String cmd;
		do {
			System.out
					.println("Choose a device to install?(l:display all devices, q:quit, others: device serial number)");
			cmd = scanner.nextLine();
			if (cmd.equals("l"))
				DisplayTask();
			else if (cmd.equals("q")) {
				AndroidDebugBridge.disconnectBridge();
			} else {
				try {
					String deviceName = cmd;
					List<IDevice> iDevices = getIDevs(deviceName.trim());
					for (IDevice iDevice : iDevices) {
						AnDeviceThread devThread = devThreads.get(iDevice);
						devThread.removeAllApk();
						// Get all apks needed to be installed
						devThread
								.insertApk("D:\\apk_hub\\apks\\angrybirds.apk");
						devThread
								.insertApk("D:\\apk_hub\\apks\\yingyonghui.apk");
						System.out.println("devThread run");
						(new Thread(devThread)).start();
						System.out.println("devThread run complete");
					}
				} catch (Exception e) {
					System.err.println("Please enter a valid SerialNumber");
				}
			}

		} while (!cmd.equals("q"));
	}

	public List<IDevice> getIDevs(String deviceSerialNumber) {
		Set<IDevice> iDevices = devThreads.keySet();
		List<IDevice> ret = new ArrayList<IDevice>();
		for (IDevice iDevice : iDevices) {
			if (iDevice.getSerialNumber().equals(deviceSerialNumber))
				ret.add(iDevice);
		}
		return ret;
	}

	public void DisplayTask() {
		// Refresh device list
		Set<IDevice> iDevices = devThreads.keySet();
		int i = 0;
		for (IDevice iDevice : iDevices) {
			i++;
			System.out.print(i + "." + iDevice.getSerialNumber() + ", ");
		}
		System.out.println();
	}

	private boolean init() {
		AndroidDebugBridge.init(false);
		adb = AndroidDebugBridge.createBridge(
				"D:\\android-sdk-windows\\platform-tools\\adb.exe", true);
		return true;
	}

	@Override
	public void deviceChanged(IDevice iDev, int arg1) {
		System.out.print("@deviceChanged," + iDev);
		if (arg1 == IDevice.CHANGE_BUILD_INFO) {
			System.out.print(",CHANGE_BUILD_INFO");
			AnDeviceThread anDevThread = new AnDeviceThread(iDev);
			devThreads.put(iDev, anDevThread);
		} else if (arg1 == IDevice.CHANGE_CLIENT_LIST) {
			System.out.print(",CHANGE_CLIENT_LIST");
		} else if (arg1 == IDevice.CHANGE_STATE) {
			System.out.print(",CHANGE_STATE");
		}
		System.out.println();
	}

	@Override
	public void deviceConnected(IDevice iDev) {
		System.out.println("@deviceConnected," + iDev);
	}

	@Override
	public void deviceDisconnected(IDevice iDev) {
		System.out.println("@deviceDisconnected," + iDev);
		devThreads.remove(iDev);
	}

	@Override
	public void run() {
		System.out.println("#Ready to server");
		serv();
	}

	@Override
	public void clientChanged(Client client, int changeMask) {
		System.out.println("@clientChanged," + client + "," + changeMask);

	}

	@Override
	public void bridgeChanged(AndroidDebugBridge arg0) {
		System.out.println("@bridgeChanged," + arg0);

	}
}
