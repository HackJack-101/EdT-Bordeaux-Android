package com.bordeaux1.emplois;

public class DrawerItem {
	 
    public String title;
    public int code;
    public boolean clickable;

    public DrawerItem(String title, int code, boolean clickable) {
          super();
          this.title = title;
          this.code = code;
          this.clickable = clickable;
    }
    
    public String getTitle()
    {
    	return title;
    }
    
    public int getCode()
    {
    	return code;
    }
    
    public boolean getClikable()
    {
    	return clickable;
    }

}