package org.fxp.android.apk.tester;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.fxp.android.apk.ApkBean;
import org.fxp.android.apk.ApkFileManager;
import org.fxp.tools.FileUtilsExt;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ApkTestDiag extends JDialog implements Runnable {
	ApkTestDiag mThis;
	JTextArea textArea;

	Thread t;
	AdbClient adbClient;
	List<ApkBean> apksToTest = new ArrayList<ApkBean>();

	String logName = "apktest.log";
	public static FileHandler fh;
	public static Logger logger = Logger.getLogger("apktest");

	public void log(Level level, String log) {
		logger.log(level, log);
		textArea.setText(textArea.getText() + "\r\n" + log);
	}

	public void setAdbClient(AdbClient adbClient) {
		this.adbClient = adbClient;
	}

	public void setApksToTest(List<ApkBean> apksToTest) {
		this.apksToTest = apksToTest;
	}

	public void start() {
		t = new Thread(this, "ApkTester Thread");
		t.start();
	}

	public void close() {
		log(Level.INFO, "Closing");
		if (adbClient != null)
			adbClient.close();
		if (t != null)
			t = null;
		dispose();
	}

	public int askForRun() {
		int yn = JOptionPane.showConfirmDialog(null, "Run correctly?(Y/N)", "",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

		if (yn == JOptionPane.YES_OPTION) {
			System.out.println("Roger!");
			return 0;
		} else {
			System.out.println("Bad boy, kick out!");
			return -1;
		}
	}

	public ApkTestDiag() throws SecurityException, IOException {
		mThis = this;
		setBounds(100, 100, 462, 475);
		getContentPane().setLayout(new BorderLayout());
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Export Log");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						JFileChooser chooser = new JFileChooser();
						chooser.setMultiSelectionEnabled(false);
						chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

						int retval = chooser.showSaveDialog(mThis);
						if (retval == JFileChooser.APPROVE_OPTION) {
							File saveLog = chooser.getSelectedFile();
							FileUtilsExt.movefile(logName,
									saveLog.getAbsolutePath());
						}
						close();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Bye !");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						close();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}

		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);

		fh = new FileHandler(logName, true);
		fh.setFormatter(new SimpleFormatter());
		logger.addHandler(fh);
	}

	@Override
	public void run() {
		log(Level.INFO, "Start testing");
		for (ApkBean apk : apksToTest) {
			try {
				log(Level.INFO, "Testing " + apk.getApkLocalPath());
				adbClient.doTest(apk, this);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		log(Level.INFO, "Finish testing");
	}

	public static void TEST() {
		try {
			List<ApkBean> apks = ApkFileManager.getAllApk("C:\\apkworkspace");
			ApkTestDiag dialog = new ApkTestDiag();
			dialog.setVisible(true);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setAdbClient(new AdbClient("HT05EPL07220", new FileManager(
					"C:\\apkworkspace")));
			dialog.setApksToTest(apks);

			dialog.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
