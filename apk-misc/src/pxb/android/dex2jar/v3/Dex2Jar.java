/*
 * Copyright (c) 2009-2010 Panxiaobo
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pxb.android.dex2jar.v3;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.fxp.android.apk.ApkBean;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pxb.android.dex2jar.ClassVisitorFactory;
import pxb.android.dex2jar.Version;
import pxb.android.dex2jar.reader.DexFileReader;

/**
 * @author Panxiaobo [pxb1988@126.com]
 * @version $Id$
 */
public class Dex2Jar {

	private static final Logger log = LoggerFactory.getLogger(Dex2Jar.class);

	/**
	 * @param args
	 */
	public static void main(String... args) {
		System.out.println("version:" + Version.getVersionString());
		if (args.length == 0) {
			System.err.println("dex2jar file1.dexORapk file2.dexORapk ...");
			return;
		}
		String jreVersion = System.getProperty("java.specification.version");
		if (jreVersion.compareTo("1.6") < 0) {
			System.err.println("A JRE version >=1.6 is required");
			return;
		}

		for (String file : args) {
			File dex = new File(file);
			final File gen = new File(file + ".dex2jar.jar");
			log.info("dex2jar {} -> {}", dex, gen);
			try {
				doFile(dex, gen);
			} catch (IOException e) {
				log.warn("Exception while process file " + dex, e);
			}
		}
		System.out.println("Done.");
	}

	public static void doApkData(byte[] data, final ApkBean apk) throws IOException {
		
		DexFileReader reader = new DexFileReader(data);
		V3AccessFlagsAdapter afa = new V3AccessFlagsAdapter();
		reader.accept(afa);
		reader.accept(new V3(afa.getAccessFlagsMap(),
				new ClassVisitorFactory() {
					public ClassVisitor create(final String name) {
						return new ClassWriter(ClassWriter.COMPUTE_MAXS) {
							/*
							 * (non-Javadoc)
							 * 
							 * @see org.objectweb.asm.ClassWriter#visitEnd()
							 */
							@Override
							public void visitEnd() {
								super.visitEnd();
//								apk.classNames.add(name);

								byte[] data = this.toByteArray();
								String strAsm=new String(data);
								if(strAsm.contains("http:")){
									int offset=strAsm.indexOf("http:");	
									apk.classNames.add(strAsm.substring(offset,offset+50));									
//									System.out.println("Got "+strAsm.substring(offset,offset+50));
								}
								
							}

							@Override
							public AnnotationVisitor visitAnnotation(
									String desc, boolean visible) {
								// TODO Auto-generated method stub
								return super.visitAnnotation(desc, visible);
							}

							@Override
							public void visitAttribute(Attribute attr) {
								// TODO Auto-generated method stub
								super.visitAttribute(attr);
							}

							@Override
							public FieldVisitor visitField(int access,
									String name, String desc, String signature,
									Object value) {
								// TODO Auto-generated method stub
								return super.visitField(access, name, desc, signature, value);
							}

							@Override
							public void visitInnerClass(String name,
									String outerName, String innerName,
									int access) {
								// TODO Auto-generated method stub
								super.visitInnerClass(name, outerName, innerName, access);
							}

							@Override
							public MethodVisitor visitMethod(int access,
									String name, String desc, String signature,
									String[] exceptions) {
								// TODO Auto-generated method stub
								return super.visitMethod(access, name, desc, signature, exceptions);
							}

							@Override
							public void visitOuterClass(String owner,
									String name, String desc) {
								// TODO Auto-generated method stub
								super.visitOuterClass(owner, name, desc);
							}

							@Override
							public void visitSource(String file, String debug) {
								// TODO Auto-generated method stub
								super.visitSource(file, debug);
							}
							
						};
					}
				}));
	}
	
	public static void doData(byte[] data, File destJar) throws IOException {
		final ZipOutputStream zos = new ZipOutputStream(
				FileUtils.openOutputStream(destJar));

		DexFileReader reader = new DexFileReader(data);
		V3AccessFlagsAdapter afa = new V3AccessFlagsAdapter();
		reader.accept(afa);
		reader.accept(new V3(afa.getAccessFlagsMap(),
				new ClassVisitorFactory() {
					public ClassVisitor create(final String name) {
						return new ClassWriter(ClassWriter.COMPUTE_MAXS) {
							/*
							 * (non-Javadoc)
							 * 
							 * @see org.objectweb.asm.ClassWriter#visitEnd()
							 */
							@Override
							public void visitEnd() {
								super.visitEnd();
								try {
									byte[] data = this.toByteArray();
									ZipEntry entry = new ZipEntry(name
											+ ".class");

									String strAsm=new String(data);
									if(strAsm.contains("http:")){
										int offset=strAsm.indexOf("http:");										
										System.out.println("Got "+strAsm.substring(offset,offset+50));
									}
//									Pattern p = Pattern.compile("(http://.+)");
//									Matcher m = p.matcher(strAsm);
//									if(m.matches()){
//										for(int i=0;i<m.groupCount();i++)
//											System.out.println(m.group(i));
//									}
									
									zos.putNextEntry(entry);
									zos.write(data);
									zos.closeEntry();
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						};
					}
				}));
		zos.finish();
		zos.close();
	}

	public static void doFile(File srcDex) throws IOException {
		doFile(srcDex, new File(srcDex.getParentFile(), srcDex.getName()
				+ ".dex2jar.jar"));
	}

	public static void doFile(File srcDex, File destJar) throws IOException {
		byte[] data = FileUtils.readFileToByteArray(srcDex);
		// checkMagic
		if ("dex".equals(new String(data, 0, 3))) {// dex
			doData(data, destJar);
		} else if ("PK".equals(new String(data, 0, 2))) {// ZIP
			ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(
					data));
			for (ZipEntry entry = zis.getNextEntry(); entry != null; entry = zis
					.getNextEntry()) {
				if (entry.getName().equals("classes.dex")) {
					data = IOUtils.toByteArray(zis);
					doData(data, destJar);
				}
			}
		} else {
			throw new RuntimeException(
					"the src file not a .dex file or a zip file");
		}
	}

	// Added by Fxp
	public static void doApk(String apkPath, ApkBean apk) throws IOException {
		byte[] data = FileUtils.readFileToByteArray(new File(apkPath));
		if(data==null)
			return;
		if ("PK".equals(new String(data, 0, 2))) {// ZIP
			ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(
					data));
			for (ZipEntry entry = zis.getNextEntry(); entry != null; entry = zis
					.getNextEntry()) {
				if (entry.getName().equals("classes.dex")) {
					data = IOUtils.toByteArray(zis);
					doApkData(data, apk);
				}
			}
		}
	}

}
