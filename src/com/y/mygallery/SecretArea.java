package com.y.mygallery;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

import com.y.adapter.SecretAdapter;
import com.y.aes.MySecret;
import com.y.mygallery.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
public class SecretArea extends Activity{

	private GridView gridView;
	private ArrayList<String> paths,name;
	private String path;
	private MySecret mysecret;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		setTitle(R.string.secret_area);
		setContentView(R.layout.secret_view);
		mysecret=new MySecret();
		gridView=(GridView)findViewById(R.id.gridview);
		Intent intent=getIntent();
		path=intent.getStringExtra("path");
		//File file=new File(path);
		Log.d("SecretArea", path);
		getFileDir(path);
		
		/*
		for(int i=0;i<paths.size();i++){
			String p=paths.get(i);
			Log.i("解密", p);
			mysecret.removeByte(p);
			//mysecret.aesDecrypt(p);
		}*/
		//设置数据适配器
		gridView.setAdapter(new SecretAdapter(this,paths));
		//设置点击监听
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO 自动生成的方法存根
				Log.i("skip", arg2+"跳转了");
				Intent intent=new Intent(SecretArea.this, SecretImageView.class);
				intent.putExtra("data", (String[])paths.toArray(new String[paths.size()]));
				intent.putExtra("path", path);
				intent.putExtra("id", arg2);
				startActivity(intent);
			}
		});
	}
 	
	@Override
	protected void onResume() {
		// TODO 自动生成的方法存根
		super.onResume();
		getFileDir(path);
	}
	
 	@Override
 	protected void onStop() {
 		// TODO 自动生成的方法存根
 		super.onStop();
 		/*
		// 先判断是否已经回收
		if(SecretAdapter.instance.bitmap!= null && !SecretAdapter.instance.bitmap.isRecycled()){ 
		        // 回收并且置为null
		        SecretAdapter.instance.bitmap.recycle(); 
		        SecretAdapter.instance.bitmap = null; 
		} 
		System.gc();*/
 	}
 	
 	@Override
 	protected void onDestroy() {
 		// TODO 自动生成的方法存根
 		super.onDestroy();	
 		/*
		// 先判断是否已经回收
		if(SecretAdapter.instance.bitmap != null && !SecretAdapter.instance.bitmap.isRecycled()){ 
		        // 回收并且置为null
		        SecretAdapter.instance.bitmap.recycle(); 
		        SecretAdapter.instance.bitmap = null; 
		} 
		System.gc();*/
 	}
 	
	/*
	private ArrayList<String> getFileNameList(File path){
        ArrayList<String> FileNameList=new ArrayList<String>();
        //如果是文件夹的话
        if(path.isDirectory()){
          //什么也不做
        }
        //如果是文件的话直接加入
        else{
            Log.i("SecretArea", path.getAbsolutePath());
            //进行文件的处理
            String filePath = path.getAbsolutePath();
            //文件名
            String fileName = filePath.substring(filePath.lastIndexOf("/")+1);
            //添加
           FileNameList.add(fileName);
        }
        return FileNameList;
    }*/
	
 	//获取私密空间目录下的所有图片文件的绝对路径
	public void getFileDir(String filePath) {  
        try{   
            name = new ArrayList<String>();  
            paths = new ArrayList<String>();  
            File f = new File(filePath);  
            File[] files = f.listFiles();// 列出所有文件  
            // 将所有文件存入list中  
            if(files != null){  
                int count = files.length;// 文件个数  
                for (int i = 0; i < count; i++) {  
                    File file = files[i];
                    if(!file.isDirectory()){
    							 name.add(file.getName());  
    							 paths.add(file.getAbsolutePath()); 
    							 Log.i("path", file.getAbsolutePath()); 
                    	}
                	}
                }
            }catch(Exception ex){  
            ex.printStackTrace();  
        }  
  
    }
	
}