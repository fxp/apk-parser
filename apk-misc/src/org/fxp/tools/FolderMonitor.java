package org.fxp.tools;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


class DownloadTimeoutTask extends TimerTask {
	File dir = null;
	int seconds = 0;
	Timer timer = null;
	int curCount=-1;

	public DownloadTimeoutTask(File dir, int seconds, Timer timer) {
		super();
		this.dir = dir;
		this.seconds = seconds;
		this.timer = timer;
	}

	public void run() {
		File[] files=dir.listFiles();;
		if(curCount==-1){
			curCount=files.length;
			return ;
		}
		System.out.println("Changing rate: "+(files.length-curCount)+"/"+FolderMonitor.MONITOR_INTERVAL+"sec"+"(Total "+files.length+")");
		curCount=files.length;
	}
}

public class FolderMonitor {

	public static int MONITOR_INTERVAL = 30;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 1)
			return;
		File file = new File(args[0]);

		if(!file.isDirectory())
			return ;
		Timer timer = new Timer();
		DownloadTimeoutTask remindTask = new DownloadTimeoutTask(file,
				MONITOR_INTERVAL, timer);
		timer.schedule(remindTask, 0, MONITOR_INTERVAL * 1000);		
	}
}
