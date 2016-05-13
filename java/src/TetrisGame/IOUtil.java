package TetrisGame;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class IOUtil {
	
	/**
	 * Returns the date in a String formatted as "MM/dd/yyyy HH:mm:ss".
	 * @return String - the current date
	 */
	public static String getDate() {
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss"); //"yyyy-MM-dd HH:mm:ss"
		return sdf.format(cal.getTime());
	}

	/**
	 * Returns a String representing the array as a comma separated list. No brackets are included.
	 * If the array given is empty, the String returned will be empty.
	 * <pre>example: "Apple, Banana, Cherry, Durian, Elderberries"
	 * (not necessarily alphabetized)</pre>
	 * @param array - String array to be built into one comma separated String
	 * @return Comma separated list of all Strings in the array
	 */
	public static Object ArrayToListString(String[] array) {
		if(array == null || array.length == 0) return "";
		StringBuilder build = new StringBuilder();
		for(int i = 0; i < array.length; i++) {
			build.append(array[i]);
			if(i+1 < array.length) build.append(", ");
		}
		return build.toString();
	}

	public static String path = "";
	
	static {
		IOUtil.path = new File(".").getAbsolutePath();
		IOUtil.path = IOUtil.path.replace('\\', '/');
		IOUtil.path = IOUtil.path.substring(0, IOUtil.path.lastIndexOf("/"));
		IOUtil.path = IOUtil.path.substring(0, IOUtil.path.lastIndexOf("/"));
	}
	
//	System.out.println("---- FILES ----");
//	try {
//		String[] files = getResourceListing(IOUtil.class, "");
//		for(String file : files) {
//			System.out.println(file);
//		}
//	} catch(URISyntaxException e) {
//		e.printStackTrace();
//	} catch(IOException e) {
//		e.printStackTrace();
//	}
//	  /**
//	   * List directory contents for a resource folder. Not recursive.
//	   * This is basically a brute-force implementation.
//	   * Works for regular files and also JARs.
//	   * 
//	   * @author Greg Briggs
//	   * @param clazz Any java class that lives in the same place as the resources you want.
//	   * @param path Should end with "/", but not start with one.
//	   * @return Just the name of each member item, not the full paths.
//	   * @throws URISyntaxException 
//	   * @throws IOException 
//	   */
//	  @SuppressWarnings("rawtypes")
//	static String[] getResourceListing(Class clazz, String path) throws URISyntaxException, IOException {
//	      URL dirURL = clazz.getClassLoader().getResource(path);
//	      if (dirURL != null && dirURL.getProtocol().equals("file")) {
//	        /* A file path: easy enough */
//	        return new File(dirURL.toURI()).list();
//	      } 
//
//	      if (dirURL == null) {
//	        /* 
//	         * In case of a jar file, we can't actually find a directory.
//	         * Have to assume the same jar as clazz.
//	         */
//	        String me = clazz.getName().replace(".", "/")+".class";
//	        dirURL = clazz.getClassLoader().getResource(me);
//	      }
//	      
//	      if (dirURL.getProtocol().equals("jar")) {
//	        /* A JAR path */
//	        String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!")); //strip out only the JAR file
//	        JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
//	        Enumeration<JarEntry> entries = jar.entries(); //gives ALL entries in jar
//	        Set<String> result = new HashSet<String>(); //avoid duplicates in case it is a subdirectory
//	        while(entries.hasMoreElements()) {
//	          String name = entries.nextElement().getName();
//	          if (name.startsWith(path)) { //filter according to the path
//	            String entry = name.substring(path.length());
//	            int checkSubdir = entry.indexOf("/");
//	            if (checkSubdir >= 0) {
//	              // if it is a subdirectory, we just return the directory name
//	              entry = entry.substring(0, checkSubdir);
//	            }
//	            result.add(entry);
//	          }
//	        }
//	        jar.close();
//	        return result.toArray(new String[result.size()]);
//	      } 
//	        
//	      throw new UnsupportedOperationException("Cannot list files for URL "+dirURL);
//	  }

	/**
	 * Will return an input stream for a file with the given path and name. If the file
	 * is included as part of the jar, then it will be loaded from the jar, otherwise the
	 * file must exist at the path provided relative to the same folder as the jar. If the
	 * file is in neither location, a null pointer is returned.
	 * 
	 * File path should use forward slashes regardless of operating system.
	 * 
	 * @param resource
	 * @return
	 */
	public static InputStream getResourceAsStream(String resource) {
		if(resource.toLowerCase().startsWith("http://")) {
			try {
				URL url = new URL(resource);
				return url.openStream();
			} catch(Exception e) {
				return null;
			}
		}
		
		String cpRef = resource.replace('\\', '/');
		System.out.println(cpRef);
		InputStream is = IOUtil.class.getResourceAsStream(cpRef);
		if(is != null) return is;

		File file = new File(path+resource);
		try {
			is = new FileInputStream(file);
			return is;
		} catch(FileNotFoundException e) {
			return null;
		}
	}
	
}
