package com.y.mygallery;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TimerTask;

import com.y.aes.MyAES;
import com.y.aes.MySecret;
import com.y.aes.MyYH;
import com.y.des.BitmapManage;
import com.y.des.DES_Algorithm;
import com.y.des.FileIO;
import com.y.mygallery.R;
import com.y.treasure.Common;
import com.y.treasure.RotateBitmap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.MediaStore.Images.Media;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;


public class ImageActivity extends Activity{
	
	private static final String TAG = "ImageActivity";
    private static final String PREF_SLIDESHOW_REPEAT =
        "pref_gallery_slideshow_repeat_key";
    ImageView imageView1, imageView2;
	Gallery gallery=null;
	Animation anim=null;
	String[] paths=null;
	List<String> mImagePathList;
	private ImageGetter mGetter;
	private final Random mRandom = new Random(System.currentTimeMillis());
	boolean mPaused = true;
	private boolean mSlideShowLoop = false;
    private int mSlideShowInterval=1000;
    int mCurrentPosition = 0;
    private Context context;
    private SharedPreferences mPrefs;
    
    // 选择负载
    private static final int[] sOrderAdjacents = new int[] {0, 1, -1};
    private static final int[] sOrderSlideshow = new int[] {0};
    GestureDetector mGestureDetector;
	final GetterHandler mHandler = new GetterHandler();	
    static final int MODE_NORMAL = 1;
    static final int MODE_SLIDESHOW = 2;
    static final int CHANGE_NORMAL = 1;
    static final int CHANGE_DONE = 2;
    private int mMode = MODE_NORMAL;
    private int Change=CHANGE_NORMAL;
    // 正常模式下的视图的图像显�?
    private ImageViewTouch mCurrentImageView;
    private Button mSaveConfirm;
    private Button mSaveCancel;
    // 位图缩略缓存的参数实�?
    private BitmapCache mCache;
	
	String path=null;
	//GalleryAdapter galleryAdapter=null;
    ImageView downButton,upButton;
    private int mSlideShowImageCurrent = 0;
    private final ImageViewTouchBase [] mSlideShowImageViews =
            new ImageViewTouchBase[2];
    
    private Bitmap currentBitmap;
    private Bitmap effectsBitmap;
    private String currentPath=""; // 当前选定的图片路�?
    private int currentPosition;
    //常量
    public static final int FLING_MIN_DISTANCE=100;
    public static final int FLING_MIN_VELOCITY=200;
    //Menu菜单的相应参�?
    public static final int SECRET = Menu.FIRST;   
    public static final int SHARE = Menu.FIRST+1; 
    public static final int THUMBNAIL = Menu.FIRST+2; 
    public static final int DETAILS = Menu.FIRST+3; 
    public static final int ROTATE = Menu.FIRST+4;
    public static final int SETUPFOR = Menu.FIRST+5;
    public static final int DELETE = Menu.FIRST+6; 
    public static final int EFFECTS = Menu.FIRST+7; 
    private int logo;
    //private String []item;
    private MySecret mySecret;
    private int length;
    private MyAES myAES;
    private MyYH myYH;
    private Bitmap bitmap;
    private BitmapManage bManage;
    final String KEY = "QCLZ-KEY";
    public static String fileName;
    
    private void setupOnTouchListeners(View rootView) {
        mGestureDetector = new GestureDetector(this, new MyGestureListener());

        OnTouchListener rootListener = new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                mGestureDetector.onTouchEvent(event);
                
          //这里�?2个布尔�?�来实现频幕触碰事件的判�?
         //当频幕触碰事件为“UP”事件时我们不需要为其返回一个布尔�?�，因为我们没对其做出接收�?�回�?
         //而当触碰事件为DOWN事件时我们则为其返回�?个布尔�??       
                return true;
            }
        };
        rootView.setOnTouchListener(rootListener);
    }
    
    		//滑动屏幕
    
    private class MyGestureListener extends
            GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                float distanceX, float distanceY) {
            //if (mPaused) return false;
            ImageViewTouch imageView = mCurrentImageView;
            if (imageView.getScale() > 1F) {
                imageView.postTranslateCenter(-distanceX, -distanceY);
            }
            return true;
        }
        @Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			// TODO Auto-generated method stub
        	if((e1.getX()-e2.getX()>FLING_MIN_DISTANCE)&&
        			Math.abs(velocityX)>FLING_MIN_VELOCITY)
        	{
        		moveNextOrPrevious(1);
        	}
        	else if((e2.getX()-e1.getX()>FLING_MIN_DISTANCE)&&
        			Math.abs(velocityX)>FLING_MIN_VELOCITY)
			{
        		moveNextOrPrevious(-1);
			}
			return super.onFling(e1, e2, velocityX, velocityY);
		}

		@Override
        public boolean onSingleTapUp(MotionEvent e) {
			
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
        
        	setMode(MODE_NORMAL);
            openOptionsMenu();
            return true;
        }

        @Override
		public boolean onDown(MotionEvent e) {
			// TODO Auto-generated method stub
			return super.onDown(e);
		}

		@Override
        public boolean onDoubleTap(MotionEvent e) {
            //if (mPaused) return false;
            ImageViewTouch imageView = mCurrentImageView;

            // Switch between the original scale and 3x scale.
            if (imageView.getScale() > 2F) {
                mCurrentImageView.zoomTo(1f);
            } else {
                mCurrentImageView.zoomToPoint(3f, e.getX(), e.getY());
            }
            return true;
        }
    }    
    
    private static int getPreferencesInteger(
            SharedPreferences prefs, String key, int defaultValue) {
        String value = prefs.getString(key, null);
        try {
            return value == null ? defaultValue : Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            Log.e(TAG, "couldn't parse preference: " + value, ex);
            return defaultValue;
        }
    }
    void setMode(int mode) {
        if (mMode == mode) {
            return;
        }
        View slideshowPanel = findViewById(R.id.slideShowContainer);
        View normalPanel = findViewById(R.id.abs);

        Window win = getWindow();
        mMode = mode;
        if (mode == MODE_SLIDESHOW) {
        	slideshowPanel.setVisibility(View.VISIBLE);
            normalPanel.setVisibility(View.GONE);

            win.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            mCurrentImageView.clear();
            slideshowPanel.getRootView().requestLayout();

            // 须理清的数�?�有以下4�?:
            //   mUseShuffleOrder
            //   mSlideShowLoop
            //   mAnimationIndex
            //   mSlideShowInterval
            mSlideShowLoop = mPrefs.getBoolean(PREF_SLIDESHOW_REPEAT, false);
            mSlideShowInterval = getPreferencesInteger(
                    mPrefs, "pref_gallery_slideshow_interval_key", 3) * 1000;
        } else {
            slideshowPanel.setVisibility(View.GONE);
            normalPanel.setVisibility(View.VISIBLE);

            win.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);            
            win.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

            if (mGetter != null) {
                mGetter.cancelCurrent();
            }
            for (ImageViewTouchBase ivt : mSlideShowImageViews) {
                ivt.clear();
            }
            // mGetter为空则表明代理已经被暂停
            if (mGetter != null) {
                setImage(mCurrentPosition, true);
            }
        }
    }

    /**
     * 加载上一张下�?张图�?
     * @param delta
     */
    private void moveNextOrPrevious(int delta) {
        int nextImagePos = mCurrentPosition + delta;
        if ((0 <= nextImagePos) && (nextImagePos < mImagePathList.size())) {
            setImage(nextImagePos, true);
        }
    }
    
    void setImage(int pos, boolean showControls) {
        mCurrentPosition = pos;

        Bitmap b = mCache.getBitmap(pos);
        if (b != null) {           
            mCurrentImageView.setImageRotateBitmapResetBase(
                    new RotateBitmap(b, 0), true);             
            currentBitmap=b;
            Log.i("lalala", "mCurrentPosition"+mCurrentPosition);
        }

        ImageGetterCallback cb = new ImageGetterCallback() {
            public void completed() {
            }

            public boolean wantsThumbnail(int pos, int offset) {
                return !mCache.hasBitmap(pos + offset);
            }

            public boolean wantsFullImage(int pos, int offset) {
                return offset == 0;
            }

            public int fullImageSizeToUse(int pos, int offset) {
                // this number should be bigger so that we can zoom.  we may
                // need to get fancier and read in the fuller size image as the
                // user starts to zoom.
                // Originally the value is set to 480 in order to avoid OOM.
                // Now we set it to 2048 because of using
                // native memory allocation for Bitmaps.
                final int imageViewSize = 2048;
                return imageViewSize;
            }

            public int [] loadOrder() {
                return sOrderAdjacents;
            }
			
			public void imageLoaded(int pos, int offset, Bitmap bitmap,
					boolean isThumb) {
				// TODO Auto-generated method stub
                // We may get a result from a previous request. Ignore it.
                if (pos != mCurrentPosition) {
                    bitmap.recycle();
                    return;
                }

                if (isThumb) {
                    mCache.put(pos + offset, bitmap);
                }
                if (offset == 0) {
                    // isThumb: We always load thumb bitmap first, so we will
                    // reset the supp matrix for then thumb bitmap, and keep
                    // the supp matrix when the full bitmap is loaded.
                    mCurrentImageView.setImageRotateBitmapResetBase(new RotateBitmap(bitmap,0), isThumb);
                    currentBitmap=bitmap;
                }
				
			}
        };

        // Could be null if we're stopping a slide show in the course of pausing
        if (mGetter != null) {
            mGetter.setPosition(pos, cb, mImagePathList, mHandler);
        }
    }

	@Override  
	 public void onCreate(Bundle savedInstanceState) {  
	    super.onCreate(savedInstanceState);
	  
	    //实例化一个当前路径图片的位图图像
	    requestWindowFeature(Window.FEATURE_NO_TITLE);    
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,      
	                      WindowManager.LayoutParams.FLAG_FULLSCREEN);    
	       
	    setContentView(R.layout.fourth_view);  
	    Intent intent = getIntent();
	    length=intent.getIntExtra("length", 1);
        mCurrentImageView = (ImageViewTouch) findViewById(R.id.image);
        mCurrentImageView.setEnableTrackballScroll(true);
        mCache = new BitmapCache(3);
        mCurrentImageView.setRecycler(mCache);
        
        mSaveConfirm=(Button)findViewById(R.id.save_confirm);
        mSaveCancel=(Button)findViewById(R.id.save_cancel);
        mSaveConfirm.setOnClickListener((android.view.View.OnClickListener) onSaveConfirmListener);
        mSaveCancel.setOnClickListener((android.view.View.OnClickListener) onSaveCancelListener);
	    makeGetter();

        mSlideShowImageViews[0] =
                (ImageViewTouchBase) findViewById(R.id.image1_slideShow);
        mSlideShowImageViews[1] =
                (ImageViewTouchBase) findViewById(R.id.image2_slideShow);
        for (ImageViewTouchBase v : mSlideShowImageViews) {
            v.setVisibility(View.INVISIBLE);
            v.setRecycler(mCache);
        }
	    mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

	          
	    paths = intent.getStringArrayExtra("data");
	    logo=intent.getIntExtra("logo",2);
        if(paths==null)
        {
        	Log.i(TAG, "pathArray=null");
        	return;
        }
        for(int j=0;j<paths.length;j++){
        Log.i("God damn it!~", paths[j]);
        }
        
	    path=intent.getStringExtra("path");
        int position=intent.getIntExtra("id",1);
        mCurrentPosition=position;
        Log.i("当前图片", ""+position);
        Log.i("that is it", paths[position]);
        Log.i("ImageActivity_onCreate", "position="+position+"; path="+path);
        mImagePathList=new ArrayList<String>();
        for(String str: paths)
	    {
        	mImagePathList.add(str);		    
	    }

        BitmapDrawable bmd = new BitmapDrawable(effectsBitmap);	
		ImageView imageView = new ImageView(this);
		imageView.setImageDrawable(bmd);
		imageView.setScaleType(ScaleType.CENTER);//图片居中显示
		
		//设置图片点击监听
		
        setupOnTouchListeners(findViewById(R.id.rootLayout));
		Log.i(TAG, "currentPostion="+currentPosition+"; currentPath="+currentPath);
		
	
	
	   //放大图片的ImageView图片事件注册和实�?
	       upButton = (ImageView)findViewById(R.id.upbutton);
	       if(Change==CHANGE_NORMAL){
	    	   upButton.setImageResource(R.drawable.btn_fd);
	       }
	       else if(Change==CHANGE_DONE){
	    	   upButton.setImageResource(R.drawable.btn_fd1); 
	       }
	       upButton.setOnTouchListener(new OnTouchListener() {
	    	   
               public boolean onTouch(View v, MotionEvent event) { 
            	   
               if (event.getAction()==MotionEvent.ACTION_DOWN){ 
                       //按下时触�? 放大图片位图显示事件
                	 Change=CHANGE_DONE; 
                	 try {	    			
                 		mCurrentImageView.zoomIn();	 
             		} catch (Exception e) {
             			Log.i(TAG, "nonono, had err when thumbnail picture");
             			// TODO Auto-generated catch block
             			e.printStackTrace();
             			Toast.makeText(ImageActivity.this, R.string.no_memory,Toast.LENGTH_SHORT).show();  
             		}
                 }
               if (event.getAction()==MotionEvent.ACTION_UP){
                	 Change=CHANGE_NORMAL;
                 }   
               return true; 
         } 
               }); 
	     //缩小图片的ImageView图片事件注册和实�?
	       downButton = (ImageView)findViewById(R.id.downbutton);
	       if(Change==CHANGE_NORMAL){
	    	   downButton.setImageResource(R.drawable.btn_sx);	    	   
	       }
	       else if(Change==CHANGE_DONE){
	    	   downButton.setImageResource(R.drawable.btn_sx1); 
	       }
           downButton.setOnTouchListener(new OnTouchListener() {
               
               public boolean onTouch(View v, MotionEvent event) { 
            	   if (event.getAction()==MotionEvent.ACTION_DOWN) { 
                       //按下时触�? 缩小图片位图显示事件
                	 Change=CHANGE_DONE; 
                	 try {	    			
                		 mCurrentImageView.zoomOut();   
             		} catch (Exception e) {
             			Log.i(TAG, "nonono, had err when thumbnail picture");
             			// TODO Auto-generated catch block
             			e.printStackTrace();
             			Toast.makeText(ImageActivity.this, R.string.no_memory,Toast.LENGTH_SHORT).show();  
             		}
                 } 
            	   if (event.getAction()==MotionEvent.ACTION_UP){
                	 Change=CHANGE_NORMAL;
                 }
        
               return true; 
           } 
               }); 
	 }
	
	//图像的Position更新
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		Log.i("yeah", "code arrives here");
		super.onStart();
        int count = mImagePathList.size();
        if (count == 0) {
            finish();
            return;
        } else if (count <= mCurrentPosition) {
            mCurrentPosition = count - 1;
        }
        if (mGetter == null) {
            makeGetter();
        }
        setImage(mCurrentPosition, false);
	}

	
	//启动了imageGetter里的线程
	private void makeGetter() {
	    mGetter = new ImageGetter(getContentResolver());
		}
	public void startAnimation()
	 	{
		 mCurrentImageView.startAnimation(anim);
	 	}	 

    public void rotate(int r){
    	 
    	if(currentBitmap==null)
		{
			 Log.i(TAG, "oh, nonono~~~, currentBitmap=null!");
			 return;
		}
    	
    	Log.i(TAG, "the currentPosition="+currentPosition);
    	
        int bmWidth = currentBitmap.getWidth();
        int bmHeight = currentBitmap.getHeight();        
        Matrix matrix = new Matrix();
        
        try
        {
           matrix.postRotate(r); 
           Bitmap resizeBmp = Bitmap.createBitmap(currentBitmap,0,0,bmWidth,bmHeight,matrix,true);
        
           mCurrentImageView.setImageBitmap(resizeBmp);

           currentBitmap=resizeBmp;
        }
        catch(Exception err)
        {
        	err.printStackTrace();
        	Toast.makeText(ImageActivity.this, R.string.no_memory,Toast.LENGTH_SHORT).show();
        	return;
        }
    }


  //实例化一个Handler线程对象，并为其实现相应的方�?
    Handler handler = new Handler(){  
           public void handleMessage(Message msg) {  
    	          switch (msg.what) {      
    	            case 1:
    	            	currentPosition++;
    	            	int animationPosition=currentPosition%paths.length;
    	            	Log.i(TAG, "currentPosition="+currentPosition+"; animationPosition="+animationPosition);
    	            	gallery.setSelection(animationPosition);
    	                break;      
    	            }      
    	            super.handleMessage(msg);  
    	   }       
     };  

     TimerTask task = new TimerTask(){  
    	 public void run() {  
    	    Message message = new Message();      
            message.what = 1;      
            handler.sendMessage(message);    
         }            
     }; 
    	
     //菜单事项按钮的添�?
	 @Override
	 public boolean onCreateOptionsMenu(Menu menu) {
		   super.onCreateOptionsMenu(menu);
		   menu.add(0,SECRET,1,R.string.secret);
		   menu.add(0, SHARE, 2, R.string.share);
		   menu.add(0, THUMBNAIL, 3, R.string.thumbnail);
		   menu.add(0, DETAILS, 4, R.string.details);
		   menu.add(1, ROTATE, 5, R.string.retate);
		   menu.add(0, DELETE, 6, R.string.delete);
		
		   
		   
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
	           case SHARE: 
	        	   /*
	        	    * 调用其他应用
	        	    * 
	        	    * 图片分享
	        	    */  	   
	  			 Intent intent=new Intent(Intent.ACTION_SEND);  
	  			//intent.setType("text/plain");  
	  			 intent.setType("image/png");
	  			  currentPath=mImagePathList.get(mCurrentPosition);
	  			   final File file=new File(currentPath);
	  			  Uri u = Uri.fromFile(file);
	  			 intent.putExtra(Intent.EXTRA_STREAM, u);
	  			
	  			 	System.out.println(currentPath);
	  				intent.putExtra(Intent.EXTRA_SUBJECT, "分享");  
	  				intent.putExtra(Intent.EXTRA_TEXT, "图片分享");  
	  				startActivity(Intent.createChooser(intent, getTitle()));  

	        	   break;
	  
	        	   
	      }  
	          
	      return super.onOptionsItemSelected(item);  
	 }  

	private void showSecret() {
		// TODO 自动生成的方法存根
		if(logo==1){
			String []item={getText(R.string.add).toString()};
			AlertDialog dialog=new AlertDialog.Builder(ImageActivity.this)
					.setIcon(R.drawable.bg)
					.setTitle(R.string.secret)
					.setItems(item, onShowSecret).create();
			dialog.show();
		}
		else{
			String []item={(String)getText(R.string.restore)};
			AlertDialog dialog=new AlertDialog.Builder(ImageActivity.this)
					.setIcon(R.drawable.bg)
					.setTitle(R.string.secret)
					.setItems(item, onShowSecret).create();
			dialog.show();
		}
		
	}
	//旋转图片的方法调用以及提示信息的实现
	private void showRotate() {
		// TODO Auto-generated method stub
		 String[] items={(String) getText(R.string.retate_left),
				 (String) getText(R.string.retate_right),
				 (String) getText(R.string.retate_back)}; 

          AlertDialog dialog = new AlertDialog.Builder(ImageActivity.this)  
         .setIcon(R.drawable.bg)  
         .setTitle(R.string.retate)
         .setItems(items, onShowRotateSelect).create();  
         dialog.show();  
	}
    //设置�?...的方法调用以及提示信息的实现
	private void showSetupFor() {
		 String[] items={(String) getText(R.string.setup_wallpaper),
				 (String) getText(R.string.setup_favarete)}; 
		  AlertDialog dialog = new AlertDialog.Builder(ImageActivity.this)  
		 .setIcon(R.drawable.bg)
	     .setTitle(R.string.setupfor)
         .setItems(items, onShowSetUpSelect).create();  
		 dialog.show(); 
	}
    //删除图片的方法调用以及提示信息的实现
	private void showDelete() {
		// TODO Auto-generated method stub
	   currentPath=mImagePathList.get(mCurrentPosition);
	   final File file=new File(currentPath);
  	   if(file.exists())
  	   {
  			new AlertDialog.Builder(ImageActivity.this)
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
  	                    	Log.v(TAG, "remove before n="+mImagePathList.size());
  	                    	mImagePathList.remove(currentPath);
  	                    	Log.v(TAG, "remove before n="+mImagePathList.size());
  	                    	try
  	                    	{                           
  	                    	    file.delete();
  	                    	    scanFileAsync(ImageActivity.this, currentPath);
  	                    	    //getcontentresolver().delete(Media.EXTERNAL_CONTENT_URI, Media.DATA + "=?",path);
  	                    	    
  	                    	    Toast.makeText(ImageActivity.this,
  	                    	    		(String) getText(R.string.picture_file)+" "+file.getName()
  	                    	    		+" "+(String) getText(R.string.delete_success),Toast.LENGTH_SHORT).show();
  	                    	}
  	                    	catch(Exception err)
  	                    	{
  	                    		err.printStackTrace();
  	                    		Toast.makeText(ImageActivity.this, R.string.unkonw_err,Toast.LENGTH_SHORT).show();
  	                    		return;
  	                    	}
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
		currentPath=mImagePathList.get(mCurrentPosition);
		final File file=new File(currentPath);
		 String[] items={(String) getText(R.string.picture_name)+": "+file.getName(),
				 (String) getText(R.string.picture_path)+": "+file.getParentFile().getAbsolutePath(),
				 (String) getText(R.string.picture_size)+": "+Common.getFileSize(Common.getFileSize(file)), 
				 (String) getText(R.string.picture_modified_date)+": "+Common.getFileDateTime(file)}; 
		 
		 //创建�?个能够显示图片详细内容的Dialong窗口，并追加onShowDetailsSelect方法
		 AlertDialog dialog = new AlertDialog.Builder(ImageActivity.this)  
		           .setIcon(R.drawable.bg)  
		           .setTitle(R.string.details)
		           .setItems(items, onShowDetailsSelect).create();  
		 dialog.show();  
	}
	/**  
	* 移动文件  
	* @param srcFileName    源文件完整路径 
	* @param destDirName    目的目录完整路径 
	* @return 文件移动成功返回true，否则返回false  
	*/  
	public boolean moveFile(String srcFileName, String destDirName) {  
	      
	    File srcFile = new File(srcFileName);  
	    if(!srcFile.exists() || !srcFile.isFile())   
	        return false;  
	      
	    File destDir = new File(destDirName);  
	    if (!destDir.exists())  
	        destDir.mkdirs();  
	      
	    return srcFile.renameTo(new File(destDirName + File.separator + srcFile.getName()));  
	}
	
	//加解密
	OnClickListener onShowSecret=new OnClickListener() {
		
		@Override
		public void onClick(DialogInterface arg0, int arg1) {
			// TODO 自动生成的方法存根
			//mySecret=new MySecret();
			//myAES=new MyAES();
			//myYH=new MyYH();
			bManage=new BitmapManage(context);
			if(logo==1){
				
						//获取当前文件的绝对路径
						currentPath=mImagePathList.get(mCurrentPosition);
						//moveFile(currentPath, MainActivity.sa);
						
					
							Log.i("O.O", currentPath);
				
						//带格式的名字	
						fileName = currentPath.substring(currentPath.lastIndexOf("/")+1);
						//纯名字
						String name=currentPath.substring(currentPath.lastIndexOf("/")+1,currentPath.lastIndexOf("."));
						
						//带格式的新名字
						String newname=name+".dat";
						//Log.i("新名字", newname);
						
						//文件名没变的新路径(私密空间)
						String newFile=MainActivity.sa+"/"+fileName;
						
						//加密后dat文件的新路径(私密空间)
						//String newFile=MainActivity.sa+"/"+newname;
						
						//File file1=new File(currentPath);
						//File file2=new File(newFile);
				
						//Log.i("newfile", newFile);
						
						/*try {
							myYH.encryptImg(file1, file2);
						} catch (Exception e) {
							// TODO 自动生成的 catch 块
							e.printStackTrace();
						}*/
						//mySecret.addByte(currentPath, newFile);
						//mySecret.aesEncrypt(currentPath, newFile);
						
						
						/*File file=new File(newFile);
						byte []k=MyAES.get(context);
						try {
							
							Log.i("-.-", "yes");
							
							MyAES.encrypt(file, k);
							} catch (Exception e) {
							// TODO 自动生成的 catch 块
								Log.i("shit", "干");
							e.printStackTrace();
							}*/
						
						
						try {
							bitmap=bManage.LoadBitmap(currentPath);
						} catch (FileNotFoundException e) {
							// TODO 自动生成的 catch 块
							e.printStackTrace();
						}
						byte[] by=bManage.Bitmap2Bytes(bitmap);
						by=DES_Algorithm.desCrypto(by, KEY);
						FileIO.saveFileByBytes(by, newFile);
						
						//currentPath=mImagePathList.get(mCurrentPosition);
						   final File file=new File(currentPath);
					  	   if(file.exists())
					  	   {
					  	                    	ArrayList<String> list=new ArrayList<String>();
					  	                    	for(String str:paths)
					  	                    	{
					  	                    		if(str.equals(currentPath))
					  	                    			continue;
					  	                    		list.add(str);
					  	                    	}
					  	                    	Log.v(TAG, "remove before n="+mImagePathList.size());
					  	                    	mImagePathList.remove(currentPath);
					  	                    	Log.v(TAG, "remove before n="+mImagePathList.size());
					  	                    	try
					  	                    	{                           
					  	                    	    file.delete();
					  	                    	    scanFileAsync(ImageActivity.this, currentPath);
					  	                    	}
					  	                    	catch(Exception err)
					  	                    	{
					  	                    		err.printStackTrace();
					  	                    		Toast.makeText(ImageActivity.this, R.string.unkonw_err,Toast.LENGTH_SHORT).show();
					  	                    		return;
					  	                    	}
					  	                    }
			}
			else{
				Thread t2=new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO 自动生成的方法存根
						//moveFile(currentPath, Environment.getExternalStorageDirectory().getAbsolutePath());
						currentPath=mImagePathList.get(mCurrentPosition);
						mySecret.removeByte(currentPath);
						//mySecret.aesDecrypt(currentPath);
						/*
						File file=new File(currentPath);
						byte []k=MyAES.get(context);
						try {
							MyAES.decrypt(file, k);
							} catch (Exception e) {
								// TODO 自动生成的 catch 块
								e.printStackTrace();
							}*/
						moveFile(currentPath, Environment.getExternalStorageDirectory().getAbsolutePath());
						}
					});
					t2.start();
				}
			}
	};
	
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
	/*
	 * 待追加�?��?��?��?��?��?��?��?��?��?��?��?��?��?��?��?��??
	 * 编辑图片的事件监�?
	 */
	OnClickListener onShowThumbnailSelect = new OnClickListener() {  
	    public void onClick(DialogInterface dialog, int which) {  
	         // TODO Auto-generated method stub  
	      switch(which)
	      {
	        
	        case 0:
	        	try{
	        		//setEffects();
	        	}catch (Exception e) {
	    			// TODO Auto-generated catch block
	    			Log.i(TAG, "特效查看加载缩略位图时出现异�?");
	    			e.printStackTrace();
	    			Toast.makeText(ImageActivity.this, "特效查看异常",Toast.LENGTH_SHORT).show();  
	    		}
	        	break;
	        case 1:
	        	try{
	        		showSetupFor();
	        	}catch (Exception e) {
	    			// TODO Auto-generated catch block
	    			Log.i(TAG, "设置�?..加载缩略位图时出现异�?");
	    			e.printStackTrace();
	    			Toast.makeText(ImageActivity.this, "设置图片异常",Toast.LENGTH_SHORT).show();  
	    		}
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
		    			 Toast.makeText(ImageActivity.this, R.string.setup_wallpaper_success,Toast.LENGTH_SHORT).show();
		    		} catch (IOException e) {
		    			// TODO Auto-generated catch block
		    			e.printStackTrace();
		    			Toast.makeText(ImageActivity.this, R.string.unkonw_err,Toast.LENGTH_SHORT).show();  
		    		}
		    		break;
		    }
	   }
    };
    private android.view.View.OnClickListener onSaveConfirmListener = new android.view.View.OnClickListener(){
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
			// TODO Auto-generated method stub
			File file = new File(mImagePathList.get(mCurrentPosition));
	          FileOutputStream out;
	          try{
	                out = new FileOutputStream(file);
	                if(effectsBitmap.compress(Bitmap.CompressFormat.PNG, 100, out))
	                {
	                    out.flush();
	                    out.close();
	                    return ;
	                }
	          }
	          catch (FileNotFoundException e)
	          {
	        	   Log.i(TAG, "saveBitmap has err! FileNotFoundException!");
	               e.printStackTrace();
	          }
	          catch (IOException e)
	          {
	        	  Log.i(TAG, "saveBitmap has err! IOException!");
	               e.printStackTrace();
	          }
			   mSaveConfirm.setVisibility(View.GONE);
			   mSaveCancel.setVisibility(View.GONE);
		}
    	
    };
    private android.view.View.OnClickListener onSaveCancelListener = new android.view.View.OnClickListener(){

		public void onClick(View v) {
			// TODO Auto-generated method stub
			 mCurrentImageView.setImageBitmap(currentBitmap);	
			   mSaveConfirm.setVisibility(View.GONE);
			   mSaveCancel.setVisibility(View.GONE);
		}
    	
    };
 
    //旋转（追加菜单）菜单项事件监听
	@Override
	 public boolean onContextItemSelected(MenuItem item) { 
		 
		 switch (item.getItemId()) {  
		   case 0:
			   rotate(-90);
			   return true;
		   case 1:
			   rotate(90);
			   return true;
		   case 2:
			   rotate(180);
			   return true;
		   default:  
			   return super.onContextItemSelected(item);  
	     }
	 }
}
 
class ImageViewTouch extends ImageViewTouchBase {
    private final ImageActivity mViewImage;
    private boolean mEnableTrackballScroll;

    public ImageViewTouch(Context context) {
        super(context);
        mViewImage = (ImageActivity) context;
    }

    public ImageViewTouch(Context context, AttributeSet attrs) {
        super(context, attrs);
        mViewImage = (ImageActivity) context;
    }

    public void setEnableTrackballScroll(boolean enable) {
        mEnableTrackballScroll = enable;
    }

    protected void postTranslateCenter(float dx, float dy) {
        super.postTranslate(dx, dy);
        center(true, true);
    }

    private static final float PAN_RATE = 20;

    // This is the time we allow the dpad to change the image position again.
    private long mNextChangePositionTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mViewImage.mPaused) return false;

        // Don't respond to arrow keys if trackball scrolling is not enabled
        if (!mEnableTrackballScroll) {
            if ((keyCode >= KeyEvent.KEYCODE_DPAD_UP)
                    && (keyCode <= KeyEvent.KEYCODE_DPAD_RIGHT)) {
                return super.onKeyDown(keyCode, event);
            }
        }

        int current = mViewImage.mCurrentPosition;

        int nextImagePos = -2; // default no next image
        try {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_LEFT: {
                    if (getScale() <= 1F && event.getEventTime()
                            >= mNextChangePositionTime) {
                        nextImagePos = current - 1;
                        mNextChangePositionTime = event.getEventTime() + 500;
                    } else {
                        panBy(PAN_RATE, 0);
                        center(true, false);
                    }
                    return true;
                }
                case KeyEvent.KEYCODE_DPAD_RIGHT: {
                    if (getScale() <= 1F && event.getEventTime()
                            >= mNextChangePositionTime) {
                        nextImagePos = current + 1;
                        mNextChangePositionTime = event.getEventTime() + 500;
                    } else {
                        panBy(-PAN_RATE, 0);
                        center(true, false);
                    }
                    return true;
                }
                case KeyEvent.KEYCODE_DPAD_UP: {
                    panBy(0, PAN_RATE);
                    center(false, true);
                    return true;
                }
                case KeyEvent.KEYCODE_DPAD_DOWN: {
                    panBy(0, -PAN_RATE);
                    center(false, true);
                    return true;
                }
            }
        } finally {
            if (nextImagePos >= 0
                    && nextImagePos < mViewImage.mImagePathList.size()) {
                synchronized (mViewImage) {
                    mViewImage.setImage(nextImagePos, true);
                }
           } else if (nextImagePos != -2) {
               center(true, true);
           }
        }

        return super.onKeyDown(keyCode, event);
    }
}

// This is a cache for Bitmap displayed in ViewImage (normal mode, thumb only).
class BitmapCache implements ImageViewTouchBase.Recycler {
    public static class Entry {
        int mPos;
        Bitmap mBitmap;
        public Entry() {
            clear();
        }
        public void clear() {
            mPos = -1;
            mBitmap = null;
        }
    }

    private final Entry[] mCache;
    public BitmapCache(int size) {
        mCache = new Entry[size];
        for (int i = 0; i < mCache.length; i++) {
            mCache[i] = new Entry();
        }
    }

    // Given the position, find the associated entry. Returns null if there is
    // no such entry.
    private Entry findEntry(int pos) {
        for (Entry e : mCache) {
            if (pos == e.mPos) {
                return e;
            }
        }
        return null;
    }
    // Returns the thumb bitmap if we have it, otherwise return null.
    public synchronized Bitmap getBitmap(int pos) {
        Entry e = findEntry(pos);
        if (e != null) {
            return e.mBitmap;
        }
        return null;
    }

    public synchronized void put(int pos, Bitmap bitmap) {
        // First see if we already have this entry.
        if (findEntry(pos) != null) {
            return;
        }

        // Find the best entry we should replace.
        // See if there is any empty entry.
        // Otherwise assuming sequential access, kick out the entry with the
        // greatest distance.
        Entry best = null;
        int maxDist = -1;
        for (Entry e : mCache) {
            if (e.mPos == -1) {
                best = e;
                break;
            } else {
                int dist = Math.abs(pos - e.mPos);
                if (dist > maxDist) {
                    maxDist = dist;
                    best = e;
                }
            }
        }
        // Recycle the image being kicked out.
        // This only works because our current usage is sequential, so we
        // do not happen to recycle the image being displayed.
        if (best.mBitmap != null) {
            best.mBitmap.recycle();
        }

        best.mPos = pos;
        best.mBitmap = bitmap;
    }

    // Recycle all bitmaps in the cache and clear the cache.
    public synchronized void clear() {
        for (Entry e : mCache) {
            if (e.mBitmap != null) {
                e.mBitmap.recycle();
            }
            e.clear();
        }
    }

    // Returns whether the bitmap is in the cache.
    public synchronized boolean hasBitmap(int pos) {
        Entry e = findEntry(pos);
        return (e != null);
    }

    // Recycle the bitmap if it's not in the cache.
    // The input must be non-null.
    public synchronized void recycle(Bitmap b) {
        for (Entry e : mCache) {
            if (e.mPos != -1) {
                if (e.mBitmap == b) {
                    return;
                }
            }
        }
        b.recycle();
    }
}
