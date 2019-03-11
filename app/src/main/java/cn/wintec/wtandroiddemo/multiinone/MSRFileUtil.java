package cn.wintec.wtandroiddemo.multiinone;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

//import com.andorid.RM2091demo.RM2091Application;
//import com.gary.msr.MainApplication;
import  cn.wintec.wtandroidjar.multiinone.MSRLayerParams.MSRLayer1Params;
import  cn.wintec.wtandroidjar.multiinone.MSRLayerParams.MSRLayer2Params;
import  cn.wintec.wtandroidjar.multiinone.MSRLayerParams.MSRLayerNParam;

public class MSRFileUtil {
	private static MSRLayer1Params param1;
	private static MSRLayer2Params param2;
	private static List<MSRLayerNParam> paramns = new ArrayList<MSRLayerNParam>();

	public static List<MSRLayerNParam> getParamns() {
		return paramns;
	}

	public static MSRLayer1Params getParam1() {
		return param1;
	}

	public static MSRLayer2Params getParam2() {
		return param2;
	}

	public static void setParam1(MSRLayer1Params _param1) {
		param1 = _param1;
	}

	public static void setParamns(List<MSRLayerNParam> paramns) {
		MSRFileUtil.paramns = paramns;
	}

	public static void setParam2(MSRLayer2Params _param2) {
		param2 = _param2;
	}

	public static void writeParams(String filename) {
		if (param1 == null || param2 == null)
			return;
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		File f = new File(MainApplication.getInstance().getFilesDir(), filename);
		//File f = new File("", filename);
		try {
			fos = new FileOutputStream(f);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(param1);
			oos.writeObject(param2);
			oos.writeObject(paramns);
			oos.flush();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (oos != null)
					oos.close();
				if (fos != null)
					fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void readParams(String filename) {
		param1 = null;
		param2 = null;

		FileInputStream fis = null;
		ObjectInputStream ois = null;
		File f = new File(MainApplication.getInstance().getFilesDir(), filename);
		//File f = new File("", filename);
		try {
			fis = new FileInputStream(f);
			ois = new ObjectInputStream(fis);
			param1 = (MSRLayer1Params) ois.readObject();
			param2 = (MSRLayer2Params) ois.readObject();
			paramns = (List<MSRLayerNParam>) ois.readObject();
		} catch (FileNotFoundException e) {
			e.printStackTrace();

		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ois != null)
					ois.close();
				if (fis != null)
					fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static String[] getFilenames() {
		File dir = MultiInOneApplication.getInstance().getApplicationContext()
				.getFilesDir();
		return dir.list(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String filename) {
				if (filename == null)
					return false;
				File f = new File(dir, filename);
				if (f.exists() && f.isFile())
					return true;
				return false;
			}
		});
	}

	public static boolean isFileExist(String filename) {
		String fnames[] = getFilenames();
		for (String fname : fnames) {
			File f = new File(fname);
			if (f.getName().equals(filename))
				return true;
		}
		return false;
	}
}
