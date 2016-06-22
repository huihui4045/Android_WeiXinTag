package com.zhd.tag;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity {
	FlowLayout mTagLayout, mAddTagLayout;
	EditText mEditText;
	private ArrayList<TagItem> mAddTags = new ArrayList<TagItem>();
	private int MAX_TAG_CNT = 5;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mTagLayout = (FlowLayout) findViewById(R.id.tag_layout);
		mAddTagLayout = (FlowLayout) findViewById(R.id.addtag_layout);
		mEditText = (EditText) findViewById(R.id.add_edit);
		mEditText
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {

					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						if (actionId == EditorInfo.IME_ACTION_DONE) {
							String text = mEditText.getEditableText()
									.toString().trim();
							if (text.length() > 0) {
								if (idxTextTag(text) < 0) {
									doAddText(text, true, -1);
								}
								mEditText.setText("");
							}
							return true;
						}
						return false;
					}
				});
		initLayout();
	}

	protected int idxTextTag(String text) {
		int mTagCnt = mAddTags.size();
		for (int i = 0; i < mTagCnt; i++) {
			TagItem item = mAddTags.get(i);
			if (text.equals(item.tagText)) {
				return i;
			}
		}
		return -1;
	}

	String[] mTextStr = { "有点所", "有的", "控", "件都往左飘", "的感觉", "第一行满了", "往第二行飘", "test", "de", "e" };

	private void initLayout() {
		for (int i = 0; i < mTextStr.length; i++) {
			final int pos = i;
			final TextView text = (TextView) LayoutInflater.from(this).inflate(
					R.layout.tag_text, mTagLayout, false);
			text.setText(mTextStr[i]);
			text.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					text.setActivated(!text.isActivated());
					doResetAddTagsStatus();
					if (text.isActivated()) {
						boolean bResult = doAddText(mTextStr[pos], false, pos);
						text.setActivated(bResult);
					} else {
						doDelText(mTextStr[pos]);
					}
				}
			});
			mTagLayout.addView(text);
		}
	}

	protected void doDelText(String string) {
		int mTagCnt = mAddTags.size();
		mEditText.setVisibility(View.VISIBLE);
		for (int i = 0; i < mTagCnt; i++) {
			TagItem item = mAddTags.get(i);
			if (string.equals(item.tagText)) {
				mAddTagLayout.removeViewAt(i);
				mAddTags.remove(i);
				if (!item.tagCustomEdit) {
					mTagLayout.getChildAt(item.idx).setActivated(false);
				}
				return;
			}
		}
	}

	protected void doAddTagLayout(String str) {
		final TextView text = (TextView) LayoutInflater.from(this).inflate(
				R.layout.addtag_text, mAddTagLayout, false);
		text.setText(str);
		text.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (text.isActivated()) {
					mAddTagLayout.removeView(text);
					mEditText.setVisibility(View.GONE);
				} else {
					text.setActivated(true);
				}
			}
		});
		mAddTagLayout.addView(text, 0);
	}

	private boolean doAddText(final String str, boolean bCustom, int idx) {
		int tempIdx = idxTextTag(str);
		if (tempIdx >= 0) {
			TagItem item = mAddTags.get(tempIdx);
			item.tagCustomEdit = false;
			item.idx = tempIdx;
			
			return true;
		}
		
		int tagCnt = mAddTags.size();
		if (tagCnt == MAX_TAG_CNT) {
			Toast.makeText(MainActivity.this, "最多选择" + MAX_TAG_CNT + "个标签", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		TagItem item = new TagItem();
		item.tagText = str;
		item.tagCustomEdit = bCustom;
		item.idx = idx;
		mAddTags.add(item);
		
		final TextView view = (TextView) LayoutInflater.from(this).inflate(
				R.layout.addtag_text, mAddTagLayout, false);
		item.mView = view;
		view.setText(str);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (view.isActivated()) {
					doDelText(str);
				} else {
					doResetAddTagsStatus();
					view.setText(view.getText() + "x");
					view.setActivated(true);
				}
			}
		});
		mAddTagLayout.addView(view, tagCnt);
		tagCnt++;
		if (tagCnt == MAX_TAG_CNT) {
			mEditText.setVisibility(View.GONE);
		}
		
		return true;
	}

	protected void doResetAddTagsStatus() {
		int cnt = mAddTags.size();
		for (int i = 0; i < cnt; i++) {
			TagItem item = mAddTags.get(i);
			item.mView.setActivated(false);
			item.mView.setText(item.tagText);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
