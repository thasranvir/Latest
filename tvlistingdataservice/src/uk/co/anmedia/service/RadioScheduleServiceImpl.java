/**
 * 
 */
package uk.co.anmedia.service;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import uk.co.anmedia.util.Constant;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author admin
 * 
 */
public class RadioScheduleServiceImpl implements RadioScheduleService {

	private static final Logger log = Logger.getLogger(RadioScheduleServiceImpl.class.getName());

	@Override
	public String getPrepareFeedURL(String channel) {
		Properties props = new Properties();
		String atlasURL = null;
		try {
			getClass().getClassLoader();
			props.load(ClassLoader.getSystemResourceAsStream("transcoder-config.properties"));
			String apiKey = props.getProperty("API_KEY");
			String baseURL = props.getProperty("BASE_ATLAS_URL");
			String pub = props.getProperty("PUBLISHER");
			String annos = props.getProperty("ANNOTATIONS");

			Calendar cal = Calendar.getInstance();
			Date currDate = cal.getTime();
			String startDate = new SimpleDateFormat("yyyy-MM-dd").format(currDate);
			startDate = startDate.concat("T06:00:00");
//			log.info("Start Date : " + startDate);
			
			/*if(cal.get(Calendar.DAY_OF_WEEK) == 7){
				cal.add(Calendar.DATE, 2);
			}
			else{*/
				cal.add(Calendar.DATE, 1);
			/*}*/

			Date nxtDate = cal.getTime();
			String endDate = new SimpleDateFormat("yyyy-MM-dd").format(nxtDate);
			endDate = endDate.concat("T06:00:00");
//			log.info("End Date : " + endDate);
			
			atlasURL = baseURL + "?apiKey=" + apiKey + "&publisher=" + pub
					+ "&from=" + startDate + "&to=" + endDate + "&channel_id="
					+ channel + "&annotations=" + annos;

			log.info("URL = " + atlasURL);
		} catch (Exception e) {
			log.error("Exception : " + e.toString());
		}
		return atlasURL;
	}

	@Override
	public void transcode(String channel, int channelIndex) {
		int i = 0;
		try {
			JsonNode schedule = null;
			JsonNode items;
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(new URL(getPrepareFeedURL(channel)));
			log.debug("Success !! JSON root node : " + root.toString());
			JsonNode schedules = root.path("schedule");
			for (JsonNode jsonNode : schedules) {
				schedule = jsonNode;
				((ObjectNode)schedule).remove("channel_uri");
				items = schedule.path("items");
				for (JsonNode item : items) {
					itemHandler(item);
				}
				log.debug(items);
				if (new File(Constant.JSON_DIR_RADIO).exists() == true) {
					mapper.writeValue(
							new File(Constant.JSON_DIR_RADIO+ Constant.JSON_FEED_FILE_RADIO + "-" + channelIndex+ ".json"), root);
				} else if (new File(Constant.JSON_DIR_RADIO).mkdirs() == true) {
					mapper.writeValue(new File(Constant.JSON_DIR_RADIO+ Constant.JSON_FEED_FILE_RADIO + "-" + channelIndex+ ".json"), root);
				} else {
					log.error("Error in creating folder ");
				}
			}
		} catch (Exception e) {
			log.error("Exception: CANNOT PARSE ITEM " + i + ": " + e.toString());
			return;
		}
	}

	@Override
	public void itemHandler(JsonNode item) {
		JsonNode title = item.path("title");
		JsonNode desc = item.path("description");
		ArrayNode broadcasts = (ArrayNode) item.path("broadcasts");
		
		((ObjectNode)item).remove("locations");
		((ObjectNode)item).remove("black_and_white");
		((ObjectNode)item).remove("countries_of_origin");
		((ObjectNode)item).remove("languages");
		((ObjectNode)item).remove("subtitles");
		((ObjectNode)item).remove("certificates");
		((ObjectNode)item).remove("release_dates");
		((ObjectNode)item).remove("publisher");
		((ObjectNode)item).remove("tags");
		((ObjectNode)item).remove("clips");
		((ObjectNode)item).remove("topics");
		((ObjectNode)item).remove("key_phrases");
		((ObjectNode)item).remove("related_links");
		((ObjectNode)item).remove("products");
		((ObjectNode)item).remove("content_groups");
		((ObjectNode)item).remove("same_as");
		((ObjectNode)item).remove("people");
		((ObjectNode)item).remove("media_type");
		((ObjectNode)item).remove("specialization");
		((ObjectNode)item).remove("schedule_only");
		((ObjectNode)item).remove("aliases");
		((ObjectNode)item).remove("uri");
		((ObjectNode)item).remove("curie");
		((ObjectNode)item).remove("type");
		
//		log.info("Printing : "+item);
		if(!broadcasts.isMissingNode()){
			for (JsonNode broadcast : broadcasts) {
				int broadcast_duration = (broadcast.path("broadcast_duration").intValue())/60;
				StringBuilder duration = new StringBuilder(); 
				duration.append(broadcast_duration);
				((ObjectNode)broadcast).put("broadcast_duration", duration.toString());
				((ObjectNode)broadcast).remove("duration");
				((ObjectNode)broadcast).remove("broadcast_on");
				((ObjectNode)broadcast).remove("subtitled");
				((ObjectNode)broadcast).remove("signed");
				((ObjectNode)broadcast).remove("audio_described");
				((ObjectNode)broadcast).remove("high_definition");
				((ObjectNode)broadcast).remove("widescreen");
				((ObjectNode)broadcast).remove("premier");
				((ObjectNode)broadcast).remove("new_series");
				((ObjectNode)broadcast).remove("restriction");
//				log.info("broadcast node : "+ broadcasts);
			}
			
		}
		if (title.isMissingNode()) {
			log.warn("NO TITLE AVAILABLE");
		} else {
			log.debug("TITLE: " + title);
		}

		if (desc.isMissingNode()) {
			((ObjectNode)item).put("description", "");
			log.warn("NO DESC AVAILABLE, setting blank node...");
		} else {
			log.debug("DESC: " + desc);
		}

	}

	@Override
	public void prepareBundleJSON(List<String> jsonList) {
		List<String> file = jsonList;
		JsonNode root = null;
		ArrayList<JsonNode> megaRoot = new ArrayList<JsonNode>();
		Map<String, ArrayList<JsonNode>> map = new HashMap<String, ArrayList<JsonNode>>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			for (String stringNode : file) {
				root = mapper.readTree(new FileInputStream(new File(stringNode)));
				megaRoot.add(root);
				map.put("megaschedule", megaRoot);
				for (JsonNode jsonNode : root) {
					for (JsonNode jsonNode2 : jsonNode) {
						log.info("---------------------------- Radio --------------------------------------");
						log.info("Channel Title : "+jsonNode2.path("channel_title"));
						log.info("Channel Key : "+jsonNode2.path("channel_key"));
						log.info(""+jsonNode2.path("channel").path("id"));
					}
				}
			}
			if (new File(Constant.JSON_DIR_RADIO).exists() == true) {
				mapper.writeValue(new File(Constant.JSON_DIR_RADIO + Constant.JSON_FEED_FILE_RADIO + ".json"), map);
				String data = mapper.writeValueAsString(map);
				data = "var scheduleData = " + data;
				if (new File(Constant.DATA_DIR).exists() == true){
					FileOutputStream out = new FileOutputStream(new File(Constant.DATA_DIR + Constant.JSON_FEED_FILE_RADIO + ".js"));
					out.write(data.getBytes());
					out.close();
				}else if (new File(Constant.DATA_DIR).mkdirs() == true){
					FileOutputStream out = new FileOutputStream(new File(Constant.DATA_DIR + Constant.JSON_FEED_FILE_RADIO + ".js"));
					out.write(data.getBytes());
					out.close();
				}
			} else if (new File(Constant.JSON_DIR_RADIO).mkdirs() == true) {
				mapper.writeValue(new File(Constant.JSON_DIR_RADIO + Constant.JSON_FEED_FILE_RADIO + ".json"), map);
				String data = mapper.writeValueAsString(map);
				data = "var scheduleData = " + data;
				if (new File(Constant.DATA_DIR).exists() == true){
					FileOutputStream out = new FileOutputStream(new File(Constant.DATA_DIR + Constant.JSON_FEED_FILE_RADIO + ".js"));
					out.write(data.getBytes());
					out.close();
				}else if (new File(Constant.DATA_DIR).mkdirs() == true){
					FileOutputStream out = new FileOutputStream(new File(Constant.DATA_DIR + Constant.JSON_FEED_FILE_RADIO + ".js"));
					out.write(data.getBytes());
					out.close();
				}
			} else {
				log.error("Error in creating folder : ");
			}
		} catch (JsonProcessingException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}

	}

	@Override
	public List<String> getChannelList() {
		List<String> channelList = new ArrayList<String>();

		channelList.add("cbn4");
		channelList.add("cbnX");
		channelList.add("cbc2");
		channelList.add("cbbZ");
		channelList.add("cbdh");
		channelList.add("cbbY");
		channelList.add("cbb2");
		channelList.add("cbb4");
		channelList.add("cbb5");
		channelList.add("cbb6");
		channelList.add("cbb7");
		channelList.add("cbdf");
		channelList.add("cbb8");
		channelList.add("cbb9");
		channelList.add("cbc9");
		channelList.add("cbcg");
		channelList.add("cbch");
		channelList.add("cbcp");
		channelList.add("cbcq");
		channelList.add("cbcr");
		channelList.add("cbc7");
		channelList.add("cbcs");
		channelList.add("cbct");
		channelList.add("cbcv");
		channelList.add("cbcw");
		channelList.add("cbcx");
		channelList.add("cbc5");
		channelList.add("cbcy");
		channelList.add("cbcz");
		channelList.add("cbcB");
		channelList.add("cbcC");
		channelList.add("cbcF");
		channelList.add("cbcM");
		channelList.add("cbFg");
		channelList.add("cbdb");
		channelList.add("cbcJ");
		channelList.add("cbcK");
		channelList.add("cbcN");
		channelList.add("cbCd");
		channelList.add("cbc4");
		channelList.add("cbn8");
		channelList.add("cbw4");
		channelList.add("cbpg");
		channelList.add("cbpd");
		channelList.add("cbxQ");
		channelList.add("cbhp");
		channelList.add("cbyc");
		channelList.add("cbn7");
		channelList.add("cbn9");
		channelList.add("cbcQ");
		channelList.add("cbcR");
		channelList.add("cbcT");
		channelList.add("cbcV");
		channelList.add("cbdg");
		channelList.add("cbcW");
		channelList.add("cbcX");
		channelList.add("cbcZ");
		channelList.add("cbcb");
		channelList.add("cbcc");
		channelList.add("cbcd");
		channelList.add("cbcf");
		channelList.add("cbcj");
		channelList.add("cbck");
		channelList.add("cbcm");
		channelList.add("cbcn");
		channelList.add("cbcD");

		channelList.add("cbcG");
		channelList.add("cbcH");
		channelList.add("cbpf");
		channelList.add("cbvf");
		channelList.add("cbnW");
		channelList.add("cbcS");


		return channelList;
	}

	@Override
	public List<String> getJsonList(List<String> channelList) {
		List<String> jsonList = new ArrayList<String>();
		List<String> channel = channelList;
		for (String string : channel) {
			jsonList.add(Constant.JSON_DIR_RADIO + Constant.JSON_FEED_FILE_RADIO + "-"+ channel.indexOf(string) + ".json");
		}
		return jsonList;
	}

}
