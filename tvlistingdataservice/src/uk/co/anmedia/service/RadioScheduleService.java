package uk.co.anmedia.service;

import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;

public interface RadioScheduleService {
	public String  getPrepareFeedURL(String channel);

	public void transcode( String channel, int channelIndex);

	public void prepareBundleJSON(List<String> jsonList);
	
	public List<String> getChannelList();
	
	public List<String> getJsonList(List<String> channelList);

	void itemHandler(JsonNode item);
	
	
}
