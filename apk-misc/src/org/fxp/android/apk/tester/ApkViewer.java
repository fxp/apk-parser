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

import java.util.Iterator;
import java.util.List;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.fxp.crawler.bean.CertBean;

import java.io.File;
import java.io.IOException;
import java.security.cert.Certificate;
import org.jdesktop.beansbinding.ObjectProperty;

public class ApkViewer extends JFrame {

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
	private JLabel lblAxmlIsComing;
	private JLabel lblVersioncode;
	private JTextField textField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ApkViewer frame = new ApkViewer();
					frame.setStatus("init");
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void setStatus(String status){
		if(status.equals("badadpk")){
			JOptionPane.showMessageDialog(this, "File parsing failed",
                    "Error",JOptionPane.ERROR_MESSAGE);
		}else if(status.equals("init")){
			JOptionPane.showMessageDialog(this, "After closing this dialog, drag a apk file.",
                    "Hey dude!",JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * Create the frame.
	 */
	public ApkViewer() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 584, 751);
		m_contentPane = new FileDropPanel(this);
		
		setContentPane(m_contentPane);
		//
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 1.0E-4 };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				1.0, 1.0, 1.0, 0.0, 1.0E-4 };
		m_contentPane.setLayout(gridBagLayout);

		JLabel apkFileChecksumLabel = new JLabel("ApkFileChecksum:");
		GridBagConstraints labelGbc_0 = new GridBagConstraints();
		labelGbc_0.insets = new Insets(5, 5, 5, 5);
		labelGbc_0.gridx = 0;
		labelGbc_0.gridy = 0;
		m_contentPane.add(apkFileChecksumLabel, labelGbc_0);

		apkFileChecksumJTextField = new JTextField();
		GridBagConstraints componentGbc_0 = new GridBagConstraints();
		componentGbc_0.insets = new Insets(5, 0, 5, 0);
		componentGbc_0.fill = GridBagConstraints.HORIZONTAL;
		componentGbc_0.gridx = 1;
		componentGbc_0.gridy = 0;
		m_contentPane.add(apkFileChecksumJTextField, componentGbc_0);

		JLabel apkLocalPathLabel = new JLabel("ApkLocalPath:");
		GridBagConstraints labelGbc_1 = new GridBagConstraints();
		labelGbc_1.insets = new Insets(5, 5, 5, 5);
		labelGbc_1.gridx = 0;
		labelGbc_1.gridy = 1;
		m_contentPane.add(apkLocalPathLabel, labelGbc_1);

		apkLocalPathJTextField = new JTextField();
		GridBagConstraints componentGbc_1 = new GridBagConstraints();
		componentGbc_1.insets = new Insets(5, 0, 5, 0);
		componentGbc_1.fill = GridBagConstraints.HORIZONTAL;
		componentGbc_1.gridx = 1;
		componentGbc_1.gridy = 1;
		m_contentPane.add(apkLocalPathJTextField, componentGbc_1);

		JLabel mainNameLabel = new JLabel("MainName:");
		GridBagConstraints labelGbc_2 = new GridBagConstraints();
		labelGbc_2.insets = new Insets(5, 5, 5, 5);
		labelGbc_2.gridx = 0;
		labelGbc_2.gridy = 2;
		m_contentPane.add(mainNameLabel, labelGbc_2);

		mainNameJTextField = new JTextField();
		GridBagConstraints componentGbc_2 = new GridBagConstraints();
		componentGbc_2.insets = new Insets(5, 0, 5, 0);
		componentGbc_2.fill = GridBagConstraints.HORIZONTAL;
		componentGbc_2.gridx = 1;
		componentGbc_2.gridy = 2;
		m_contentPane.add(mainNameJTextField, componentGbc_2);

		JLabel packageNameLabel = new JLabel("PackageName:");
		GridBagConstraints labelGbc_4 = new GridBagConstraints();
		labelGbc_4.insets = new Insets(5, 5, 5, 5);
		labelGbc_4.gridx = 0;
		labelGbc_4.gridy = 3;
		m_contentPane.add(packageNameLabel, labelGbc_4);

		packageNameJTextField = new JTextField();
		GridBagConstraints componentGbc_4 = new GridBagConstraints();
		componentGbc_4.insets = new Insets(5, 0, 5, 0);
		componentGbc_4.fill = GridBagConstraints.HORIZONTAL;
		componentGbc_4.gridx = 1;
		componentGbc_4.gridy = 3;
		m_contentPane.add(packageNameJTextField, componentGbc_4);
		
		lblVersioncode = new JLabel("VersionCode:");
		GridBagConstraints gbc_lblVersioncode = new GridBagConstraints();
		gbc_lblVersioncode.anchor = GridBagConstraints.EAST;
		gbc_lblVersioncode.insets = new Insets(0, 0, 5, 5);
		gbc_lblVersioncode.gridx = 0;
		gbc_lblVersioncode.gridy = 4;
		m_contentPane.add(lblVersioncode, gbc_lblVersioncode);
		
		textField = new JTextField();
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 0);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 1;
		gbc_textField.gridy = 4;
		m_contentPane.add(textField, gbc_textField);
		textField.setColumns(10);

		JLabel versionNameLabel = new JLabel("VersionName:");
		GridBagConstraints labelGbc_9 = new GridBagConstraints();
		labelGbc_9.insets = new Insets(5, 5, 5, 5);
		labelGbc_9.gridx = 0;
		labelGbc_9.gridy = 5;
		m_contentPane.add(versionNameLabel, labelGbc_9);

		versionNameJTextField = new JTextField();
		GridBagConstraints componentGbc_9 = new GridBagConstraints();
		componentGbc_9.insets = new Insets(5, 0, 5, 0);
		componentGbc_9.fill = GridBagConstraints.HORIZONTAL;
		componentGbc_9.gridx = 1;
		componentGbc_9.gridy = 5;
		m_contentPane.add(versionNameJTextField, componentGbc_9);

		lblPermission = new JLabel("Permission");
		GridBagConstraints gbc_lblPermission = new GridBagConstraints();
		gbc_lblPermission.insets = new Insets(0, 0, 5, 5);
		gbc_lblPermission.gridx = 0;
		gbc_lblPermission.gridy = 6;
		m_contentPane.add(lblPermission, gbc_lblPermission);

		scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 1;
		gbc_scrollPane.gridy = 6;
		m_contentPane.add(scrollPane, gbc_scrollPane);

		list_1 = new JList();
		scrollPane.setViewportView(list_1);

		lblCertificates = new JLabel("Certificates");
		GridBagConstraints gbc_lblCertificates = new GridBagConstraints();
		gbc_lblCertificates.insets = new Insets(0, 0, 5, 5);
		gbc_lblCertificates.gridx = 0;
		gbc_lblCertificates.gridy = 7;
		m_contentPane.add(lblCertificates, gbc_lblCertificates);

		scrollPane_1 = new JScrollPane();
		GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
		gbc_scrollPane_1.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_1.gridx = 1;
		gbc_scrollPane_1.gridy = 7;
		m_contentPane.add(scrollPane_1, gbc_scrollPane_1);

		list_2 = new JList();
		list_2.setCellRenderer(new CustomListCellRenderer());
		scrollPane_1.setViewportView(list_2);
		
		lblAxmlIsComing = new JLabel("Axml is Coming");
		GridBagConstraints gbc_lblAxmlIsComing = new GridBagConstraints();
		gbc_lblAxmlIsComing.insets = new Insets(0, 0, 5, 5);
		gbc_lblAxmlIsComing.gridx = 0;
		gbc_lblAxmlIsComing.gridy = 8;
		m_contentPane.add(lblAxmlIsComing, gbc_lblAxmlIsComing);

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
		BeanProperty<ApkBean, String> apkFileChecksumProperty = BeanProperty.create("apkFileChecksum");
		BeanProperty<JTextField, String> textProperty = BeanProperty.create("text");
		AutoBinding<ApkBean, String, JTextField, String> autoBinding = Bindings.createAutoBinding(UpdateStrategy.READ, apkBean, apkFileChecksumProperty, apkFileChecksumJTextField, textProperty);
		autoBinding.bind();
		//
		BeanProperty<ApkBean, String> apkLocalPathProperty = BeanProperty.create("apkLocalPath");
		BeanProperty<JTextField, String> textProperty_1 = BeanProperty.create("text");
		AutoBinding<ApkBean, String, JTextField, String> autoBinding_1 = Bindings.createAutoBinding(UpdateStrategy.READ, apkBean, apkLocalPathProperty, apkLocalPathJTextField, textProperty_1);
		autoBinding_1.bind();
		//
		BeanProperty<ApkBean, String> mainNameProperty = BeanProperty.create("mainName");
		BeanProperty<JTextField, String> textProperty_2 = BeanProperty.create("text");
		AutoBinding<ApkBean, String, JTextField, String> autoBinding_2 = Bindings.createAutoBinding(UpdateStrategy.READ, apkBean, mainNameProperty, mainNameJTextField, textProperty_2);
		autoBinding_2.bind();
		//
		BeanProperty<ApkBean, String> packageNameProperty = BeanProperty.create("packageName");
		BeanProperty<JTextField, String> textProperty_4 = BeanProperty.create("text");
		AutoBinding<ApkBean, String, JTextField, String> autoBinding_4 = Bindings.createAutoBinding(UpdateStrategy.READ, apkBean, packageNameProperty, packageNameJTextField, textProperty_4);
		autoBinding_4.bind();
		//
		BeanProperty<ApkBean, String> versionNameProperty = BeanProperty.create("versionName");
		BeanProperty<JTextField, String> textProperty_7 = BeanProperty.create("text");
		AutoBinding<ApkBean, String, JTextField, String> autoBinding_9 = Bindings.createAutoBinding(UpdateStrategy.READ, apkBean, versionNameProperty, versionNameJTextField, textProperty_7);
		autoBinding_9.bind();
		//
		BeanProperty<ApkBean, List<String>> apkBeanBeanProperty = BeanProperty.create("apkPermission");
		JListBinding<String, ApkBean, JList> jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ, apkBean, apkBeanBeanProperty, list_1);
		jListBinding.bind();
		//
		BeanProperty<ApkBean, List<CertBean>> apkBeanBeanProperty_1 = BeanProperty.create("certs");
		JListBinding<CertBean, ApkBean, JList> jListBinding_1 = SwingBindings.createJListBinding(UpdateStrategy.READ, apkBean, apkBeanBeanProperty_1, list_2);
		//
		BeanProperty<CertBean, Certificate> certBeanBeanProperty = BeanProperty.create("certificate");
		jListBinding_1.setDetailBinding(certBeanBeanProperty);
		//
		jListBinding_1.bind();
		//
		BeanProperty<ApkBean, Integer> apkBeanBeanProperty_2 = BeanProperty.create("versionCode");
		BeanProperty<JTextField, String> jTextFieldBeanProperty = BeanProperty.create("text");
		AutoBinding<ApkBean, Integer, JTextField, String> autoBinding_3 = Bindings.createAutoBinding(UpdateStrategy.READ, apkBean, apkBeanBeanProperty_2, textField, jTextFieldBeanProperty);
		autoBinding_3.bind();
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
		return bindingGroup;
	}
}


class FileDropPanel extends JPanel implements DropTargetListener {
	/**
	 * 
	 */
	private ApkViewer viewer;
	private static final long serialVersionUID = -6019717972561019158L;

	public FileDropPanel(ApkViewer viewer) {
		this.viewer = viewer;
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
				while (iterator.hasNext()) {
					File f = (File) iterator.next();
					ApkBean apk = ApkFileManager.unzipApk(f.getAbsolutePath());
					if (apk != null) {
						this.viewer.setApkBean(apk);
						System.out.println("Put apk " + apk.apkLocalPath);
					}
					// this.setText("无法解析\t" + f.getAbsolutePath());
					else {
						// this.setText(apk.toString());
						// Search database
						// dao.fillApk(apk);

					}
				}

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

class TestMultiLineRendering extends JFrame {
	private static final long serialVersionUID = 1L;
	String[] data = {
			"One big, big, big, big, big, " + "big, big, big, big, big, "
					+ "big, big, big, big, big, "
					+ "big, big, big, big, big line", "Two\nlines" };

	public TestMultiLineRendering() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container content = getContentPane();
		final JList jl = new JList(data);
		jl.setCellRenderer(new CustomListCellRenderer());
		JButton reload = new JButton(new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				jl.setListData(data);
			}
		});
		reload.setText("Reload data");
		content.add(new JScrollPane(jl), BorderLayout.CENTER);
		content.add(reload, BorderLayout.SOUTH);
		setSize(300, 300);
	}
}

class CustomListCellRenderer extends JPanel implements ListCellRenderer {
	private static final long serialVersionUID = 5682196012314929313L;
	private static final Border NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);
	private final JTextArea contentArea;

	CustomListCellRenderer() {
		contentArea = new JTextArea();
		contentArea.setLineWrap(true);
		contentArea.setWrapStyleWord(true);
		contentArea.setOpaque(true);
		contentArea.setAutoscrolls(true);
		build();
	}

	private void build() {
		setLayout(new BorderLayout());
		add(contentArea, BorderLayout.CENTER);
	}

	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		setBorder(cellHasFocus ? UIManager
				.getBorder("List.focusCellHighlightBorder") : NO_FOCUS_BORDER);
		contentArea.setText(value.toString());
		return this;
	}
}