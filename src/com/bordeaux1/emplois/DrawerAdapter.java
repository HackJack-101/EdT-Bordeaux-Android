package com.bordeaux1.emplois;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DrawerAdapter extends ArrayAdapter<DrawerItem>{

    Context context; 
    int layoutResourceId;
    List<DrawerItem> drawerItemList;
    
    public DrawerAdapter(Context context, int layoutResourceId, List<DrawerItem> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.drawerItemList = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	View view = convertView;
        DrawerHolder holder;
        DrawerItem dItem = (DrawerItem) this.drawerItemList.get(position);
        
        if (convertView == null)
		{
            LayoutInflater inflater = (LayoutInflater) ((MainActivity)context).getLayoutInflater();
        	view = inflater.inflate(R.layout.element_menu, parent, false);
        	view.setLongClickable(false);
        	
            holder = new DrawerHolder();
            holder.title = (TextView) view.findViewById(R.id.menu_title);
            holder.icon = (ImageView) view.findViewById(R.id.drawerIcon);
            view.setTag(holder);
        }
        else
        {
            holder = (DrawerHolder)view.getTag();
        }
        holder = (DrawerHolder)view.getTag();
                

        holder.title.setText(dItem.getTitle());
        switch(dItem.getCode())
        {
        	case 0:
        		holder.title.setTextSize(18);
        		break;
        	case 1:
        		holder.title.setTextColor(Color.parseColor("#3D3D3D"));
        		holder.icon.setImageResource(R.drawable.ic_action_settings);
        		break;
        	case 2:
        		holder.title.setTextColor(Color.parseColor("#008BC9"));
        		holder.icon.setImageResource(R.drawable.ic_action_not_important);
        		break;
        	case 3:
        		holder.icon.setImageResource(R.drawable.ic_action_cloud);
        		holder.title.setTextColor(Color.parseColor("#005EC9"));
        		break;
        	case 4:
        		holder.icon.setImageResource(R.drawable.ic_action_group);
        		holder.title.setTextColor(Color.parseColor("#005EC9"));
        		break;
        	case 5:
        		holder.icon.setImageResource(R.drawable.ic_action_collection);
        		holder.title.setTextColor(Color.parseColor("#005EC9"));
        		break;
        	case 6:
        		holder.icon.setImageResource(R.drawable.ic_action_email);
        		holder.title.setTextColor(Color.parseColor("#005EC9"));
        		break;
        	case 7:
        		holder.icon.setImageResource(R.drawable.ic_action_chat);
        		holder.title.setTextColor(Color.parseColor("#005EC9"));
        		break;
        	case 8:
        		holder.title.setTextColor(Color.parseColor("#3091FF"));
        		holder.icon.setImageResource(R.drawable.ic_action_dock);
        		break;
        	case 9:
        		holder.title.setTextColor(Color.parseColor("#005EC9"));
        		holder.icon.setImageResource(R.drawable.ic_action_map);
        		break;
        	case 10:
        		holder.title.setTextColor(Color.parseColor("#005EC9"));
        		holder.icon.setImageResource(R.drawable.ic_action_go_to_today);
        		break;
        	case 11:
        		holder.title.setTextColor(Color.parseColor("#008BC9"));
        		holder.icon.setImageResource(R.drawable.ic_action_important);
        		break;
        	default:
        		break;
        }
        if(dItem.getCode() > 1)
        {
        	holder.icon.setPadding(holder.icon.getPaddingLeft() + 20, holder.icon.getPaddingTop(), holder.icon.getPaddingRight(), holder.icon.getPaddingBottom());
        }
        
        return view;
    }
    
    
    public boolean isEnable(int position)
    {
    	DrawerItem dItem = (DrawerItem) this.drawerItemList.get(position);
		return dItem.getClikable();
    }

    static class DrawerHolder
    {
        TextView title;
        ImageView icon;
    }
}