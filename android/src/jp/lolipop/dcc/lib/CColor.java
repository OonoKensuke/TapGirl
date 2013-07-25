package jp.lolipop.dcc.lib;

public class CColor {
	private float Red = 1.0f;
	public float getRed() {
		return Red;
	}
	public void setRed(float red) {
		Red = red;
	}
	private float Green = 1.0f;
	public float getGreen() {
		return Green;
	}
	public void setGreen(float green) {
		Green = green;
	}
	private float Blue = 1.0f;
	public float getBlue() {
		return Blue;
	}
	public void setBlue(float blue) {
		Blue = blue;
	}
	private float Alpha = 1.0f;
	public float getAlpha() {
		return Alpha;
	}
	public void setAlpha(float alpha) {
		Alpha = alpha;
	}
	
	public CColor(float red, float green, float blue, float alpha)
	{
		setRed(red);
		setGreen(green);
		setBlue(blue);
		setAlpha(alpha);
	}
	
	public void setByInt(int red, int green, int blue, int alpha)
	{
		setRed((float)red / 255.0f);
		setGreen((float)green / 255.0f);
		setBlue((float)blue / 255.0f);
		setAlpha((float)alpha / 255.0f);
	}

}
