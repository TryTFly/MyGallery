package com.y.mygallery;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.y.adapter.FirstViewAdapter;
import com.y.database.MyDB;
import com.y.mygallery.R;
import com.y.treasure.ImageCommon;

public class MainActivity extends ListActivity {
	protected static final String TAG = "MainActivity";
	private static final String PATH = "/SecretArea/.thumbnails/";
	private List<String> items = null;
	private List<String> paths = null;
	private String rootPath = "/", secretA;
	public static String sa;
	private MyDB myDB = null;
	private final String INTERNAL = "/flash/";
	private LinkedList<String> extens = null;
	protected final static int MENU_EDIT = Menu.FIRST;
	protected final static int MENU_ABOUT = Menu.FIRST + 1;
	protected final static int MENU_EXIT = Menu.FIRST + 2;
	public boolean Damn = true;
	public static int skin = 0;
	int delay = 40; // �����ӳٵĸ���ѭ��
	int maxBarValue = 5000;
	int typeBar = 0; // ȷ���͵Ľ��ȣ�0 =΢����1 =ˮƽ
	private Button button, dButton;

	ProgressThread progThread;
	ProgressDialog progDialog;
	private String local;

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		// ��ʼ����ʾ���֣���ʼ����ΪLayout�����main
		if (Damn) {
			setTitle(R.string.app_name);
			setContentView(R.layout.first_view);
			// mInflater = LayoutInflater.from(this);
			getRootView(rootPath);
			createFile();
			button = (Button) findViewById(R.id.searchbtn);
			dButton = (Button) findViewById(R.id.deletebtn);
			dButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO �Զ����ɵķ������
					Toast.makeText(MainActivity.this, R.string.deletelog, Toast.LENGTH_LONG).show();
					deleteImage();
				}
			});

			button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO �Զ����ɵķ������
					Toast.makeText(MainActivity.this, R.string.dialogMs, Toast.LENGTH_LONG).show();
					searchImage();
				}
			});
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (progThread != null)
			progThread.setState(ProgressThread.DONE);
		if (myDB != null)
			myDB.close();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 0: // Spinner
			progDialog = new ProgressDialog(this);
			// progDialog.setCancelable(false); //��ֹ�ͻ��������ء���
			progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progDialog.setMessage((String) getText(R.string.scan));
			progThread = new ProgressThread(handler);
			progThread.setState(ProgressThread.RUNNING);
			progThread.start();
			return progDialog;
		case 1: // Horizontal
			progDialog = new ProgressDialog(this);
			// progDialog.setCancelable(false);
			progDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progDialog.setMax(maxBarValue);
			progDialog.setMessage("Dollars in checking account:");
			progThread = new ProgressThread(handler);
			progThread.start();
			return progDialog;
		default:
			return null;
		}
	}

	// ɨ�����ý���·��
	private void getRootView(String filePath) {
		items = new ArrayList<String>();
		paths = new ArrayList<String>();
		items.add("internal");
		paths.add("/flash/");

		String SDpath = Environment.getExternalStorageDirectory() + "/";
		items.add("sdcard");
		paths.add(SDpath);

		items.add("secretarea");
		paths.add("secret");

		setListAdapter(new FirstViewAdapter(this, items, paths));
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		String path = paths.get(position).toString();
		Log.i("onListItemClick", "path=" + path);

		if (path.equals("secret")) // ˽�ܿռ�
		{
			Intent intent = new Intent(MainActivity.this, MyIDCheck.class);
			intent.putExtra("path", secretA);
			startActivity(intent);
		} else// �ڶ�����
		{
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, FileActivity.class);
			intent.putExtra("path", path);
			MainActivity.this.startActivity(intent);
		}
	}

	// ���ݿ�ͼ���������
	private void searchImage() {
		if(myDB==null)
		{
			myDB=new MyDB(MainActivity.this);
		}
		typeBar = 0;
		showDialog(typeBar);
	}
	
	//ɾ�����ݿ�����
	private void deleteImage() {
		// TODO �Զ����ɵķ������
		if(myDB==null)
		{
			myDB=new MyDB(MainActivity.this);
		}
		myDB.open();
		myDB.deleteAllImage();
		myDB.close();
	}

	public void getExtens() {
		extens.add(".JPEG");
		extens.add(".JPG");
		extens.add(".PNG");
		extens.add(".GIF");
		extens.add(".BMP");
	}

	// ����ͼ���ļ�(����ͼ)�����ݿ�
	public void saveImageFile(File file) {
		Log.i(TAG + "_before accept",
				"code comes to saveImageFile, the name=" + file.getName() + "the path=" + file.getAbsolutePath());
		file.listFiles(new FileFilter() {
			public boolean accept(File file) {
				String name = file.getName();
				int i = name.lastIndexOf('.');
				if (i != -1) {
					name = name.substring(i).toUpperCase();
					if (extens.contains(name)) {
						Log.i(TAG + "_accept-file", "for ready to savePicture! name=" + file.getName() + "; the path="
								+ file.getAbsolutePath());

						savePicture(file);
						return true;
					}
				} else if (file.isDirectory()) {
					saveImageFile(file);
				}
				return false;
			}

			private void savePicture(File file) {
				// TODO Auto-generated method stub
				String name = file.getName();
				String album = file.getParent();
				String path = file.getAbsolutePath();
				Log.i(TAG + "_savePicture", "the name=" + name + "; the parent=" + album + "; the path=" + path);

				album = album.substring(album.lastIndexOf("/") + 1);
				Log.i(TAG, "after sub, the album=" + album);

				// ͼƬ������
				Bitmap bitmap = ImageCommon.getFitSizePicture(file);
				if (bitmap == null) {
					Resources res = getResources();
					bitmap = BitmapFactory.decodeResource(res, R.drawable.bg);
				}
				long rowId = myDB.insertImage(name, album, path, bitmap);

				Log.i(TAG, "after inserted the rowid=" + rowId);
				if (rowId == -1) {
					Log.i(TAG, "insert new image has err!");
					myDB.close();
				}
			}
		});
	}

	/*
	 * 
	 * �˵��¼�����
	 * 
	 * 
	 */
	// �˵�ѡ���¼�����
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case MENU_ABOUT:// ����
			Toast.makeText(MainActivity.this, R.string.about, Toast.LENGTH_LONG).show();
			break;
		case MENU_EXIT:// �˳�
			System.exit(0);
			break;
		}
		return true;
	}

	// ��Ӳ˵���
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// �Ӳ˵�
		SubMenu sub = menu.addSubMenu("����ʲô��");
		sub.setIcon(android.R.drawable.ic_menu_slideshow);
		sub.add(0, MENU_ABOUT, 0, "��  ��");
		sub.add(0, MENU_EXIT, 0, "��  ��");
		return true;
	}

	// �������Ҫ���û����棩�߳̽�����Ϣ���̵߳Ľ��Ⱥ͸��¡�
	final Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			// ��øñ����ĵ�ǰֵ�ܵ���Ϣ���ݲ����½�������
			int total = msg.getData().getInt("total");
			Log.i(TAG, "Handler total=" + total);
			// progDialog.setProgress(total);
			if (total == 1) {
				Log.i(TAG, "dismissDialog, the dialog will shutdown! the tatal=" + total);
				dismissDialog(typeBar);
				progThread.setState(ProgressThread.DONE);
			}
		}
	};

	// �ڲ����ִ�н��ȼ����һ���̡߳�ʵ���̵߳����̺߳���д��run()������
	// ���ṩ��һ������״̬�ķ�������ֹ�����̵߳����С�

	private class ProgressThread extends Thread {

		// �ೣ��������̵߳�״̬
		final static int DONE = 0;
		final static int RUNNING = 1;

		Handler mHandler;
		int mState;
		int total;

		// ���캯�������ڸ��߳��е�ָ��������������Ϣ���͵����߳�

		ProgressThread(Handler h) {
			mHandler = h;
		}

		// ����run()�������������̡߳�
		// ����Ҫ���½��������߷���һ����Ϣ���������������̣߳���ʵ���ϸı��Ӿ����ֵĽ�����
		// ��������������Ǽ���ָ���ܽ�Ϊ�㣬����ˮƽ��������һһ����ʼ��

		@Override
		public void run() {
			Log.i(TAG, "coming run again!");
			// mState = RUNNING;
			if (mState == RUNNING) {
				// �߳��쳣��׽����Thread.interrupt() ��������״̬ʱ���벶���쳣
				try {
					// ���Ƹ����ٶȣ�����ʱ���Ȳ��ܱ�֤��
					total = 0;
					if (myDB == null) {
						total = 1;
						Thread.sleep(delay);
					} else {
						myDB.open();
						myDB.deleteAllImage();

						extens = new LinkedList<String>();
						getExtens();

						Log.i(TAG, "create new dir");
						File dir = new File(PATH);
						if (dir.exists()) {
							dir.delete();
						}
						dir.mkdir();

						File file = Environment.getRootDirectory();
						local = file.getAbsolutePath();
						Log.i("���ش洢", local);
						saveImageFile(file);
						myDB.close();

						total = 1;
						Thread.sleep(delay);
					}
				} catch (InterruptedException e) {
					Log.e("ERROR", "Thread was Interrupted");
				}

				// ������Ϣ�������û������̣߳��Ա���Ը��½�������
				Message msg = mHandler.obtainMessage();
				Bundle b = new Bundle();
				b.putInt("total", total);
				msg.setData(b);
				mHandler.sendMessage(msg);

			} else {
				// ������ǰ����
				// dbAdapter.close();
				Log.i(TAG, "Thread interrupt!");
				Thread.currentThread().interrupt();
			}
		}

		// ���õ�ǰ�̵߳�״̬
		public void setState(int state) {
			mState = state;
		}
	}

	// ����˽�ܿռ�
	private void createFile() {
		// TODO �Զ����ɵķ������
		String my = Environment.getExternalStorageDirectory() + "/SecretArea/";
		File file = new File(my);
		if (!file.exists()) {
			file.mkdir();
		}
		secretA = file.getAbsolutePath();
		sa = secretA;
		Log.i("sdcard", sa);
	}

}
