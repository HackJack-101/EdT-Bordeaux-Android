package com.bordeaux1.emplois;

import http.client.EdTTools;
import cache.client.CacheManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import org.json.JSONObject;

import com.bordeaux1.emplois.R;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.LayoutParams;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity
{
	private ProgressDialog pDialog;
	private TextWatcher refreshWithSearch;
	private static EdTTools edtTools;
	private EditText editTextSearch;

	private DrawerLayout menuLayout; // Layout Principal
	private ListView menuElementsList; // Menu
	private ActionBarDrawerToggle menuToggle;
	DrawerAdapter drawerAdapter;

	List<DrawerItem> dataList;
	private boolean searchOpened;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		if (getIntent().getBooleanExtra("EXIT", false))
		{
			finish();
		}
		else
		{
			setContentView(R.layout.activity_main);
			searchOpened = false;

			// Navigation drawer
			// Device width
			Display display = getWindowManager().getDefaultDisplay();
			DisplayMetrics outMetrics = new DisplayMetrics();
			display.getMetrics(outMetrics);
			Double width = outMetrics.widthPixels * 0.75;

			menuLayout = (DrawerLayout) findViewById(R.id.menu_layout);
			menuElementsList = (ListView) findViewById(R.id.menu_elements);

			LayoutParams params = (LayoutParams) menuElementsList.getLayoutParams();
			params.width = width.intValue();
			menuElementsList.setLayoutParams(params);

			menuLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

			getActionBar().setDisplayHomeAsUpEnabled(true);
			getActionBar().setHomeButtonEnabled(true);

			menuToggle = new ActionBarDrawerToggle(this, menuLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close)
			{
				public void onDrawerClosed(View view)
				{
					getActionBar().setTitle("EdT Bordeaux");
					invalidateOptionsMenu();
				}

				public void onDrawerOpened(View drawerView)
				{
					getActionBar().setTitle("EdT Bordeaux");
					invalidateOptionsMenu();

					dataList = new ArrayList<DrawerItem>();
					dataList.add(new DrawerItem("Groupe(s) favori(s)", 0, false));

					Set<String> groups = CacheManager.getSetPreferences(MainActivity.this, "favorites", new TreeSet<String>());

					String mainGroup = CacheManager.getPreferences(MainActivity.this, "group", "");

					if (groups.isEmpty() && mainGroup.equals(""))
						dataList.add(new DrawerItem("Aucun favori", -1, false));
					if (!mainGroup.equals(""))
						dataList.add(new DrawerItem(mainGroup, 11, true));
					Iterator<String> itr = groups.iterator();
					while (itr.hasNext())
					{
						dataList.add(new DrawerItem(itr.next(), 2, true));
					}

					dataList.add(new DrawerItem("ENT", 0, false));
					dataList.add(new DrawerItem("Accueil", 3, true));
					dataList.add(new DrawerItem("Calendrier", 10, true));
					dataList.add(new DrawerItem("Groupes de TD", 4, true));
					dataList.add(new DrawerItem("Portail de scolarité", 5, true));
					dataList.add(new DrawerItem("Boite mail", 6, true));
					dataList.add(new DrawerItem("Newsgroups", 7, true));
					dataList.add(new DrawerItem("Plans du campus", 0, true));
					dataList.add(new DrawerItem("1ère et 2ème tranches", 9, true));
					dataList.add(new DrawerItem("3ème tranche", 9, true));
					dataList.add(new DrawerItem("Associations", 0, true));
					dataList.add(new DrawerItem("Label[i]", 8, true));
					dataList.add(new DrawerItem("Paramètres", 1, true));

					drawerAdapter = new DrawerAdapter(MainActivity.this, R.layout.element_menu, dataList);
					menuElementsList.setAdapter(drawerAdapter);
				}
			};
			menuLayout.setDrawerListener(menuToggle);
			menuElementsList.setOnItemClickListener(new OnItemClickListener()
			{
				@Override
				public void onItemClick(AdapterView<?> arg0, View view, int position, long id)
				{
					Set<String> groups = CacheManager.getSetPreferences(MainActivity.this, "favorites", new TreeSet<String>());
					String mainGroup = CacheManager.getPreferences(MainActivity.this, "group", "");
					int favorites = groups.size() + (mainGroup.length() > 0 ? 1 : 0);
					boolean empty = favorites == 0;
					if (empty)
					{
						favorites = 1;
					}

					if (position == 0)
					{
					}
					else if (position == dataList.size() - 1)
					{
						Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
						startActivity(intent);
					}
					else if (position <= favorites && !empty)
					{
						if (mainGroup.length() > 0)
						{
							Intent intent = new Intent(getBaseContext(), ScheduleActivity.class);
							if (position == 1)
								intent.putExtra("code", mainGroup);
							else
							{
								Iterator<String> itr = groups.iterator();
								for (int i = 1; i < position - 1; i++)
									itr.next();
								intent.putExtra("code", itr.next());
							}
							startActivity(intent);
						}
						else
						{
							Iterator<String> itr = groups.iterator();
							for (int i = 0; i < position - 1; i++)
								itr.next();
							Intent intent = new Intent(getBaseContext(), ScheduleActivity.class);
							intent.putExtra("code", itr.next());
							startActivity(intent);
						}
					}
					else if (position > favorites + 1)
					{
						Intent intent = new Intent(getBaseContext(), WebViewActivity.class);
						switch (position - favorites - 1)
						{
						case 1:
							intent.putExtra("url", "http://hackjack.info/calendar/links.php?id=1");
							intent.putExtra("title", "ENT Université Bordeaux");
							startActivity(intent);
							break;
						case 2:
							intent.putExtra("url", "http://hackjack.info/calendar/links.php?id=2");
							intent.putExtra("title", "Calendrier Universitaire");
							startActivity(intent);
							break;
						case 3:
							intent.putExtra("url", "http://hackjack.info/calendar/links.php?id=3");
							intent.putExtra("title", "Groupes de TD");
							startActivity(intent);
							break;
						case 4:
							intent.putExtra("url", "http://hackjack.info/calendar/links.php?id=4");
							intent.putExtra("title", "Portail de scolarité");
							startActivity(intent);
							break;
						case 5:
							intent.putExtra("url", "http://hackjack.info/calendar/links.php?id=5");
							intent.putExtra("title", "Boite mail Zimbra");
							startActivity(intent);
							break;
						case 6:
							intent.putExtra("url", "http://hackjack.info/calendar/links.php?id=6");
							intent.putExtra("title", "Newsgroups");
							startActivity(intent);
							break;
						case 8:
							intent.putExtra("url", "http://hackjack.info/calendar/links.php?id=7");
							intent.putExtra("title", "Plan Bordeaux S&T");
							startActivity(intent);
							break;
						case 9:
							intent.putExtra("url", "http://hackjack.info/calendar/links.php?id=8");
							intent.putExtra("title", "Plan Bordeaux S&T");
							startActivity(intent);
							break;
						case 11:
							intent.putExtra("url", "http://hackjack.info/calendar/links.php?id=101");
							intent.putExtra("title", "Agenda Label[i]");
							startActivity(intent);
							break;
						default:
							break;
						}
					}
					else
					{
						// Toast.makeText(getApplicationContext(), "Position : "
						// + position + " ID : " + id,
						// Toast.LENGTH_LONG).show();
					}
				}
			});
			MainActivity.this.setTitle("EdT Bordeaux");

			edtTools = new EdTTools(getBaseContext());
			new GetEdTList().execute();

			if (getIntent().getAction() == Intent.ACTION_VIEW)
			{
				Uri data = getIntent().getData();
				if (data.getUserInfo() != null)
				{
					if (data.getUserInfo().equals(new String("sacp")))
					{

						if (data.getQueryParameter("class") != null)
							CacheManager.savePreferences(MainActivity.this, "group", data.getQueryParameter("class").replace('_', ' '));
						if (data.getQueryParameter("filters") != null)
							CacheManager.savePreferences(MainActivity.this, "filters", data.getQueryParameter("filters"));
						if (data.getQueryParameter("advancedFilters") != null)
							CacheManager.savePreferences(MainActivity.this, "advancedFilters", data.getQueryParameter("advancedFilters").replace('+', ' '));

						CacheManager.cleanCache(MainActivity.this);

						Intent settingsIntent = new Intent(getBaseContext(), SettingsActivity.class);
						startActivity(settingsIntent);
					}
				}
				else
				{
					String host = data.getHost();
					if (host == "www.disvu.u-bordeaux1.fr")
					{
						Toast.makeText(getApplicationContext(), "Vous pouvez retrouver votre emploi du temps en utilisant le bouton de recherche.",
								Toast.LENGTH_SHORT).show();
					}
					else
					{
						if (getParameters(data.getPath()) != null)
						{
							Intent intent = new Intent(getBaseContext(), ScheduleActivity.class);
							intent.putExtra("code", getParameters(data.getPath()));
							startActivity(intent);
						}
						else
							Toast.makeText(getApplicationContext(), "Vous pouvez retrouver votre emploi du temps en utilisant le bouton de recherche.",
									Toast.LENGTH_SHORT).show();
					}
				}
			}
			else
			{
				String groupSettings = CacheManager.getPreferences(MainActivity.this, "group", "");
				if (!groupSettings.equals(""))
				{
					Intent intent = new Intent(getBaseContext(), ScheduleActivity.class);
					intent.putExtra("code", groupSettings);
					startActivity(intent);
				}
			}

		}
	}

	protected void onPostCreate(Bundle savedInstanceState)
	{
		super.onPostCreate(savedInstanceState);
		menuToggle.syncState();

	}

	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		menuToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		boolean drawerOpen = menuLayout.isDrawerOpen(menuElementsList);
		menu.findItem(R.id.action_search).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (menuToggle.onOptionsItemSelected(item))
		{
			return true;
		}
		switch (item.getItemId())
		{
		case R.id.action_search:
			searchOpened = true;
			ActionBar actionBar = getActionBar();
			actionBar.setCustomView(R.layout.actionbar_view);
			editTextSearch = (EditText) actionBar.getCustomView().findViewById(R.id.searchfield);
			editTextSearch.addTextChangedListener(refreshWithSearch);
			actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
			actionBar.setDisplayHomeAsUpEnabled(true);
			Button eraseSearch = (Button) actionBar.getCustomView().findViewById(R.id.remove_searchfield);

			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(0, 0);

			eraseSearch.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					editTextSearch.setText("");
				}

			});
			return true;
		case R.id.action_leave:
			Intent exitIntent = new Intent(getApplicationContext(), MainActivity.class);
			exitIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			exitIntent.putExtra("EXIT", true);
			startActivity(exitIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (event.getAction() == KeyEvent.ACTION_DOWN)
		{
			switch (keyCode)
			{
			case KeyEvent.KEYCODE_BACK:
				if (searchOpened)
				{
					ActionBar actionBar = getActionBar();
					editTextSearch = (EditText) actionBar.getCustomView().findViewById(R.id.searchfield);
					if (editTextSearch.getText().toString().equals(""))
					{
						searchOpened = false;

						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(editTextSearch.getWindowToken(), 0);

						actionBar.setDisplayShowCustomEnabled(false);
						actionBar.setDisplayShowTitleEnabled(true);
						actionBar.setDisplayShowHomeEnabled(true);
						actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_TITLE);
						actionBar.setDisplayHomeAsUpEnabled(true);
					}
					else
					{
						editTextSearch.setText("");
					}

				}
				else
				{
					finish();
				}
				return true;
			}

		}
		return super.onKeyDown(keyCode, event);
	}

	private String getParameters(String path)
	{
		// Week view with argument
		if (path.matches("^/et/[A-Za-z0-9]+_[A-Za-z0-9]+/s[0-9]+/?$"))
			return path.replaceAll("^/et/([A-Za-z0-9]+)_([A-Za-z0-9]+)/s([0-9]+)/?$", "$1 $2");
		// Week view without argument
		if (path.matches("^/et/[A-Za-z0-9]+_[A-Za-z0-9]+/s/?$"))
			return path.replaceAll("^/et/([A-Za-z0-9]+)_([A-Za-z0-9]+)/s/?$", " $1 $2");
		// Day view with argument
		if (path.matches("^/et/[A-Za-z0-9]+_[A-Za-z0-9]+/20[0-9][0-9]/[0-1][0-9]/[0-3][0-9]/?$"))
			return path.replaceAll("^/et/([A-Za-z0-9]+)_([A-Za-z0-9]+)/(20[0-9][0-9])/([0-1][0-9])/([0-3][0-9])/?$", "$1 $2");
		// Day view without argument
		if (path.matches("^/et/[A-Za-z0-9]+_[A-Za-z0-9]+/?$"))
			return path.replaceAll("^/et/([A-Za-z0-9]+)_([A-Za-z0-9]+)/?$", "$1 $2");
		// Home and others cases
		return null;
	}

	private class GetEdTList extends AsyncTask<String, String, String>
	{
		private JSONObject EdTList;
		private ListView EdTListView;
		private ArrayAdapter<String> adapter;
		private ArrayList<String> rawList;
		private ArrayList<String> list;
		private String itemSelected;

		public GetEdTList()
		{
			EdTListView = (ListView) findViewById(R.id.listView1);
		}

		@Override
		protected void onPreExecute()
		{
			pDialog = new ProgressDialog(MainActivity.this);
			pDialog.setMessage("Chargement de la liste");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... args)
		{
			EdTList = MainActivity.edtTools.getList();
			return null;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void onPostExecute(String file_url)
		{
			if(EdTList == null)
			{
				Toast.makeText(MainActivity.this, "Vous devez vous connecter à Internet au moins une fois pour afficher une version en cache.", Toast.LENGTH_LONG).show();
				pDialog.dismiss();
				return;
			}
			if(!MainActivity.edtTools.isNetworkAvailable())
				Toast.makeText(MainActivity.this, "Version hors ligne.", Toast.LENGTH_SHORT).show();

			rawList = getRawList();
			list = (ArrayList<String>) rawList.clone();

			refreshList(true);
			pDialog.dismiss();

			refreshWithSearch = new TextWatcher()
			{
				@Override
				public void afterTextChanged(Editable arg0)
				{
				}

				@Override
				public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
				{
				}

				@Override
				public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
				{
					recreateList(editTextSearch.getText().toString());
				}
			};

			EdTListView.setOnItemClickListener(new OnItemClickListener()
			{
				@Override
				public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3)
				{
					String item = ((TextView) view).getText().toString();
					Intent intent = new Intent(getBaseContext(), ScheduleActivity.class);
					intent.putExtra("code", item);
					startActivity(intent);
				}
			});

			EdTListView.setOnItemLongClickListener(new OnItemLongClickListener()
			{
				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View v, int pos, long lng)
				{
					final Dialog popup = new Dialog(MainActivity.this);
					popup.setContentView(R.layout.popup_main);
					itemSelected = EdTListView.getItemAtPosition(pos).toString();
					popup.setTitle(itemSelected);

					Button saveGroup = (Button) popup.findViewById(R.id.save_group);

					Button actionFavorite = (Button) popup.findViewById(R.id.action_favorite);

					Set<String> favorites = CacheManager.getSetPreferences(MainActivity.this, "favorites", new TreeSet<String>());
					if (favorites.contains(itemSelected))
						actionFavorite.setText("Supprimer des favoris");
					String mainGroup = CacheManager.getPreferences(MainActivity.this, "group", "");
					if(mainGroup.equals(itemSelected))
						saveGroup.setText("Oublier le groupe principal");

					popup.show();

					saveGroup.setOnClickListener(new View.OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							String action;
							String mainGroup = CacheManager.getPreferences(MainActivity.this, "group", "");
							if(!mainGroup.equals(itemSelected))
							{
								CacheManager.savePreferences(MainActivity.this, "group", itemSelected);
								action = " enregistré";
							}
							else
							{
								CacheManager.savePreferences(MainActivity.this, "group", "");
								action = " oublié";
							}
							Toast.makeText(MainActivity.this, itemSelected + action + " en tant que groupe principal de TD", Toast.LENGTH_SHORT).show();
							popup.dismiss();
						}

					});

					actionFavorite.setOnClickListener(new View.OnClickListener()
					{
						@Override
						public void onClick(View v)
						{
							String action;
							Set<String> favorites = CacheManager.getSetPreferences(MainActivity.this, "favorites", new TreeSet<String>());
							if (!favorites.contains(itemSelected))
							{
								favorites.add(itemSelected);
								action = " ajouté aux";
							}
							else
							{
								favorites.remove(itemSelected);
								action = " supprimé des";
							}
							CacheManager.saveSetPreferences(MainActivity.this, "favorites", favorites);
							popup.dismiss();
							Toast.makeText(getApplicationContext(), "Groupe " + itemSelected + action + " favoris", Toast.LENGTH_LONG).show();
						}

					});

					return true;
				}
			});

		}

		private ArrayList<String> getRawList()
		{
			ArrayList<String> ar = new ArrayList<String>();
			Iterator<?> keys = EdTList.keys();
			while (keys.hasNext())
			{
				String key = (String) keys.next();
				ar.add(key.replace("_", " "));
			}
			return ar;
		}

		public void refreshList(boolean notifyChange)
		{
			adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, list);
			adapter.sort(new Comparator<String>()
			{
				@Override
				public int compare(String lhs, String rhs)
				{
					return lhs.compareTo(rhs);
				}
			});
			EdTListView.setAdapter(adapter);
			adapter.notifyDataSetChanged();
		}

		@SuppressWarnings("unchecked")
		public void recreateList(String search)
		{
			list.clear();
			if (search.length() == 0)
				list = (ArrayList<String>) rawList.clone();
			else
			{
				search = search.toLowerCase(Locale.FRANCE);
				for (int i = 0; i < rawList.size(); i++)
				{
					String groupName = rawList.get(i).toLowerCase(Locale.FRANCE);
					if (groupName.contains(search))
						list.add(rawList.get(i));
				}
			}
			refreshList(true);
		}
	}

}