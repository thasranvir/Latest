package uk.co.anmedia.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.log4j.Logger;

import uk.co.anmedia.util.Constant;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class GenreServiceTVImpl implements GenreServiceTV {

	private static final Logger log = Logger.getLogger(GenreServiceTVImpl.class.getName());
	
	@Override
	public void createGenres() {
		JsonNode root = null;
		JsonNode items = null;
		JsonNode genre = null;
		JsonNode genreRoot = null;

		Map<String, List<JsonNode>> map = new HashMap<String, List<JsonNode>>();
		ObjectMapper mapper = new ObjectMapper();
		genreRoot = mapper.createObjectNode();
		List<JsonNode> listNode = new ArrayList<JsonNode>();
		try {
			root = mapper.readTree(new FileInputStream(new File(Constant.JSON_DIR_TV + Constant.JSON_FEED_TV_FILE + ".json")));
			for (JsonNode scheduleArray : root) {
				for (JsonNode scheduleNode : scheduleArray) {
					for (JsonNode itemNode : scheduleNode) {
						for (JsonNode item : itemNode) {
							items = item.path("items");
							for (JsonNode genresNode : items) {
								genre = genresNode.path("genres");
								String genreName = null;
								genreRoot = mapper.createObjectNode();
								for (JsonNode genreNode : genre) {

									String genreUri = genreNode.textValue();
									if(genreUri.contains("pressassociation.com")){
									int lastSlash = genreUri.lastIndexOf("/");
									if (lastSlash != -1) {
										genreName = genreUri.substring(lastSlash + 1);

										if (!genreName.matches("[a-zA-Z]*")) {
											genreName = this.getGenreName(genreName);
											((ObjectNode) genreRoot).put("type",WordUtils.capitalize(genreName));
											// log.debug("test : "+genresNode.path("title"));
											
											if(!item.path("channel_title").isMissingNode()){
												((ObjectNode) genreRoot).put("channel_title", item.path("channel_title"));
											}else{
												((ObjectNode) genreRoot).put("channel_title", "");
											}
											
											if(!genresNode.path("title").isMissingNode()){
												((ObjectNode) genreRoot).put("title",genresNode.path("title"));
											}else{
												((ObjectNode) genreRoot).put("title", "");
											}
											
											if(!genresNode.path("series_number").isMissingNode()){
												((ObjectNode) genreRoot).put("series_number", genresNode.path("series_number"));
											}else{
												((ObjectNode) genreRoot).put("series_number", "");
											}
											
											if(!genresNode.path("episode_number").isMissingNode()){
												((ObjectNode) genreRoot).put("episode_number", genresNode.path("episode_number"));
											}else{
												((ObjectNode) genreRoot).put("episode_number", "");
											}
											
											if(!genresNode.path("description").isMissingNode()){
												((ObjectNode) genreRoot).put("description",genresNode.path("description"));
											}else{
												((ObjectNode) genreRoot).put("description", "");
											}
											
											if(!genresNode.path("broadcasts").isMissingNode()){
												
												((ObjectNode) genreRoot).put("broadcasts",genresNode.path("broadcasts"));
											}else{
												((ObjectNode) genreRoot).put("broadcasts", "");
											}
											
											if(!genresNode.path("image").isMissingNode()){
												((ObjectNode) genreRoot).put("image",genresNode.path("image"));
											}else{
												((ObjectNode) genreRoot).put("image", "");
											}
											
										} else {
											((ObjectNode) genreRoot).put("type",WordUtils.capitalize(genreName));
											if(!item.path("channel_title").isMissingNode()){
												((ObjectNode) genreRoot).put("channel_title", item.path("channel_title"));
											}else{
												((ObjectNode) genreRoot).put("channel_title", "");
											}
											
											if(!genresNode.path("title").isMissingNode()){
												((ObjectNode) genreRoot).put("title",genresNode.path("title"));
											}else{
												((ObjectNode) genreRoot).put("title", "");
											}
											
											if(!genresNode.path("series_number").isMissingNode()){
												((ObjectNode) genreRoot).put("series_number", genresNode.path("series_number"));
											}else{
												((ObjectNode) genreRoot).put("series_number", "");
											}
											
											if(!genresNode.path("episode_number").isMissingNode()){
												((ObjectNode) genreRoot).put("episode_number", genresNode.path("episode_number"));
											}else{
												((ObjectNode) genreRoot).put("episode_number", "");
											}
											
											if(!genresNode.path("description").isMissingNode()){
												((ObjectNode) genreRoot).put("description",genresNode.path("description"));
											}else{
												((ObjectNode) genreRoot).put("description", "");
											}
											
											if(!genresNode.path("broadcasts").isMissingNode()){
												
												((ObjectNode) genreRoot).put("broadcasts",genresNode.path("broadcasts"));
											}else{
												((ObjectNode) genreRoot).put("broadcasts", "");
											}
											
											if(!genresNode.path("image").isMissingNode()){
												((ObjectNode) genreRoot).put("image",genresNode.path("image"));
											}else{
												((ObjectNode) genreRoot).put("image", "");
											}
										}
									} else {
										genreName = genreUri;
									}
									listNode.add(genreRoot);
								}
								}
							}
						}
					}
				}
			}
			
			List<JsonNode> finalList = new ArrayList<JsonNode>();

			JsonNode drama = new ObjectMapper().createObjectNode();
			ArrayNode dramaArray = new ObjectMapper().createArrayNode();

			JsonNode actionAdventure = new ObjectMapper().createObjectNode();
			ArrayNode actionAdventureArray = new ObjectMapper().createArrayNode();

			JsonNode scifiFantasy = new ObjectMapper().createObjectNode();
			ArrayNode scifiFantasyArray = new ObjectMapper().createArrayNode();

			JsonNode comedy = new ObjectMapper().createObjectNode();
			ArrayNode comedyArray = new ObjectMapper().createArrayNode();

			JsonNode soap = new ObjectMapper().createObjectNode();
			ArrayNode soapArray = new ObjectMapper().createArrayNode();

			JsonNode romance = new ObjectMapper().createObjectNode();
			ArrayNode romanceArray = new ObjectMapper().createArrayNode();

			JsonNode newsCurrAffairs = new ObjectMapper().createObjectNode();
			ArrayNode newsCurrAffairsArray = new ObjectMapper().createArrayNode();

			JsonNode gameshow = new ObjectMapper().createObjectNode();
			ArrayNode gameshowArray = new ObjectMapper().createArrayNode();

			JsonNode sport = new ObjectMapper().createObjectNode();
			ArrayNode sportArray = new ObjectMapper().createArrayNode();

			JsonNode children = new ObjectMapper().createObjectNode();
			ArrayNode childrenArray = new ObjectMapper().createArrayNode();

			JsonNode music = new ObjectMapper().createObjectNode();
			ArrayNode musicArray = new ObjectMapper().createArrayNode();

			JsonNode film = new ObjectMapper().createObjectNode();
			ArrayNode filmArray = new ObjectMapper().createArrayNode();

			JsonNode lifestyle = new ObjectMapper().createObjectNode();
			ArrayNode lifestyleArray = new ObjectMapper().createArrayNode();
			
			JsonNode homeAndDiy = new ObjectMapper().createObjectNode();
			ArrayNode homeAndDiyArray = new ObjectMapper().createArrayNode();

			JsonNode factual = new ObjectMapper().createObjectNode();
			ArrayNode factualArray = new ObjectMapper().createArrayNode();
			
			JsonNode nature = new ObjectMapper().createObjectNode();
			ArrayNode natureArray = new ObjectMapper().createArrayNode();

			JsonNode live = new ObjectMapper().createObjectNode();
			ArrayNode liveArray = new ObjectMapper().createArrayNode();

			JsonNode reserved = new ObjectMapper().createObjectNode();
			ArrayNode reservedArray = new ObjectMapper().createArrayNode();

			JsonNode offAir = new ObjectMapper().createObjectNode();
			ArrayNode offAirArray = new ObjectMapper().createArrayNode();


			for (JsonNode jsonNode : listNode) {

				if (jsonNode.path("type").textValue().equals(Constant.DRAMA)) {

					JsonNode local = new ObjectMapper().createObjectNode();
					((ObjectNode) drama).put("type", jsonNode.path("type"));
					((ObjectNode) local).put("channel_title", jsonNode.path("channel_title"));
					((ObjectNode) local).put("title", jsonNode.path("title"));
					((ObjectNode) local).put("series_number", jsonNode.path("series_number"));
					((ObjectNode) local).put("episode_number", jsonNode.path("episode_number"));
					((ObjectNode) local).put("description",jsonNode.path("description"));
					((ObjectNode) local).put("broadcasts",jsonNode.path("broadcasts"));
					((ObjectNode) local).put("image", jsonNode.path("image"));
					 dramaArray.add(local);
					((ObjectNode) drama).put("programs", dramaArray);
					continue;
				} else if (jsonNode.path("type").textValue().equals(Constant.ACTION_ADVENTURE)) {

					JsonNode local = new ObjectMapper().createObjectNode();
					((ObjectNode) actionAdventure).put("type", jsonNode.path("type"));
					((ObjectNode) local).put("channel_title", jsonNode.path("channel_title"));
					((ObjectNode) local).put("title", jsonNode.path("title"));
					((ObjectNode) local).put("series_number", jsonNode.path("series_number"));
					((ObjectNode) local).put("episode_number", jsonNode.path("episode_number"));
					((ObjectNode) local).put("description",jsonNode.path("description"));
					((ObjectNode) local).put("broadcasts",jsonNode.path("broadcasts"));
					((ObjectNode) local).put("image", jsonNode.path("image"));
					actionAdventureArray.add(local);
					((ObjectNode) actionAdventure).put("programs", actionAdventureArray);
					continue;
				} else if (jsonNode.path("type").textValue().equals(Constant.SCI_FI_FANTASY)) {

					JsonNode local = new ObjectMapper().createObjectNode();
					((ObjectNode) scifiFantasy).put("type", jsonNode.path("type"));
					((ObjectNode) local).put("channel_title", jsonNode.path("channel_title"));
					((ObjectNode) local).put("title", jsonNode.path("title"));
					((ObjectNode) local).put("series_number", jsonNode.path("series_number"));
					((ObjectNode) local).put("episode_number", jsonNode.path("episode_number"));
					((ObjectNode) local).put("description",jsonNode.path("description"));
					((ObjectNode) local).put("broadcasts",jsonNode.path("broadcasts"));
					((ObjectNode) local).put("image", jsonNode.path("image"));
					scifiFantasyArray.add(local);
					((ObjectNode) scifiFantasy).put("programs", scifiFantasyArray);
					continue;
				} else if (jsonNode.path("type").textValue().equals(Constant.COMEDY)) {

					JsonNode local = new ObjectMapper().createObjectNode();
					((ObjectNode) comedy).put("type", jsonNode.path("type"));
					((ObjectNode) local).put("channel_title", jsonNode.path("channel_title"));
					((ObjectNode) local).put("title", jsonNode.path("title"));
					((ObjectNode) local).put("series_number", jsonNode.path("series_number"));
					((ObjectNode) local).put("episode_number", jsonNode.path("episode_number"));
					((ObjectNode) local).put("description",jsonNode.path("description"));
					((ObjectNode) local).put("broadcasts",jsonNode.path("broadcasts"));
					((ObjectNode) local).put("image", jsonNode.path("image"));
					comedyArray.add(local);
					((ObjectNode) comedy).put("programs", comedyArray);
					continue;
				} else if (jsonNode.path("type").textValue().equals(Constant.SOAP)) {

					JsonNode local = new ObjectMapper().createObjectNode();
					((ObjectNode) soap).put("type", jsonNode.path("type"));
					((ObjectNode) local).put("channel_title", jsonNode.path("channel_title"));
					((ObjectNode) local).put("title", jsonNode.path("title"));
					((ObjectNode) local).put("series_number", jsonNode.path("series_number"));
					((ObjectNode) local).put("episode_number", jsonNode.path("episode_number"));
					((ObjectNode) local).put("description",jsonNode.path("description"));
					((ObjectNode) local).put("broadcasts",jsonNode.path("broadcasts"));
					((ObjectNode) local).put("image", jsonNode.path("image"));
					soapArray.add(local);
					((ObjectNode) soap).put("programs", soapArray);
					continue;
				} else if (jsonNode.path("type").textValue().equals(Constant.ROMANCE)) {

					JsonNode local = new ObjectMapper().createObjectNode();
					((ObjectNode) romance).put("type", jsonNode.path("type"));
					((ObjectNode) local).put("channel_title", jsonNode.path("channel_title"));
					((ObjectNode) local).put("title", jsonNode.path("title"));
					((ObjectNode) local).put("series_number", jsonNode.path("series_number"));
					((ObjectNode) local).put("episode_number", jsonNode.path("episode_number"));
					((ObjectNode) local).put("description",jsonNode.path("description"));
					((ObjectNode) local).put("broadcasts",jsonNode.path("broadcasts"));
					((ObjectNode) local).put("image", jsonNode.path("image"));
					romanceArray.add(local);
					((ObjectNode) romance).put("programs", romanceArray);
					continue;
				} else if (jsonNode.path("type").textValue().equals(Constant.NEWS_CURR_AFFAIRS)) {

					JsonNode local = new ObjectMapper().createObjectNode();
					((ObjectNode) newsCurrAffairs).put("type", jsonNode.path("type"));
					((ObjectNode) local).put("channel_title", jsonNode.path("channel_title"));
					((ObjectNode) local).put("title", jsonNode.path("title"));
					((ObjectNode) local).put("series_number", jsonNode.path("series_number"));
					((ObjectNode) local).put("episode_number", jsonNode.path("episode_number"));
					((ObjectNode) local).put("description",jsonNode.path("description"));
					((ObjectNode) local).put("broadcasts",jsonNode.path("broadcasts"));
					((ObjectNode) local).put("image", jsonNode.path("image"));
					newsCurrAffairsArray.add(local);
					((ObjectNode) newsCurrAffairs).put("programs", newsCurrAffairsArray);
					continue;
				} else if (jsonNode.path("type").textValue().equals(Constant.GAMESHOW)) {

					JsonNode local = new ObjectMapper().createObjectNode();
					((ObjectNode) gameshow).put("type", jsonNode.path("type"));
					((ObjectNode) local).put("channel_title", jsonNode.path("channel_title"));
					((ObjectNode) local).put("title", jsonNode.path("title"));
					((ObjectNode) local).put("series_number", jsonNode.path("series_number"));
					((ObjectNode) local).put("episode_number", jsonNode.path("episode_number"));
					((ObjectNode) local).put("description",jsonNode.path("description"));
					((ObjectNode) local).put("broadcasts",jsonNode.path("broadcasts"));
					((ObjectNode) local).put("image", jsonNode.path("image"));
					gameshowArray.add(local);
					((ObjectNode) gameshow).put("programs", gameshowArray);
					continue;
				} else if (jsonNode.path("type").textValue().equals(Constant.SPORT)) {
					
					JsonNode local = new ObjectMapper().createObjectNode();
					((ObjectNode) sport).put("type", jsonNode.path("type"));
					((ObjectNode) local).put("channel_title", jsonNode.path("channel_title"));
					((ObjectNode) local).put("title", jsonNode.path("title"));
					((ObjectNode) local).put("series_number", jsonNode.path("series_number"));
					((ObjectNode) local).put("episode_number", jsonNode.path("episode_number"));
					((ObjectNode) local).put("description",jsonNode.path("description"));
					((ObjectNode) local).put("broadcasts",jsonNode.path("broadcasts"));
					((ObjectNode) local).put("image", jsonNode.path("image"));
					sportArray.add(local);
					((ObjectNode) sport).put("programs", sportArray);
					continue;
				}
				if (jsonNode.path("type").textValue().equals(Constant.CHILDRENS)) {

					JsonNode local = new ObjectMapper().createObjectNode();
					((ObjectNode) children).put("type", jsonNode.path("type"));
					((ObjectNode) local).put("channel_title", jsonNode.path("channel_title"));
					((ObjectNode) local).put("title", jsonNode.path("title"));
					((ObjectNode) local).put("series_number", jsonNode.path("series_number"));
					((ObjectNode) local).put("episode_number", jsonNode.path("episode_number"));
					((ObjectNode) local).put("description",jsonNode.path("description"));
					((ObjectNode) local).put("broadcasts",jsonNode.path("broadcasts"));
					((ObjectNode) local).put("image", jsonNode.path("image"));
					childrenArray.add(local);
					((ObjectNode) children).put("programs", childrenArray);
					continue;
				} else if (jsonNode.path("type").textValue().equals(Constant.MUSIC)) {

					JsonNode local = new ObjectMapper().createObjectNode();
					((ObjectNode) music).put("type", jsonNode.path("type"));
					((ObjectNode) local).put("channel_title", jsonNode.path("channel_title"));
					((ObjectNode) local).put("title", jsonNode.path("title"));
					((ObjectNode) local).put("series_number", jsonNode.path("series_number"));
					((ObjectNode) local).put("episode_number", jsonNode.path("episode_number"));
					((ObjectNode) local).put("description",jsonNode.path("description"));
					((ObjectNode) local).put("broadcasts",jsonNode.path("broadcasts"));
					((ObjectNode) local).put("image", jsonNode.path("image"));
					musicArray.add(local);
					((ObjectNode) music).put("programs", musicArray);
					continue;
				} else if (jsonNode.path("type").textValue().equals(Constant.FILM)) {

					JsonNode local = new ObjectMapper().createObjectNode();
					((ObjectNode) film).put("type", jsonNode.path("type"));
					((ObjectNode) local).put("channel_title", jsonNode.path("channel_title"));
					((ObjectNode) local).put("title", jsonNode.path("title"));
					((ObjectNode) local).put("series_number", jsonNode.path("series_number"));
					((ObjectNode) local).put("episode_number", jsonNode.path("episode_number"));
					((ObjectNode) local).put("description",jsonNode.path("description"));
					((ObjectNode) local).put("broadcasts",jsonNode.path("broadcasts"));
					((ObjectNode) local).put("image", jsonNode.path("image"));
					filmArray.add(local);
					((ObjectNode) film).put("programs", filmArray);
					continue;
				} else if (jsonNode.path("type").textValue().equals(Constant.LIFESTYLE)) {

					JsonNode local = new ObjectMapper().createObjectNode();
					((ObjectNode) lifestyle).put("type", jsonNode.path("type"));
					((ObjectNode) local).put("channel_title", jsonNode.path("channel_title"));
					((ObjectNode) local).put("title", jsonNode.path("title"));
					((ObjectNode) local).put("series_number", jsonNode.path("series_number"));
					((ObjectNode) local).put("episode_number", jsonNode.path("episode_number"));
					((ObjectNode) local).put("description",jsonNode.path("description"));
					((ObjectNode) local).put("broadcasts",jsonNode.path("broadcasts"));
					((ObjectNode) local).put("image", jsonNode.path("image"));
					lifestyleArray.add(local);
					((ObjectNode) lifestyle).put("programs", lifestyleArray);
					continue;
				} else if (jsonNode.path("type").textValue().equals(Constant.HOME_AND_DIY)) {

					JsonNode local = new ObjectMapper().createObjectNode();
					((ObjectNode) homeAndDiy).put("type", jsonNode.path("type"));
					((ObjectNode) local).put("channel_title", jsonNode.path("channel_title"));
					((ObjectNode) local).put("title", jsonNode.path("title"));
					((ObjectNode) local).put("series_number", jsonNode.path("series_number"));
					((ObjectNode) local).put("episode_number", jsonNode.path("episode_number"));
					((ObjectNode) local).put("description",jsonNode.path("description"));
					((ObjectNode) local).put("broadcasts",jsonNode.path("broadcasts"));
					((ObjectNode) local).put("image", jsonNode.path("image"));
					homeAndDiyArray.add(local);
					((ObjectNode) homeAndDiy).put("programs", homeAndDiyArray);
					continue;
				}else if (jsonNode.path("type").textValue().equals(Constant.FACTUAL)) {

					JsonNode local = new ObjectMapper().createObjectNode();
					((ObjectNode) factual).put("type", jsonNode.path("type"));
					((ObjectNode) local).put("channel_title", jsonNode.path("channel_title"));
					((ObjectNode) local).put("title", jsonNode.path("title"));
					((ObjectNode) local).put("series_number", jsonNode.path("series_number"));
					((ObjectNode) local).put("episode_number", jsonNode.path("episode_number"));
					((ObjectNode) local).put("description",jsonNode.path("description"));
					((ObjectNode) local).put("broadcasts",jsonNode.path("broadcasts"));
					((ObjectNode) local).put("image", jsonNode.path("image"));
					factualArray.add(local);
					((ObjectNode) factual).put("programs", factualArray);
					continue;
				}else if (jsonNode.path("type").textValue().equals(Constant.NATURE)) {

					JsonNode local = new ObjectMapper().createObjectNode();
					((ObjectNode) nature).put("type", jsonNode.path("type"));
					((ObjectNode) local).put("channel_title", jsonNode.path("channel_title"));
					((ObjectNode) local).put("title", jsonNode.path("title"));
					((ObjectNode) local).put("series_number", jsonNode.path("series_number"));
					((ObjectNode) local).put("episode_number", jsonNode.path("episode_number"));
					((ObjectNode) local).put("description",jsonNode.path("description"));
					((ObjectNode) local).put("broadcasts",jsonNode.path("broadcasts"));
					((ObjectNode) local).put("image", jsonNode.path("image"));
					natureArray.add(local);
					((ObjectNode) nature).put("programs", natureArray);
					continue;
				} else if (jsonNode.path("type").textValue().equals(Constant.LIVE)) {

					JsonNode local = new ObjectMapper().createObjectNode();
					((ObjectNode) live).put("type", jsonNode.path("type"));
					((ObjectNode) local).put("channel_title", jsonNode.path("channel_title"));
					((ObjectNode) local).put("title", jsonNode.path("title"));
					((ObjectNode) local).put("series_number", jsonNode.path("series_number"));
					((ObjectNode) local).put("episode_number", jsonNode.path("episode_number"));
					((ObjectNode) local).put("description",jsonNode.path("description"));
					((ObjectNode) local).put("broadcasts",jsonNode.path("broadcasts"));
					((ObjectNode) local).put("image", jsonNode.path("image"));
					liveArray.add(local);
					((ObjectNode) live).put("programs", liveArray);
					continue;
				} else if (jsonNode.path("type").textValue().equals(Constant.RESERVED)) {

					JsonNode local = new ObjectMapper().createObjectNode();
					((ObjectNode) reserved).put("type", jsonNode.path("type"));
					((ObjectNode) local).put("channel_title", jsonNode.path("channel_title"));
					((ObjectNode) local).put("title", jsonNode.path("title"));
					((ObjectNode) local).put("series_number", jsonNode.path("series_number"));
					((ObjectNode) local).put("episode_number", jsonNode.path("episode_number"));
					((ObjectNode) local).put("description",jsonNode.path("description"));
					((ObjectNode) local).put("broadcasts",jsonNode.path("broadcasts"));
					((ObjectNode) local).put("image", jsonNode.path("image"));
					reservedArray.add(local);
					((ObjectNode) reserved).put("programs", reservedArray);
					continue;
				} else if (jsonNode.path("type").textValue().equals(Constant.OFF_AIR)) {

					JsonNode local = new ObjectMapper().createObjectNode();
					((ObjectNode) offAir).put("type", jsonNode.path("type"));
					((ObjectNode) local).put("channel_title", jsonNode.path("channel_title"));
					((ObjectNode) local).put("title", jsonNode.path("title"));
					((ObjectNode) local).put("series_number", jsonNode.path("series_number"));
					((ObjectNode) local).put("episode_number", jsonNode.path("episode_number"));
					((ObjectNode) local).put("description",jsonNode.path("description"));
					((ObjectNode) local).put("broadcasts",jsonNode.path("broadcasts"));
					((ObjectNode) local).put("image", jsonNode.path("image"));
					offAirArray.add(local);
					((ObjectNode) offAir).put("programs", offAirArray);
					continue;
				} 
			}
			if (null != drama && drama.size() > 0) {
				finalList.add(drama);
			}
			if (null != actionAdventure && actionAdventure.size() > 0) {
				finalList.add(actionAdventure);
			}
			if (null != scifiFantasy && scifiFantasy.size() > 0) {
				finalList.add(scifiFantasy);
			}
			if (null != comedy && comedy.size() > 0) {
				finalList.add(comedy);
			}
			if (null != soap && soap.size() > 0) {
				finalList.add(soap);
			}
			if (null != romance && romance.size() > 0) {
				finalList.add(romance);
			}
			if (null != newsCurrAffairs && newsCurrAffairs.size() > 0) {
				finalList.add(newsCurrAffairs);
			}
			if (null != gameshow && gameshow.size() > 0) {
				finalList.add(gameshow);
			}
			if (null != sport && sport.size() > 0) {
				finalList.add(sport);
			}
			if (null != children && children.size() > 0) {
				finalList.add(children);
			}
			if (null != music && music.size() > 0) {
				finalList.add(music);
			}
			if (null != film && film.size() > 0) {
				finalList.add(film);
			}
			if (null != lifestyle && lifestyle.size() > 0) {
				finalList.add(lifestyle);
			}
			if (null != homeAndDiy && homeAndDiy.size() > 0) {
				finalList.add(homeAndDiy);
			}
			if (null != factual && factual.size() > 0) {
				finalList.add(factual);
			}
			if (null != nature && nature.size() > 0) {
				finalList.add(nature);
			}
			if (null != live && live.size() > 0) {
				finalList.add(live);
			}
			if (null != reserved && reserved.size() > 0) {
				finalList.add(reserved);
			}
			if (null != offAir && offAir.size() > 0) {
				finalList.add(offAir);
			}
		
			map.put("genres", finalList);
			log.debug(map);
			if (new File(Constant.DATA_DIR).exists() == true) {
				String data = mapper.writeValueAsString(map);
				data = "var scheduleGenres = " + data;
				FileOutputStream out = new FileOutputStream(new File(Constant.DATA_DIR + Constant.JSON_GENRES_TV_FILE + ".js"));
				out.write(data.getBytes());
				out.close();
			} else if (new File(Constant.DATA_DIR).mkdirs() == true) {
				String data = mapper.writeValueAsString(map);
				data = "var scheduleGenres = " + data;
				FileOutputStream out = new FileOutputStream(new File(Constant.DATA_DIR + Constant.JSON_GENRES_TV_FILE + ".js"));
				out.write(data.getBytes());
				out.close();
			} else {
				log.error("Error in creating folder ");
			}
		} catch (JsonProcessingException e) {
			log.error("Exception : " + e);
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			log.error("Exception : " + e);
			e.printStackTrace();
		} catch (IOException e) {
			log.error("Exception : " + e);
			e.printStackTrace();
		}
	}

	@Override
	public String getGenreName(String key) {
		Map<String, String> map = new HashMap<String, String>();
		
		map.put("1000", "Drama");
		map.put("1F00", "Drama");
		map.put("1F01", "Drama");
		map.put("1F05", "Drama");
		map.put("1F06", "Drama");
		map.put("1F07", "Drama");
		map.put("1F08", "Drama");
		map.put("1F09", "Drama");
		map.put("1700", "Drama");
		map.put("1F17", "Drama");
		map.put("1F16", "Drama");
		map.put("1800", "Drama");
		map.put("1100", "Action / Adventure");
		map.put("1F0A", "Action / Adventure");
		map.put("1F0B", "Action / Adventure");
		map.put("1F0C", "Action / Adventure");
		map.put("1F0D", "Action / Adventure");
		map.put("1F0E", "Action / Adventure");
		map.put("1200", "Action / Adventure");
		map.put("1F04", "Action / Adventure");
		map.put("1F0F", "Action / Adventure");
		map.put("1F10", "Action / Adventure");
		map.put("1F11", "Action / Adventure");
		map.put("1300", "Sci Fi / Fantasy");
		map.put("1F02", "Sci Fi / Fantasy");
		map.put("1F03", "Sci Fi / Fantasy");
		map.put("1400", "Comedy");
		map.put("1F12", "Comedy");
		map.put("1F13", "Comedy");
		map.put("1F14", "Comedy");
		map.put("1500", "Soap");
		map.put("1600", "Romance");
		map.put("1F15", "Romance");
		map.put("2000", "News and Current Affairs");
		map.put("2100", "News and Current Affairs");
		map.put("2F02", "News and Current Affairs");
		map.put("2F03", "News and Current Affairs");
		map.put("2F04", "News and Current Affairs");
		map.put("2F05", "News and Current Affairs");
		map.put("2F06", "News and Current Affairs");
		map.put("2200", "News and Current Affairs");
		map.put("2300", "News and Current Affairs");
		map.put("2400", "News and Current Affairs");
		map.put("2F07", "News and Current Affairs");
		map.put("2F08", "News and Current Affairs");
		map.put("7800", "News and Current Affairs");
		map.put("7900", "News and Current Affairs");
		map.put("8000", "News and Current Affairs");
		map.put("8200", "News and Current Affairs");
		map.put("3000", "Gameshow");
		map.put("3F00", "Gameshow");
		map.put("3F01", "Gameshow");
		map.put("3F02", "Gameshow");
		map.put("3100", "Gameshow");
		map.put("3F03", "Gameshow");
		map.put("3200", "Gameshow");
		map.put("3300", "Gameshow");
		map.put("4000", "Sport");
		map.put("4F01", "Sport");
		map.put("4F03", "Sport");
		map.put("4F06", "Sport");
		map.put("4F07", "Sport");
		map.put("4F08", "Sport");
		map.put("4F0B", "Sport");
		map.put("4F0E", "Sport");
		map.put("4F0F", "Sport");
		map.put("4F10", "Sport");
		map.put("4F11", "Sport");
		map.put("4F12", "Sport");
		map.put("4F19", "Sport");
		map.put("4F1A", "Sport");
		map.put("4F1B", "Sport");
		map.put("4F20", "Sport");
		map.put("4F22", "Sport");
		map.put("4F25", "Sport");
		map.put("4F26", "Sport");
		map.put("4F28", "Sport");
		map.put("4F2A", "Sport");
		map.put("4F32", "Sport");
		map.put("4F34", "Sport");
		map.put("4F36", "Sport");
		map.put("4F39", "Sport");
		map.put("4F3A", "Sport");
		map.put("4F3B", "Sport");
		map.put("4F3D", "Sport");
		map.put("4F3F", "Sport");
		map.put("4F40", "Sport");
		map.put("4F41", "Sport");
		map.put("4F4A", "Sport");
		map.put("4F56", "Sport"); 
		map.put("4F58", "Sport");
		map.put("4F54", "Sport"); 
		map.put("4F50", "Sport");
		map.put("4100", "Sport");
		map.put("4F24", "Sport");
		map.put("4F3E", "Sport");
		map.put("4200", "Sport");
		map.put("4300", "Sport");
		map.put("4F15", "Sport");
		map.put("4F16", "Sport");
		map.put("4400", "Sport");
		map.put("4F35", "Sport");
		map.put("4600", "Sport");
		map.put("4500", "Sport");
		map.put("4F00", "Sport");
		map.put("4F02", "Sport");
		map.put("4F04", "Sport");
		map.put("4F05", "Sport");
		map.put("4F0C", "Sport");
		map.put("4F0D", "Sport");
		map.put("4F23", "Sport");
		map.put("4F1C", "Sport");
		map.put("4F2D", "Sport");
		map.put("4F2E", "Sport");
		map.put("4F2F", "Sport");
		map.put("4F30", "Sport");
		map.put("4F3C", "Sport");
		map.put("4F43", "Sport");
		map.put("4F44", "Sport");
		map.put("4F45", "Sport");
		map.put("4F46", "Sport");
		map.put("4F47", "Sport");
		map.put("4F51", "Sport");
		map.put("4F52", "Sport");
		map.put("4F53", "Sport");
		map.put("4F55", "Sport");
		map.put("4F57", "Sport");
		map.put("4800", "Sport");
		map.put("4F09", "Sport");
		map.put("4F14", "Sport");
		map.put("4F29", "Sport");
		map.put("4F2C", "Sport");
		map.put("4F31", "Sport");
		map.put("4F4D", "Sport");
		map.put("4F4E", "Sport");
		map.put("4F48", "Sport");
		map.put("4F49", "Sport");
		map.put("4F13", "Sport");
		map.put("4F1E", "Sport");
		map.put("4F1F", "Sport");
		map.put("4F33", "Sport");
		map.put("4F42", "Sport");
		map.put("4900", "Sport");
		map.put("4A00", "Sport");
		map.put("4F27", "Sport");
		map.put("4F1D", "Sport");
		map.put("4F0A", "Sport");
		map.put("4B00", "Sport");
		map.put("4F37", "Sport");
		map.put("4F4B", "Sport");
		map.put("4F4C", "Sport");
		map.put("4700", "Sport");
		map.put("4F21", "Sport");
		map.put("4F17", "Sport");
		map.put("4F2B", "Sport");
		map.put("5000", "Children's");
		map.put("5100", "Children's");
		map.put("5200", "Children's");
		map.put("5300", "Children's");
		map.put("5400", "Children's");
		map.put("5500", "Children's");
		map.put("6000", "Music");
		map.put("6F01", "Music");
		map.put("6F02", "Music");
		map.put("6F03", "Music");
		map.put("6100", "Music");
		map.put("6F06", "Music");
		map.put("6F07", "Music");
		map.put("6F08", "Music");
		map.put("6200", "Music");
		map.put("6300", "Music");
		map.put("6400", "Music");
		map.put("6500", "Music");
		map.put("6F09", "Music");
		map.put("6F0A", "Music");
		map.put("6600", "Music");
		map.put("6F05", "Music");
		map.put("7600", "Film");
		map.put("7700", "Film");
		map.put("BF01", "Film");
		map.put("7A00", "Lifestyle");
		map.put("7B00", "Lifestyle");
		map.put("A000", "Lifestyle");
		map.put("AF02", "Lifestyle");
		map.put("A100", "Lifestyle");
		map.put("A300", "Lifestyle");
		map.put("A400", "Lifestyle");
		map.put("A600", "Lifestyle");
		map.put("A700", "Home & DIY");
		map.put("AF00", "Home & DIY");
		map.put("AF03", "Home & DIY");
		map.put("A500", "Home & DIY");
		map.put("A200", "Home & DIY");
		map.put("AF01", "Home & DIY");
		map.put("7000", "Factual");
		map.put("7100", "Factual");
		map.put("7200", "Factual");
		map.put("7400", "Factual");
		map.put("7500", "Factual");
		map.put("9000", "Factual");
		map.put("9F01", "Factual");
		map.put("9F02", "Factual");
		map.put("9F03", "Factual");
		map.put("9F04", "Factual");
		map.put("9F05", "Factual");
		map.put("9F06", "Factual");
		map.put("9200", "Factual");
		map.put("9300", "Factual");
		map.put("9400", "Factual");
		map.put("500", "Factual");
		map.put("9600", "Factual");
		map.put("9F00", "Factual");
		map.put("9700", "Factual");
		map.put("9100", "Nature");
		map.put("9F07", "Nature");
		map.put("9F08", "Nature");
		map.put("9F09", "Nature");
		map.put("9F0A", "Nature");
		map.put("B300", "Live");
		map.put("BE00", "Reserved");
		map.put("BF00", "Off-Air");

		return map.get(key);
	}
}
