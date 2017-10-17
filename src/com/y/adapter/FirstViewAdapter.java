package com.y.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.y.mygallery.R;

public class FirstViewAdapter extends BaseAdapter
{
	  private LayoutInflater mInflater;
	  private Bitmap internal;
	  private Bitmap sdcard;
	  private Bitmap secret;
	  
	  private List<String> items;
	  private List<String> paths;

	  public FirstViewAdapter(Context context,List<String> it,List<String> pa)
	  {
	    mInflater = LayoutInflater.from(context);
	    items = it;
	    paths = pa;
	    internal = BitmapFactory.decodeResource(context.getResources(),
	            R.drawable.bg);
	    sdcard = BitmapFactory.decodeResource(context.getResources(),
	            R.drawable.bg);
	    secret = BitmapFactory.decodeResource(context.getResources(),
	            R.drawable.bg2);
	  }
	  
	 
	  public int getCount()
	  {
	    return items.size();
	  }

	  
	  public Object getItem(int position)
	  {
	    return items.get(position);
	  }
	  
	 
	  public long getItemId(int position)
	  {
	    return position;
	  }
	  
	 
	  public View getView(int position,View convertView,ViewGroup parent)
	  {
	    ViewHolder holder;
	    
	    if(convertView == null)
	    {
	    	Log.i("---", "here");
	      convertView = mInflater.inflate(R.layout.first_model, null);
	      holder = new ViewHolder();
	      holder.text = (TextView) convertView.findViewById(R.id.itemtv);
	      holder.icon = (ImageView) convertView.findViewById(R.id.icon);
	      
	      convertView.setTag(holder);
	    }
	    else
	    {
	        holder = (ViewHolder) convertView.getTag();
	    }

	    String type=items.get(position).toString();
	    if(type.equals("internal"))
	    {
	        holder.text.setText(R.string.internal);
	        holder.icon.setImageBitmap(internal);
	    }
	    else if(type.equals("sdcard"))
	    {
	    	 holder.text.setText("SDø®¥Ê¥¢");
	         holder.icon.setImageBitmap(sdcard);
	    }
	    else if(type.equals("secretarea"))
	    {
	    	holder.text.setText("ÀΩ√‹ø’º‰");
	         holder.icon.setImageBitmap(secret);
	    }
	    return convertView;
	  }
	  
	  /* class ViewHolder */
	  private class ViewHolder
	  {
	    TextView text;
	    ImageView icon;
	  }
	}
