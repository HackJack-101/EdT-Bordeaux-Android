package com.bordeaux1.emplois;

import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.bordeaux1.emplois.R;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class WeekListAdapter extends BaseExpandableListAdapter
{
	private Integer counter = 0;
    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<JSONObject>> _listDataChild;
 
    public WeekListAdapter(Context context, List<String> listDataHeader,HashMap<String, List<JSONObject>> listChildData) 
    {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }
 
    @Override
    public Object getChild(int groupPosition, int childPosititon)
    {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosititon);
    }
 
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
 
    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        JSONObject childObject = (JSONObject) getChild(groupPosition, childPosition);
        if (convertView == null)
        {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.day_week, null);
        }
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView subject = (TextView) convertView.findViewById(R.id.subject);
        TextView staff = (TextView) convertView.findViewById(R.id.staff);
        TextView room = (TextView) convertView.findViewById(R.id.room);
        TextView group = (TextView) convertView.findViewById(R.id.group);
        TextView annotation = (TextView) convertView.findViewById(R.id.annotation);
        

		title.setText(getInformation(childObject,"schedule"));
		subject.setText(getInformation(childObject,"subject"));
		staff.setText(getInformation(childObject,"staff"));
		room.setText(getInformation(childObject,"room"));
		group.setText(getInformation(childObject,"group"));
		annotation.setText(getInformation(childObject,"annotation"));
		
		
		convertView.setBackgroundResource(R.drawable.back);
		String style = getStyle(childObject);
		if(style.equalsIgnoreCase("a8ffa8"))
			convertView.setBackgroundResource(R.drawable.a8ffa8);
		else if(style.equalsIgnoreCase("bea8d3"))
			convertView.setBackgroundResource(R.drawable.bea8d3);
		else if(style.equalsIgnoreCase("bed3d3"))
			convertView.setBackgroundResource(R.drawable.bed3d3);
		else if(style.equalsIgnoreCase("d3a8be"))
			convertView.setBackgroundResource(R.drawable.d3a8be);
		else if(style.equalsIgnoreCase("d3a8ff"))
			convertView.setBackgroundResource(R.drawable.d3a8ff);
		else if(style.equalsIgnoreCase("dedede"))
			convertView.setBackgroundResource(R.drawable.dedede);
		else if(style.equalsIgnoreCase("ffa8ff"))
			convertView.setBackgroundResource(R.drawable.ffa8ff);
		else if(style.equalsIgnoreCase("ffffa8"))
			convertView.setBackgroundResource(R.drawable.ffffa8);
			
		
		counter++;

		
		
        return convertView;
    }
    
    private String getInformation(JSONObject child, String name)
    {
    	try {
			return child.getString(name);
		}
    	catch (JSONException e) {}
    	return "";
    }
    

	
	public String getStyle(JSONObject day)
	{
		try {
			String style = day.getString("style");
			String[] split = style.split("#");
			return split[1].substring(0, split[1].length()-1);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

 
    @Override
    public int getChildrenCount(int groupPosition)
    {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
    }
 
    @Override
    public Object getGroup(int groupPosition)
    {
        return this._listDataHeader.get(groupPosition);
    }
 
    @Override
    public int getGroupCount()
    {
        return this._listDataHeader.size();
    }
 
    @Override
    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }
 
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null)
        {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.week, null);
        }
        TextView lblListHeader = (TextView) convertView.findViewById(R.id.dayWeekHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);
 
        return convertView;
    }
 
    @Override
    public boolean hasStableIds()
    {
        return false;
    }
 
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return true;
    }
}
