package org.fxp.android.apk.tester;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Iterator;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

import org.fxp.android.apk.ApkBean;
import org.fxp.android.apk.ApkFileManager;
import org.fxp.crawler.bean.CertBean;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.JRadioButton;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import org.jdesktop.beansbinding.ObjectProperty;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JTable;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.jdesktop.swingbinding.JTableBinding;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

public class ApkEditor extends JDialog {
	private AutoBinding<ApkBean, List<CertBean>, JTextArea, JTextArea> note;

	private BindingGroup m_bindingGroup;
	private JPanel m_contentPane;
	private org.fxp.android.apk.ApkBean apkBean = new org.fxp.android.apk.ApkBean();
	private JTextField apkLocalPathJTextField;
	private JTextField packageNameJTextField;
	private JTextField versionCodeJTextField;
	private JTextField versionNameJTextField;
	private JTextField mainNameJTextField;
	private JLabel lblPermissions;
	private JLabel lblCertificates;
	private JList list;
	private JScrollPane scrollPane;
	private JScrollPane scrollPane_1;
	private JList list_1;
	private JButton btnSave;
	private JScrollPane scrollPane_3;
	private JTable table;
	private JLabel lblInfo;
	private JLabel lblDeveloper;
	private JTextField textField;
	private JScrollPane scrollPane_2;
	private JTextArea textArea;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			ApkEditorDAO.init();
			ApkEditor dialog = new ApkEditor();
//			ApkBean apk=ApkFileManager
//			.unzipApk("/home/fxp/workspace/ApkEditor/360MobileSafe_android.apk");
//			dialog.setApkBean(apk);
//			dialog.refreshApkInfo(apk);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void refreshApkInfo(ApkBean apk){
		if (apk != null) {
			ApkManDao dao = ApkManDao.GetInstance();
			// No such apk in objectdb
			if (dao.fillApk(apk) == null) {
				// No such apk in mysql
			}

			if (ApkEditorDAO.getApk(apk) == null) {
				// No such apk in objectdb
				int aaa = 0;
				aaa = 3;
			}

			setApkBean(apk);
			System.out.println("Put apk " + apk.apkLocalPath);
		}
		// this.setText("无法解析\t" + f.getAbsolutePath());
		else {
			// this.setText(apk.toString());
			// Search database
			// dao.fillApk(apk);

		}
	}

	public void setStatus(String status) {
		if (status.equals("working")) {
			btnSave.setText("Busying, don't touch me!");
		} else if (status.equals("idle")) {
			btnSave.setText("Save");
		} else {
			btnSave.setText("Save");
		}
	}

	/**
	 * Create the dialog.
	 */
	public ApkEditor() {
		setBounds(100, 100, 799, 686);
		m_contentPane = new EditorFileDropPanel(this);
		setContentPane(m_contentPane);
		//
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 145, 102, 116, 0,
				95, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 1.0E-4 };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				1.0, 1.0, 0.0, 1.0, 0.0, 1.0E-4 };
		m_contentPane.setLayout(gridBagLayout);

		JLabel apkLocalPathLabel = new JLabel("ApkLocalPath:");
		GridBagConstraints labelGbc_0 = new GridBagConstraints();
		labelGbc_0.insets = new Insets(5, 5, 5, 5);
		labelGbc_0.gridx = 0;
		labelGbc_0.gridy = 0;
		m_contentPane.add(apkLocalPathLabel, labelGbc_0);

		apkLocalPathJTextField = new JTextField();
		GridBagConstraints componentGbc_0 = new GridBagConstraints();
		componentGbc_0.insets = new Insets(5, 0, 5, 0);
		componentGbc_0.fill = GridBagConstraints.HORIZONTAL;
		componentGbc_0.gridx = 1;
		componentGbc_0.gridy = 0;
		m_contentPane.add(apkLocalPathJTextField, componentGbc_0);

		JLabel packageNameLabel = new JLabel("PackageName:");
		GridBagConstraints labelGbc_1 = new GridBagConstraints();
		labelGbc_1.insets = new Insets(5, 5, 5, 5);
		labelGbc_1.gridx = 0;
		labelGbc_1.gridy = 1;
		m_contentPane.add(packageNameLabel, labelGbc_1);

		packageNameJTextField = new JTextField();
		GridBagConstraints componentGbc_1 = new GridBagConstraints();
		componentGbc_1.insets = new Insets(5, 0, 5, 0);
		componentGbc_1.fill = GridBagConstraints.HORIZONTAL;
		componentGbc_1.gridx = 1;
		componentGbc_1.gridy = 1;
		m_contentPane.add(packageNameJTextField, componentGbc_1);

		JLabel versionCodeLabel = new JLabel("VersionCode:");
		GridBagConstraints labelGbc_2 = new GridBagConstraints();
		labelGbc_2.insets = new Insets(5, 5, 5, 5);
		labelGbc_2.gridx = 0;
		labelGbc_2.gridy = 2;
		m_contentPane.add(versionCodeLabel, labelGbc_2);

		versionCodeJTextField = new JTextField();
		GridBagConstraints componentGbc_2 = new GridBagConstraints();
		componentGbc_2.insets = new Insets(5, 0, 5, 0);
		componentGbc_2.fill = GridBagConstraints.HORIZONTAL;
		componentGbc_2.gridx = 1;
		componentGbc_2.gridy = 2;
		m_contentPane.add(versionCodeJTextField, componentGbc_2);

		JLabel versionNameLabel = new JLabel("VersionName:");
		GridBagConstraints labelGbc_3 = new GridBagConstraints();
		labelGbc_3.insets = new Insets(5, 5, 5, 5);
		labelGbc_3.gridx = 0;
		labelGbc_3.gridy = 3;
		m_contentPane.add(versionNameLabel, labelGbc_3);

		versionNameJTextField = new JTextField();
		GridBagConstraints componentGbc_3 = new GridBagConstraints();
		componentGbc_3.insets = new Insets(5, 0, 5, 0);
		componentGbc_3.fill = GridBagConstraints.HORIZONTAL;
		componentGbc_3.gridx = 1;
		componentGbc_3.gridy = 3;
		m_contentPane.add(versionNameJTextField, componentGbc_3);

		JLabel mainNameLabel = new JLabel("MainName:");
		GridBagConstraints labelGbc_4 = new GridBagConstraints();
		labelGbc_4.insets = new Insets(5, 5, 5, 5);
		labelGbc_4.gridx = 0;
		labelGbc_4.gridy = 4;
		m_contentPane.add(mainNameLabel, labelGbc_4);

		mainNameJTextField = new JTextField();
		GridBagConstraints componentGbc_4 = new GridBagConstraints();
		componentGbc_4.insets = new Insets(5, 0, 5, 0);
		componentGbc_4.fill = GridBagConstraints.HORIZONTAL;
		componentGbc_4.gridx = 1;
		componentGbc_4.gridy = 4;
		m_contentPane.add(mainNameJTextField, componentGbc_4);

		lblPermissions = new JLabel("Permissions");
		GridBagConstraints gbc_lblPermissions = new GridBagConstraints();
		gbc_lblPermissions.fill = GridBagConstraints.VERTICAL;
		gbc_lblPermissions.insets = new Insets(0, 0, 5, 5);
		gbc_lblPermissions.gridx = 0;
		gbc_lblPermissions.gridy = 5;
		m_contentPane.add(lblPermissions, gbc_lblPermissions);

		list = new JList();
		list.setValueIsAdjusting(true);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane = new JScrollPane(list);
		GridBagConstraints gbc_list = new GridBagConstraints();
		gbc_list.insets = new Insets(0, 0, 5, 0);
		gbc_list.fill = GridBagConstraints.BOTH;
		gbc_list.gridx = 1;
		gbc_list.gridy = 5;
		m_contentPane.add(scrollPane, gbc_list);

		lblCertificates = new JLabel("Certificates");
		GridBagConstraints gbc_lblCertificates = new GridBagConstraints();
		gbc_lblCertificates.insets = new Insets(0, 0, 5, 5);
		gbc_lblCertificates.gridx = 0;
		gbc_lblCertificates.gridy = 6;
		m_contentPane.add(lblCertificates, gbc_lblCertificates);
		scrollPane_1 = new JScrollPane();
		GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
		gbc_scrollPane_1.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_1.gridx = 1;
		gbc_scrollPane_1.gridy = 6;
		m_contentPane.add(scrollPane_1, gbc_scrollPane_1);

		list_1 = new JList();
		list_1.setCellRenderer(new EditorCustomListCellRenderer(list_1));
		list_1.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list_1.setValueIsAdjusting(true);
		scrollPane_1.setViewportView(list_1);

		scrollPane_3 = new JScrollPane();
		GridBagConstraints gbc_scrollPane_3 = new GridBagConstraints();
		gbc_scrollPane_3.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane_3.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_3.gridx = 1;
		gbc_scrollPane_3.gridy = 7;
		m_contentPane.add(scrollPane_3, gbc_scrollPane_3);

		table = new JTable();
		scrollPane_3.setViewportView(table);

		btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnSave.setText("Saving");
				try {
					ApkEditorDAO.putApk(apkBean);
				} catch (Exception e) {
					e.printStackTrace();
					btnSave.setText("Error");
					btnSave.setEnabled(false);
					return;
				}
				btnSave.setText("Saved");
			}
		});

		lblDeveloper = new JLabel("Developer");
		GridBagConstraints gbc_lblDeveloper = new GridBagConstraints();
		gbc_lblDeveloper.anchor = GridBagConstraints.EAST;
		gbc_lblDeveloper.insets = new Insets(0, 0, 5, 5);
		gbc_lblDeveloper.gridx = 0;
		gbc_lblDeveloper.gridy = 8;
		m_contentPane.add(lblDeveloper, gbc_lblDeveloper);

		textField = new JTextField();
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 0);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 1;
		gbc_textField.gridy = 8;
		m_contentPane.add(textField, gbc_textField);
		textField.setColumns(10);

		lblInfo = new JLabel("Info");
		GridBagConstraints gbc_lblInfo = new GridBagConstraints();
		gbc_lblInfo.insets = new Insets(0, 0, 5, 5);
		gbc_lblInfo.gridx = 0;
		gbc_lblInfo.gridy = 9;
		m_contentPane.add(lblInfo, gbc_lblInfo);

		scrollPane_2 = new JScrollPane();
		GridBagConstraints gbc_scrollPane_2 = new GridBagConstraints();
		gbc_scrollPane_2.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane_2.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_2.gridx = 1;
		gbc_scrollPane_2.gridy = 9;
		m_contentPane.add(scrollPane_2, gbc_scrollPane_2);

		textArea = new JTextArea();
		textArea.setLineWrap(true);
		scrollPane_2.setViewportView(textArea);
		GridBagConstraints gbc_btnSave = new GridBagConstraints();
		gbc_btnSave.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnSave.gridx = 1;
		gbc_btnSave.gridy = 10;
		m_contentPane.add(btnSave, gbc_btnSave);

		ButtonGroup group = new ButtonGroup();

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
		BeanProperty<ApkBean, String> apkLocalPathProperty = BeanProperty
				.create("apkLocalPath");
		BeanProperty<JTextField, String> textProperty = BeanProperty
				.create("text");
		AutoBinding<ApkBean, String, JTextField, String> autoBinding = Bindings
				.createAutoBinding(UpdateStrategy.READ, apkBean,
						apkLocalPathProperty, apkLocalPathJTextField,
						textProperty);
		autoBinding.bind();
		//
		BeanProperty<ApkBean, String> packageNameProperty = BeanProperty
				.create("packageName");
		BeanProperty<JTextField, String> textProperty_1 = BeanProperty
				.create("text");
		AutoBinding<ApkBean, String, JTextField, String> autoBinding_1 = Bindings
				.createAutoBinding(UpdateStrategy.READ, apkBean,
						packageNameProperty, packageNameJTextField,
						textProperty_1);
		autoBinding_1.bind();
		//
		BeanProperty<ApkBean, Integer> versionCodeProperty = BeanProperty
				.create("versionCode");
		BeanProperty<JTextField, String> textProperty_2 = BeanProperty
				.create("text");
		AutoBinding<ApkBean, Integer, JTextField, String> autoBinding_2 = Bindings
				.createAutoBinding(UpdateStrategy.READ, apkBean,
						versionCodeProperty, versionCodeJTextField,
						textProperty_2);
		autoBinding_2.bind();
		//
		BeanProperty<ApkBean, String> versionNameProperty = BeanProperty
				.create("versionName");
		BeanProperty<JTextField, String> textProperty_3 = BeanProperty
				.create("text");
		AutoBinding<ApkBean, String, JTextField, String> autoBinding_3 = Bindings
				.createAutoBinding(UpdateStrategy.READ, apkBean,
						versionNameProperty, versionNameJTextField,
						textProperty_3);
		autoBinding_3.bind();
		//
		BeanProperty<ApkBean, String> mainNameProperty = BeanProperty
				.create("mainName");
		BeanProperty<JTextField, String> textProperty_4 = BeanProperty
				.create("text");
		AutoBinding<ApkBean, String, JTextField, String> autoBinding_4 = Bindings
				.createAutoBinding(UpdateStrategy.READ, apkBean,
						mainNameProperty, mainNameJTextField, textProperty_4);
		autoBinding_4.bind();
		//
		BeanProperty<ApkBean, List<String>> apkBeanBeanProperty = BeanProperty
				.create("apkPermission");
		JListBinding<String, ApkBean, JList> jListBinding = SwingBindings
				.createJListBinding(UpdateStrategy.READ, apkBean,
						apkBeanBeanProperty, list);
		jListBinding.bind();
		//
		BeanProperty<ApkBean, List<CertBean>> apkBeanBeanProperty_1 = BeanProperty
				.create("certs");
		JListBinding<CertBean, ApkBean, JList> jListBinding_1 = SwingBindings
				.createJListBinding(UpdateStrategy.READ, apkBean,
						apkBeanBeanProperty_1, list_1);
		//
		ELProperty<CertBean, Object> certBeanEvalutionProperty = ELProperty
				.create("${certificate}");
		jListBinding_1.setDetailBinding(certBeanEvalutionProperty);
		//
		jListBinding_1.bind();
		//
		JTableBinding<CertBean, ApkBean, JTable> jTableBinding = SwingBindings
				.createJTableBinding(UpdateStrategy.READ, apkBean,
						apkBeanBeanProperty_1, table);
		//
		BeanProperty<CertBean, String> certBeanBeanProperty = BeanProperty
				.create("devName");
		jTableBinding.addColumnBinding(certBeanBeanProperty).setColumnName(
				"Developer");
		//
		BeanProperty<CertBean, String> certBeanBeanProperty_1 = BeanProperty
				.create("note");
		jTableBinding.addColumnBinding(certBeanBeanProperty_1).setColumnName(
				"Note");
		//
		BeanProperty<CertBean, String> certBeanBeanProperty_2 = BeanProperty
				.create("officialSite");
		jTableBinding.addColumnBinding(certBeanBeanProperty_2).setColumnName(
				"Site");
		//
		BeanProperty<CertBean, String> certBeanBeanProperty_3 = BeanProperty
				.create("verifyStatus");
		jTableBinding.addColumnBinding(certBeanBeanProperty_3).setColumnName(
				"Status");
		//
		jTableBinding.bind();
		//
		BeanProperty<ApkBean, String> apkBeanBeanProperty_2 = BeanProperty
				.create("marketBean.marketDeveloper");
		BeanProperty<JTextField, String> jTextFieldBeanProperty = BeanProperty
				.create("text");
		AutoBinding<ApkBean, String, JTextField, String> autoBinding_5 = Bindings
				.createAutoBinding(UpdateStrategy.READ, apkBean,
						apkBeanBeanProperty_2, textField,
						jTextFieldBeanProperty);
		autoBinding_5.bind();
		//
		BeanProperty<ApkBean, String> apkBeanBeanProperty_3 = BeanProperty
				.create("marketBean.marketDescription");
		BeanProperty<JTextArea, String> jTextAreaBeanProperty = BeanProperty
				.create("text");
		AutoBinding<ApkBean, String, JTextArea, String> autoBinding_6 = Bindings
				.createAutoBinding(UpdateStrategy.READ, apkBean,
						apkBeanBeanProperty_3, textArea, jTextAreaBeanProperty);
		autoBinding_6.bind();
		//
		BindingGroup bindingGroup = new BindingGroup();
		//
		bindingGroup.addBinding(autoBinding);
		bindingGroup.addBinding(autoBinding_1);
		bindingGroup.addBinding(autoBinding_2);
		bindingGroup.addBinding(autoBinding_3);
		bindingGroup.addBinding(autoBinding_4);
		bindingGroup.addBinding(jListBinding);
		bindingGroup.addBinding(jListBinding_1);
		bindingGroup.addBinding(jTableBinding);
		bindingGroup.addBinding(autoBinding_5);
		bindingGroup.addBinding(autoBinding_6);
		return bindingGroup;
	}
}

class EditorFileDropPanel extends JPanel implements DropTargetListener {
	/**
	 * 
	 */
	private ApkEditor editor;
	private static final long serialVersionUID = -6019717972561019158L;

	public EditorFileDropPanel(ApkEditor editor) {
		this.editor = editor;
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
			editor.setStatus("working");
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
						ApkManDao dao = ApkManDao.GetInstance();
						// No such apk in objectdb
						if (dao.fillApk(apk) == null) {
							// No such apk in mysql
						}

						if (ApkEditorDAO.getApk(apk) == null) {
							// No such apk in objectdb
							int aaa = 0;
							aaa = 3;
						}

						this.editor.setApkBean(apk);
						System.out.println("Put apk " + apk.apkLocalPath);
					}
					// this.setText("无法解析\t" + f.getAbsolutePath());
					else {
						// this.setText(apk.toString());
						// Search database
						// dao.fillApk(apk);

					}
				}

				editor.setStatus("idle");
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

class EditorCustomListCellRenderer extends JPanel implements ListCellRenderer {
	private static final long serialVersionUID = 5682196012314929313L;
	private static final Border NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);
	private final JTextArea contentArea;
	private JList list;

	EditorCustomListCellRenderer(JList list) {
		this.list = list;
		contentArea = new JTextArea();
		contentArea.setLineWrap(true);
		contentArea.setWrapStyleWord(true);
		contentArea.setOpaque(true);
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
		contentArea.setSize(600, list.getHeight());
		contentArea.setText(value.toString());
		return this;
	}
}