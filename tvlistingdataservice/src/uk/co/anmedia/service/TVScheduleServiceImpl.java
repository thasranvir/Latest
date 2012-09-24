/**
 * 
 */
package uk.co.anmedia.service;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import javax.media.jai.JAI;

import org.apache.commons.lang3.StringEscapeUtils;
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
public class TVScheduleServiceImpl implements TVScheduleService {

	private static final Logger log = Logger.getLogger(TVScheduleServiceImpl.class.getName());

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
			log.info("Start Date : " + startDate);
			
			/*if(cal.get(Calendar.DAY_OF_WEEK) == 7){
				cal.add(Calendar.DATE, 2);
			}else{*/
				cal.add(Calendar.DATE, 1);
			/*}*/
			Date nxtDate = cal.getTime();
			String endDate = new SimpleDateFormat("yyyy-MM-dd").format(nxtDate);
			endDate = endDate.concat("T06:00:00");
			log.info("End Date : " + endDate);
			atlasURL = baseURL + "?apiKey=" + apiKey + "&publisher=" + pub
					+ "&from=" + startDate + "&to=" + endDate + "&channel_id="
					+ channel + "&annotations=" + annos;

			log.debug("URL = " + atlasURL);
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
				log.debug("Schedule : "+schedule);
				items = schedule.path("items");
				for (JsonNode item : items) {
					itemHandler(item);
				}
				if (new File(Constant.JSON_DIR_TV).exists() == true) {
					mapper.writeValue(
							new File(Constant.JSON_DIR_TV+ Constant.JSON_FEED_TV_FILE + "-" + channelIndex+ ".json"), root);
				} else if (new File(Constant.JSON_DIR_TV).mkdirs() == true) {
					mapper.writeValue(new File(Constant.JSON_DIR_TV+ Constant.JSON_FEED_TV_FILE + "-" + channelIndex
									+ ".json"), root);
				} else {
					log.error("Error in creating folder ");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Exception: CANNOT PARSE ITEM " + i + ": " + e.getMessage());
			return;
		}
	}

	@Override
	public void itemHandler(JsonNode item) {
		JsonNode title = item.path("title");
		JsonNode desc = item.path("description");
		JsonNode img = item.path("image");
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
			String descriptionEscaped =  StringEscapeUtils.escapeHtml4(desc.textValue());
			((ObjectNode)item).put("description", descriptionEscaped);
			log.debug("DESC: " + desc);
		}

		if (!img.isMissingNode()) {
			log.debug("IMAGE: " + img);

			String imgSRC = img.textValue();
			String imageName = null;
			int lastSlash = imgSRC.lastIndexOf("/");
			
			if (lastSlash != -1) {
				imageName = imgSRC.substring(lastSlash + 1);
			} else {
				imageName = imgSRC;
			}
			imageName = downloadImage(imgSRC, imageName);

			((ObjectNode) item).put("image", imageName);
			
		} else {
			
			try {
				File defaultImage = new File(System.getProperty("user.dir")+"\\image\\default.png");
				if(!new File(Constant.IMAGE_DIR+"default.png").exists()){
					
					InputStream is = new FileInputStream(defaultImage);
					FileOutputStream fos = new FileOutputStream(Constant.IMAGE_DIR +"default.png");
					int data = -1;
					while ((data = is.read()) != -1) {
						fos.write(data);
					}
					File file = new File(Constant.IMAGE_DIR +Constant.THUMBNAIL+"default.png");
					is = new FileInputStream(defaultImage);
					BufferedImage sourceImage = ImageIO.read(is);
					Image thumbnail = sourceImage.getScaledInstance(192, 108, Image.SCALE_SMOOTH);
					BufferedImage bufferedThumbnail = new BufferedImage(thumbnail.getWidth(null),thumbnail.getHeight(null),BufferedImage.TYPE_INT_RGB);
					bufferedThumbnail.getGraphics().drawImage(thumbnail, 0, 0, null);
					ImageIO.write(bufferedThumbnail, "png", file);
					log.warn("NO IMAGE AVAILABLE; setting a default image... ");
					((ObjectNode) item).put("image", defaultImage.getName());
				}else{
					log.warn("NO IMAGE AVAILABLE; default image available in directory ... ");
					((ObjectNode) item).put("image", defaultImage.getName());
				}
				
			} catch (FileNotFoundException e) {
				log.error(e.getMessage());
			} catch (IOException e) {
				log.error(e.getMessage());
			}
		}
		

	}

	@Override
	public String downloadImage(String src, String fileName) {
		HttpURLConnection con = null;
//		FileOutputStream fos = null;
		InputStream is = null;
		String fileToSet = null;
		try {
			URL url = new URL(src);

			con = (HttpURLConnection) url.openConnection();
			int code = con.getResponseCode();

			if (code == 200) {
				fileToSet = fileName;
				is = con.getInputStream();
				if (new File(Constant.IMAGE_DIR).exists() == true) {
					compressFile(Constant.IMAGE_DIR, url, fileToSet);
//					fos = new FileOutputStream(Constant.IMAGE_DIR + fileName);
				} else if (new File(Constant.IMAGE_DIR).mkdirs() == true) {
					compressFile(Constant.IMAGE_DIR, url, fileToSet);
//					fos = new FileOutputStream(Constant.IMAGE_DIR + fileName);
				} else {
					log.error("Error in creating folder ");
				}

				/*int data = -1;

				while ((data = is.read()) != -1) {
					fos.write(data);
				}*/
				File file = new File(Constant.IMAGE_DIR +Constant.THUMBNAIL+fileName);
				BufferedImage sourceImage = ImageIO.read(url);
				Image thumbnail = sourceImage.getScaledInstance(192, 108, Image.SCALE_SMOOTH);
				BufferedImage bufferedThumbnail = new BufferedImage(thumbnail.getWidth(null),thumbnail.getHeight(null),BufferedImage.TYPE_INT_RGB);
				bufferedThumbnail.getGraphics().drawImage(thumbnail, 0, 0, null);
				ImageIO.write(bufferedThumbnail, "jpg", file);
				fileToSet = fileName;
			} else {
				fileToSet = "default.png";
				log.warn("COULD NOT FIND IMAGE ONLINE...   setting defalut image");
				File defaultImage = new File(System.getProperty("user.dir")+"\\image\\"+fileToSet);
				if(!new File(Constant.IMAGE_DIR+"default.png").exists()){
					compressFile(Constant.IMAGE_DIR, url, fileToSet);
				/*is = new FileInputStream(defaultImage);
				fos = new FileOutputStream(Constant.IMAGE_DIR + fileToSet);
				int data = -1;
				while ((data = is.read()) != -1) {
					fos.write(data);
				}*/
				File file = new File(Constant.IMAGE_DIR +Constant.THUMBNAIL+fileToSet);
				is = new FileInputStream(defaultImage);
				BufferedImage sourceImage = ImageIO.read(is);
				Image thumbnail = sourceImage.getScaledInstance(192, 108, Image.SCALE_SMOOTH);
				BufferedImage bufferedThumbnail = new BufferedImage(thumbnail.getWidth(null),thumbnail.getHeight(null),BufferedImage.TYPE_INT_RGB);
				bufferedThumbnail.getGraphics().drawImage(thumbnail, 0, 0, null);
				ImageIO.write(bufferedThumbnail, "png", file);
				}else{
					log.warn("COULD NOT FIND IMAGE ONLINE; default image available in directory ... ");
					fileToSet = defaultImage.getName();
				}
			}
		} catch (Exception e) {
			fileToSet = "default.png";
			log.error("CANNOT DOWNLOAD IMAGE: " + e.getMessage());
			e.printStackTrace();
		} finally {
			if(fileToSet==null){
				fileToSet = "default.png";
			}
			try {
				is.close();
			} catch (Exception unhandled) {
				log.error(unhandled.getMessage());
			}

			try {
//				fos.close();
			} catch (Exception unhandled) {
				log.error(unhandled.getMessage());
			}

			try {
				con.disconnect();
			} catch (Exception unhandled) {
				log.error(unhandled.getMessage());
			}
		}
		return fileToSet;
	}

	private void compressFile(String realPath, URL in, String fileName) {
		
		BufferedImage input = null;
		
		try {
			RenderedImage img1 = (RenderedImage) JAI.create("url",	in);
			input = getBufferedImage(fromRenderedToBuffered(img1));
		
		if (input == null)
			return;
		
		// Get Writer and set compression
		Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpg");
		
			if (iter.hasNext()) {
				ImageWriter writer = (ImageWriter) iter.next();
				ImageWriteParam iwp = writer.getDefaultWriteParam();
				iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
				float values[] = iwp.getCompressionQualityValues();
				iwp.setCompressionQuality(values[2]);
				String newName = realPath + fileName;
				File outFile = new File(newName);
				FileImageOutputStream output;
		
				output = new FileImageOutputStream(outFile);
				writer.setOutput(output);
				IIOImage image =	new IIOImage(input, null, null);
				writer.write(null, image, iwp);
				output.flush();
				output.close();
				outFile = null;
				image = null;
				output = null;
				input.flush();
				input = null;
				writer.dispose();
				writer = null;
			}
		} catch (FileNotFoundException finfExcp) {
				System.out.println(finfExcp);
			} catch (IOException ioExcp) {
				System.out.println(ioExcp);
			}
		}
		
		private BufferedImage getBufferedImage(Image img) {
	
			// create a new BufferedImage and draw the original image on it
			int w = img.getWidth(null);
			int h = img.getHeight(null);
			int thumbWidth = 480;
			int thumbHeight = 270;
	
		    // if width is less than 480 keep the width as it is.
			if (w < thumbWidth)
				thumbWidth = w;
	
			// if height is less than 270 keep the height as it is.
			if (h < thumbHeight)
				thumbHeight = h;
	
			//if less than 330*250 then do not compress
			if (w > 480 || h > 270) {
	
				double imageRatio = (double) w / (double) h;
				double thumbRatio = (double) thumbWidth / (double) thumbWidth;
	
				if (thumbRatio < imageRatio) {
					thumbHeight = (int) (thumbWidth / imageRatio);
				} else {
					thumbWidth = (int) (thumbHeight * imageRatio);
				}
			}
			// draw original image to thumbnail image object and
			// scale it to the new size on-the-fly
			BufferedImage bi = new BufferedImage(thumbWidth, thumbHeight, BufferedImage.TYPE_INT_RGB);
			Graphics2D g2d = bi.createGraphics();
			g2d.drawImage(img, 0, 0, thumbWidth, thumbHeight, null);
			g2d.dispose();
			return bi;
		}
	
		public BufferedImage fromRenderedToBuffered(RenderedImage img) {
			if (img instanceof BufferedImage) {
				return (BufferedImage) img;
			}
			ColorModel cm = img.getColorModel();
			int w  = img.getWidth();
			int h  = img.getHeight();
			WritableRaster raster = cm.createCompatibleWritableRaster(w,h);
			boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
			Hashtable<String, Object> props = new Hashtable<String, Object>();
			String[] keys = img.getPropertyNames();
	
			if (keys != null) {
				for (int i = 0 ; i < keys.length ; i++) {
					props.put(keys[i], img.getProperty(keys[i]));
				}
			}
			BufferedImage ret = new BufferedImage(cm, raster, isAlphaPremultiplied, props);
			img.copyData(raster);
			cm = null;
			return ret;
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
						log.debug("----------------------------------- TV ----------------------------------");
						log.debug("Channel Title : "+jsonNode2.path("channel_title"));
						log.debug("Channel Key : "+jsonNode2.path("channel_key"));
						log.debug(""+jsonNode2.path("channel").path("id"));
					}
				}
			}
			if (new File(Constant.JSON_DIR_TV).exists() == true) {
				mapper.writeValue(new File(Constant.JSON_DIR_TV + Constant.JSON_FEED_TV_FILE + ".json"), map);
				String data = mapper.writeValueAsString(map);
				data = "var scheduleData = " + data;
				if (new File(Constant.DATA_DIR).exists() == true){
					FileOutputStream out = new FileOutputStream(new File(Constant.DATA_DIR + Constant.JSON_FEED_TV_FILE + ".js"));
					out.write(data.getBytes());
					out.close();
				}else if (new File(Constant.DATA_DIR).mkdirs() == true){
					FileOutputStream out = new FileOutputStream(new File(Constant.DATA_DIR + Constant.JSON_FEED_TV_FILE + ".js"));
					out.write(data.getBytes());
					out.close();
				}
			} else if (new File(Constant.JSON_DIR_TV).mkdirs() == true) {
				mapper.writeValue(new File(Constant.JSON_DIR_TV + Constant.JSON_FEED_TV_FILE + ".json"), map);
				String data = mapper.writeValueAsString(map);
				data = "var scheduleData = " + data;
				if (new File(Constant.DATA_DIR).exists() == true){
					FileOutputStream out = new FileOutputStream(new File(Constant.DATA_DIR + Constant.JSON_FEED_TV_FILE + ".js"));
					out.write(data.getBytes());
					out.close();
				}else if (new File(Constant.DATA_DIR).mkdirs() == true){
					FileOutputStream out = new FileOutputStream(new File(Constant.DATA_DIR + Constant.JSON_FEED_TV_FILE + ".js"));
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
		
		channelList.add("cbB2");
		channelList.add("cbdt");
		channelList.add("cbdv");
		channelList.add("cbkN");
		channelList.add("cbgK");
		channelList.add("cbf6");
		channelList.add("cbhP");
		channelList.add("cbbV");
		channelList.add("cbbR");
		channelList.add("cbbh");
		channelList.add("cbbG");
		channelList.add("cbbP");
		channelList.add("cbbQ");
		channelList.add("cbhx");
		channelList.add("cbjm");
		channelList.add("cbg6");
		channelList.add("cbf2");
		channelList.add("cbbW");
		channelList.add("cbbX");
		channelList.add("cbm4");
		channelList.add("cbm2");
		channelList.add("cbmZ");
		channelList.add("cbfR");
		channelList.add("cbdj");
		channelList.add("cbdq");
		channelList.add("cbfz");
		channelList.add("cbk8");
		channelList.add("cbfY");
		channelList.add("cbgV");
		channelList.add("cbgW");
		channelList.add("cbjB");
		channelList.add("cbjC");
		channelList.add("cbkd");
		channelList.add("cbjc");
		channelList.add("cbjd");
		channelList.add("cbgF");
		channelList.add("cbgj");
		channelList.add("cbhg");
		channelList.add("cbgp");
		channelList.add("cbjR");
		channelList.add("cbhq");
		channelList.add("cbmc");
		channelList.add("cbkb");
		channelList.add("cbdn");
		channelList.add("cbjh");
		channelList.add("cbmS");
		channelList.add("cbh8");
		channelList.add("cbjV");
		channelList.add("cbhn");
		channelList.add("cbjx");
		channelList.add("cbwP");
		channelList.add("cbm6");
		channelList.add("cbjg");
		channelList.add("cbgw");
		channelList.add("cbhM");
		channelList.add("cbGK");
		channelList.add("cbgr");
		channelList.add("cbfT");
		channelList.add("cbjn");
		channelList.add("cbdD");
		channelList.add("cbdZ");
		channelList.add("cbd2");
		channelList.add("cbd5");
		channelList.add("cbmY");
		channelList.add("cbmv");
		channelList.add("cbdk");
		channelList.add("cbmr");
		channelList.add("cbfH");
		channelList.add("cbkc");
		channelList.add("cbgt");
		channelList.add("cbg5");
		channelList.add("cbnb");
		channelList.add("cbgY");
		channelList.add("cbhQ");
		channelList.add("cbnV");
		channelList.add("cbjP");
		channelList.add("cbmP");
		channelList.add("cbjy");
		channelList.add("cbh2");
		channelList.add("cbnq");
		channelList.add("cbhz");
		channelList.add("cbmz");
		channelList.add("cbns");
		channelList.add("cbny");
		channelList.add("cbkv");
		channelList.add("cbkF");
		channelList.add("cbks");
		channelList.add("cbkm");
		channelList.add("cbkz");
		channelList.add("cbkx");
		channelList.add("cbkK");
		channelList.add("cbkH");
		channelList.add("cbkp");
		channelList.add("cbkC");
		channelList.add("cbnv");
		channelList.add("cbf8");
		channelList.add("cbhs");
		channelList.add("cbht");
		channelList.add("cbhv");
		channelList.add("999");
		channelList.add("cbhk");
		channelList.add("cbhf");
		channelList.add("cbnF");
		channelList.add("cbgX");
		channelList.add("cbD6");
		channelList.add("cbjs");
		channelList.add("cbgR");
		channelList.add("cbgZ");
		channelList.add("cbjZ");
		channelList.add("cbgy");
		channelList.add("cbjN");
		channelList.add("cbmr");
		channelList.add("cbgN");
		channelList.add("cbm5");
		channelList.add("cbmx");
		channelList.add("cbhT");
		channelList.add("cbDj");
		channelList.add("cbfP");
		
		return channelList;
	}

	@Override
	public List<String> getJsonList(List<String> channelList) {
		List<String> jsonList = new ArrayList<String>();
		List<String> channel = channelList;
		for (String string : channel) {
			jsonList.add(Constant.JSON_DIR_TV + Constant.JSON_FEED_TV_FILE + "-"+ channel.indexOf(string) + ".json");
		}
		return jsonList;
	}

}
