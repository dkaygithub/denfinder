package template.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import template.data.census.CensusService;
import template.data.education.EducationService;
import template.data.fipsconversion.FipsConversionService;
import template.m1.LatLon;
import template.m1.LatLonBulk;
import template.m1.LatLonData;
import template.scoring.LocationDataPopulator;
import template.scoring.LocationDataWrapper;

@Controller
public class MainController {
	@RequestMapping("/")
	public String home() {

		return "index";
	}

	@RequestMapping("/map")
	public String map() {

		return "map";
	}

	@RequestMapping("/test")
	public String test(Model model) {
		List<LocationDataWrapper> locationDataWrappers = new ArrayList<LocationDataWrapper>();
		LocationDataWrapper baylor = new LocationDataWrapper(31.5472, -97.1139);
		LocationDataWrapper cbu = new LocationDataWrapper(33.9300, -117.4250);
		LocationDataWrapper biola = new LocationDataWrapper(33.9056, -118.0181);
		locationDataWrappers.add(baylor);
		locationDataWrappers.add(cbu);
		locationDataWrappers.add(biola);

		for (LocationDataWrapper ldw : locationDataWrappers) {
			LocationDataPopulator.populate(ldw);
		}

		model.addAttribute("things", locationDataWrappers);
		return "test";
	}

	@RequestMapping("education")
	public String education(Model model) {
		model.addAttribute("schools", EducationService.getSchools(31.5472, -97.1139, 0, 2));
		return "education";
	}

	@RequestMapping("census")
	public String census(Model model) {
		model.addAttribute("places",
				CensusService.getPlaces(FipsConversionService.getCountyFipsCode(31.5472, -97.1139)));
		return "census";
	}
	
	@RequestMapping(value = "/m2", method = RequestMethod.GET)
	public String m2input(Model model) {
		model.addAttribute("latLonForm", new LatLon());
		return "m2input";
	}

	@RequestMapping(value = "/m2", method = RequestMethod.POST)
	public String m2submit(@ModelAttribute LatLon latLon, Model model) {
		List<LocationDataWrapper> locationDataWrappers = new ArrayList<LocationDataWrapper>();
		if (latLon == null) {
			latLon = new LatLon();
		}
		
		Random r = new Random();
		for (int i = -2; i < 3; ++i) {
			for (int j = -2 ; j < 3; ++j) {
				LocationDataWrapper toAdd = new LocationDataWrapper(latLon.getLatitude() + i * 0.01,
						latLon.getLongitude() + j * 0.01);
				LocationDataPopulator.populate(toAdd);
				toAdd.setScore(r.nextDouble());
				locationDataWrappers.add(toAdd);
			}
		}

		Collections.sort(locationDataWrappers, new Comparator<LocationDataWrapper>() {
			@Override
			public int compare(LocationDataWrapper ldw1, LocationDataWrapper ldw2) {
				return (int) ((ldw2.getScore() - ldw1.getScore()) * 1000);
			}
		});

		model.addAttribute("locationDataWrappers", locationDataWrappers);
		return "m2submit";
	}

	@RequestMapping(value = "/m1", method = RequestMethod.GET)
	public String m1input(Model model) {
		model.addAttribute("latLonForm", new LatLon());
		model.addAttribute("latLonBulk", new LatLonBulk());
		return "m1input";
	}

	@RequestMapping(value = "/m1", method = RequestMethod.POST)
	public String m1submit(@ModelAttribute LatLon latLon, Model model) {
		List<LatLonData> latLons = new ArrayList<LatLonData>();
		if (latLon == null) {
			latLon = new LatLon();
		}
		String fipsCode = FipsConversionService.getCountyFipsCode(latLon.getLatitude(),
				latLon.getLongitude());
		LatLonData toAdd = new LatLonData(latLon, CensusService.getPlaces(fipsCode),
				EducationService.getSchools(latLon, 5));
		latLons.add(toAdd);
		model.addAttribute("latLons", latLons);
		return "m1submit";
	}

	@RequestMapping(value = "/m1bulk", method = RequestMethod.POST)
	public String m1submitbulk(@ModelAttribute LatLonBulk latLonBulk, Model model) {
		model.addAttribute("message", latLonBulk.getInput());
		String[] lines = latLonBulk.getInput().split("\n");
		List<LatLonData> latLons = new ArrayList<LatLonData>();
		for (String line : lines) {
			if (line.isEmpty()) {
				continue;
			}
			if (!line.contains(" ")) {
				continue;
			}
			String[] split = line.trim().split("\\s+");
			Double lat = new Double(split[0]);
			Double lon = new Double(split[1]);
			LatLon latLon = new LatLon(lat, lon);
			String fipsCode = FipsConversionService.getCountyFipsCode(latLon.getLatitude(),
					latLon.getLongitude());
			LatLonData toAdd = new LatLonData(latLon, CensusService.getPlaces(fipsCode),
					EducationService.getSchools(latLon, 5));
			latLons.add(toAdd);
		}
		model.addAttribute("latLons", latLons);
		return "m1submit";
	}
}
