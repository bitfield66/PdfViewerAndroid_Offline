package com.artifex.mupdf;

import com.librelio.task.SafeAsyncTask;

import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class MuPDFPageAdapter extends BaseAdapter {
	private static final String TAG = "MuPDFPageAdapter";

	private final Context context;
	private static MuPDFCore core;
	private final MuPDFCore cores[];
	private final SparseArray<PointF> mPageSizes = new SparseArray<PointF>();
	int pages[];

	int position1=0,x=0;
	public MuPDFPageAdapter(Context context, MuPDFCore cores[]) {
		this.context = context;
		this.cores = cores;
		pages=new int[cores.length];
		for(int i=0;i<cores.length;i++)
		{
			
			x=x+cores[i].countPages();
			pages[i]=x;
			Log.e("x", ""+x);
			
		}
	}

	public int getCount() 
	{
		Log.e("size", Integer.toString(pages[cores.length-1]));
		return pages[cores.length-1];
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		Log.d(TAG,"getView");
		final MuPDFPageView pageView;
		
		for(int i=0;i<pages.length;i++)
		{
			if(position<pages[i])
			{
				core=cores[i];
				if(i==0)
					position1=position;
				else
					position1=position-pages[i-1];
				break;
			}
		}
		
		
		
		
			pageView = new MuPDFPageView(context, core, new Point(parent.getWidth(), parent.getHeight()));
		

		PointF pageSize = mPageSizes.get(position1);
		if (pageSize != null) {
			pageView.setPage(position1, pageSize);
		} else {
			pageView.blank(position1);
			SafeAsyncTask<Void,Void,PointF> sizingTask = new SafeAsyncTask<Void,Void,PointF>()
			{
				@Override
				protected PointF doInBackground(Void... arg0) 
				{
					return core.getPageSize(position1);
				}

				@Override
				protected void onPostExecute(PointF result)
				{
					if (isCancelled()) {
						return;
					}
					mPageSizes.put(position1, result);
					if (pageView.getPage() == position1)
						pageView.setPage(position1, result);
				}
			};

			sizingTask.safeExecute();
		}
		return pageView;
	}
}
