package com.y.mygallery;

import com.y.mygallery.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

//���ȷ����¼����
public class MyIDCheck extends Activity{
	SharedPreferences shared = null;
	Editor editor = null;
	private EditText password = null;//��½����
	private EditText twoedit = null;//������
	private EditText threeedit = null;//������
	private EditText fouredit = null;//ȷ��������
	String password01;//Ĭ������
	View DialogView = null;
	View dialogView = null;
	private String path;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.idcheck);
        Intent intent=getIntent();
        path=intent.getStringExtra("path");
        shared = getSharedPreferences("passwordshared", Activity.MODE_PRIVATE);
        temp();  
        Toast.makeText(MyIDCheck.this, "ע�⣺���ĳ�ʼ����Ϊadmin!", Toast.LENGTH_SHORT).show();
    }
	
	public void temp(){//��½ҳ��
		editor = shared.edit();//��ָ��shared�ļ������ �������򴴽���
		password01 = shared.getString("password", "admin");
		LayoutInflater factory = LayoutInflater.from(this);//�õ��Զ���Ի���
        DialogView = factory.inflate(R.layout.main3, null);//����xml�����ļ�����һ���Ի�����ʽԤ��ʹ��
        password = (EditText)DialogView.findViewById(R.id.password);
		AlertDialog dlg = new AlertDialog.Builder(this)
        .setTitle("�������������") 
        .setView(DialogView)
        .setPositiveButton("��¼", new DialogInterfaceOnClick2())  //����ȷ����ť���Ұ󶨼����¼�
        .setNeutralButton("�޸�", new DialogInterfaceOnClick3())   //�����޸İ�ť���Ұ󶨼����¼�
        .setNegativeButton("�˳�", new DialogInterfaceOnClick6())  //����ȡ����ť���Ұ󶨼����¼�
        .create();//����ȡ����ť������
        dlg.show();//��ʾ
	}
	
	public void alter(){//�޸�ҳ��
		LayoutInflater factory = LayoutInflater.from(MyIDCheck.this);//�õ��Զ���Ի���
		dialogView = factory.inflate(R.layout.main2, null);
		AlertDialog dlg = new AlertDialog.Builder(MyIDCheck.this)
		.setTitle("���޸��������")
		.setView(dialogView)
		.setPositiveButton("ȷ��", new DialogInterfaceOnClick5())
		.setNegativeButton("ȡ��", new DialogInterfaceOnClick4())
		.create();
		dlg.show();
		
	}
    
    class DialogInterfaceOnClick2 implements DialogInterface.OnClickListener{//��½

		
		public void onClick(DialogInterface dialog, int whichButton) {
			String pass = password.getText().toString();//�õ��û������ֵ
			if(pass.equals(password01)){
				Toast.makeText(MyIDCheck.this, "��¼�ɹ�", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				intent.setClass(MyIDCheck.this, SecretArea.class);
				intent.putExtra("path", path);
				MyIDCheck.this.startActivity(intent);
				finish();
			}else{
				Toast.makeText(MyIDCheck.this, "�������", Toast.LENGTH_SHORT).show();
				temp();
			}
		}
    }
    
    class DialogInterfaceOnClick3 implements DialogInterface.OnClickListener{//�޸�

		public void onClick(DialogInterface dialog, int whicButton) {
			alter();
			twoedit = (EditText)dialogView.findViewById(R.id.twoedit);
			threeedit = (EditText)dialogView.findViewById(R.id.threeedit);
			fouredit = (EditText)dialogView.findViewById(R.id.fouredit);
		}
    	
    }
    class DialogInterfaceOnClick4 implements DialogInterface.OnClickListener{//ȡ��

		
		public void onClick(DialogInterface dialog, int whichButton) {
			temp();
		}
    }
    class DialogInterfaceOnClick6 implements DialogInterface.OnClickListener{//�˳�

		
		public void onClick(DialogInterface dialog, int whichButton) {
			MyIDCheck.this.finish();//����˳���ť֮�����finish��������Ӧ�ó���			
		}
    	
    }
    
    class DialogInterfaceOnClick5 implements DialogInterface.OnClickListener{//�޸�����ȷ��

	
		public void onClick(DialogInterface dialog, int whichButton) {
		String two = twoedit.getText().toString();//������
		String three = threeedit.getText().toString();//������
		String four = fouredit.getText().toString();//ȷ��������
			if(two.equals(password01)&&four.equals(three)){
//				editor = shared.edit();  //��ָ��shared�ļ�������������򴴽���
				editor.putString("password", three);
				editor.commit();//�ύ	
				temp();
				Toast.makeText(MyIDCheck.this, "�޸ĳɹ�", Toast.LENGTH_SHORT).show();
			}else if(!two.equals(password01)){
					Toast.makeText(MyIDCheck.this, "�������������", Toast.LENGTH_SHORT).show();
					temp();
			}else if(!four.equals(three)){
					Toast.makeText(MyIDCheck.this, "�������������벻һ��", Toast.LENGTH_SHORT).show();
					temp();
			}
		}
    	
    }
}