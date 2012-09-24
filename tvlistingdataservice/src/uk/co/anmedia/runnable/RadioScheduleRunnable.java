package uk.co.anmedia.runnable;

import java.util.List;

import org.apache.log4j.Logger;

import uk.co.anmedia.service.GenreServiceRadioImpl;
import uk.co.anmedia.service.RadioScheduleServiceImpl;

public class RadioScheduleRunnable implements Runnable {
	
	public RadioScheduleRunnable() {
		super();
	}

	final Logger log = Logger.getLogger(this.getClass().getName());
	
	@Override
	public void run() {
		long startTime = System.currentTimeMillis();
		RadioScheduleServiceImpl radioScheduleImpl = new RadioScheduleServiceImpl();
		List <String> channelList = radioScheduleImpl.getChannelList();
		for (Object channelName : channelList) {
			log.info("\n**********************************************************\n");
			log.info("Processing Started For Radio Channel : "+channelName.toString());
			log.info(channelList.indexOf(channelName.toString()));
			log.info("Channel number  :: "+ channelList.indexOf(channelName));
			radioScheduleImpl.transcode(channelName.toString(), channelList.indexOf(channelName));
			log.info("Processing Complete for Radio Channel : "+channelName.toString());
		}
		List<String> file = radioScheduleImpl.getJsonList(channelList);
		radioScheduleImpl.prepareBundleJSON(file);
		
		log.info("Genre Radio Service processing starts......");
		GenreServiceRadioImpl gnre = new GenreServiceRadioImpl();
		gnre.createGenres();
		log.info("Genre Radio Service processing stops......");
		log.info("\n**********************************************************\n");
		long stopTime = System.currentTimeMillis();
		log.info("Total Radio data processing time : "+ (stopTime - startTime));
	}

}
