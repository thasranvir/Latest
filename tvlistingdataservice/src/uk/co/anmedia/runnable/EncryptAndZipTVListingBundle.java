package uk.co.anmedia.runnable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import uk.co.anmedia.util.Constant;

import de.idyl.crypto.zip.impl.AesZipFileEncrypter;

public class EncryptAndZipTVListingBundle implements Runnable {
	private final static Logger log = Logger.getLogger(EncryptAndZipTVListingBundle.class);
	private final String srcFolder = Constant.DATE_DIR;
	private final String destFolder = Constant.HOME_DIR+new SimpleDateFormat("yyyyMMdd").format(new Date())+"_TVL.zip";
	
	private String getpassword(){
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		String month = ((cal.get(Calendar.MONTH)+1) < 10) ? "0" + (cal.get(Calendar.MONTH)+1) : "" + (cal.get(Calendar.MONTH)+1);
		String day = (cal.get(Calendar.DATE) < 10) ? "0" + cal.get(Calendar.DATE) : "" + cal.get(Calendar.DATE) ;
		System.out.println("Year : "+year);
		System.out.println("Day : "+day);
		System.out.println("Month : "+month);
		String password = "$"+year+"$"+month+"$"+day+"$Kf$4m4z0n$";
		return password;
	}

	@Override
	public void run() {
		log.info("entering  " + this.getClass().getName() + "  run()");
		try {
			// name of zip file to create

			// create ZipOutputStream object
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(destFolder));

			// path to the folder to be zipped
			File zipFolder = new File(srcFolder);

			// get path prefix so that the zip file does not contain the whole
			// path
			// eg. if folder to be zipped is /com/home/test
			// the zip file when opened will have test folder and not
			// com/home/test folder
			int len = zipFolder.getAbsolutePath().lastIndexOf(File.separator);
			log.info("debug : "+ zipFolder.getAbsolutePath());
			log.info("debug : "+ srcFolder);
			log.info("debug : "+ destFolder);
			log.info("debug : "+ len);
			String baseName = zipFolder.getAbsolutePath().substring(0, len+9);
			log.info("debug : "+ baseName);

			addFolderToZip(zipFolder, out, baseName);
			out.close();
			System.out.println("Zip getting encrypted with password : "+getpassword());
			AesZipFileEncrypter enc = new AesZipFileEncrypter("temp");
			enc.addEncrypted(new File(destFolder), this.getpassword());
			System.out.println("Zip encrypted with password : "+getpassword());
			new File(destFolder).delete();
			new File("temp").renameTo(new File(destFolder));
			    
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(e.getCause());
			e.printStackTrace();
		}
		
		log.info("exiting  " + this.getClass().getName() + "  run()");

	}

	private void addFolderToZip(File folder, ZipOutputStream zip, String baseName) throws IOException {
		File[] files = folder.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				addFolderToZip(file, zip, baseName);
			} else {
				String name = file.getAbsolutePath().substring(baseName.length());
				ZipEntry zipEntry = new ZipEntry(name);
				zip.putNextEntry(zipEntry);
				IOUtils.copy(new FileInputStream(file), zip);
				zip.closeEntry();
			}
		}
	}
}
