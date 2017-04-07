package bit_dec.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileTools {
	/**
	 * 复制一个目录及其子目录、文件到另外一个目录
	 * @param src
	 * @param dest
	 * @throws IOException
	 */
	public static void copyFolder(File src, File dest) throws IOException {
		if (src.isDirectory()) {
			if (!dest.exists()) {
				dest.mkdir();
			}
			String files[] = src.list();
			for (String file : files) {
				File srcFile = new File(src, file);
				File destFile = new File(dest, file);
				copyFolder(srcFile, destFile);
			}
		} else {
			InputStream in = new FileInputStream(src);
			OutputStream out = new FileOutputStream(dest);

			byte[] buffer = new byte[1024];

			int length;
			
			while ((length = in.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}
			in.close();
			out.close();
		}
	}
	
	/**
	 * 复制一个文件
	 * @param src
	 * @param dest
	 * @throws IOException
	 */
	public static void copyFile(File src, File dest) throws IOException{
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dest);
		byte[] buffer = new byte[1024];
		int length;
		while ((length = in.read(buffer)) > 0) {
			out.write(buffer, 0, length);
		}
		in.close();
		out.close();
	}
	
	/**
	 * 删除文件夹
	 * @param file
	 */
	public static void deleteFile(File file) {
        if (file != null) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                //System.out.println(file.getPath());
                File[] subFiles = file.listFiles();
                int n = subFiles.length;
                for (int i = 0; i < n; i++) {
                    deleteFile(subFiles[i]);
                }
                file.delete();
            }
        }
    }
	
	
	/**
	 * Java文件去注释以及去掉特殊的字段（例如：super()|goto等）
	 * @param filePathAndName
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public static void clearComment(String filePathAndName)
			throws FileNotFoundException, UnsupportedEncodingException {
		StringBuffer buffer = new StringBuffer();
		String line = null;
		InputStream is = new FileInputStream(filePathAndName);
		BufferedReader reader = new BufferedReader(new InputStreamReader(is,
				"UTF-8"));
		try {
			line = reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		while (line != null) {
			buffer.append(line);
			buffer.append("\r\n");
			try {
				line = reader.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		String filecontent = buffer.toString();
		Map<String, String> patterns = new HashMap<String, String>();
		patterns.put("([^:])\\/\\/.*", "$1");
		patterns.put("\\s+\\/\\/.*", "");
		patterns.put("^\\/\\/.*", "");
		patterns.put("^\\/\\*\\*.*\\*\\/$", "");
		patterns.put("\\/\\*.*\\*\\/", "");
		patterns.put("/\\*(\\s*\\*\\s*.*\\s*?)*\\*\\/", "");
		Iterator<String> keys = patterns.keySet().iterator();
		String key = null, value = "";
		while (keys.hasNext()) {
			key = keys.next();
			value = patterns.get(key);
			filecontent = replaceAll(filecontent, key, value);
		}
		try {
			File f = new File(filePathAndName);
			if (!f.getParentFile().exists()) {
				f.getParentFile().mkdirs();
			}
			FileOutputStream out = new FileOutputStream(filePathAndName);
			byte[] bytes = filecontent.getBytes("UTF-8");
			out.write(bytes);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String replaceAll(String fileContent, String patternString,
			String replace) {
		String str = "";
		Matcher m = null;
		Pattern p = null;
		try {
			p = Pattern.compile(patternString);
			m = p.matcher(fileContent);
			str = m.replaceAll(replace);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			m = null;
			p = null;
		}
		return str;
	}
}
