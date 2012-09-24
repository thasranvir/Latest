/**
 * 
 */
package uk.co.anmedia.runnable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import uk.co.anmedia.util.Constant;

/**
 * @author admin
 * 
 */
public class StaticContentCopierRunnable implements Runnable {
	public StaticContentCopierRunnable() {
		super();
	}

	final Logger log = Logger.getLogger(this.getClass().getName());

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		long startTime = System.currentTimeMillis();
		log.info("\n**********************************************************\n");
		log.info("Starting copying static content to TVL bundle ......");
		int a = 0;
		String src = Constant.STATIC_DIR;
		String dest = Constant.DATE_DIR;

		File source = new File(src);

		if (!source.exists()) {
			log.error("Source File or directory does not exist.");
			System.exit(0);
		}

		File destination = new File(dest);

		if (!destination.exists()) {

			log.warn("The destination directory  " + dest
					+ " does not exists. Creating new directory...");

			destination.mkdir();
			try {
				copyDirectory(source, destination);
			} catch (IOException e) {
				log.error(e.getMessage());
				e.printStackTrace();
			}
			a = 1;
		} else {
			try {
				copyDirectory(source, destination);
			} catch (IOException e) {
				log.error(e.getMessage());
				e.printStackTrace();
			}
			a = 1;
		}

		if (a == 1) {
			try {
				if(destination.isDirectory()){
					File[] searchJson = destination.listFiles();
					for (File search : searchJson) {
						if(search.getName().equals("json")){
							if (!delete(search)) {
								throw new IOException("Unable to delete json folder");
							}
						}
						
					}
				}
			} catch (IOException e) {
				log.error(e.getMessage());
				e.printStackTrace();
			}

		} else if (a == 0) {
			System.exit(0);
		}
		log.info("Copied the content to TVL bundle ......");
		long stopTime = System.currentTimeMillis();
		log.info("Total processing time  for copy static content : "+ (stopTime - startTime));

	}

	public void copyDirectory(File sourceDir, File destDir)
			throws IOException {

		if (!destDir.exists()) {
			log.info("Creating " + destDir + " directory.. ");
			destDir.mkdir();
		}
		File[] children = sourceDir.listFiles();
		for (File sourceChild : children) {
			String name = sourceChild.getName();

			File destChild = new File(destDir, name);
			if (sourceChild.isDirectory()) {
				copyDirectory(sourceChild, destChild);
			} else {
				copyFile(sourceChild, destChild);
			}

		}
	}

	public void copyFile(File source, File dest) throws IOException {

		if (!dest.exists()) {
			dest.createNewFile();
		}
		InputStream in = null;
		OutputStream out = null;
		try {
			in = new FileInputStream(source);
			out = new FileOutputStream(dest);
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
		}catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}finally {
			in.close();
			out.close();
		}
	}

	public boolean delete(File resource) throws IOException {
		if (resource.isDirectory() ) {
			File[] childFiles = resource.listFiles();
			for (File child : childFiles) {
				delete(child);
			}
		}
		return resource.delete();
	}

}
