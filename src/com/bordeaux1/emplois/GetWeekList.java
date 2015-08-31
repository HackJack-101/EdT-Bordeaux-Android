package com.bordeaux1.emplois;

import http.client.EdTTools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

class GetWeekList extends AsyncTask<String, String, String>
{
	Activity context;
	View view;

	EdTTools edtTools;

	JSONArray JSONList;
	ExpandableListView EdTListView;

	String code;
	String name;
	String group;

	ProgressDialog pDialog;

	int currentWeek;
	int week;

	public GetWeekList(Activity context, View view, String code, int weekNumber)
	{
		this.view = view;
		this.context = context;

		this.code = code;

		this.edtTools = new EdTTools(context);

		String[] split = code.split(" ");
		name = split[0];
		group = split[1];

		EdTListView = (ExpandableListView) view.findViewById(R.id.weekList);
		this.currentWeek = generateWeek();

		if (weekNumber == 0)
			this.week = this.currentWeek;
		else
			this.week = weekNumber;
	}

	@Override
	protected void onPreExecute()
	{
		pDialog = new ProgressDialog(context);
		pDialog.setMessage("Chargement de l'emploi du temps");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(true);
		pDialog.show();
		super.onPreExecute();
	}

	@Override
	protected String doInBackground(String... args)
	{
		JSONList = this.edtTools.getWeek(name, group, week);
		return null;
	}

	@Override
	protected void onPostExecute(String file_url)
	{
		TextView dayDate = (TextView) view.findViewById(R.id.textWeek);
		dayDate.setText("Semaine " + week);

		if (!this.edtTools.isNetworkAvailable())
			Toast.makeText(context, "Vous devez vous connecter à Internet au moins une fois pour afficher une version en cache.", Toast.LENGTH_LONG).show();

		ArrayList<JSONObject> Monday = new ArrayList<JSONObject>();
		ArrayList<JSONObject> Tuesday = new ArrayList<JSONObject>();
		ArrayList<JSONObject> Wednesday = new ArrayList<JSONObject>();
		ArrayList<JSONObject> Thursday = new ArrayList<JSONObject>();
		ArrayList<JSONObject> Friday = new ArrayList<JSONObject>();
		ArrayList<JSONObject> Saturday = new ArrayList<JSONObject>();
		if (JSONList == null)
			Toast.makeText(context, "Aucune donnée n'est disponible. Assurez vous de ne pas avoir configuré un groupe obsolète et d'être connecté à Internet.", Toast.LENGTH_LONG).show();
		else
		{
			for (int dayNumber = 0; dayNumber < JSONList.length(); dayNumber++)
			{
				try
				{
					switch (dayNumber)
					{
					case 0:
						JSONArray MondayList = new JSONArray(JSONList.get(0).toString());
						for (int j = 0; j < MondayList.length(); j++)
							if ((new JSONObject(MondayList.get(j).toString())).length() > 0)
								Monday.add(new JSONObject(MondayList.get(j).toString()));
						break;
					case 1:
						JSONArray TuesdayList = new JSONArray(JSONList.get(1).toString());
						for (int j = 0; j < TuesdayList.length(); j++)
							if ((new JSONObject(TuesdayList.get(j).toString())).length() > 0)
								Tuesday.add(new JSONObject(TuesdayList.get(j).toString()));
						break;
					case 2:
						JSONArray WednesdayList = new JSONArray(JSONList.get(2).toString());
						for (int j = 0; j < WednesdayList.length(); j++)
							if ((new JSONObject(WednesdayList.get(j).toString())).length() > 0)
								Wednesday.add(new JSONObject(WednesdayList.get(j).toString()));
						break;
					case 3:
						JSONArray ThursdayList = new JSONArray(JSONList.get(3).toString());
						for (int j = 0; j < ThursdayList.length(); j++)
							if ((new JSONObject(ThursdayList.get(j).toString())).length() > 0)
								Thursday.add(new JSONObject(ThursdayList.get(j).toString()));
						break;
					case 4:
						JSONArray FridayList = new JSONArray(JSONList.get(4).toString());
						for (int j = 0; j < FridayList.length(); j++)
							if ((new JSONObject(FridayList.get(j).toString())).length() > 0)
								Friday.add(new JSONObject(FridayList.get(j).toString()));
						break;
					case 5:
						JSONArray SaturdayList = new JSONArray(JSONList.get(5).toString());
						for (int j = 0; j < SaturdayList.length(); j++)
							if ((new JSONObject(SaturdayList.get(j).toString())).length() > 0)
								Saturday.add(new JSONObject(SaturdayList.get(j).toString()));
						break;
					default:
						break;
					}
				}
				catch (JSONException e)
				{
					e.printStackTrace();
				}
			}
		}

		List<String> daysWeek = new ArrayList<String>();
		HashMap<String, List<JSONObject>> scheduleData = new HashMap<String, List<JSONObject>>();

		// Adding days of the week
		daysWeek.add(getDay(0));
		daysWeek.add(getDay(1));
		daysWeek.add(getDay(2));
		daysWeek.add(getDay(3));
		daysWeek.add(getDay(4));
		daysWeek.add(getDay(5));

		scheduleData.put(daysWeek.get(0), Monday);
		scheduleData.put(daysWeek.get(1), Tuesday);
		scheduleData.put(daysWeek.get(2), Wednesday);
		scheduleData.put(daysWeek.get(3), Thursday);
		scheduleData.put(daysWeek.get(4), Friday);
		scheduleData.put(daysWeek.get(5), Saturday);

		WeekListAdapter adapter = new WeekListAdapter(context, daysWeek, scheduleData);

		EdTListView.setAdapter(adapter);
		adapter.notifyDataSetChanged();

		pDialog.dismiss();
		View.OnClickListener dayButtonHandler = new View.OnClickListener()
		{
			public void onClick(View v)
			{
				switch (v.getId())
				{
				case R.id.previousWeekButton:
					new GetWeekList(context, view, code, getPreviousWeek()).execute();
					break;
				case R.id.nextWeekButton:
					new GetWeekList(context, view, code, getNextWeek()).execute();
					break;
				}
			}
		};

		Button b1 = (Button) view.findViewById(R.id.previousWeekButton);
		Button b2 = (Button) view.findViewById(R.id.nextWeekButton);
		b1.setOnClickListener(dayButtonHandler);
		b2.setOnClickListener(dayButtonHandler);
	}

	private int getNextWeek()
	{
		return (week + 1 > 52) ? 1 : week + 1;
	}

	private int getPreviousWeek()
	{
		return (week - 1 < 1) ? 52 : week - 1;
	}

	private int generateWeek()
	{
		Calendar cal = Calendar.getInstance();
		cal.get(Calendar.WEEK_OF_YEAR);
		return cal.get(Calendar.WEEK_OF_YEAR);
	}

	private String getDay(int step)
	{
		TimeZone timeZone = TimeZone.getDefault();
		Calendar calendar = Calendar.getInstance(timeZone, Locale.FRANCE);
		int year = calendar.get(Calendar.YEAR);
		calendar.clear();
		calendar.set(Calendar.WEEK_OF_YEAR, week);
		if (currentWeek > 32 && week < 10)
			calendar.set(Calendar.YEAR, year + 1);
		else
			calendar.set(Calendar.YEAR, year);
		calendar.add(Calendar.DATE, step);
		return getFomatedDate(calendar.getTime());
	}

	private String getFomatedDate(Date date)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd/MM/yyyy", Locale.FRANCE);
		String strDate = sdf.format(date);
		return Character.toUpperCase(strDate.charAt(0)) + strDate.substring(1);
	}
}
