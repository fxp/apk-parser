package org.fxp.android.apk.tester;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.security.cert.Certificate;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.fxp.android.apk.ApkBean;
import org.fxp.crawler.bean.CertBean;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;

public class ApkBrowser extends JPanel {

	private static final long serialVersionUID = -1760921084180001932L;
	private BindingGroup m_bindingGroup;
	private org.fxp.android.apk.ApkBean apkBean = new org.fxp.android.apk.ApkBean();
	private JTextField apkFileChecksumJTextField;
	private JTextField apkLocalPathJTextField;
	private JTextField mainNameJTextField;
	private JTextField miscJTextField;
	private JTextField packageNameJTextField;
	private JTextField versionCodeJTextField;
	private JTextField versionNameJTextField;
	private JList list_1;

	
	public ApkBrowser(org.fxp.android.apk.ApkBean newApkBean) {
		this();
		
	}

	public ApkBrowser() {
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 1.0E-4 };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 1.0, 1.0E-4 };
		setLayout(gridBagLayout);

		JLabel apkFileChecksumLabel = new JLabel("ApkFileChecksum:");
		GridBagConstraints labelGbc_0 = new GridBagConstraints();
		labelGbc_0.insets = new Insets(5, 5, 5, 5);
		labelGbc_0.gridx = 0;
		labelGbc_0.gridy = 0;
		add(apkFileChecksumLabel, labelGbc_0);

		apkFileChecksumJTextField = new JTextField();
		GridBagConstraints componentGbc_0 = new GridBagConstraints();
		componentGbc_0.insets = new Insets(5, 0, 5, 0);
		componentGbc_0.fill = GridBagConstraints.HORIZONTAL;
		componentGbc_0.gridx = 1;
		componentGbc_0.gridy = 0;
		add(apkFileChecksumJTextField, componentGbc_0);

		JLabel apkLocalPathLabel = new JLabel("ApkLocalPath:");
		GridBagConstraints labelGbc_1 = new GridBagConstraints();
		labelGbc_1.insets = new Insets(5, 5, 5, 5);
		labelGbc_1.gridx = 0;
		labelGbc_1.gridy = 1;
		add(apkLocalPathLabel, labelGbc_1);

		apkLocalPathJTextField = new JTextField();
		GridBagConstraints componentGbc_1 = new GridBagConstraints();
		componentGbc_1.insets = new Insets(5, 0, 5, 0);
		componentGbc_1.fill = GridBagConstraints.HORIZONTAL;
		componentGbc_1.gridx = 1;
		componentGbc_1.gridy = 1;
		add(apkLocalPathJTextField, componentGbc_1);

		JLabel mainNameLabel = new JLabel("MainName:");
		GridBagConstraints labelGbc_2 = new GridBagConstraints();
		labelGbc_2.insets = new Insets(5, 5, 5, 5);
		labelGbc_2.gridx = 0;
		labelGbc_2.gridy = 2;
		add(mainNameLabel, labelGbc_2);

		mainNameJTextField = new JTextField();
		GridBagConstraints componentGbc_2 = new GridBagConstraints();
		componentGbc_2.insets = new Insets(5, 0, 5, 0);
		componentGbc_2.fill = GridBagConstraints.HORIZONTAL;
		componentGbc_2.gridx = 1;
		componentGbc_2.gridy = 2;
		add(mainNameJTextField, componentGbc_2);

		JLabel miscLabel = new JLabel("Misc:");
		GridBagConstraints labelGbc_3 = new GridBagConstraints();
		labelGbc_3.insets = new Insets(5, 5, 5, 5);
		labelGbc_3.gridx = 0;
		labelGbc_3.gridy = 3;
		add(miscLabel, labelGbc_3);

		miscJTextField = new JTextField();
		GridBagConstraints componentGbc_3 = new GridBagConstraints();
		componentGbc_3.insets = new Insets(5, 0, 5, 0);
		componentGbc_3.fill = GridBagConstraints.HORIZONTAL;
		componentGbc_3.gridx = 1;
		componentGbc_3.gridy = 3;
		add(miscJTextField, componentGbc_3);

		JLabel packageNameLabel = new JLabel("PackageName:");
		GridBagConstraints labelGbc_4 = new GridBagConstraints();
		labelGbc_4.insets = new Insets(5, 5, 5, 5);
		labelGbc_4.gridx = 0;
		labelGbc_4.gridy = 4;
		add(packageNameLabel, labelGbc_4);

		packageNameJTextField = new JTextField();
		GridBagConstraints componentGbc_4 = new GridBagConstraints();
		componentGbc_4.insets = new Insets(5, 0, 5, 0);
		componentGbc_4.fill = GridBagConstraints.HORIZONTAL;
		componentGbc_4.gridx = 1;
		componentGbc_4.gridy = 4;
		add(packageNameJTextField, componentGbc_4);

		JLabel versionCodeLabel = new JLabel("VersionCode:");
		GridBagConstraints labelGbc_5 = new GridBagConstraints();
		labelGbc_5.insets = new Insets(5, 5, 5, 5);
		labelGbc_5.gridx = 0;
		labelGbc_5.gridy = 5;
		add(versionCodeLabel, labelGbc_5);

		versionCodeJTextField = new JTextField();
		GridBagConstraints componentGbc_5 = new GridBagConstraints();
		componentGbc_5.insets = new Insets(5, 0, 5, 0);
		componentGbc_5.fill = GridBagConstraints.HORIZONTAL;
		componentGbc_5.gridx = 1;
		componentGbc_5.gridy = 5;
		add(versionCodeJTextField, componentGbc_5);

		JLabel versionNameLabel = new JLabel("VersionName:");
		GridBagConstraints labelGbc_6 = new GridBagConstraints();
		labelGbc_6.insets = new Insets(5, 5, 5, 5);
		labelGbc_6.gridx = 0;
		labelGbc_6.gridy = 6;
		add(versionNameLabel, labelGbc_6);

		versionNameJTextField = new JTextField();
		GridBagConstraints componentGbc_6 = new GridBagConstraints();
		componentGbc_6.insets = new Insets(5, 0, 5, 0);
		componentGbc_6.fill = GridBagConstraints.HORIZONTAL;
		componentGbc_6.gridx = 1;
		componentGbc_6.gridy = 6;
		add(versionNameJTextField, componentGbc_6);
		
		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 1;
		gbc_scrollPane.gridy = 7;
		add(scrollPane, gbc_scrollPane);
		
		list_1 = new JList();
		list_1.setCellRenderer(new CustomListCellRenderer());
		scrollPane.setViewportView(list_1);

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
		BeanProperty<ApkBean, String> miscProperty = BeanProperty.create("misc");
		BeanProperty<JTextField, String> textProperty_3 = BeanProperty.create("text");
		AutoBinding<ApkBean, String, JTextField, String> autoBinding_3 = Bindings.createAutoBinding(UpdateStrategy.READ, apkBean, miscProperty, miscJTextField, textProperty_3);
		autoBinding_3.bind();
		//
		BeanProperty<ApkBean, String> packageNameProperty = BeanProperty.create("packageName");
		BeanProperty<JTextField, String> textProperty_4 = BeanProperty.create("text");
		AutoBinding<ApkBean, String, JTextField, String> autoBinding_4 = Bindings.createAutoBinding(UpdateStrategy.READ, apkBean, packageNameProperty, packageNameJTextField, textProperty_4);
		autoBinding_4.bind();
		//
		BeanProperty<ApkBean, Integer> versionCodeProperty = BeanProperty.create("versionCode");
		BeanProperty<JTextField, String> textProperty_5 = BeanProperty.create("text");
		AutoBinding<ApkBean, Integer, JTextField, String> autoBinding_5 = Bindings.createAutoBinding(UpdateStrategy.READ, apkBean, versionCodeProperty, versionCodeJTextField, textProperty_5);
		autoBinding_5.bind();
		//
		BeanProperty<ApkBean, String> versionNameProperty = BeanProperty.create("versionName");
		BeanProperty<JTextField, String> textProperty_6 = BeanProperty.create("text");
		AutoBinding<ApkBean, String, JTextField, String> autoBinding_6 = Bindings.createAutoBinding(UpdateStrategy.READ, apkBean, versionNameProperty, versionNameJTextField, textProperty_6);
		autoBinding_6.bind();
		//
		BeanProperty<ApkBean, List<CertBean>> apkBeanBeanProperty = BeanProperty.create("certs");
		JListBinding<CertBean, ApkBean, JList> jListBinding = SwingBindings.createJListBinding(UpdateStrategy.READ, apkBean, apkBeanBeanProperty, list_1);
		//
		BeanProperty<CertBean, Certificate> certBeanBeanProperty = BeanProperty.create("certificate");
		jListBinding.setDetailBinding(certBeanBeanProperty);
		//
		jListBinding.bind();
		//
		BindingGroup bindingGroup = new BindingGroup();
		//
		bindingGroup.addBinding(autoBinding);
		bindingGroup.addBinding(autoBinding_1);
		bindingGroup.addBinding(autoBinding_2);
		bindingGroup.addBinding(autoBinding_3);
		bindingGroup.addBinding(autoBinding_4);
		bindingGroup.addBinding(autoBinding_5);
		bindingGroup.addBinding(autoBinding_6);
		bindingGroup.addBinding(jListBinding);
		return bindingGroup;
	}
}
