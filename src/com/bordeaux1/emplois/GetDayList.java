package com.bordeaux1.emplois;

import http.client.EdTTools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cache.client.CacheManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

class GetDayList extends AsyncTask<String, String, String>
{
	Activity context;
	View view;

	EdTTools edtTools;

	JSONArray JSONList;
	ListView EdTListView;

	String code;
	String name;
	String group;
	Date date;

	ProgressDialog pDialog;

	ArrayList<JSONObject> dayList;

	public GetDayList(Activity context, View view, String code, Date dayDate)
	{
		this.view = view;
		this.context = context;

		this.code = code;

		this.edtTools = new EdTTools(context);

		String[] split = code.split(" ");
		this.name = split[0];
		this.group = split[1];

		this.EdTListView = (ListView) view.findViewById(R.id.dayListView);
		if (dayDate == null)
			this.date = generateDate();
		else
			this.date = dayDate;
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
		this.JSONList = this.edtTools.getDay(name, group, date);
		return null;
	}

	@Override
	protected void onPostExecute(String file_url)
	{

		TextView dayDate = (TextView) view.findViewById(R.id.textDay);
		dayDate.setText(getDate());

		dayList = new ArrayList<JSONObject>();

		if(JSONList == null)
		{
			ArrayList<String> ar = new ArrayList<String>();
			ar.add("Version en cache non disponible");
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.simple_list_item, ar);
			EdTListView.setAdapter(adapter);
			adapter.notifyDataSetChanged();
			Toast.makeText(context, "Vous devez vous connecter à Internet au moins une fois pour afficher une version en cache.", Toast.LENGTH_SHORT).show();
		}
		else if (JSONList.length() == 0)
		{
			ArrayList<String> ar = new ArrayList<String>();
			ar.add("Pas de cours");
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.simple_list_item, ar);
			EdTListView.setAdapter(adapter);
			adapter.notifyDataSetChanged();
		}
		else
		{
			for (int i = 0; i < JSONList.length(); i++)
			{
				try
				{
					dayList.add(new JSONObject(JSONList.get(i).toString()));
				}
				catch (JSONException e)
				{
					e.printStackTrace();
				}
			}

			DayAdapter adapter = new DayAdapter(context, dayList);

			adapter.sort(new Comparator<JSONObject>()
			{
				@Override
				public int compare(JSONObject lhs, JSONObject rhs)
				{
					try
					{
						return lhs.getString("schedule").compareTo(rhs.getString("schedule"));
					}
					catch (JSONException e)
					{
						e.printStackTrace();
					}
					return 0;
				}
			});

			EdTListView.setAdapter(adapter);
			adapter.notifyDataSetChanged();
		}
		pDialog.dismiss();
		View.OnClickListener dayButtonHandler = new View.OnClickListener()
		{
			public void onClick(View v)
			{
				switch (v.getId())
				{
				case R.id.previousDayButton:
					new GetDayList(context, view, code, getPreviousDay()).execute();
					break;
				case R.id.nextDayButton:
					new GetDayList(context, view, code, getNextDay()).execute();
					break;
				}
			}
		};

		Button b1 = (Button) view.findViewById(R.id.previousDayButton);
		Button b2 = (Button) view.findViewById(R.id.nextDayButton);
		b1.setOnClickListener(dayButtonHandler);
		b2.setOnClickListener(dayButtonHandler);
	}

	private String getDate()
	{
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd/MM/yyyy", Locale.FRANCE);
		String strDate = sdf.format(date);
		return Character.toUpperCase(strDate.charAt(0)) + strDate.substring(1);
	}

	private Date generateDate()
	{
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.US);
		if (sdf.format(date).equalsIgnoreCase("sunday"))
		{
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.DATE, 1);
			return cal.getTime();
		}
		return date;
	}

	private Date getNextDay()
	{
		return getDay(1);
	}

	private Date getPreviousDay()
	{
		return getDay(-1);
	}

	private Date getDay(int step)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.US);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, step);
		Date day = cal.getTime();
		if (sdf.format(day).equalsIgnoreCase("sunday"))
		{
			cal.setTime(day);
			cal.add(Calendar.DATE, step);
			return cal.getTime();
		}
		return day;
	}

	public class DayAdapter extends BaseAdapter
	{

		class DayViewHolder
		{
			public RelativeLayout layout;
			public TextView title;
			public TextView subject;
			public TextView staff;
			public TextView room;
			public TextView group;
			public TextView annotation;
		}

		ArrayList<JSONObject> dayList;
		Context context;

		public DayAdapter(Context context, ArrayList<JSONObject> list)
		{
			this.context = context;
			this.dayList = list;
		}

		@Override
		public int getCount()
		{
			return dayList.size();
		}

		public void sort(Comparator<JSONObject> comparator)
		{
		}

		@Override
		public String getItem(int position)
		{
			return dayList.get(position).toString();
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			DayViewHolder holder = null;

			if (convertView == null)
			{
				LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				convertView = inflater.inflate(R.layout.day, parent, false);
				convertView.setLongClickable(false);
				convertView.setClickable(false);

				holder = new DayViewHolder();

				holder.layout = (RelativeLayout) convertView.findViewById(R.id.dayEdTLayout);
				holder.title = (TextView) convertView.findViewById(R.id.title);
				holder.subject = (TextView) convertView.findViewById(R.id.subject);
				holder.staff = (TextView) convertView.findViewById(R.id.staff);
				holder.room = (TextView) convertView.findViewById(R.id.room);
				holder.group = (TextView) convertView.findViewById(R.id.group);
				holder.annotation = (TextView) convertView.findViewById(R.id.annotation);

				convertView.setTag(holder);
			}
			else
				holder = (DayViewHolder) convertView.getTag();

			JSONObject day = dayList.get(position);

			holder.title.setText(getInformation(day, "schedule"));
			holder.subject.setText(getInformation(day, "subject"));
			holder.staff.setText(getInformation(day, "staff"));
			holder.room.setText(getInformation(day, "room"));
			holder.group.setText(getInformation(day, "group"));
			holder.annotation.setText(getInformation(day, "annotation"));

			final String UEcode = getInformation(day, "subject").split(" ")[0];
			final String groups = getInformation(day, "group").replaceAll("( \\| )", "\n");

			holder.layout.setBackgroundResource(R.drawable.back);
			String style = getStyle(day);
			if (style.equalsIgnoreCase("a8ffa8"))
				holder.layout.setBackgroundResource(R.drawable.a8ffa8);
			else if (style.equalsIgnoreCase("bea8d3"))
				holder.layout.setBackgroundResource(R.drawable.bea8d3);
			else if (style.equalsIgnoreCase("bed3d3"))
				holder.layout.setBackgroundResource(R.drawable.bed3d3);
			else if (style.equalsIgnoreCase("d3a8a8"))
				holder.layout.setBackgroundResource(R.drawable.d3a8a8);
			else if (style.equalsIgnoreCase("d3a8be"))
				holder.layout.setBackgroundResource(R.drawable.d3a8be);
			else if (style.equalsIgnoreCase("d3a8ff"))
				holder.layout.setBackgroundResource(R.drawable.d3a8ff);
			else if (style.equalsIgnoreCase("dedede"))
				holder.layout.setBackgroundResource(R.drawable.dedede);
			else if (style.equalsIgnoreCase("ffa8ff"))
				holder.layout.setBackgroundResource(R.drawable.ffa8ff);
			else if (style.equalsIgnoreCase("ffffa8"))
				holder.layout.setBackgroundResource(R.drawable.ffffa8);

			convertView.setOnLongClickListener(new OnLongClickListener()
			{
				@SuppressLint("NewApi")
				public boolean onLongClick(View v)
				{
					final Dialog popup = new Dialog(v.getContext());
					popup.setContentView(R.layout.popup_schedule);
					popup.setTitle(UEcode);

					Button excludeUE = (Button) popup.findViewById(R.id.exclude_ue);
					Button filterUE = (Button) popup.findViewById(R.id.filter_ue);
					popup.show();

					excludeUE.setOnClickListener(new View.OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							String filters = CacheManager.getPreferences(v.getContext(), "filters", "");
							if (filters == "")
								CacheManager.savePreferences(v.getContext(), "filters", UEcode);
							else
								CacheManager.savePreferences(v.getContext(), "filters", filters + "," + UEcode);
							CacheManager.cleanCache(v.getContext());
							Toast.makeText(v.getContext(), UEcode + " a été ajouté aux filtres pour votre groupe principal", Toast.LENGTH_SHORT).show();
							popup.dismiss();
						}

					});

					filterUE.setOnClickListener(new View.OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							final Dialog popupFilter = new Dialog(v.getContext());
							popupFilter.setContentView(R.layout.popup_filter);
							popupFilter.setTitle("Filtrer les groupes");
							
							TextView excludeUE = (TextView) popupFilter.findViewById(R.id.filter_group);
							EditText filterUE = (EditText) popupFilter.findViewById(R.id.group_filtered);
							Button confirmFilter = (Button) popupFilter.findViewById(R.id.confirm_filter);

							excludeUE.setText("Vous pouvez définir votre groupe spécifique à l'UE " + UEcode + ".\n"
									+ "Seuls les créneaux comprenant le groupe que vous spécifierez seront affichés.\n\n" +
									"Si le formulaire suivant contient plusieurs lignes, éditez-le pour ne laisser que votre groupe puis validez :");
							filterUE.setText(groups);
							
							popupFilter.show();
							
							confirmFilter.setOnClickListener(new View.OnClickListener()
							{
								@Override
								public void onClick(View v)
								{
									EditText filterUE = (EditText) popupFilter.findViewById(R.id.group_filtered);
									String code = UEcode + ":" + filterUE.getText().toString();
									if(code.matches("(?is).*[\n\r].*"))
									{
										Toast.makeText(v.getContext(), "Erreur : Il ne faut laisser qu'un seul goupe.", Toast.LENGTH_LONG).show();
									}
									else
									{
										String filters = CacheManager.getPreferences(v.getContext(), "advancedFilters", "");
										if (filters == "")
											CacheManager.savePreferences(v.getContext(), "advancedFilters", code);
										else
											CacheManager.savePreferences(v.getContext(), "advancedFilters", filters + "," + code);
										CacheManager.cleanCache(v.getContext());
										Toast.makeText(v.getContext(), UEcode + " a été ajouté aux filtres pour votre groupe principal", Toast.LENGTH_SHORT).show();
										popupFilter.dismiss();
										popup.dismiss();
									}
								}

							});
						}

					});

					return true;
				}
			});

			return convertView;
		}

		@Override
		public boolean areAllItemsEnabled()
		{
			return true;
		}

		@Override
		public boolean isEnabled(int position)
		{
			return true;
		}

		public String getInformation(JSONObject day, String name)
		{
			try
			{
				if (day.getString(name) != "")
					return day.getString(name);
				else
				{
					if (name.equals("annotation"))
						return "";
					else if (name.equals("room"))
						return "Non communiquee";
					return "Non communique";
				}
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
			return null;
		}

		public String getStyle(JSONObject day)
		{
			try
			{
				String style = day.getString("style");
				String[] split = style.split("#");
				return split[1].substring(0, split[1].length() - 1);
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
			return null;
		}

	}

}