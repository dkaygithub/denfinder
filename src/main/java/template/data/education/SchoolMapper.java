package template.data.education;

import org.json.JSONException;
import org.json.JSONObject;

public class SchoolMapper {

	public static School map(JSONObject jsonSchool) {
		School school = new School();
		try {
//			school.schoolId = jsonSchool.getString("schoolid");
//			school.schoolName = jsonSchool.getString("schoolname");
//			school.zip = jsonSchool.getString("zip");
//			school.address = jsonSchool.getString("address");
//			school.city = jsonSchool.getString("city");
//			school.districtId = jsonSchool.getString("districtid");
//			school.districtLeaId = jsonSchool.optString("districtleaid");
//			school.aypResult = jsonSchool.optString("AYPResult").equals("yes") ? true : false;
//			school.aypResultYear = new Integer(jsonSchool.optString("AYPResultYear", "0"));
//			school.distance = new Double(jsonSchool.getDouble("distance"));
//			school.totalEnrollment = jsonSchool.getJSONObject("enrollment").getInt("total");
//			school.gradeLevel = jsonSchool.getString("gradelevel");
//			school.latitude = jsonSchool.getDouble("latitude");
//			school.longitude = jsonSchool.getDouble("longitude");
//			school.phoneNumber = jsonSchool.getString("phonenumber");
//			school.schoolDistrictName = jsonSchool.getString("schooldistrictname");
//			school.schoolType = jsonSchool.getString("schooltype");
//			school.state = jsonSchool.getString("state");
//			school.totalStudentTeacherRatio = jsonSchool.getJSONObject("studentteacherratio")
//					.getInt("total");
//			school.website = jsonSchool.get("url") != null ? jsonSchool.getString("url") : null;
//			school.ncesId = jsonSchool.get("nces_id") != null ? jsonSchool.getString("nces_id") : null;
//			school.url = jsonSchool.getString("url");
			school.testRatingText = jsonSchool.getString("testrating_text");
//			school.testRatingImageLarge = jsonSchool.optString("testrating_image_large");
//			school.testRatingImageSmall = jsonSchool.optString("testrating_image_small");
//			school.testRatingYear = jsonSchool.optString("testrating_year");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return school;
	}

}
