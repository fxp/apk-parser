package org.fxp.android.apk.tester;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.fxp.android.apk.ApkBean;
import org.fxp.android.apk.ApkFileManager;
import org.fxp.crawler.bean.CertBean;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;

import java.io.StringReader;

public class ApkTesterGUIv3a extends JFrame {
	ApkTesterGUIv3a instance;

	PrintStream aPrintStream = new PrintStream(new FilteredStream(
			new ByteArrayOutputStream()));

	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextArea textArea;
	JFileChooser chooser = new JFileChooser();
	/**
	 * @wbp.nonvisual location=699,277
	 */
	private ApkBean apkBean = new ApkBean();
	private JLabel lblNewLabel;
	private JTextField textField_3;
	private JList list;
	private JList list_1;
	/**
	 * @wbp.nonvisual location=150,287
	 */
	private List<File> fileArrayList = new ArrayList<File>();
	private JList list_2;
	private DebugInputStream in;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ApkTesterGUIv3a frame = new ApkTesterGUIv3a();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ApkTesterGUIv3a() {
		setTitle("Apk测试器");
		instance = this;
		in = new DebugInputStream();
		// this.fileArrayList.add(new File("E:\\apkTest\\0.GFan.100336.TEMP"));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 794, 929);
		contentPane = new JPanel();
		contentPane.setAutoscrolls(true);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(gl_contentPane.createParallelGroup(
				Alignment.LEADING).addComponent(splitPane,
				GroupLayout.DEFAULT_SIZE, 760, Short.MAX_VALUE));
		gl_contentPane.setVerticalGroup(gl_contentPane.createParallelGroup(
				Alignment.LEADING).addComponent(splitPane,
				GroupLayout.DEFAULT_SIZE, 568, Short.MAX_VALUE));

		JScrollPane scrollPane = new JScrollPane();
		splitPane.setRightComponent(scrollPane);

		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);

		JSplitPane splitPane_1 = new JSplitPane();
		splitPane.setLeftComponent(splitPane_1);

		JPanel panel = new JPanel();
		splitPane_1.setLeftComponent(panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 93, 0 };
		gbl_panel.rowHeights = new int[] { 524, 0, 23, 0, 0 };
		gbl_panel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panel.rowWeights = new double[] { 1.0, 0.0, 0.0, 0.0,
				Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		JScrollPane scrollPane_3 = new JScrollPane();
		GridBagConstraints gbc_scrollPane_3 = new GridBagConstraints();
		gbc_scrollPane_3.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_3.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane_3.gridx = 0;
		gbc_scrollPane_3.gridy = 0;
		panel.add(scrollPane_3, gbc_scrollPane_3);

		list_2 = new JList();
		list_2.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				if (arg0.getValueIsAdjusting() == false) {
					JList list = (JList) arg0.getSource();
					Object selecte = list.getSelectedValue();
					if (selecte == null)
						return;
					System.out.println("Selected " + selecte);
					String fileName = selecte.toString();
					ApkBean apk = ApkFileManager.unzipApk(fileName);
					if (apk == null) {
						JOptionPane.showMessageDialog(null,
								"Opps, a bad apk...", "Error",
								JOptionPane.ERROR_MESSAGE);
						System.err.println("Parser error " + fileName);
					} else {
						instance.setCurrentApk(apk);
						System.err.println("Parser success " + fileName);
					}
					initDataBindings();
				}
			}
		});
		scrollPane_3.setViewportView(list_2);

		JButton btnNewButton_3 = new JButton("Clear");
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				instance.fileArrayList.clear();
				initDataBindings();

			}
		});

		JButton btnNewButton_4 = new JButton("Test All !");
		btnNewButton_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Total " + fileArrayList.size()
						+ " file to test");
				List<ApkBean> apks = new ArrayList<ApkBean>();
				for (File file : fileArrayList) {
					ApkBean apk = ApkFileManager.unzipApk(file
							.getAbsolutePath());
					if (apk != null)
						apks.add(apk);
				}
				System.out.println();
				int yn = JOptionPane.showConfirmDialog(null,
						"Test " + apks.size() + " files?", "",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

				if (yn == JOptionPane.YES_OPTION) {
					ApkTestDiag dialog;
					try {
						dialog = new ApkTestDiag();
						dialog.setVisible(true);
						dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
						dialog.setAdbClient(new AdbClient("HT05EPL07220",
								new FileManager("C:\\apkworkspace")));
						dialog.setApksToTest(apks);

						dialog.start();
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else
					return;
				initDataBindings();
			}
		});
		GridBagConstraints gbc_btnNewButton_4 = new GridBagConstraints();
		gbc_btnNewButton_4.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnNewButton_4.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton_4.gridx = 0;
		gbc_btnNewButton_4.gridy = 1;
		panel.add(btnNewButton_4, gbc_btnNewButton_4);
		GridBagConstraints gbc_btnNewButton_3 = new GridBagConstraints();
		gbc_btnNewButton_3.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnNewButton_3.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton_3.anchor = GridBagConstraints.SOUTH;
		gbc_btnNewButton_3.gridx = 0;
		gbc_btnNewButton_3.gridy = 2;
		panel.add(btnNewButton_3, gbc_btnNewButton_3);

		JButton btnNewButton_2 = new JButton("Add Files");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				chooser.setMultiSelectionEnabled(true);
				int retval = chooser.showDialog(instance, null);
				if (retval == JFileChooser.APPROVE_OPTION) {
					File theFile = chooser.getSelectedFile();
					if (theFile != null) {
						if (theFile.isDirectory()) {
							for (File dirFile : chooser.getSelectedFiles()) {
								File[] apkFiles = dirFile.listFiles();
								instance.addApkFiles(apkFiles);
							}
						} else {
							instance.addApkFiles(chooser.getSelectedFiles());
						}
						return;
					}
				}
				JOptionPane.showMessageDialog(instance, "No file was chosen.");
			}
		});
		GridBagConstraints gbc_btnNewButton_2 = new GridBagConstraints();
		gbc_btnNewButton_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnNewButton_2.anchor = GridBagConstraints.SOUTH;
		gbc_btnNewButton_2.gridx = 0;
		gbc_btnNewButton_2.gridy = 3;
		panel.add(btnNewButton_2, gbc_btnNewButton_2);

		JPanel panel_1 = new JPanel();
		splitPane_1.setRightComponent(panel_1);

		lblNewLabel = new JLabel("Location");

		textField = new JTextField();
		textField.setColumns(10);

		textField_1 = new JTextField();
		textField_1.setColumns(10);

		JLabel lblNewLabel_1 = new JLabel("PackageName");

		textField_2 = new JTextField();
		textField_2.setColumns(10);

		JLabel lblVersioncode = new JLabel("VersionCode");

		textField_3 = new JTextField();
		textField_3.setColumns(10);

		JLabel lblVersionname = new JLabel("VersionName");

		JLabel lblPermissions = new JLabel("Permissions");

		JScrollPane scrollPane_1 = new JScrollPane();

		JScrollPane scrollPane_2 = new JScrollPane();

		JLabel lblCertificates = new JLabel("Certificates");

		JButton btnNewButton = new JButton("Yingyonghui");
		btnNewButton.setEnabled(false);

		JButton btnNewButton_1 = new JButton("Google");
		btnNewButton_1.setEnabled(false);

		JButton btnGoTest = new JButton("GO TEST!");
		btnGoTest.setEnabled(false);
		btnGoTest.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//List<ApkBean> apks = new ArrayList<ApkBean>();
				//apks.add(instance.apkBean);
				//DebugConsole dc = new DebugConsole(apks);
				// File[] files = (new
				// File("z:\\apkWorkspace\\raw")).listFiles();
				// for (File file : files) {
				// dc.doApk(file.getAbsolutePath());
				// return;
				// }
			}
		});
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addComponent(lblNewLabel)
						.addComponent(lblNewLabel_1)
						.addComponent(lblVersioncode)
						.addComponent(lblVersionname)
						.addComponent(lblPermissions)
						.addComponent(lblCertificates))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane_1, GroupLayout.DEFAULT_SIZE, 461, Short.MAX_VALUE)
						.addComponent(scrollPane_2, GroupLayout.DEFAULT_SIZE, 461, Short.MAX_VALUE)
						.addGroup(gl_panel_1.createSequentialGroup()
							.addComponent(btnNewButton)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnNewButton_1)
							.addPreferredGap(ComponentPlacement.RELATED, 220, Short.MAX_VALUE)
							.addComponent(btnGoTest))
						.addComponent(textField_1, GroupLayout.DEFAULT_SIZE, 461, Short.MAX_VALUE)
						.addComponent(textField, GroupLayout.DEFAULT_SIZE, 461, Short.MAX_VALUE)
						.addComponent(textField_2, GroupLayout.DEFAULT_SIZE, 461, Short.MAX_VALUE)
						.addComponent(textField_3, GroupLayout.DEFAULT_SIZE, 461, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_panel_1.setVerticalGroup(
			gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNewLabel)
						.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(textField_1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblNewLabel_1))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(textField_2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblVersioncode))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(textField_3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblVersionname))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 216, GroupLayout.PREFERRED_SIZE)
						.addComponent(lblPermissions))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
						.addComponent(lblCertificates)
						.addComponent(scrollPane_2, GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE))
					.addGap(18)
					.addGroup(gl_panel_1.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnNewButton)
						.addComponent(btnNewButton_1)
						.addComponent(btnGoTest))
					.addContainerGap())
		);

		list_1 = new JList();
		list_1.setCellRenderer(new CustomListCellRenderer());
		scrollPane_2.setViewportView(list_1);

		list = new JList();
		scrollPane_1.setViewportView(list);
		panel_1.setLayout(gl_panel_1);
		splitPane_1.setDividerLocation(200);
		splitPane.setDividerLocation(620);
		contentPane.setLayout(gl_contentPane);

		System.setOut(aPrintStream);
		System.setErr(aPrintStream);
		System.setIn(in);

		initDataBindings();
	}

	protected void setCurrentApk(ApkBean apk) {
		this.apkBean = apk;
		initDataBindings();
	}

	protected void addApkFiles(File[] files) {
		for (File file : files) {
			if (!fileArrayList.contains(file)) {
				System.out.println("File added " + file.getAbsolutePath());
				fileArrayList.add(file);
			}
		}
		initDataBindings();
	}

	protected void initDataBindings() {
		BeanProperty<ApkBean, String> apkBeanBeanProperty = BeanProperty
				.create("apkLocalPath");
		BeanProperty<JTextField, String> jTextFieldBeanProperty = BeanProperty
				.create("text");
		AutoBinding<ApkBean, String, JTextField, String> autoBinding = Bindings
				.createAutoBinding(UpdateStrategy.READ, apkBean,
						apkBeanBeanProperty, textField, jTextFieldBeanProperty);
		autoBinding.bind();
		//
		BeanProperty<ApkBean, String> apkBeanBeanProperty_1 = BeanProperty
				.create("packageName");
		BeanProperty<JTextField, String> jTextFieldBeanProperty_1 = BeanProperty
				.create("text");
		AutoBinding<ApkBean, String, JTextField, String> autoBinding_1 = Bindings
				.createAutoBinding(UpdateStrategy.READ, apkBean,
						apkBeanBeanProperty_1, textField_1,
						jTextFieldBeanProperty_1);
		autoBinding_1.bind();
		//
		BeanProperty<ApkBean, Integer> apkBeanBeanProperty_2 = BeanProperty
				.create("versionCode");
		BeanProperty<JTextField, String> jTextFieldBeanProperty_2 = BeanProperty
				.create("text");
		AutoBinding<ApkBean, Integer, JTextField, String> autoBinding_2 = Bindings
				.createAutoBinding(UpdateStrategy.READ, apkBean,
						apkBeanBeanProperty_2, textField_2,
						jTextFieldBeanProperty_2);
		autoBinding_2.bind();
		//
		BeanProperty<ApkBean, String> apkBeanBeanProperty_3 = BeanProperty
				.create("versionName");
		BeanProperty<JTextField, String> jTextFieldBeanProperty_3 = BeanProperty
				.create("text");
		AutoBinding<ApkBean, String, JTextField, String> autoBinding_3 = Bindings
				.createAutoBinding(UpdateStrategy.READ, apkBean,
						apkBeanBeanProperty_3, textField_3,
						jTextFieldBeanProperty_3);
		autoBinding_3.bind();
		//
		BeanProperty<ApkBean, List<String>> apkBeanBeanProperty_4 = BeanProperty
				.create("apkPermission");
		JListBinding<String, ApkBean, JList> jListBinding = SwingBindings
				.createJListBinding(UpdateStrategy.READ, apkBean,
						apkBeanBeanProperty_4, list);
		jListBinding.bind();
		//
		BeanProperty<ApkBean, List<CertBean>> apkBeanBeanProperty_5 = BeanProperty
				.create("certs");
		JListBinding<CertBean, ApkBean, JList> jListBinding_1 = SwingBindings
				.createJListBinding(UpdateStrategy.READ, apkBean,
						apkBeanBeanProperty_5, list_1);
		//
		BeanProperty<CertBean, Certificate> certBeanBeanProperty = BeanProperty
				.create("certificate");
		jListBinding_1.setDetailBinding(certBeanBeanProperty);
		//
		jListBinding_1.bind();
		//
		JListBinding<File, List<File>, JList> jListBinding_2 = SwingBindings
				.createJListBinding(UpdateStrategy.READ, fileArrayList, list_2);
		//
		BeanProperty<File, String> fileBeanProperty = BeanProperty
				.create("absolutePath");
		jListBinding_2.setDetailBinding(fileBeanProperty);
		//
		jListBinding_2.bind();
	}

	class FilteredStream extends FilterOutputStream {
		boolean logFile = true;

		public FilteredStream(OutputStream aStream) {
			super(aStream);
		}

		public void write(byte b[]) throws IOException {
			String aString = new String(b);
			textArea.append(aString);
		}

		public void write(byte b[], int off, int len) throws IOException {
			String aString = new String(b, off, len);
			textArea.append(aString);
			if (logFile) {
				FileWriter aWriter = new FileWriter("error.log", true);
				aWriter.write(aString);
				aWriter.close();
			}
		}
	}

	class DebugInputStream extends InputStream {
		public DebugInputStream() {
			input = "";
			position = 0;
		}

		public synchronized void addInput(String newInput) {
			input = input + newInput;
			notify();
		}

		public synchronized int read() {
			while (input.length() == position) {
				input = "";
				position = 0;
				try {
					wait();
				} catch (InterruptedException e) {
				}
			}
			char c = input.charAt(position);
			position++;
			return c;
		}

		public int read(byte[] b) {
			b[0] = (byte) read();
			return 1;
		}

		private String input;
		private int position;
	}
}
