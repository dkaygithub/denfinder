package template.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import template.data.geocode.GeocodeService;
import template.m1.LatLon;
import template.scoring.LocationDataPopulator;
import template.scoring.LocationDataWrapper;

@Controller
public class MainController {
	private final int WIDTH_OF_TARGET_BOX = 3;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String index(Model model) {
		model.addAttribute("ideal", new LocationDataWrapper());
		return "index";
	}
	@RequestMapping(value = "/m2", method = RequestMethod.GET)
	public String m2input(Model model) {
		model.addAttribute("ideal", new LocationDataWrapper());
		return "m2input";
	}
	
	@RequestMapping(value = "/m2", method = RequestMethod.POST)
	public String m2submit(@RequestParam Map<String, String> allRequestParams, Model model) {
	List<LocationDataWrapper> locationDataWrappers = new ArrayList<LocationDataWrapper>();
	LocationDataWrapper idealLoc = null;
	if (idealLoc == null) {
	idealLoc = new LocationDataWrapper();
	}
	String address = allRequestParams.get("address");
	idealLoc.setLocation(GeocodeService.getLatLon(address));
	idealLoc.setMedianAge(Double.valueOf(allRequestParams.get("medianAge")));
	idealLoc.setMedianIncome(Integer.valueOf(allRequestParams.get("medianIncome")));
	idealLoc.setSchoolWeight(Double.valueOf(allRequestParams.get("schoolWeight")));
	LocationDataPopulator.populate(idealLoc);
	LatLon latLon = idealLoc.getLocation();
	for (int i = -1 * WIDTH_OF_TARGET_BOX; i <= WIDTH_OF_TARGET_BOX; ++i) {
	for (int j = -1 * WIDTH_OF_TARGET_BOX; j <= WIDTH_OF_TARGET_BOX; ++j) {
	LocationDataWrapper toAdd = new LocationDataWrapper(
	latLon.getLatitude() + i * 0.01, latLon.getLongitude()
	+ j * 0.01);
	LocationDataPopulator.populate(toAdd);
	toAdd.compareToIdeal(idealLoc);
	locationDataWrappers.add(toAdd);
	}
	}
	Collections.sort(locationDataWrappers,
	new Comparator<LocationDataWrapper>() {
	@Override
	public int compare(LocationDataWrapper ldw1,
	LocationDataWrapper ldw2) {
	return (int) ((ldw2.getScore() - ldw1.getScore()) * 1000);
	}
	});
	model.addAttribute("locationDataWrappers", locationDataWrappers);
	return "m2submit";
	}

	@RequestMapping(value = "/map", method = RequestMethod.POST)
	public String map(@RequestParam Map<String, String> allRequestParams, Model model) {
		List<LocationDataWrapper> locationDataWrappers = new ArrayList<LocationDataWrapper>();

		LocationDataWrapper idealLoc = null;
		if (idealLoc == null) {
			idealLoc = new LocationDataWrapper();
		}
		String address = allRequestParams.get("address");
		idealLoc.setLocation(GeocodeService.getLatLon(address));
		idealLoc.setMedianAge(Double.valueOf(allRequestParams.get("medianAge")));
		idealLoc.setMedianIncome(Integer.valueOf(allRequestParams.get("medianIncome")));
		idealLoc.setSchoolWeightInput(Double.valueOf(allRequestParams.get("schoolWeightInput")));

		//LocationDataPopulator.populate(idealLoc);

		LatLon latLon = idealLoc.getLocation();
		Double lowestScore = null;
		for (int i = -1 * WIDTH_OF_TARGET_BOX; i <= WIDTH_OF_TARGET_BOX; ++i) {
			for (int j = -1 * WIDTH_OF_TARGET_BOX; j <= WIDTH_OF_TARGET_BOX; ++j) {
				LocationDataWrapper toAdd = new LocationDataWrapper(
						latLon.getLatitude() + i * 0.03, latLon.getLongitude() + j * 0.03);
				LocationDataPopulator.populate(toAdd);
				toAdd.compareToIdeal(idealLoc);
				if (lowestScore == null) {
					lowestScore = toAdd.getScore();
				} else if (lowestScore > toAdd.getScore()) {
					lowestScore = toAdd.getScore();
				}
				locationDataWrappers.add(toAdd);
			}
		}

		Collections.sort(locationDataWrappers, new Comparator<LocationDataWrapper>() {
			@Override
			public int compare(LocationDataWrapper ldw1, LocationDataWrapper ldw2) {
				return (int) ((ldw2.getScore() - ldw1.getScore()) * 1000);
			}
		});
		String locData = "";
		boolean first = true;
		for (LocationDataWrapper ldw : locationDataWrappers) {
			if (first) {
				first = false;
			} else {
				locData += ",\n";
			}
			// Compensate for negative scores
			Double modifier = lowestScore < 0 ? Math.abs(lowestScore) : 0;
			locData += String.format(
					"{location: new google.maps.LatLng(%.4f, %.4f), weight: %.4f}", ldw
							.getLocation().getLatitude(), ldw.getLocation().getLongitude(),
					ldw.getScore() + modifier);
		}
		model.addAttribute("ideal", new LocationDataWrapper());
		model.addAttribute("locData", locData);
		model.addAttribute("latitude", latLon.getLatitude());
		model.addAttribute("longitude", latLon.getLongitude());
		return "map";
	}
}
