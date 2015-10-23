package http.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cache.client.CacheManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class EdTTools
{
	private static String	url		= "http://hackjack.info/et/json.php";
																		
	private JSONParser		jParser;
	private Context			context;

	public EdTTools(Context c)
	{
		jParser = new JSONParser();
		context = c.getApplicationContext();
	}

	public JSONObject getList()
	{
		String fname = "edt_list.json";
		int cacheTime = 1000 * 3600 * Integer.parseInt(CacheManager.getPreferences(context, "cacheDuration", "1"));
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		if (isNetworkAvailable())
		{
			if (CacheManager.fileExists(context, fname))
			{
				File edtList = context.getFileStreamPath(fname);
				if ((edtList.lastModified() + cacheTime) < System.currentTimeMillis())
				{
					JSONObject request = jParser.makeHttpRequest(url, "GET", params);
					if (request != null)
						writeToFile(fname, request.toString());
					else
						return null;
				}
			} else
			{
				JSONObject request = jParser.makeHttpRequest(url, "GET", params);
				if (request != null)
					writeToFile(fname, request.toString());
				else
					return null;
			}

			try
			{
				return new JSONObject(readFromFile(fname));
			} catch (JSONException e)
			{
				return jParser.makeHttpRequest(url, "GET", params);
			}
		} else
		{
			if (CacheManager.fileExists(context, fname))
			{
				try
				{
					return new JSONObject(readFromFile(fname));
				} catch (JSONException e)
				{
					return null;
				}
			}
			return null;
		}
	}

	public JSONArray getDay(String name, String group, Date day)
	{
		String fname = name + "_" + group + "_" + dateToStringFile(day) + ".json";
		int cacheTime = 1000 * 3600 * Integer.parseInt(CacheManager.getPreferences(context, "cacheDuration", "1"));
		String mainGroup = CacheManager.getPreferences(context, "group", "");

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("type", "day"));
		params.add(new BasicNameValuePair("name", name));
		params.add(new BasicNameValuePair("group", group));
		params.add(new BasicNameValuePair("date", dateToString(day)));

		if (mainGroup.equals(new String(name + " " + group)))
		{
			String filters = CacheManager.getPreferences(context, "filters", "") + "@@@" + CacheManager.getPreferences(context, "advancedFilters", "");
			if (!filters.equals("@@@"))
			{
				fname = name + "_" + group + "_" + filters + "_" + dateToStringFile(day) + ".json";
				params.add(new BasicNameValuePair("filters", filters));
			}
		}
		if (isNetworkAvailable())
		{
			if (CacheManager.fileExists(context, fname))
			{
				File edtList = context.getFileStreamPath(fname);
					if ((edtList.lastModified() + cacheTime) < System.currentTimeMillis())
					{
						JSONArray request = jParser.makeHttpRequestArray(url, "GET", params);
						if (request != null)
							writeToFile(fname, request.toString());
						else
							return null;
					}
			} else
			{
				JSONArray request = jParser.makeHttpRequestArray(url, "GET", params);
				if (request != null)
					writeToFile(fname, request.toString());
				else
					return null;
			}

			try
			{
				return new JSONArray(readFromFile(fname));
			} catch (JSONException e)
			{
				return jParser.makeHttpRequestArray(url, "GET", params);
			}
		} else
		{
			if (CacheManager.fileExists(context, fname))
			{
				try
				{
					return new JSONArray(readFromFile(fname));
				} catch (JSONException e)
				{
					return null;
				}
			}
			return null;
		}
	}

	public JSONArray getWeek(String name, String group, int week)
	{
		String fname = name + "_" + group + "_s" + week + ".json";
		int cacheTime = 1000 * 3600 * Integer.parseInt(CacheManager.getPreferences(context, "cacheDuration", "1"));
		String mainGroup = CacheManager.getPreferences(context, "group", "");

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("type", "week"));
		params.add(new BasicNameValuePair("name", name));
		params.add(new BasicNameValuePair("group", group));
		params.add(new BasicNameValuePair("week", Integer.toString(week)));

		if (mainGroup.equals(new String(name + " " + group)))
		{
			String filters = CacheManager.getPreferences(context, "filters", "") + "@@@" + CacheManager.getPreferences(context, "advancedFilters", "");
			if (!filters.equals("@@@"))
			{
				fname = name + "_" + group + "_" + filters + "_s" + week + ".json";
				params.add(new BasicNameValuePair("filters", filters));
			}
		}

		if (isNetworkAvailable())
		{
			if (CacheManager.fileExists(context, fname))
			{
				File edtList = context.getFileStreamPath(fname);
				if (edtList.lastModified() < (System.currentTimeMillis() - cacheTime))
				{
					JSONArray request = jParser.makeHttpRequestArray(url, "GET", params);
					if (request != null)
						writeToFile(fname, request.toString());
					else
						return null;
				}
			} else
			{
				JSONArray request = jParser.makeHttpRequestArray(url, "GET", params);
				if (request != null)
					writeToFile(fname, request.toString());
				else
					return null;
			}

			try
			{
				return new JSONArray(readFromFile(fname));
			} catch (JSONException e)
			{
				return jParser.makeHttpRequestArray(url, "GET", params);
			}
		} else
		{
			if (CacheManager.fileExists(context, fname))
			{
				try
				{
					return new JSONArray(readFromFile(fname));
				} catch (JSONException e)
				{
					return new JSONArray();
				}
			}
			return new JSONArray();
		}
	}

	private String dateToString(Date date)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.FRENCH);
		return sdf.format(date);
	}

	private String dateToStringFile(Date date)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd", Locale.FRENCH);
		return sdf.format(date);
	}

	private void writeToFile(String filename, String data)
	{
		try
		{
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE));
			outputStreamWriter.write(data);
			outputStreamWriter.close();
		} catch (IOException e)
		{
			Log.e("Exception", "File write failed: " + e.toString());
		}
	}

	private String readFromFile(String filename)
	{
		String data = new String("{}");
		try
		{
			InputStream inputStream = context.openFileInput(filename);
			if (inputStream != null)
			{
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String receiveString = new String("");
				StringBuilder stringBuilder = new StringBuilder();

				while ((receiveString = bufferedReader.readLine()) != null)
					stringBuilder.append(receiveString);

				inputStream.close();
				data = stringBuilder.toString();
			}
		} catch (FileNotFoundException e)
		{
			Log.e("login activity", "File not found: " + e.toString());
		} catch (IOException e)
		{
			Log.e("login activity", "Can not read file: " + e.toString());
		}
		return data;
	}

	public boolean isNetworkAvailable()
	{
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

}
