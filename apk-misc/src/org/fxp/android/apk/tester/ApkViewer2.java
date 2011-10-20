package org.fxp.android.apk.tester;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.fxp.android.apk.ApkBean;
import org.fxp.android.apk.ApkFileManager;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.fxp.crawler.bean.CertBean;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.cert.Certificate;
import org.jdesktop.beansbinding.ObjectProperty;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import org.jdesktop.beansbinding.ELProperty;
import java.awt.event.ActionListener;

public class ApkViewer2 extends JFrame {

	private BindingGroup m_bindingGroup;
	private JPanel m_contentPane;
	private org.fxp.android.apk.ApkBean apkBean = new org.fxp.android.apk.ApkBean();
	private JTextField apkFileChecksumJTextField;
	private JTextField apkLocalPathJTextField;
	private JTextField mainNameJTextField;
	private JTextField packageNameJTextField;
	private JTextField versionNameJTextField;
	private JLabel lblPermission;
	private JScrollPane scrollPane;
	private JList list_1;
	private JLabel lblCertificates;
	private JScrollPane scrollPane_1;
	private JList list_2;
	private JLabel lblVersioncode;
	private JTextField textField;
	private JScrollPane scrollPane_2;
	private JList list_3;

	private List<File> files = new ArrayList<File>();
	private String fileCount;
	private JButton btnExportReport;
	private JCheckBox chckbxNewCheckBox;
	private JCheckBox chckbxNewCheckBox_1;
	private JCheckBox chckbxNewCheckBox_2;
	private JCheckBox chckbxNewCheckBox_3;
	private JCheckBox chckbxNewCheckBox_4;
	private JCheckBox chckbxNewCheckBox_5;
	private JTextField textField_1;
	private JLabel lblYingyonghuiStatus;
	private JScrollPane scrollPane_3;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ApkViewer2 frame = new ApkViewer2();
					frame.setStatus("init");
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void setStatus(String status) {
		if (status.equals("badadpk")) {
			JOptionPane.showMessageDialog(this, "File parsing failed", "Error",
					JOptionPane.ERROR_MESSAGE);
		} else if (status.equals("init")) {
			JOptionPane.showMessageDialog(this,
					"After closing this dialog, drag a apk file.", "Hey dude!",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	public void setFiles(List<File> files) {
		this.files = files;
		/*
		 * for(File file:files){ this.files.add(file); ApkBean
		 * apk=ApkFileManager.unzipApk(file.getAbsolutePath());
		 * this.setApkBean(apk); }
		 */
		setFiles(files, true);
	}
	public List<File> getFiles() {
		return this.files ;
	}
	public void setFiles(List<File> files, boolean update) {
		if (update) {
			if (m_bindingGroup != null) {
				m_bindingGroup.unbind();
				m_bindingGroup = null;
			}
			if (files != null) {
				m_bindingGroup = initDataBindings();
			}
		}
	}

	/**
	 * Create the frame.
	 */
	public ApkViewer2() {
		setTitle("Apk Viewer");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 981, 859);
		m_contentPane = new FileDropPanel2(this);
//		m_contentPane = new JPanel();
		fileCount = "Export Report";
		setContentPane(m_contentPane);

		JLabel apkFileChecksumLabel = new JLabel("ApkFileChecksum:");

		apkFileChecksumJTextField = new JTextField();

		JLabel apkLocalPathLabel = new JLabel("ApkLocalPath:");

		apkLocalPathJTextField = new JTextField();

		JLabel mainNameLabel = new JLabel("MainName:");

		mainNameJTextField = new JTextField();

		JLabel packageNameLabel = new JLabel("PackageName:");

		packageNameJTextField = new JTextField();

		lblVersioncode = new JLabel("VersionCode:");

		textField = new JTextField();
		textField.setColumns(10);

		JLabel versionNameLabel = new JLabel("VersionName:");

		versionNameJTextField = new JTextField();

		lblPermission = new JLabel("Permission:");

		scrollPane = new JScrollPane();

		list_1 = new JList();
		scrollPane.setViewportView(list_1);

		lblCertificates = new JLabel("Certificates:");

		scrollPane_1 = new JScrollPane();

		list_2 = new JList();
		list_2.setCellRenderer(new CustomListCellRenderer());
		scrollPane_1.setViewportView(list_2);

		scrollPane_2 = new JScrollPane();

		chckbxNewCheckBox = new JCheckBox("Package Name");
		chckbxNewCheckBox.setSelected(true);

		chckbxNewCheckBox_1 = new JCheckBox("Version Name");
		chckbxNewCheckBox_1.setSelected(true);

		chckbxNewCheckBox_2 = new JCheckBox("Version Code");
		chckbxNewCheckBox_2.setSelected(true);

		chckbxNewCheckBox_3 = new JCheckBox("Permissions");
		chckbxNewCheckBox_3.setSelected(true);

		chckbxNewCheckBox_4 = new JCheckBox("File Path");
		chckbxNewCheckBox_4.setSelected(true);

		chckbxNewCheckBox_5 = new JCheckBox("Resources");
		chckbxNewCheckBox_5.setEnabled(false);

		btnExportReport = new JButton();
		btnExportReport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				int returnVal = chooser.showOpenDialog(null);
				String reportFile = null;
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					reportFile = chooser.getSelectedFile().getAbsolutePath();
					if (!reportFile.endsWith(".csv"))
						reportFile += ".csv";
					if (reportFile == null)
						return;
				} else
					return;

				Object[] selected = list_3.getSelectedValues();
				try {
					BufferedWriter writer = new BufferedWriter(new FileWriter(
							reportFile));
					for (File file : files) {
						System.out.println();
						String fileName = file.getAbsolutePath();
						ApkBean apk = ApkFileManager.unzipApk(fileName);
						if (apk == null)
							JOptionPane.showMessageDialog((Component) null,
									"Opps, a bad apk... " + fileName, "Error",
									JOptionPane.ERROR_MESSAGE);
						else {
							if (chckbxNewCheckBox.isSelected()) {
								writer.write(apk.getPackageName() + ",");
							}
							if (chckbxNewCheckBox_1.isSelected()) {
								writer.write(apk.getVersionName() + ",");
							}
							if (chckbxNewCheckBox_2.isSelected()) {
								writer.write(apk.getVersionCode() + ",");
							}
							if (chckbxNewCheckBox_3.isSelected()) {
								for (String permission : apk.getApkPermission()) {
									writer.write(permission + "/");
								}
								writer.write(",");
							}
							if (chckbxNewCheckBox_4.isSelected()) {
								writer.write(apk.getApkLocalPath() + ",");
							}
							if (chckbxNewCheckBox_5.isSelected()) {
								// writer.write(apk.get);
							}
							writer.write("\r\n");
							System.out.println("Writen " + apk.getApkLocalPath());
						}
					}

					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		textField_1 = new JTextField();
		textField_1.setVisible(false);
		textField_1.setEditable(false);
		textField_1.setEnabled(false);
		textField_1.setColumns(10);
		
		lblYingyonghuiStatus = new JLabel("Yingyonghui status");
		lblYingyonghuiStatus.setVisible(false);
		lblYingyonghuiStatus.setEnabled(false);
		
		JLabel lblDescription = new JLabel("Description");
		lblDescription.setVisible(false);
		lblDescription.setEnabled(false);
		
		scrollPane_3 = new JScrollPane();
		scrollPane_3.setVisible(false);
		
		JButton btnNewButton = new JButton("Clear");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				files.clear();
				setApkBean(null);
			}
		});

		GroupLayout gl_m_contentPane = new GroupLayout(m_contentPane);
		gl_m_contentPane.setHorizontalGroup(
			gl_m_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_m_contentPane.createSequentialGroup()
					.addGroup(gl_m_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_m_contentPane.createSequentialGroup()
							.addGap(612)
							.addComponent(btnExportReport, GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE))
						.addGroup(gl_m_contentPane.createSequentialGroup()
							.addGroup(gl_m_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_m_contentPane.createSequentialGroup()
									.addGap(13)
									.addGroup(gl_m_contentPane.createParallelGroup(Alignment.TRAILING)
										.addGroup(gl_m_contentPane.createSequentialGroup()
											.addComponent(apkFileChecksumLabel)
											.addPreferredGap(ComponentPlacement.UNRELATED)
											.addComponent(apkFileChecksumJTextField, GroupLayout.PREFERRED_SIZE, 500, GroupLayout.PREFERRED_SIZE))
										.addGroup(gl_m_contentPane.createSequentialGroup()
											.addComponent(apkLocalPathLabel)
											.addPreferredGap(ComponentPlacement.UNRELATED)
											.addComponent(apkLocalPathJTextField, GroupLayout.PREFERRED_SIZE, 500, GroupLayout.PREFERRED_SIZE))
										.addGroup(gl_m_contentPane.createSequentialGroup()
											.addComponent(mainNameLabel)
											.addPreferredGap(ComponentPlacement.UNRELATED)
											.addComponent(mainNameJTextField, GroupLayout.PREFERRED_SIZE, 500, GroupLayout.PREFERRED_SIZE))
										.addGroup(gl_m_contentPane.createSequentialGroup()
											.addComponent(packageNameLabel)
											.addPreferredGap(ComponentPlacement.UNRELATED)
											.addComponent(packageNameJTextField, GroupLayout.PREFERRED_SIZE, 500, GroupLayout.PREFERRED_SIZE))
										.addGroup(gl_m_contentPane.createSequentialGroup()
											.addComponent(lblVersioncode)
											.addPreferredGap(ComponentPlacement.UNRELATED)
											.addComponent(textField, GroupLayout.PREFERRED_SIZE, 500, GroupLayout.PREFERRED_SIZE))
										.addGroup(gl_m_contentPane.createSequentialGroup()
											.addComponent(versionNameLabel)
											.addPreferredGap(ComponentPlacement.UNRELATED)
											.addComponent(versionNameJTextField, GroupLayout.PREFERRED_SIZE, 500, GroupLayout.PREFERRED_SIZE))
										.addGroup(gl_m_contentPane.createSequentialGroup()
											.addComponent(lblPermission)
											.addPreferredGap(ComponentPlacement.UNRELATED)
											.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 500, GroupLayout.PREFERRED_SIZE))
										.addGroup(gl_m_contentPane.createSequentialGroup()
											.addGroup(gl_m_contentPane.createParallelGroup(Alignment.TRAILING)
												.addComponent(lblDescription)
												.addComponent(lblCertificates)
												.addComponent(lblYingyonghuiStatus))
											.addPreferredGap(ComponentPlacement.UNRELATED)
											.addGroup(gl_m_contentPane.createParallelGroup(Alignment.LEADING)
												.addComponent(scrollPane_3, GroupLayout.PREFERRED_SIZE, 501, GroupLayout.PREFERRED_SIZE)
												.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 500, GroupLayout.PREFERRED_SIZE)))))
								.addGroup(gl_m_contentPane.createSequentialGroup()
									.addContainerGap()
									.addComponent(textField_1)))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_m_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(btnNewButton, GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE)
								.addComponent(scrollPane_2, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE)
								.addGroup(gl_m_contentPane.createSequentialGroup()
									.addGroup(gl_m_contentPane.createParallelGroup(Alignment.LEADING)
										.addComponent(chckbxNewCheckBox)
										.addComponent(chckbxNewCheckBox_2))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(gl_m_contentPane.createParallelGroup(Alignment.LEADING)
										.addComponent(chckbxNewCheckBox_5)
										.addComponent(chckbxNewCheckBox_4))
									.addPreferredGap(ComponentPlacement.RELATED)
									.addGroup(gl_m_contentPane.createParallelGroup(Alignment.LEADING)
										.addComponent(chckbxNewCheckBox_1)
										.addComponent(chckbxNewCheckBox_3))))))
					.addContainerGap())
		);
		gl_m_contentPane.setVerticalGroup(
			gl_m_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_m_contentPane.createSequentialGroup()
					.addGap(6)
					.addGroup(gl_m_contentPane.createParallelGroup(Alignment.LEADING, false)
						.addGroup(gl_m_contentPane.createSequentialGroup()
							.addGroup(gl_m_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(apkFileChecksumJTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(apkFileChecksumLabel))
							.addGap(10)
							.addGroup(gl_m_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(apkLocalPathJTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(apkLocalPathLabel))
							.addGap(10)
							.addGroup(gl_m_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(mainNameJTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(mainNameLabel))
							.addGap(10)
							.addGroup(gl_m_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(packageNameJTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(packageNameLabel))
							.addGap(5)
							.addGroup(gl_m_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(textField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(lblVersioncode))
							.addGap(10)
							.addGroup(gl_m_contentPane.createParallelGroup(Alignment.BASELINE)
								.addComponent(versionNameJTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(versionNameLabel))
							.addGroup(gl_m_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_m_contentPane.createSequentialGroup()
									.addGap(5)
									.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 202, GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_m_contentPane.createSequentialGroup()
									.addGap(99)
									.addComponent(lblPermission)))
							.addGroup(gl_m_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_m_contentPane.createSequentialGroup()
									.addGap(5)
									.addComponent(scrollPane_1, GroupLayout.PREFERRED_SIZE, 202, GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_m_contentPane.createSequentialGroup()
									.addGap(100)
									.addComponent(lblCertificates))))
						.addComponent(scrollPane_2))
					.addGap(4)
					.addGroup(gl_m_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(textField_1, GroupLayout.PREFERRED_SIZE, 43, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnNewButton, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_m_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_m_contentPane.createSequentialGroup()
							.addComponent(lblYingyonghuiStatus)
							.addGroup(gl_m_contentPane.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_m_contentPane.createSequentialGroup()
									.addPreferredGap(ComponentPlacement.UNRELATED)
									.addComponent(scrollPane_3, GroupLayout.PREFERRED_SIZE, 62, GroupLayout.PREFERRED_SIZE))
								.addGroup(gl_m_contentPane.createSequentialGroup()
									.addGap(35)
									.addComponent(lblDescription))))
						.addGroup(gl_m_contentPane.createParallelGroup(Alignment.TRAILING)
							.addGroup(gl_m_contentPane.createSequentialGroup()
								.addComponent(chckbxNewCheckBox_1)
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addComponent(chckbxNewCheckBox_3))
							.addGroup(gl_m_contentPane.createSequentialGroup()
								.addGroup(gl_m_contentPane.createParallelGroup(Alignment.TRAILING, false)
									.addComponent(chckbxNewCheckBox, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addComponent(chckbxNewCheckBox_4, Alignment.LEADING))
								.addGap(3)
								.addGroup(gl_m_contentPane.createParallelGroup(Alignment.BASELINE)
									.addComponent(chckbxNewCheckBox_2)
									.addComponent(chckbxNewCheckBox_5)))))
					.addGap(19)
					.addComponent(btnExportReport, GroupLayout.PREFERRED_SIZE, 49, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		
		JTextArea textArea = new JTextArea();
		textArea.setVisible(false);
		textArea.setEditable(false);
		textArea.setEnabled(false);
		scrollPane_3.setViewportView(textArea);

		list_3 = new JList();
		list_3.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
				if (arg0.getValueIsAdjusting() == false) {
					JList list = (JList) arg0.getSource();
					Object[] selected = list.getSelectedValues();
					fileCount = "Export Report ( " + files.size() + " )";

					for (int i = 0; i < selected.length; i++) {
						Object sel = selected[i];
						System.out.println(sel);
						String fileName = sel.toString();
						ApkBean apk = ApkFileManager.unzipApk(fileName);
						if (apk == null)
							JOptionPane.showMessageDialog((Component) null,
									"Opps, a bad apk...", "Error",
									JOptionPane.ERROR_MESSAGE);
						else
							setApkBean(apk);
					}
				}
			}
		});
		scrollPane_2.setViewportView(list_3);
		m_contentPane.setLayout(gl_m_contentPane);

		if (apkBean != null) {
			m_bindingGroup = initDataBindings();
		}
	}

	public org.fxp.android.apk.ApkBean getApkBean() {
		return apkBean;
	}

	public void setApkBean(org.fxp.android.apk.ApkBean newApkBean) {
		setApkBean(newApkBean, true);
	}

	public void setApkBean(org.fxp.android.apk.ApkBean newApkBean,
			boolean update) {
		apkBean = newApkBean;
		if (update) {
			if (m_bindingGroup != null) {
				m_bindingGroup.unbind();
				m_bindingGroup = null;
			}
			if (apkBean != null) {
				m_bindingGroup = initDataBindings();
			}
		}
	}

	protected BindingGroup initDataBindings() {
		BeanProperty<ApkBean, String> apkFileChecksumProperty = BeanProperty
				.create("apkFileChecksum");
		BeanProperty<JTextField, String> textProperty = BeanProperty
				.create("text");
		AutoBinding<ApkBean, String, JTextField, String> autoBinding = Bindings
				.createAutoBinding(UpdateStrategy.READ, apkBean,
						apkFileChecksumProperty, apkFileChecksumJTextField,
						textProperty);
		autoBinding.bind();
		//
		BeanProperty<ApkBean, String> apkLocalPathProperty = BeanProperty
				.create("apkLocalPath");
		BeanProperty<JTextField, String> textProperty_1 = BeanProperty
				.create("text");
		AutoBinding<ApkBean, String, JTextField, String> autoBinding_1 = Bindings
				.createAutoBinding(UpdateStrategy.READ, apkBean,
						apkLocalPathProperty, apkLocalPathJTextField,
						textProperty_1);
		autoBinding_1.bind();
		//
		BeanProperty<ApkBean, String> mainNameProperty = BeanProperty
				.create("mainName");
		BeanProperty<JTextField, String> textProperty_2 = BeanProperty
				.create("text");
		AutoBinding<ApkBean, String, JTextField, String> autoBinding_2 = Bindings
				.createAutoBinding(UpdateStrategy.READ, apkBean,
						mainNameProperty, mainNameJTextField, textProperty_2);
		autoBinding_2.bind();
		//
		BeanProperty<ApkBean, String> packageNameProperty = BeanProperty
				.create("packageName");
		BeanProperty<JTextField, String> textProperty_4 = BeanProperty
				.create("text");
		AutoBinding<ApkBean, String, JTextField, String> autoBinding_4 = Bindings
				.createAutoBinding(UpdateStrategy.READ, apkBean,
						packageNameProperty, packageNameJTextField,
						textProperty_4);
		autoBinding_4.bind();
		//
		BeanProperty<ApkBean, String> versionNameProperty = BeanProperty
				.create("versionName");
		BeanProperty<JTextField, String> textProperty_7 = BeanProperty
				.create("text");
		AutoBinding<ApkBean, String, JTextField, String> autoBinding_9 = Bindings
				.createAutoBinding(UpdateStrategy.READ, apkBean,
						versionNameProperty, versionNameJTextField,
						textProperty_7);
		autoBinding_9.bind();
		//
		BeanProperty<ApkBean, List<String>> apkBeanBeanProperty = BeanProperty
				.create("apkPermission");
		JListBinding<String, ApkBean, JList> jListBinding = SwingBindings
				.createJListBinding(UpdateStrategy.READ, apkBean,
						apkBeanBeanProperty, list_1);
		jListBinding.bind();
		//
		BeanProperty<ApkBean, List<CertBean>> apkBeanBeanProperty_1 = BeanProperty
				.create("certs");
		JListBinding<CertBean, ApkBean, JList> jListBinding_1 = SwingBindings
				.createJListBinding(UpdateStrategy.READ, apkBean,
						apkBeanBeanProperty_1, list_2);
		//
		BeanProperty<CertBean, Certificate> certBeanBeanProperty = BeanProperty
				.create("certificate");
		jListBinding_1.setDetailBinding(certBeanBeanProperty);
		//
		jListBinding_1.bind();
		//
		BeanProperty<ApkBean, Integer> apkBeanBeanProperty_2 = BeanProperty
				.create("versionCode");
		BeanProperty<JTextField, String> jTextFieldBeanProperty = BeanProperty
				.create("text");
		AutoBinding<ApkBean, Integer, JTextField, String> autoBinding_3 = Bindings
				.createAutoBinding(UpdateStrategy.READ, apkBean,
						apkBeanBeanProperty_2, textField,
						jTextFieldBeanProperty);
		autoBinding_3.bind();
		//
		JListBinding<File, List<File>, JList> jListBinding_2 = SwingBindings
				.createJListBinding(UpdateStrategy.READ, files, list_3);
		//
		BeanProperty<File, String> fileBeanProperty = BeanProperty
				.create("absolutePath");
		jListBinding_2.setDetailBinding(fileBeanProperty);
		//
		jListBinding_2.bind();
		//
		BeanProperty<JButton, String> jButtonBeanProperty = BeanProperty
				.create("text");
		AutoBinding<String, String, JButton, String> autoBinding_5 = Bindings
				.createAutoBinding(UpdateStrategy.READ, fileCount,
						btnExportReport, jButtonBeanProperty);
		autoBinding_5.bind();
		//
		BindingGroup bindingGroup = new BindingGroup();
		//
		bindingGroup.addBinding(autoBinding);
		bindingGroup.addBinding(autoBinding_1);
		bindingGroup.addBinding(autoBinding_2);
		bindingGroup.addBinding(autoBinding_4);
		bindingGroup.addBinding(autoBinding_9);
		bindingGroup.addBinding(jListBinding);
		bindingGroup.addBinding(jListBinding_1);
		bindingGroup.addBinding(autoBinding_3);
		bindingGroup.addBinding(jListBinding_2);
		bindingGroup.addBinding(autoBinding_5);
		return bindingGroup;
	}
}

class FileDropPanel2 extends JPanel implements DropTargetListener {
	/**
	 * 
	 */
	private ApkViewer2 viewer;

	public FileDropPanel2(ApkViewer2 apkViewer2) {
		this.viewer = apkViewer2;
		new DropTarget(this, DnDConstants.ACTION_COPY_OR_MOVE, this);
	}

	public void dragEnter(DropTargetDragEvent dtde) {
	}

	public void dragOver(DropTargetDragEvent dtde) {
	}

	public void dropActionChanged(DropTargetDragEvent dtde) {
	}

	public void dragExit(DropTargetEvent dte) {
	}

	public void drop(DropTargetDropEvent dtde) {
		try {
			Transferable tr = dtde.getTransferable();
			this.updateUI();

			if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
				System.out.println("file cp");
				List list = (List) (dtde.getTransferable()
						.getTransferData(DataFlavor.javaFileListFlavor));
				Iterator iterator = list.iterator();
				List<File> files=viewer.getFiles();
				while (iterator.hasNext()) {
					File f = (File) iterator.next();
					files.add(f);
					viewer.setApkBean(new ApkBean());
					// viewer.setApkBean(ApkFileManager.unzipApk(f
					// .getAbsolutePath()));
				}
				
				viewer.setFiles(files);

				dtde.dropComplete(true);
				this.updateUI();
			} else {
				dtde.rejectDrop();
			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (UnsupportedFlavorException ufe) {
			ufe.printStackTrace();
		}
	}
}
