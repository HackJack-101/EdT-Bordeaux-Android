package cache.client;

import java.io.File;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class CacheManager
{

	public static final String edtList = "edt_list.json";

	public static boolean fileExists(Context context, String fname)
	{
		File file = context.getApplicationContext().getFileStreamPath(fname);
		return file.exists();
	}

	public static void cleanCache(Context context)
	{
		for (String s : context.getApplicationContext().fileList())
			context.getApplicationContext().deleteFile(s);
	}

	public static String getPreferences(Context context, String key, String defaultValue)
	{
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPreferences.getString(key, defaultValue);
	}

	public static void savePreferences(Context context, String key, String value)
	{
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = sharedPreferences.edit();
//		editor.clear();
		editor.putString(key, value);
		editor.commit();
	}

	public static Set<String> getSetPreferences(Context context, String key, Set<String> defaultValues)
	{
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		return sharedPreferences.getStringSet(key, defaultValues);
	}

	public static void saveSetPreferences(Context context, String key, Set<String> values)
	{
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = sharedPreferences.edit();
//		editor.clear();
		editor.putStringSet(key, values);
		editor.commit();
	}
}
