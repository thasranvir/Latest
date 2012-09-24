package uk.co.anmedia.runnable;

import java.util.List;

import org.apache.log4j.Logger;

import uk.co.anmedia.service.GenreServiceTVImpl;
import uk.co.anmedia.service.TVScheduleServiceImpl;

public class TVScheduleRunnable implements Runnable {
	
	public TVScheduleRunnable() {
		super();
	}

	final Logger log = Logger.getLogger(this.getClass().getName());
	
	@Override
	public void run() {
		long startTime = System.currentTimeMillis();
		TVScheduleServiceImpl tvScheduleImpl = new TVScheduleServiceImpl();
		List <String> channelList = tvScheduleImpl.getChannelList();
		for (Object channelName : channelList) {
			log.info("\n**********************************************************\n");
			log.info("Processing Started For TV Channel : "+channelName.toString());
			log.info(channelList.indexOf(channelName.toString()));
			log.info("Channel number  :: "+ channelList.indexOf(channelName));
			tvScheduleImpl.transcode(channelName.toString(), channelList.indexOf(channelName));
			log.info("Processing Complete for TV Channel : "+channelName.toString());
		}
		List<String> file = tvScheduleImpl.getJsonList(channelList);
		tvScheduleImpl.prepareBundleJSON(file);
		
		log.info("Genre TV Service processing starts......");
		GenreServiceTVImpl gnre = new GenreServiceTVImpl();
		gnre.createGenres();
		log.info("Genre TV Service processing stops......");
		log.info("\n**********************************************************\n");
		long stopTime = System.currentTimeMillis();
		log.info("Total TV dataprocessing time : "+ (stopTime - startTime));
	}

}
