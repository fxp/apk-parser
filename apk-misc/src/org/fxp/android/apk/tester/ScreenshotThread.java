package org.fxp.android.apk.tester;

public class ScreenshotThread implements Runnable {
	Thread runner;
	public ScreenshotThread(String threadName) {
		runner = new Thread(this, threadName); 
		runner.start();
	}
	@Override
	public void run() {
		
	}
	
	
}
