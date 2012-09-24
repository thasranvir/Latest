package uk.co.anmedia.main;

import java.io.File;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import uk.co.anmedia.net.ftp.FtpTVListingBundle;
import uk.co.anmedia.runnable.EncryptAndZipTVListingBundle;
import uk.co.anmedia.runnable.RadioScheduleRunnable;
import uk.co.anmedia.runnable.StaticContentCopierRunnable;
import uk.co.anmedia.runnable.TVScheduleRunnable;

public class TVListingDataService {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		PropertyConfigurator.configure(TVListingDataService.class.getClassLoader().getResource("log4j.properties"));
		final Logger log = Logger.getLogger(TVListingDataService.class);
		if(new File(System.getProperty("user.dir")+"\\logs\\tvlistingdataservice.log").exists()==true){
			log.info("tvlistingdataservice.log exists at location "+System.getProperty("user.dir")+"\\logs\\tvlistingdataservice.log"+", writing logs...");
		}else if(new File(System.getProperty("user.dir")+"\\logs\\tvlistingdataservice.log").mkdirs()==true){
			log.info("tvlistingdataservice.log does not exists at location "+System.getProperty("user.dir")+"\\logs\\tvlistingdataservice.log"+", creating the log file...");
		}else{
			log.error("Unable to create tvlistingdataservice.log file ");
		}
		Thread tvSchedule = new Thread(new TVScheduleRunnable());
		Thread radioSchedule = new Thread(new RadioScheduleRunnable());
		Thread copyStaticContent = new Thread(new StaticContentCopierRunnable());
		Thread encrypter = new Thread(new EncryptAndZipTVListingBundle());
		radioSchedule.start();
		log.info(" Thread for RADIO started");
		try {
			tvSchedule.start();
			log.info(" Thread for TV started");
			tvSchedule.join();
		} catch (InterruptedException e) {
			log.error(e.getMessage());
		}
		/*if(!tvSchedule.isAlive() && !radioSchedule.isAlive()){
			try {
				copyStaticContent.start();
				log.info(" Thread for copying static content has started");
				copyStaticContent.join();
			} catch (InterruptedException e) {
				log.error(e.getMessage());
				e.printStackTrace();
			}
		}
		if(!copyStaticContent.isAlive() && !tvSchedule.isAlive() && !radioSchedule.isAlive()){
			try {
				encrypter.start();
				log.info(" Thread for encrypt and zip content has started");
				encrypter.join();
			} catch (InterruptedException e) {
				log.error(e.getMessage());
				e.printStackTrace();
			}
		}
		
		if(!encrypter.isAlive()){
			log.info("Now FTP the content to AKAMAI SERVER : Thread started..");
			FtpTVListingBundle.main(null); 
		}*/
		
	}
}
