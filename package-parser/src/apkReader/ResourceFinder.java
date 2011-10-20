package apkReader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

public class ResourceFinder {

	public boolean ISDEBUG = false;

	private ArrayList<String> resIdList;
	private Map<String, ArrayList<String>> responseMap;

	static final boolean DEBUG = false;

	static final short RES_STRING_POOL_TYPE = 0x0001;
	static final short RES_TABLE_TYPE = 0x0002;
	static final short RES_TABLE_PACKAGE_TYPE = 0x0200;
	static final short RES_TABLE_TYPE_TYPE = 0x0201;
	static final short RES_TABLE_TYPE_SPEC_TYPE = 0x0202;

	String[] valueStringPool = null;
	String[] typeStringPool = null;
	String[] keyStringPool = null;

	private int package_id = 0;

	// Contains no data.
	final static byte TYPE_NULL = 0x00;
	// The 'data' holds a ResTable_ref, a reference to another resource
	// table entry.
	final static byte TYPE_REFERENCE = 0x01;
	// The 'data' holds an attribute resource identifier.
	final static byte TYPE_ATTRIBUTE = 0x02;
	// The 'data' holds an index into the containing resource table's
	// global value string pool.
	final static byte TYPE_STRING = 0x03;
	// The 'data' holds a single-precision floating point number.
	final static byte TYPE_FLOAT = 0x04;
	// The 'data' holds a complex number encoding a dimension value,
	// such as "100in".
	final static byte TYPE_DIMENSION = 0x05;
	// The 'data' holds a complex number encoding a fraction of a
	// container.
	final static byte TYPE_FRACTION = 0x06;
	// The 'data' is a raw integer value of the form n..n.
	final static byte TYPE_INT_DEC = 0x10;
	// The 'data' is a raw integer value of the form 0xn..n.
	final static byte TYPE_INT_HEX = 0x11;
	// The 'data' is either 0 or 1, for input "false" or "true" respectively.
	final static byte TYPE_INT_BOOLEAN = 0x12;
	// The 'data' is a raw integer value of the form #aarrggbb.
	final static byte TYPE_INT_COLOR_ARGB8 = 0x1c;
	// The 'data' is a raw integer value of the form #rrggbb.
	final static byte TYPE_INT_COLOR_RGB8 = 0x1d;
	// The 'data' is a raw integer value of the form #argb.
	final static byte TYPE_INT_COLOR_ARGB4 = 0x1e;
	// The 'data' is a raw integer value of the form #rgb.
	final static byte TYPE_INT_COLOR_RGB4 = 0x1f;

	Map<String, ArrayList<String>> processResourceTable(ByteBuffer bb,
			ArrayList<String> resIdList) throws Exception {
		// Resource table structure
		//
		this.resIdList = resIdList;
		this.responseMap = new HashMap<String, ArrayList<String>>();

		short type = bb.getShort();
		short headerSize = bb.getShort();
		int size = bb.getInt();
		int packageCount = bb.getInt();

		if (type != RES_TABLE_TYPE) {
			throw new Exception("No RES_TABLE_TYPE found!");
		}
		if (size != bb.capacity()) {
			throw new Exception(
					"The buffer size not matches to the resource table size.");
		}

		int realStringPoolCount = 0;
		int realPackageCount = 0;

		while (true) {
			int pos = bb.position();
			short t = bb.getShort();
			short hs = bb.getShort();
			int s = bb.getInt();

			if (t == RES_STRING_POOL_TYPE) {
				// Process the string pool
				if (realStringPoolCount == 0) {
					// Only the first string pool is processed.
					if (ISDEBUG)
						System.out.println("Processing the string pool ...");

					byte[] buffer = new byte[s];
					bb.position(pos);
					bb.get(buffer);

					ByteBuffer bb2 = ByteBuffer.wrap(buffer);
					bb2.order(ByteOrder.LITTLE_ENDIAN);
					valueStringPool = processStringPool(bb2);
				}
				realStringPoolCount++;
			} else if (t == RES_TABLE_PACKAGE_TYPE) {
				// Process the package
				if (ISDEBUG)
					System.out.println("Processing the package "
							+ realPackageCount + " ...");

				byte[] buffer = new byte[s];
				bb.position(pos);
				bb.get(buffer);

				ByteBuffer bb2 = ByteBuffer.wrap(buffer);
				bb2.order(ByteOrder.LITTLE_ENDIAN);
				processPackage(bb2);

				realPackageCount++;
			} else {
				System.err.println("Unsupported type");
			}

			bb.position(pos + s);
			if (!bb.hasRemaining())
				break;
		}

		if (realStringPoolCount != 1) {
			throw new Exception("More than 1 string pool found!");
		}
		if (realPackageCount != packageCount) {
			throw new Exception(
					"Real package count not equals the declared count.");
		}

		return responseMap;
	}

	private void processPackage(ByteBuffer bb) throws Exception {
		// Package structure
		//
		short type = bb.getShort();
		short headerSize = bb.getShort();
		int size = bb.getInt();

		int id = bb.getInt();
		package_id = id;

		char[] name = new char[128];
		for (int i = 0; i < 128; ++i) {
			name[i] = bb.getChar();
		}
		int typeStrings = bb.getInt();
		int lastPublicType = bb.getInt();
		int keyStrings = bb.getInt();
		int lastPublicKey = bb.getInt();

		if (typeStrings != headerSize) {
			throw new Exception(
					"TypeStrings must immediately following the package structure header.");
		}

		// Type strings
		//
		if (ISDEBUG)
			System.out.println("Type strings:");
		bb.position(typeStrings);
		ByteBuffer bbTypeStrings = bb.slice();
		bbTypeStrings.order(ByteOrder.LITTLE_ENDIAN);
		typeStringPool = processStringPool(bbTypeStrings);

		// Key strings
		//
		if (ISDEBUG)
			System.out.println("Key strings:");
		bb.position(keyStrings);
		short key_type = bb.getShort();
		short key_headerSize = bb.getShort();
		int key_size = bb.getInt();

		bb.position(keyStrings);
		ByteBuffer bbKeyStrings = bb.slice();
		bbKeyStrings.order(ByteOrder.LITTLE_ENDIAN);
		keyStringPool = processStringPool(bbKeyStrings);

		// Iterate through all chunks
		//
		int typeSpecCount = 0;
		int typeCount = 0;

		bb.position(keyStrings + key_size);
		while (true) {
			int pos = bb.position();
			short t = bb.getShort();
			short hs = bb.getShort();
			int s = bb.getInt();

			if (t == RES_TABLE_TYPE_SPEC_TYPE) {
				// Process the string pool
				byte[] buffer = new byte[s];
				bb.position(pos);
				bb.get(buffer);

				ByteBuffer bb2 = ByteBuffer.wrap(buffer);
				bb2.order(ByteOrder.LITTLE_ENDIAN);
				processTypeSpec(bb2);

				typeSpecCount++;
			} else if (t == RES_TABLE_TYPE_TYPE) {
				// Process the package
				byte[] buffer = new byte[s];
				bb.position(pos);
				bb.get(buffer);

				ByteBuffer bb2 = ByteBuffer.wrap(buffer);
				bb2.order(ByteOrder.LITTLE_ENDIAN);
				processType(bb2);

				typeCount++;
			}

			bb.position(pos + s);
			if (!bb.hasRemaining())
				break;
		}

		return;
	}

	Hashtable<Integer, ArrayList<String>> entryMap = new Hashtable<Integer, ArrayList<String>>();

	private void processType(ByteBuffer bb) throws Exception {
		short type = bb.getShort();
		short headerSize = bb.getShort();
		int size = bb.getInt();
		byte id = bb.get();
		byte res0 = bb.get();
		short res1 = bb.getShort();
		int entryCount = bb.getInt();
		int entriesStart = bb.getInt();

		// Map<Integer, ArrayList<String>> refKeys = new HashMap<Integer,
		// ArrayList<String>>();
		Hashtable<String, Integer> refKeys = new Hashtable<String, Integer>();
		// if(ISDEBUG) System.out.println("Processing type " + typeStringPool[id
		// - 1] + "...");

		int config_size = bb.getInt();

		// Skip the config data
		bb.position(headerSize);

		if (headerSize + entryCount * 4 != entriesStart) {
			throw new Exception(
					"HeaderSize, entryCount and entriesStart are not valid.");
		}

		// Start to get entry indices
		//
		int[] entryIndices = new int[entryCount];
		for (int i = 0; i < entryCount; ++i) {
			entryIndices[i] = bb.getInt();
		}

		// Get entries
		//
		for (int i = 0; i < entryCount; ++i) {
			if (entryIndices[i] == -1)
				continue;

			int resource_id = (package_id << 24) | (id << 16) | i;

			int pos = bb.position();
			short entry_size = bb.getShort();
			short entry_flag = bb.getShort();
			int entry_key = bb.getInt();

			// Get the value (simple) or map (complex)

			final int FLAG_COMPLEX = 0x0001;
			if ((entry_flag & FLAG_COMPLEX) == 0) {
				// Simple case
				short value_size = bb.getShort();
				byte value_res0 = bb.get();
				byte value_dataType = bb.get();
				int value_data = bb.getInt();

				String idStr = Integer.toHexString(resource_id);
				String keyStr = keyStringPool[entry_key];
				String data = null;

				if (ISDEBUG)
					System.out.println("Entry 0x" + idStr + ", key: " + keyStr
							+ ", simple value type: ");

				ArrayList<String> entryArr = entryMap.get(Integer.valueOf(
						idStr, 16));
				if (entryArr == null)
					entryArr = new ArrayList<String>();
				entryArr.add(keyStr);
				entryMap.put(Integer.valueOf(idStr, 16), entryArr);
				// printDataType(value_dataType);
				if (value_dataType == TYPE_STRING) {
					data = valueStringPool[value_data];
					if (ISDEBUG)
						System.out.println(", data: "
								+ valueStringPool[value_data] + "");
				} else if (value_dataType == TYPE_REFERENCE) {
					String hexIndex = Integer.toHexString(new Integer(
							value_data));
					refKeys.put(idStr, value_data);
					// data=hexIndex;
					// ArrayList<String> refValues=refKeys.get(value_data);
					// if(refValues==null){
					// ArrayList<String> values=new ArrayList<String>();
					// refKeys.put(value_data, value)
					// }
				} else {
					data = new Integer(value_data).toString();
					if (ISDEBUG)
						System.out.println(", data: " + value_data + "");
				}

				// if (inReqList("@" + idStr)) {
				putIntoMap("@" + idStr, data);
				// }
			} else {
				// Complex case
				int entry_parent = bb.getInt();
				int entry_count = bb.getInt();

				for (int j = 0; j < entry_count; ++j) {
					int ref_name = bb.getInt();
					short value_size = bb.getShort();
					byte value_res0 = bb.get();
					byte value_dataType = bb.get();
					int value_data = bb.getInt();
				}

				if (ISDEBUG)
					System.out.println("Entry 0x"
							+ Integer.toHexString(resource_id) + ", key: "
							+ keyStringPool[entry_key]
							+ ", complex value, not printed.");
			}
		}
		Set<String> refKs = refKeys.keySet();

		for (String refK : refKs) {
			ArrayList<String> values = responseMap.get("@"
					+ Integer.toHexString(refKeys.get(refK)).toUpperCase());
			if (values != null)
				for (String value : values) {
					putIntoMap("@" + refK, value);
				}
			// if (value != null)
		}
		return;
	}

	private String[] processStringPool(ByteBuffer bb) throws Exception {
		// String pool structure
		//
		short type = bb.getShort();
		short headerSize = bb.getShort();
		int size = bb.getInt();
		int stringCount = bb.getInt();
		int styleCount = bb.getInt();
		int flags = bb.getInt();
		int stringsStart = bb.getInt();
		int stylesStart = bb.getInt();

		boolean isUTF_8 = (flags & 256) != 0;

		int[] offsets = new int[stringCount];
		for (int i = 0; i < stringCount; ++i) {
			offsets[i] = bb.getInt();
		}

		String[] strings = new String[stringCount];
		
		for (int i = 0; i < stringCount; ++i) {
			int pos = stringsStart + offsets[i];
			short len = bb.getShort(pos);
			if (len < 0) {
				short extendShort = bb.getShort();
			}

			pos += 2;
			strings[i] = "";

			if (isUTF_8) {
				int start = pos;
				int length = 0;
				while (bb.get(pos) != 0) {
					length++;
					pos++;
				}
				byte[] oneData = new byte[length];
				if (length > 0) {
					byte[] byteArray = bb.array();
					for (int k = 0; k < length; k++) {
						oneData[k] = byteArray[start + k];
					}
				}
				if (oneData.length > 0)
					strings[i] = new String(oneData, "utf-8");
				else
					strings[i] = "";
			} else {
				char c;
				while ((c = bb.getChar(pos)) != 0) {
					strings[i] += c;
					pos += 2;
				}
			}
			if (ISDEBUG)
				System.out.println("String " + i + " : " + strings[i] + "");
		}

		return strings;
	}

	private void processTypeSpec(ByteBuffer bb) throws Exception {
		short type = bb.getShort();
		short headerSize = bb.getShort();
		int size = bb.getInt();
		byte id = bb.get();
		byte res0 = bb.get();
		short res1 = bb.getShort();
		int entryCount = bb.getInt();

		if (ISDEBUG)
			System.out.println("Processing type spec " + typeStringPool[id - 1]
					+ "...");

		int[] flags = new int[entryCount];
		for (int i = 0; i < entryCount; ++i) {
			flags[i] = bb.getInt();
		}

		return;
	}

	private void putIntoMap(String resId, String value) {
		ArrayList<String> valueList = responseMap.get(resId.toUpperCase());
		if (valueList == null) {
			valueList = new ArrayList<String>();
		}
		valueList.add(value);
		responseMap.put(resId.toUpperCase(), valueList);
		return;
		//
		// if (valueList != null) {
		// // if(ISDEBUG) System.out.println("Find New Value Multi");
		// valueList.add(value);
		// } else {
		// // if(ISDEBUG) System.out.println("Find New Value:"+value);
		// valueList = new ArrayList<String>();
		// valueList.add(value);
		// responseMap.put(resId.toUpperCase(), valueList);
		// }
	}

	private boolean inReqList(String resId) {
		// for (String str : resIdList) {
		// if (str.toUpperCase().equals(resId.toUpperCase()))
		return true;
		// }
		// return false;
	}

}
