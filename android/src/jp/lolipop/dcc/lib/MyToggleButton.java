package jp.lolipop.dcc.lib;

import android.content.Context;
import android.widget.ToggleButton;

public class MyToggleButton extends ToggleButton {
	private int mResIdOn = 0;

	public int getResIdOn() {
		return mResIdOn;
	}

	public void setResIdOn(int resIdOn) {
		mResIdOn = resIdOn;
	}
	
	private int mResIdOff = 0;

	public int getResIdOff() {
		return mResIdOff;
	}

	public void setResIdOff(int resIdOff) {
		mResIdOff = resIdOff;
	}

	public MyToggleButton(Context context) {
		super(context);
	}
	
	public void onCheckedChanged(boolean isChecked)
	{
		if (isChecked) {
			setBackgroundResource(mResIdOn);
		}
		else {
			setBackgroundResource(mResIdOff);
		}
	}

}
