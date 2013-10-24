package com.artifex.mupdf;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.widget.EditText;
import android.widget.FrameLayout;

abstract class PassClickResultVisitor {
	public abstract void visitText(PassClickResultText result);
	public abstract void visitChoice(PassClickResultChoice result);
}

class PassClickResult {
	public final boolean changed;

	public PassClickResult(boolean _changed) {
		changed = _changed;
	}

	public void acceptVisitor(PassClickResultVisitor visitor) {
	}
}

class PassClickResultText extends PassClickResult {
	public final String text;

	public PassClickResultText(boolean _changed, String _text) {
		super(_changed);
		text = _text;
	}

	public void acceptVisitor(PassClickResultVisitor visitor) {
		visitor.visitText(this);
	}
}

class PassClickResultChoice extends PassClickResult {
	public final String [] options;
	public final String [] selected;

	public PassClickResultChoice(boolean _changed, String [] _options, String [] _selected) {
		super(_changed);
		options = _options;
		selected = _selected;
	}

	public void acceptVisitor(PassClickResultVisitor visitor) {
		visitor.visitChoice(this);
	}
}

public class MuPDFPageView extends PageView {
	private static final String TAG = "MuPDFPageView";
	public static final String PATH_KEY = "path";
	public static final String LINK_URI_KEY = "link_uri";

	private final MuPDFCore mCore;
	private AsyncTask<Void,Void,PassClickResult> mPassClick;
	private RectF mWidgetAreas[];
	private AsyncTask<Void,Void,RectF[]> mLoadWidgetAreas;
//	private AlertDialog.Builder mTextEntryBuilder;
//	private AlertDialog.Builder mChoiceEntryBuilder;
//	private AlertDialog mTextEntry;
	private EditText mEditText;
	private AsyncTask<String,Void,Boolean> mSetWidgetText;
	private AsyncTask<String,Void,Void> mSetWidgetChoice;
	private Runnable changeReporter;
	
	// Wind customization
	private HashMap<String, FrameLayout> mediaHolders = new HashMap<String, FrameLayout>();
	private ArrayList<String> runningLinks;

	public MuPDFPageView(Context c, MuPDFCore muPdfCore, Point parentSize) {
		super(c, parentSize);
		this.mCore = muPdfCore;
		runningLinks = new ArrayList<String>();
	}

//	public LinkInfo hitLink(float x, float y) {
//		// Since link highlighting was implemented, the super class
//		// PageView has had sufficient information to be able to
//		// perform this method directly. Making that change would
//		// make MuPDFCore.hitLinkPage superfluous.
//		float scale = mSourceScale*(float)getWidth()/(float)mSize.x;
//		float docRelX = (x - getLeft())/scale;
//		float docRelY = (y - getTop())/scale;
//
//		for (LinkInfo l: mLinks)
//			if (l.rect.contains(docRelX, docRelY))
//				return l;
//
//		return null;
//	}
	@Override
	public void setPage(int page, PointF size) {
		super.setPage(page, size);

	}
//
//	@Override
//	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//		for (Map.Entry<String, FrameLayout> entry : mediaHolders.entrySet()) {
//			MediaHolder mLinkHolder = (MediaHolder) entry.getValue();
//			LinkInfoExternal currentLink = mLinkHolder.getLinkInfo();
//			float scale = mSourceScale * (float) getWidth() / (float) mSize.x;
//
//			int width = (int) ((currentLink.rect.right - currentLink.rect.left) * scale);
//			int height = (int) ((currentLink.rect.bottom - currentLink.rect.top) * scale);
//			// mLinkHolder.measure(widthMeasureSpec, heightMeasureSpec);
//			mLinkHolder.measure(View.MeasureSpec.EXACTLY | width,
//					View.MeasureSpec.EXACTLY | height);
//
//		}
//
//	}
//
//	@Override
//	protected void onLayout(boolean changed, int left, int top, int right,
//			int bottom) {
//		super.onLayout(changed, left, top, right, bottom);
//		float scale = mSourceScale * (float) getWidth() / (float) mSize.x;
//
//		for (Map.Entry<String, FrameLayout> entry : mediaHolders.entrySet()) {
//			MediaHolder mLinkHolder = (MediaHolder) entry.getValue();
//			LinkInfo currentLink = mLinkHolder.getLinkInfo();
//			mLinkHolder.layout((int) (currentLink.rect.left * scale),
//					(int) (currentLink.rect.top * scale),
//					(int) (currentLink.rect.right * scale),
//					(int) (currentLink.rect.bottom * scale));
//		}
//	}

//	@Override
//	public void blank(int page) {
//		super.blank(page);
//		Iterator<Entry<String, FrameLayout>> i = mediaHolders.entrySet()
//				.iterator();
//		while (i.hasNext()) {
//			Entry<String, FrameLayout> entry = i.next();
//			MediaHolder mLinkHolder = (MediaHolder) entry.getValue();
//			mLinkHolder.recycle();
//			i.remove();
//			removeView(mLinkHolder);
//			mLinkHolder = null;
//		}
//	}

//	@Override
//	public void removeHq() {
//		super.removeHq();
//		Iterator<Entry<String, FrameLayout>> i = mediaHolders.entrySet()
//				.iterator();
//		while (i.hasNext()) {
//			Entry<String, FrameLayout> entry = i.next();
////			MediaHolder mLinkHolder = (MediaHolder) entry.getValue();
////			i.remove();
////			removeView(mLinkHolder);
////			mLinkHolder = null;
//		}
//	}
//
//	@Override
//	public void addHq(boolean b) {
//		super.addHq(b);
//		for (Map.Entry<String, FrameLayout> entry : mediaHolders.entrySet()) {
////			MediaHolder mLinkHolder = (MediaHolder) entry.getValue();
//
////			mLinkHolder.bringToFront();
//		}
//	}

//	public void addMediaHolder(MediaHolder h, String uriString) {
//		this.mediaHolders.put(uriString, h);
//	}

	public void cleanRunningLinkList() {
		runningLinks.clear();
	}

	@Override
	protected Bitmap drawPage(int sizeX, int sizeY, int patchX,
			int patchY, int patchWidth, int patchHeight) {
		return mCore.drawPage(mPageNumber, sizeX, sizeY, patchX, patchY, patchWidth, patchHeight);
	}

	@Override
	protected Bitmap updatePage(BitmapHolder h, int sizeX, int sizeY,
			int patchX, int patchY, int patchWidth, int patchHeight) {
		return mCore.updatePage(h, mPageNumber, sizeX, sizeY, patchX, patchY, patchWidth, patchHeight);
	}
}
