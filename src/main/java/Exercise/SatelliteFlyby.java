package Exercise;

import java.time.LocalDateTime;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;

public class SatelliteFlyby
{
	public static final String InputSpec = "<latitude> <longitude>";
	public static final String LatitudeSpecError = "Incorrect value for the latitude"
		+ "input:";
	public static final String LongitudeSpecError = "Incorrect value for the longitude"
		+ "input:";
	
	public static final String NASAAssetsURL = 
		"https://api.nasa.gov/planetary/earth/assets";
	public static final int NASAYearsInterval = 3;

	public static final String NASAAPIParameterLatitude = "lat";
	public static final String NASAAPIParameterLongitude = "lon";
	public static final String NASAAPIParameterBegin = "begin";
	public static final String NASAAPIParameterEnd = "end";
	public static final String NASAAPIParameterApikey = "api_key";
	public static final String NASAAPIParameterApiKeyValue =
		"9Jz6tLIeJ0yY9vjbEUWaH9fsXA930J9hspPchute";
	
	public static final String NASAAPIResultCount = "count";
	public static final String NASAAPIResults = "results";
	public static final String NASAAPIResultDate = "date";
	
	public static final String NASAAPIResponse = "api_key";
	
	public static final String RetryRequest =
		"Could you retry the flyby request at a later time?";
	public static final String NASAServerInternalError =
		"The NASA server which contains the flyby data is unavailable at this "
		+ " time.";
	public static final String NASAServerUpgradedError =
		"NASA Server response has been upgraded"
		+ " Please try your request when the Flyby server has been"
		+ " upgraded.";
	public static final String ServerInternalError =
		"The Flyby server is being upgraded at this time.";
	
	public void checkHttpStatusCode(int httpStatusCode)
		throws Exception
	{
		String errorMessage;
		if (httpStatusCode != 200 && httpStatusCode!= 201) {
			errorMessage = NASAServerInternalError;
			throw new Exception(errorMessage);
		}
	}
	
	public String buildQueryString(String charset, float latitude, float longitude)
		throws Exception
	{
		ZonedDateTime localDateTimeNow = ZonedDateTime.now();
		ZonedDateTime zonedDateTimeBegin = localDateTimeNow.minusYears(NASAYearsInterval);

		String queryStartDate = zonedDateTimeBegin.format(DateTimeFormatter.ISO_LOCAL_DATE);
		queryStartDate = URLEncoder.encode(queryStartDate, charset);
		String queryEndDate = localDateTimeNow.format(DateTimeFormatter.ISO_LOCAL_DATE);
		queryEndDate = URLEncoder.encode(queryEndDate, charset);
		String apiKey = URLEncoder.encode(NASAAPIParameterApiKeyValue, charset);
		
		// NASA Assets Api has this format
		// ?lon=100.75&lat=1.5&begin=2014-02-01&api_key=DEMO_KEY
		String query = String.format("%s=%.2f&%s=%.2f&%s=%s&%s=%s&%s=%s",
			NASAAPIParameterLatitude, latitude,
			NASAAPIParameterLongitude, longitude,
			NASAAPIParameterBegin, queryStartDate,
			NASAAPIParameterEnd, queryEndDate,
			NASAAPIParameterApikey, apiKey);
		
		return query;
	}
	
	public class FlybyResult {
		double averageDeltaSeconds;
		LocalDateTime lastFlybyTime;
	}
	
	public LocalDateTime[] parseFlybyDataFromNasaResponse(String response)
		throws Exception
	{
		LocalDateTime flybyDates[] = null;
		
		try {
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = (JSONObject)parser.parse(response);
			
			if (jsonObject == null) {
				return null;
			}
			long resultCount = (Long)jsonObject.get(NASAAPIResultCount);
			if (resultCount < 1) {
				return null;
			}
			JSONArray resultsArray = (JSONArray)jsonObject.get(NASAAPIResults);
			if (resultsArray == null || resultsArray.size() < 1) {
				return null;
			}
			
			flybyDates = new LocalDateTime[resultsArray.size()];
	
			for (int i = 0; i < resultsArray.size(); i++) {
				JSONObject jsonDate = (JSONObject) resultsArray.get(i);
				if (jsonDate == null) {
					return null;
				}
				String dateString = (String) jsonDate.get(NASAAPIResultDate);
				if (dateString == null || dateString.isEmpty()) {
					return null;
				}
				// Nasa currently returns dates in the following format
				// '2014-02-13T18:04:04'
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
				//DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX") 
				LocalDateTime flybyDate = LocalDateTime.parse(dateString, formatter);
				flybyDates[i] = flybyDate;
			}
			Arrays.sort(flybyDates);
			
		} catch (Exception e) {
			throw new Exception(ServerInternalError);
		}
		return flybyDates;
	}
			
	public FlybyResult getAverageDeltaTime(LocalDateTime[] flybyDates)
		throws Exception
	{
		FlybyResult flyByResult = new FlybyResult();
		
		try {
			LocalDateTime lastFlybyDate = null;
			double totalDeltaSeconds = 0.0;
			for (int i = 0; i < flybyDates.length; i++) {
				if (lastFlybyDate != null) {
					long seconds = lastFlybyDate.until( flybyDates[i], ChronoUnit.SECONDS);
					totalDeltaSeconds += seconds;
				}
				lastFlybyDate = flybyDates[i];
			}
			flyByResult.averageDeltaSeconds = totalDeltaSeconds/flybyDates.length;
			flyByResult.lastFlybyTime = lastFlybyDate;
			
		} catch (Exception e) {
			throw new Exception(ServerInternalError);
		}
		return flyByResult;
	}
	
	public String queryNasaForAssetsData(float latitude, float longitude)
		throws Exception
	{
		String response;
		
		try {
			String charset = "UTF-8";
			
			String query = buildQueryString(charset, latitude, longitude);
			URL url = new URL(NASAAssetsURL + "?" + query.toString());
			
			HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
			httpUrlConnection.setRequestProperty("Accept-Charset", charset);
			httpUrlConnection.setRequestMethod("GET");
			httpUrlConnection.connect();
			
			int responseCode = httpUrlConnection.getResponseCode();
			checkHttpStatusCode(responseCode);
			
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(httpUrlConnection.getInputStream()));
			String inputLine;
			StringBuffer responseBuffer = new StringBuffer();
	
			while ((inputLine = in.readLine()) != null) {
				responseBuffer.append(inputLine);
				// If response is larger than 10M then the dataset is bigger
				// than we are expecting
				if (responseBuffer.length() > 1024*1024*10) {
					throw new Exception();
				}
			}
			in.close();
			response = responseBuffer.toString();
		} catch (Exception e) {
			throw new Exception(ServerInternalError);
		}
		return response;
	}
		
	public LocalDateTime getNextFlybyTime(double latitude, double longitude)
	throws Exception
	{
		String nasaResponse = queryNasaForAssetsData((float)latitude, 
			(float)longitude);
		
		LocalDateTime[] flybyResults = parseFlybyDataFromNasaResponse(nasaResponse);
		FlybyResult flybyResult = getAverageDeltaTime(flybyResults);
		if (flybyResult == null || flybyResult.lastFlybyTime == null) {
			throw new Exception(NASAServerInternalError);
		}
		
		LocalDateTime lastFlybyTime = flybyResult.lastFlybyTime;
		long averageDeltaSeconds = (long)flybyResult.averageDeltaSeconds;
		
		LocalDateTime nextFlybyTime = lastFlybyTime.plus(
			averageDeltaSeconds, ChronoUnit.SECONDS);
		return nextFlybyTime;
	}
	
	public void flyby(double latitude, double longitude)
		throws Exception
	{
		LocalDateTime nextFlybyTime = getNextFlybyTime(latitude, longitude);
		String nextFlybyTimeString = nextFlybyTime.format(
			DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		
		System.out.println("Next time: " + nextFlybyTimeString);
	}
	
    public static void main( String[] args )
    {
        if (args == null || (args.length < 2) ||
        	args[0].isEmpty() || args[1].isEmpty()) {
        	System.out.println("SatelliteFlyby ");
        	System.exit(1);
        }
        Double latitude = 0.0;
        Double longitude = 0.0;
        try {
        	latitude = Double.parseDouble(args[0]);
        } catch (Exception e) {
        	System.out.println("SatelliteFlyby:" + LatitudeSpecError + args[0]);
        	System.exit(1);
        }
        try {
        	longitude = Double.parseDouble(args[1]);
        } catch (Exception e) {
        	System.out.println("SatelliteFlyby:" + LongitudeSpecError + args[1]);
        	System.exit(1);
        }
        SatelliteFlyby satelliteFlyby = new SatelliteFlyby();
        try {
        	satelliteFlyby.flyby(latitude, longitude);
        } catch (Exception exception) {
        	System.out.println("Error encountered while getting the next flyby time" +
        		exception.getMessage());
        }		
    }
}
