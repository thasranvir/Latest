package uk.co.anmedia.net.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import uk.co.anmedia.util.Constant;

public class FtpTVListingBundle {

	/**
	* main - Unit test program
	* @param args Command line arguments
	*
	*/
	public static void main(String args[]) {
		try {
			Runtime.getRuntime().exec("C:\\Windows\\system32\\netsh advfirewall set global StatefulFTP disable");
			String ftpHost = "dmnewspapers.upload.akamai.com";
			String ftpUserName = "dmkindleupload";
			String ftpPassword = "FrW4xP";
			String ftpRemoteDirectory = "content/test";
			String fileToTransmit = Constant.HOME_DIR+new SimpleDateFormat("yyyyMMdd").format(new Date())+"_TVL.zip";
		   
			//Create a Jakarta Commons Net FTP Client object
			FTPClient ftp = new FTPClient();

			//A datatype to store responses from the FTP server
			int reply;
			   
			//Connect to the server

			ftp.connect(ftpHost);
		   
			// After connection attempt, you should check the reply code to verify success.
			
			reply = ftp.getReplyCode();   
			if(!FTPReply.isPositiveCompletion(reply)) {
				try {
					ftp.disconnect();
				} catch (Exception e) {
					System.err.println("Unable to disconnect from FTP server " + "after server refused connection. "+e.toString());
				}
				throw new Exception ("FTP server refused connection.");
			}			 
			System.out.println("Connected to " + ftpHost + ". "+ftp.getReplyString());
			   
			//Try to login

			if (!ftp.login(ftpUserName, ftpPassword)) {
				throw new Exception ("Unable to login to FTP server " +
									"using username "+ftpUserName+" " +
									"and password "+ftpPassword);
			}

			System.out.println(ftp.getReplyString());
			System.out.println("Remote system is " + ftp.getRemoteAddress().getHostName());

			//Set our file transfer mode to either ASCII or Binary

			//ftp.setFileType(FTP.ASCII_FILE_TYPE);
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			   
			//Change the remote directory

			if (ftpRemoteDirectory != null && ftpRemoteDirectory.trim().length() > 0) {
				ftp.cwd(ftpRemoteDirectory);
				System.out.println("Changing to FTP remote dir: " + ftpRemoteDirectory);
				System.out.println("Work Directory " +ftp.printWorkingDirectory());
				//ftp.changeWorkingDirectory(ftpRemoteDirectory);
				reply = ftp.getReplyCode();
				System.out.println("Work Directory " +ftp.printWorkingDirectory());
				if(!FTPReply.isPositiveCompletion(reply)) {
					throw new Exception ("Unable to change working directory " +
										"to:"+ftpRemoteDirectory);
				}
			}
			   
			//Get the file that we will transfer and send it.

			File f = new File(fileToTransmit);
			System.out.println("Storing file as remote filename: " + f.getName());
//			ftp.enterLocalPassiveMode();
			boolean retValue = ftp.storeFile(f.getName(), new FileInputStream(f));
			if (!retValue) {
			  throw new Exception ("Storing of remote file failed. ftp.storeFile()" +
								  " returned false.");
			}
			   
			 //
			//Disconnect from the FTP server
			//
			try {
//				ftp.logout();
				ftp.disconnect();
			} catch (Exception exc) {
				System.err.println("Unable to disconnect from FTP server. " + exc.toString());
			}
		   
		} catch (Exception e) {
			System.err.println("Error: "+e.toString());
		}
	   
		System.out.println("Process Complete.");
		System.exit(0);
	}
	
	
}
