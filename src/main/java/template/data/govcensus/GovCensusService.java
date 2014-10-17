package template.data.govcensus;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;

import template.data.WebApiService;
import template.data.census.CensusService;
import template.util.ApiKeys;

public class GovCensusService extends WebApiService {
	private static final Logger logger = LogManager.getLogger(CensusService.class);
	private static final String cApiKey = ApiKeys.cGovCensusKey,
			cAcsBaseUrl = "http://api.census.gov/data/2012/acs5";

	private static Integer getInteger(String state, String county, String tract, String blockGroup,
			String key) {
		ArrayList<Integer> result = new ArrayList<Integer>();
		Map<String, String> params = new HashMap<String, String>();
		params.put("key", "c85059729d73eac8352618e9665149ae7ecf2603");
		params.put("for", "block+group:" + blockGroup);
		params.put("in", String.format("state:%s+county:%s+tract:%s", state, county, tract));
		params.put("get", key);

		try {
			logger.debug(encodeUrl(cAcsBaseUrl, params, false));
			JSONArray apiResponse = getResponseArr(encodeUrl(cAcsBaseUrl, params, false));
			for (int i = 1; i < apiResponse.length(); ++i) {
				result.add(new Integer(apiResponse.getJSONArray(i).getString(0)));
			}
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (result.size() < 1) {
			return null;
		}
		return result.get(0);
	}

	// TODO: This shouldn't be an entirely separate method
	private static Double getDouble(String state, String county, String tract, String blockGroup,
			String key) {
		ArrayList<Double> result = new ArrayList<Double>();
		Map<String, String> params = new HashMap<String, String>();
		params.put("key", "c85059729d73eac8352618e9665149ae7ecf2603");
		params.put("for", "block+group:" + blockGroup);
		params.put("in", String.format("state:%s+county:%s+tract:%s", state, county, tract));
		params.put("get", key);

		try {
			logger.debug(encodeUrl(cAcsBaseUrl, params, false));
			JSONArray apiResponse = getResponseArr(encodeUrl(cAcsBaseUrl, params, false));
			for (int i = 1; i < apiResponse.length(); ++i) {
				result.add(new Double(apiResponse.getJSONArray(i).getString(0)));
			}
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (result.size() < 1) {
			return null;
		}
		return result.get(0);
	}

	public static Integer getMedianIncome(String state, String county, String tract,
			String blockGroup) {
		return getInteger(state, county, tract, blockGroup, "B19013_001E");
	}

	public static Double getMedianAge(String state, String county, String tract, String blockGroup) {
		return getDouble(state, county, tract, blockGroup, "B01002_001E");
	}

}
