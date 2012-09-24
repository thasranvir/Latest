/**
 * 
 */
package uk.co.anmedia.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author admin
 *
 */
public final class Constant {
	public static final String THUMBNAIL = "thumb_";
	public final static String JSON_FEED_TV_FILE="schedule-tv";
	public final static String JSON_GENRES_TV_FILE="genres-tv";
	public final static String JSON_FEED_FILE_RADIO="schedule-radio";
	public final static String JSON_GENRES_RADIO_FILE="genres-radio";
	public final static String HOME_DIR = "C:\\Users\\admin\\TVL\\";
	public final static String STATIC_DIR = HOME_DIR+"static";
	public final static String DATE_DIR = HOME_DIR+new SimpleDateFormat("yyyyMMdd").format(new Date());
	public static final String DATA_DIR = DATE_DIR+"\\data\\";
	public final static String JSON_DIR_TV = DATE_DIR + "\\json\\tv\\";
	public final static String JSON_DIR_RADIO = DATE_DIR+"\\json\\radio\\";
	public final static String IMAGE_DIR = DATE_DIR+"\\img\\";
	public final static String CHILDRENS = "Childrens";
	public final static String DRAMA = "Drama";
	public final static String ACTION_ADVENTURE = "Action / Adventure";
	public final static String SCI_FI_FANTASY = "Sci Fi / Fantasy";
	public final static String COMEDY = "Comedy";
	public final static String SOAP = "Soap";
	public final static String ROMANCE = "Romance";
	public final static String NEWS_CURR_AFFAIRS = "News and Current Affairs";
	public final static String GAMESHOW = "Gameshow";
	public final static String SPORT = "Sport";
	public final static String CHILDREN = "Children's";
	public final static String MUSIC = "Music";
	public final static String FILM = "Film";
	public final static String LIFESTYLE = "Lifestyle";
	public final static String FACTUAL = "Factual";
	public final static String LIVE = "Live";
	public final static String RESERVED = "Reserved";
	public final static String OFF_AIR = "Off-Air";
	public final static String HOME_AND_DIY = "Home & DIY";
	public final static String NATURE = "Nature";
	
}
