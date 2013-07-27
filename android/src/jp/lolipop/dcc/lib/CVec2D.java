package jp.lolipop.dcc.lib;

import android.view.MotionEvent;

public class CVec2D
{
	float mX;
	public float getX() {
		return mX;
	}
	public void setX(float x) {
		mX = x;
	}
	float mY;
	public float getY() {
		return mY;
	}
	public void setY(float y) {
		mY = y;
	}
	
	public void init()
	{
		mX = mY = -1.0f;
	}
	
	public void setParam(CVec2D pos)
	{
		mX = pos.getX();
		mY = pos.getY();
	}
	
	public void setParam(MotionEvent event)
	{
		mX = event.getX();
		mY = event.getY();
	}
}
