package com.bordeaux1.emplois;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.bordeaux1.emplois.R;

import android.net.Uri;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class WebViewActivity extends Activity
{

	String	current_url;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		Bundle bundle = getIntent().getExtras();
		String url = bundle.getString("url");
		String title = bundle.getString("title");
		current_url = url;

		WebViewActivity.this.setTitle(title);

		final Context myApp = this;
		setContentView(R.layout.activity_webview);

		CookieSyncManager.createInstance(myApp);

		WebSettings settings = getWebView().getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setSupportZoom(true);
		settings.setBuiltInZoomControls(true);
		settings.setDisplayZoomControls(false);
		// settings.setLoadWithOverviewMode(true);
		settings.setUseWideViewPort(true);

		final WebView webview = (WebView) getWebView();
		webview.setWebViewClient(new MyWebViewClient());
		webview.setWebChromeClient(new MyWebChromeClient());

		webview.loadUrl(url);
	}

	public class MyWebChromeClient extends WebChromeClient
	{
		@Override
		public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result)
		{
			Log.d("alert", message);
			Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
			result.confirm();
			return true;
		}
	}

	public class MyWebViewClient extends WebViewClient
	{
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon)
		{
			if (state == 0)
			{
				pDialog = new ProgressDialog(WebViewActivity.this);
				if (url.startsWith("http://hackjack.info/calendar/map"))
					pDialog.setMessage("Chargement de la carte");
				else if (url.startsWith("http://hackjack.info/calendar/link"))
					pDialog.setMessage("Chargement de la page");
				else if (url.startsWith("http://hackjack.info/calendar/"))
					pDialog.setMessage("Chargement de l'agenda");
				else if (url.startsWith("https://webmel.u-bordeaux.fr"))
					pDialog.setMessage("Chargement des mails");
				else
					pDialog.setMessage("Chargement de la page");
				pDialog.setIndeterminate(false);
				pDialog.setCancelable(true);
				pDialog.show();
				state = 1;
			}
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onPageFinished(WebView view, String url)
		{
			String javascript = new String();
			if (url.startsWith("https://apps1.u-bordeaux.fr/groupesetuBx/"))
				javascript = "javascript:" + getScriptContent("td.js");
//			else if (url.startsWith("https://apogee.u-bordeaux.fr"))
//				javascript = "javascript:" + getScriptContent("apogee.js");
//			else
				javascript = "javascript:function(){};";
			view.loadUrl(javascript);
			pDialog.dismiss();
			pDialog.cancel();
			current_url = view.getUrl();
			state = 0;
			CookieSyncManager.getInstance().sync();
		}

		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl)
		{
			view.loadUrl("file:///android_asset/error.html");

		}

		private ProgressDialog	pDialog;
		private int				state	= 0;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.webview, menu);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case android.R.id.home:
				finish();
				return true;
			case R.id.action_website:
				this.openBrowser(current_url);
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (event.getAction() == KeyEvent.ACTION_DOWN)
		{
			switch (keyCode)
			{
				case KeyEvent.KEYCODE_BACK:
					if (getWebView().canGoBack() == true)
					{
						getWebView().goBack();
					} else
					{
						finish();
					}
					return true;
			}

		}
		return super.onKeyDown(keyCode, event);
	}

	private WebView getWebView()
	{

		WebView wvSite = (WebView) findViewById(R.id.webview);
		wvSite.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

		return wvSite;
	}

	public String getScriptContent(String filename)
	{
		try
		{
			AssetManager mngr = getBaseContext().getAssets();
			return getStringFromInputStream(mngr.open(filename));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static String getStringFromInputStream(InputStream is)
	{
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		String line;
		try
		{
			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null)
				sb.append(line);
		} catch (IOException e)
		{
			e.printStackTrace();
		} finally
		{
			if (br != null)
				try
				{
					br.close();
				} catch (IOException e)
				{
					e.printStackTrace();
				}
		}
		return sb.toString();
	}

	private void openBrowser(String url)
	{
		Uri uriUrl = Uri.parse(url);
		Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
		startActivity(launchBrowser);
	}
}
