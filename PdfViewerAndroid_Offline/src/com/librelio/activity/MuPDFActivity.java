package com.librelio.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.artifex.mupdf.MuPDFCore;
import com.artifex.mupdf.MuPDFPageAdapter;
import com.artifex.mupdf.view.DocumentReaderView;
import com.artifex.mupdf.view.ReaderView;

public class MuPDFActivity extends Activity
{
	private static final String TAG = "MuPDFActivity";
	private MuPDFCore core;
	private MuPDFCore cores[];
	private ReaderView docView;
	private MuPDFPageAdapter mDocViewAdapter;
	String paths[]={ Environment.getExternalStorageDirectory().getAbsolutePath()+ "/and.pdf"};

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		cores= new MuPDFCore[paths.length];
		for(int i=0;i<paths.length;i++)
		{
			
			cores[i] = openFile(paths[i]);
			
		}
		createUI(savedInstanceState);
	}
	private void createUI(Bundle savedInstanceState) 
	{
		docView = new DocumentReaderView(this)
		{
			@Override
			protected void onMoveToChild(View view, int i) 
			{
				super.onMoveToChild(view, i);
			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2,float distanceX, float distanceY)
			{
				return super.onScroll(e1, e2, distanceX, distanceY);
			}

			@Override
			protected void onContextMenuClick() 
			{

			}

			@Override
			protected void onBuy(String path) 
			{
			
			}

		};
		
		mDocViewAdapter = new MuPDFPageAdapter(this, cores);
		docView.setAdapter(mDocViewAdapter);
		RelativeLayout layout = new RelativeLayout(this);
		layout.addView(docView);
		layout.setBackgroundColor(Color.BLACK);
		setContentView(layout);
	}
	

	private MuPDFCore openFile(String path) 
	{
		try
		{
			core = new MuPDFCore(path);
		} catch (Exception e) {
			Log.e(TAG, "get core failed", e);
			return null;
		}
		return core;
	}
}