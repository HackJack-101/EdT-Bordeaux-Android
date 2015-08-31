package com.bordeaux1.emplois;

import java.util.Set;
import java.util.TreeSet;

import cache.client.CacheManager;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class ScheduleActivity extends FragmentActivity implements ActionBar.TabListener
{

	AppSectionsPagerAdapter	mAppSectionsPagerAdapter;

	static String			code;
	ViewPager				mViewPager;

	@SuppressLint("NewApi")
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedule_layout);

		Bundle bundle = getIntent().getExtras();
		code = bundle.getString("code");
		getActionBar().setDisplayHomeAsUpEnabled(true);
		ScheduleActivity.this.setTitle(getBeautifulTitle(code));

		mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager(), code);

		final ActionBar actionBar = getActionBar();

		actionBar.setHomeButtonEnabled(true);

		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mAppSectionsPagerAdapter);
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
		{
			@Override
			public void onPageSelected(int position)
			{
				actionBar.setSelectedNavigationItem(position);
			}
		});

		for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++)
		{
			actionBar.addTab(actionBar.newTab().setText(mAppSectionsPagerAdapter.getPageTitle(i)).setTabListener(this));
		}
	}

	private String getBeautifulTitle(String code)
	{
		String[] split = code.split(" ");
		if (split[0].equals("Master"))
			return code;
		return split[0] + " Groupe " + split[1];
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.schedule, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				finish();
				return true;
			case R.id.action_googleCalendar:
				String[] split = code.split(" ");
				String name = split[0];
				String group = split[1];
				String url = "https://www.google.com/calendar/render?cid=http://www.hackjack.info/et/" + name + "_" + group + "/gcal";
				Uri uriUrl = Uri.parse(url);
				Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
				startActivity(launchBrowser);
				return true;
			case R.id.action_leave:
				Intent exitIntent = new Intent(getApplicationContext(), MainActivity.class);
				exitIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				exitIntent.putExtra("EXIT", true);
				startActivity(exitIntent);
				return true;
			case R.id.menu_favorite:
				final Dialog popup = new Dialog(this);
				popup.setContentView(R.layout.popup_main);
				popup.setTitle(code);

				Button saveGroup = (Button) popup.findViewById(R.id.save_group);

				Button actionFavorite = (Button) popup.findViewById(R.id.action_favorite);

				Set<String> favorites = CacheManager.getSetPreferences(this, "favorites", new TreeSet<String>());
				if (favorites.contains(code))
					actionFavorite.setText("Supprimer des favoris");
				String mainGroup = CacheManager.getPreferences(this, "group", "");
				if(mainGroup.equals(code))
					saveGroup.setText("Oublier le groupe principal");

				popup.show();

				saveGroup.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						String action;
						String mainGroup = CacheManager.getPreferences(ScheduleActivity.this, "group", "");
						if(!mainGroup.equals(code))
						{
							CacheManager.savePreferences(ScheduleActivity.this, "group", code);
							action = " enregistré";
						}
						else
						{
							CacheManager.savePreferences(ScheduleActivity.this, "group", "");
							action = " oublié";
						}
						Toast.makeText(ScheduleActivity.this, code + action + " en tant que groupe principal de TD", Toast.LENGTH_SHORT).show();
						popup.dismiss();
					}

				});

				actionFavorite.setOnClickListener(new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						String action;
						Set<String> favorites = CacheManager.getSetPreferences(ScheduleActivity.this, "favorites", new TreeSet<String>());
						if (!favorites.contains(code))
						{
							favorites.add(code);
							action = " ajouté aux";
						}
						else
						{
							favorites.remove(code);
							action = " supprimé des";
						}
						CacheManager.saveSetPreferences(ScheduleActivity.this, "favorites", favorites);
						popup.dismiss();
						Toast.makeText(getApplicationContext(), "Groupe " + code + action + " favoris", Toast.LENGTH_LONG).show();
					}

				});
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
	{
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
	{
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction)
	{
	}

	public static class AppSectionsPagerAdapter extends FragmentPagerAdapter
	{

		public AppSectionsPagerAdapter(FragmentManager fm, String code)
		{
			super(fm);
		}

		@Override
		public Fragment getItem(int i)
		{
			switch (i)
			{
				case 0:
					return new DaySectionFragment();

				default:
					return new WeekSectionFragment();
			}
		}

		@Override
		public int getCount()
		{
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position)
		{
			switch (position)
			{
				case 0:
					return "Jour";
				default:
					return "Semaine";
			}
		}
	}

	public static class DaySectionFragment extends Fragment
	{

		LayoutInflater	inflater;
		Activity		mActivity;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			this.inflater = inflater;
			View rootView = this.inflater.inflate(R.layout.activity_day, container, false);
			new GetDayList(mActivity, rootView, code, null).execute();
			return rootView;
		}

		@Override
		public void onAttach(Activity activity)
		{
			super.onAttach(activity);
			mActivity = activity;
		}

		@Override
		public void onDetach()
		{
			super.onDetach();
		}

	}

	public static class WeekSectionFragment extends Fragment
	{

		LayoutInflater	inflater;
		Activity		mActivity;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			this.inflater = inflater;
			View rootView = this.inflater.inflate(R.layout.activity_week, container, false);
			new GetWeekList(mActivity, rootView, code, 0).execute();
			return rootView;
		}

		@Override
		public void onAttach(Activity activity)
		{
			super.onAttach(activity);
			mActivity = activity;
		}

		@Override
		public void onDetach()
		{
			super.onDetach();
		}
	}
}
