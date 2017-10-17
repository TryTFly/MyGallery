package com.y.mygallery;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.y.bitmap.CompressBitmap;
import com.y.des.BitmapManage;
import com.y.des.DES_Algorithm;
import com.y.des.FileIO;
import com.y.mygallery.R;
import com.y.treasure.Common;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;

public class SecretImageView extends Activity implements OnGestureListener{

	 ImageView image=null;
	 private GestureDetector gestureScanner;
	 private String[] paths=null;
	 private List<String> allpaths;
	 private int currentPosition;
	 final String KEY = "QCLZ-KEY";
	 private Bitmap currentBitmap=null;
	 public static final int SECRET = Menu.FIRST;   
	 //public static final int SHARE = Menu.FIRST+1; 
	 public static final int THUMBNAIL = Menu.FIRST+2; 
	 public static final int DETAILS = Menu.FIRST+3; 
	 public static final int ROTATE = Menu.FIRST+4;
	 public static final int SETUPFOR = Menu.FIRST+5;
	 public static final int DELETE = Menu.FIRST+6; 
	 public static final int EFFECTS = Menu.FIRST+7; 
	 private String currentPath;
	 private Context context;
	 private BitmapManage bmanage;
	 
	 
	 @Override  
	 public void onCreate(Bundle savedInstanceState) {  
	    super.onCreate(savedInstanceState);  
	    
	    requestWindowFeature(Window.FEATURE_NO_TITLE);    

	    getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN ,      
	                      WindowManager.LayoutParams. FLAG_FULLSCREEN);    
	       
	    setContentView(R.layout.secret_image_view);  
	    
	    image = (ImageView)this.findViewById(R.id.imageview);
	    
	    Intent intent = getIntent();  
        paths = intent.getStringArrayExtra("data"); 
        currentPosition=intent.getIntExtra("id", 0);
        allpaths=new ArrayList<String>();
        for(String str:paths){
        	allpaths.add(str);
        }
        //Log.i("ImageGridView_onCreate", "path="+path);
        currentPath=allpaths.get(currentPosition);               
		//加载加密后的图片字节流
		byte[] by=FileIO.loadFile2Bytes(currentPath);
		//解密
		try {
			by=DES_Algorithm.decrypt(by, KEY);
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
		bmanage=new BitmapManage(context);
		//压缩图片
		try{
		currentBitmap=CompressBitmap.bitmapreadBitMap(by);
		//currentBitmap=bmanage.Bytes2Bimap(by);
		//currentBitmap=CompressBitmap.bitmapScale(currentBitmap, 0.05f);
		
		image.setImageBitmap(currentBitmap);
		image.setScaleType(ScaleType.CENTER);
		}catch (OutOfMemoryError e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
        image.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO 自动生成的方法存根
				openOptionsMenu();
			}
		});
        
		gestureScanner = new GestureDetector(this);
		gestureScanner.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
			
		     public boolean onDoubleTap(MotionEvent e) {
		        int bmpWidth=currentBitmap.getWidth();
		        int bmpHeight=currentBitmap.getHeight();
			    float scale=1.25f;
					
				Matrix matrix=new Matrix();
				matrix.postScale(scale, scale);
				Bitmap resizeBmp=Bitmap.createBitmap(currentBitmap, 0, 0, bmpWidth,bmpHeight, matrix, true );
				image.setImageBitmap(resizeBmp);
				
		      Log.i("setOnDoubleTapListener", "--code comes here---");
		      return false;
		     }

			
			public boolean onDoubleTapEvent(MotionEvent e) {
				// TODO Auto-generated method stub
				
				 Log.i("onDoubleTapEvent", "--code comes here---");
				
				return false;
			}

			
			public boolean onSingleTapConfirmed(MotionEvent e) {
				 Log.i("onSingleTapConfirmed", "--code comes here---");
				// TODO Auto-generated method stub
				return false;
			}
		});
	 }
	 
	 @Override
	protected void onStop() {
		// TODO 自动生成的方法存根
		super.onStop();
		/**/// 先判断是否已经回收
		if(currentBitmap != null && !currentBitmap.isRecycled()){ 
		        // 回收并且置为null
		        currentBitmap.recycle(); 
		        currentBitmap = null; 
		} 
		System.gc();
	}
	 
	 @Override
	protected void onDestroy() {
		// TODO 自动生成的方法存根
		super.onDestroy();
		/**/// 先判断是否已经回收
		if(currentBitmap != null && !currentBitmap.isRecycled()){ 
		        // 回收并且置为null
		        currentBitmap.recycle(); 
		        currentBitmap = null; 
		} 
		System.gc();
	}
	 
	//菜单事项按钮的添�?
		 @Override
		 public boolean onCreateOptionsMenu(Menu menu) {
			   super.onCreateOptionsMenu(menu);
			   menu.add(0,SECRET,1,R.string.secret);
			   menu.add(0, THUMBNAIL, 2, R.string.thumbnail);
			   menu.add(0, DETAILS,3, R.string.details);
			   menu.add(1, ROTATE, 4, R.string.retate);
			   menu.add(0, DELETE, 5, R.string.delete);
			
			   
			   
			   return true;
		 }
		 

		//旋转图片的菜单追加
		 @Override  
	     public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {  
			 
	         menu.setHeaderTitle(R.string.retate);
			 menu.add(1, 0, 0, R.string.retate_left); 
			 menu.add(1, 1, 1, R.string.retate_right);
			 menu.add(1, 2, 2, R.string.retate_back);
			 
				   
			 super.onCreateContextMenu(menu, v, menuInfo);  
		 }

		 @Override  
		 public boolean onOptionsItemSelected(MenuItem item) {  
			 
		      switch(item.getItemId()){   
		           case ROTATE://旋转图片
		        	   showRotate();
		        	   break; 
		     
		           case THUMBNAIL: //设置图片�?..
		        	   showSetupFor();
		        	   break;   
		           case DELETE: //图片删除
		        	   showDelete();
		        	   break;
		           case DETAILS: //图片信息详情
		        	   showDetails();
		        	   break;
		           case SECRET://添加加密
		        		showSecret();
		        		break;
		  
		        	   
		      }  
		          
		      return super.onOptionsItemSelected(item);  
		 }
		    

	public boolean onDown(MotionEvent arg0) {
		
		Log.i("onDown", "--code comes here---");
		// TODO Auto-generated method stub
		return false;
	}

	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		
		Log.i("onFling", "--code comes here---");
		// TODO Auto-generated method stub
		return false;
	}

	public void onLongPress(MotionEvent arg0) {
		
		Log.i("onLongPress", "--code comes here---");
		// TODO Auto-generated method stub
		
	}
	
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		
		Log.i("onScroll", "--code comes here---");
		// TODO Auto-generated method stub
		return false;
	}

	public void onShowPress(MotionEvent arg0) {
		
		Log.i("onShowPress", "--code comes here---");
		// TODO Auto-generated method stub
		
	}
	
	public boolean onSingleTapUp(MotionEvent arg0) {
		
		Log.i("onSingleTapUp", "--code comes here---");
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean moveFile(String srcFileName, String destDirName) {  
	      
	    File srcFile = new File(srcFileName);  
	    if(!srcFile.exists() || !srcFile.isFile())   
	        return false;  
	      
	    File destDir = new File(destDirName);  
	    if (!destDir.exists())  
	        destDir.mkdirs();  
	      
	    return srcFile.renameTo(new File(destDirName + File.separator + srcFile.getName()));  
	}
	
OnClickListener onShowSecret=new OnClickListener() {
		
		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			// TODO 自动生成的方法存根
						
						String path=Environment.getExternalStorageDirectory().getAbsolutePath();
						//saveBitmap(currentBitmap,path);
						saveImageToGallery(SecretImageView.this, currentBitmap);
						
						final File file=new File(currentPath);
					  	   if(file.exists())
					  	   {
					  	                    	try
					  	                    	{                           
					  	                    	    file.delete();
					  	                    	    scanFileAsync(SecretImageView.this, currentPath);
					  	                    	}
					  	                    	catch(Exception err)
					  	                    	{
					  	                    		err.printStackTrace();
					  	                    		Toast.makeText(SecretImageView.this, R.string.unkonw_err,Toast.LENGTH_SHORT).show();
					  	                    		return;
					  	                    	}
					  	                    }
						
						//moveFile(currentPath, Environment.getExternalStorageDirectory().getAbsolutePath());
					System.exit(0);
				}
	};
	public void sBitmap(Bitmap bitmap, String path) {    
		  
		    FileOutputStream mFileOutputStream = null;    
		  
		         
		  
		    try {    
		  
		     File mFile = new File(path);    
		  
		     //创建文件    
		  
		  mFile.createNewFile();    
		  
		  //创建文件输出流    
		  
		  mFileOutputStream = new FileOutputStream(mFile);    
		  
		  //保存Bitmap到PNG文件    
		  
		  //图片压缩质量为75，对于PNG来说这个参数会被忽略    
		  
		  bitmap.compress(CompressFormat.PNG, 75, mFileOutputStream);    
		  
		  //Flushes this stream.     
		  
		  //Implementations of this method should ensure that any buffered data is written out.     
		  
		  //This implementation does nothing.    
		  
		  mFileOutputStream.flush();    
		  
		 } catch (IOException e) {    
		  
		  // TODO Auto-generated catch block    
		  
		  e.printStackTrace();    
		  
		 } finally {    
		  
		  try {    
		  
		   mFileOutputStream.close();    
		  
		  } catch (IOException e) {    
		  
		   // TODO: handle exception    
		  
		   e.printStackTrace();    
		  
		  }    
		 }
		 } 
		    
	public void saveImageToGallery(Context context, Bitmap bmp) {
	    // 首先保存图片
	    File appDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath());
	    if (!appDir.exists()) {
	        appDir.mkdir();
	    }
	    
	    String fileName = System.currentTimeMillis() + ".jpg";
	    File file = new File(appDir, fileName);
	    try {
	        FileOutputStream fos = new FileOutputStream(file);
	        bmp.compress(CompressFormat.JPEG, 100, fos);
	        fos.flush();
	        fos.close();
	        Log.i("", "图片保存了");
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
		}
	    
	    // 其次把文件插入到系统图库
	    try {
	        MediaStore.Images.Media.insertImage(context.getContentResolver(),
					file.getAbsolutePath(), fileName, null);
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    }
	    // 最后通知图库更新
	    //context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
	    context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(file.getPath()))));
	}
	
	public void saveBitmap(Bitmap bitmap,String path) {
		  Log.i("SecretImageView", "保存图片");
		  File f = new File(path);
		  if (f.exists()) {
		   f.delete();
		  }
		  try {
		   FileOutputStream out = new FileOutputStream(f);
		   bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
		   out.flush();
		   out.close();
		   Log.i("", "已经保存");
		  } catch (FileNotFoundException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
		  } catch (IOException e) {
		   // TODO Auto-generated catch block
		   e.printStackTrace();
		  }
		//保存图片后发送广播通知更新数据库  
	        Uri uri = Uri.fromFile(f);  
	        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
		 }
	
	//图片旋转的角度事件监�?
		OnClickListener onShowRotateSelect = new OnClickListener() {  
		    public void onClick(DialogInterface dialog, int which) {  
		         // TODO Auto-generated method stub  
		      switch(which)
		      {
		        case 0:
		        	rotate(-90);
		    		break;
		        case 1:
		        	rotate(90);
		    		break;
		        case 2:
		        	rotate(180);
		    		break;
		      }
		   }
	    };
	    
	    //图片的详细信息事件监�? 
		OnClickListener onShowDetailsSelect = new OnClickListener() {  
			    public void onClick(DialogInterface dialog, int which) {  
			         // TODO Auto-generated method stub  
			    	switch(which)
			    	{
			           default:
			    	      break;
			    	}
			   }
		};
		
		//图片的设置为...的事件监�?
		OnClickListener onShowSetUpSelect = new OnClickListener() {  
		    public void onClick(DialogInterface dialog, int which) {  
		         // TODO Auto-generated method stub  
		    	switch(which)
			    {
			        case 0:
			        	try {
			    			 setWallpaper(currentBitmap);
			    			 Toast.makeText(SecretImageView.this, R.string.setup_wallpaper_success,Toast.LENGTH_SHORT).show();
			    		} catch (IOException e) {
			    			// TODO Auto-generated catch block
			    			e.printStackTrace();
			    			Toast.makeText(SecretImageView.this, R.string.unkonw_err,Toast.LENGTH_SHORT).show();  
			    		}
			    		break;
			    }
		   }
	    };
	
	//旋转
	public void rotate(int r){
   	 
    	if(currentBitmap==null)
		{
			 //Log.i(TAG, "oh, nonono~~~, currentBitmap=null!");
			 return;
		}
    	
    	//Log.i(TAG, "the currentPosition="+currentPosition);
    	
        int bmWidth = currentBitmap.getWidth();
        int bmHeight = currentBitmap.getHeight();        
        Matrix matrix = new Matrix();
        
        try
        {
           matrix.postRotate(r); 
           Bitmap resizeBmp = Bitmap.createBitmap(currentBitmap,0,0,bmWidth,bmHeight,matrix,true);
        
           image.setImageBitmap(resizeBmp);

           currentBitmap=resizeBmp;
        }
        catch(Exception err)
        {
        	err.printStackTrace();
        	Toast.makeText(SecretImageView.this, R.string.no_memory,Toast.LENGTH_SHORT).show();
        	return;
        }
    }
	
	private void showSecret() {
		// TODO 自动生成的方法存根
			String []item={(String)getText(R.string.restore)};
			AlertDialog dialog=new AlertDialog.Builder(SecretImageView.this)
					.setIcon(R.drawable.bg)
					.setTitle(R.string.secret)
					.setItems(item, onShowSecret).create();
			dialog.show();
		
	}
	//旋转图片的方法调用以及提示信息的实现
	private void showRotate() {
		// TODO Auto-generated method stub
		 String[] items={(String) getText(R.string.retate_left),
				 (String) getText(R.string.retate_right),
				 (String) getText(R.string.retate_back)}; 

          AlertDialog dialog = new AlertDialog.Builder(SecretImageView.this)  
         .setIcon(R.drawable.bg)  
         .setTitle(R.string.retate)
         .setItems(items, onShowRotateSelect).create();  
         dialog.show();  
	}
    //设置�?...的方法调用以及提示信息的实现
	private void showSetupFor() {
		 String[] items={(String) getText(R.string.setup_wallpaper),
				 (String) getText(R.string.setup_favarete)}; 
		  AlertDialog dialog = new AlertDialog.Builder(SecretImageView.this)  
		 .setIcon(R.drawable.bg)
	     .setTitle(R.string.setupfor)
         .setItems(items, onShowSetUpSelect).create();  
		 dialog.show(); 
	}
    //删除图片的方法调用以及提示信息的实现
	private void showDelete() {
		// TODO Auto-generated method stub
	   currentPath=allpaths.get(currentPosition);
	   final File file=new File(currentPath);
  	   if(file.exists())
  	   {
  			new AlertDialog.Builder(SecretImageView.this)
  	        .setIcon(R.drawable.bg)
  	        .setTitle(R.string.delete_redue)
  	        .setMessage((String) getText(R.string.delete_redue_question_a)+" "+file.getName()
  	        		+" "+(String) getText(R.string.delete_redue_question_b))
  	        .setPositiveButton(R.string.dialogLB, 
  	                new DialogInterface.OnClickListener() {
  	                    public void onClick(DialogInterface dialog,
  	                            int whichButton) {
  	                       
  	                    	ArrayList<String> list=new ArrayList<String>();
  	                    	for(String str:paths)
  	                    	{
  	                    		if(str.equals(currentPath))
  	                    			continue;
  	                    		list.add(str);
  	                    	}
  	                    	
  	                    	allpaths.remove(currentPath);
  	                    	
  	                    	try
  	                    	{                           
  	                    	    file.delete();
  	                    	    scanFileAsync(SecretImageView.this, currentPath);
  	                    	    //getcontentresolver().delete(Media.EXTERNAL_CONTENT_URI, Media.DATA + "=?",path);
  	                    	    
  	                    	    Toast.makeText(SecretImageView.this,
  	                    	    		(String) getText(R.string.picture_file)+" "+file.getName()
  	                    	    		+" "+(String) getText(R.string.delete_success),Toast.LENGTH_SHORT).show();
  	                    	}
  	                    	catch(Exception err)
  	                    	{
  	                    		err.printStackTrace();
  	                    		Toast.makeText(SecretImageView.this, R.string.unkonw_err,Toast.LENGTH_SHORT).show();
  	                    		return;
  	                    	}
  	                    	System.exit(0);
  	                    }
  	                })
  	        .setNegativeButton(R.string.dialogRB,
  	                new DialogInterface.OnClickListener() {
  	                    public void onClick(DialogInterface dialog,
  	                            int whichButton) {

  	                        /* User clicked OK so do some stuff */
  	                    }
  	                })
  	        .show();
  	   }
	}
	
	//发送广播刷新
	public void scanFileAsync(Context ctx, String filePath) { 
		Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE); 
		scanIntent.setData(Uri.fromFile(new File(filePath))); 
		ctx.sendBroadcast(scanIntent); 
		}
	
   //图片详细信息的方法调用以及提示信息的实现
	private void showDetails() {
		// TODO Auto-generated method stub
		currentPath=allpaths.get(currentPosition);
		final File file=new File(currentPath);
		 String[] items={(String) getText(R.string.picture_name)+": "+file.getName(),
				 (String) getText(R.string.picture_path)+": "+file.getParentFile().getAbsolutePath(),
				 (String) getText(R.string.picture_size)+": "+Common.getFileSize(Common.getFileSize(file)), 
				 (String) getText(R.string.picture_modified_date)+": "+Common.getFileDateTime(file)}; 
		 
		 //创建�?个能够显示图片详细内容的Dialong窗口，并追加onShowDetailsSelect方法
		 AlertDialog dialog = new AlertDialog.Builder(SecretImageView.this)  
		           .setIcon(R.drawable.bg)  
		           .setTitle(R.string.details)
		           .setItems(items, onShowDetailsSelect).create();  
		 dialog.show();  
	}
	
}