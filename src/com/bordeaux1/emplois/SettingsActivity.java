package com.bordeaux1.emplois;

import com.bordeaux1.emplois.R;

import http.client.EdTTools;
import cache.client.CacheManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends Activity
{

	public static EdTTools	edtTools;
	public ProgressDialog	pDialog;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_settings);
		SettingsActivity.this.setTitle("Paramètres");
		edtTools = new EdTTools(getBaseContext());

		displayGroup();
		setFilters();
		setCache();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				finish();
				return true;
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void displayGroup()
	{
		TextView groupContent = (TextView) findViewById(R.id.groupSettingsContent);
		String groupSettings = CacheManager.getPreferences(SettingsActivity.this, "group", "");
		if (!groupSettings.equals(""))
			groupContent.setText(groupSettings);
		else
			groupContent.setText("inconnu");
	}

	private void setFilters()
	{
		EditText filters = (EditText) findViewById(R.id.filters);
		EditText advancedFilters = (EditText) findViewById(R.id.advancedFilters);
		filters.setText(CacheManager.getPreferences(SettingsActivity.this, "filters", ""));
		advancedFilters.setText(CacheManager.getPreferences(SettingsActivity.this, "advancedFilters", ""));

		Button saveFilters = (Button) findViewById(R.id.saveFilters);
		saveFilters.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				EditText filters = (EditText) findViewById(R.id.filters);
				EditText advancedFilters = (EditText) findViewById(R.id.advancedFilters);
				CacheManager.savePreferences(SettingsActivity.this, "filters", filters.getText().toString());
				CacheManager.savePreferences(SettingsActivity.this, "advancedFilters", advancedFilters.getText().toString());
				CacheManager.cleanCache(SettingsActivity.this);
				Toast.makeText(SettingsActivity.this, "Filtres enregistrés", Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void setCache()
	{
		EditText cache = (EditText) findViewById(R.id.cacheSettings);
		cache.setText(CacheManager.getPreferences(SettingsActivity.this, "cacheDuration", "1"));

		Button saveCache = (Button) findViewById(R.id.saveCache);
		saveCache.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				EditText cache = (EditText) findViewById(R.id.cacheSettings);
				CacheManager.savePreferences(SettingsActivity.this, "cacheDuration", cache.getText().toString());
				Toast.makeText(SettingsActivity.this, "Durée de cache enregistrée à " + cache.getText().toString() + "h", Toast.LENGTH_SHORT).show();
			}
		});
	}

}
