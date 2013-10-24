package com.artifex.mupdf.view;

import android.content.Context;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.artifex.mupdf.PageView;

public abstract class DocumentReaderView extends ReaderView {
	private static final String TAG = "DocumentReaderView";
	private static int tapPageMargin = 70;
	private boolean showButtonsDisabled;

	public DocumentReaderView(Context context) {
		super(context);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		tapPageMargin = (int) (getWidth() * .1);
	}
	@Override
	public boolean onScaleBegin(ScaleGestureDetector d) {
		// Disabled showing the buttons until next touch.
		// Not sure why this is needed, but without it
		// pinch zoom can make the buttons appear
		showButtonsDisabled = true;
		return super.onScaleBegin(d);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
			showButtonsDisabled = false;
		}
		return super.onTouchEvent(event);
	}

	public boolean isShowButtonsDisabled() {
		return showButtonsDisabled;
	}

	abstract protected void onContextMenuClick();
	abstract protected void onBuy(String path);

	@Override
	protected void onMoveToChild(View view, int i) {
	}

	@Override
	protected void onSettle(View v) {
		((PageView)v).addHq(true);
	}

	@Override
	protected void onUnsettle(View v) {
		((PageView)v).removeHq();
	}

	@Override
	protected void onNotInUse(View v) {
		((PageView)v).releaseResources();
	}
}
