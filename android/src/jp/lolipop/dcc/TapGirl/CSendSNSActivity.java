package jp.lolipop.dcc.TapGirl;

import jp.lolipop.dcc.lib.CBaseActivity;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;

public class CSendSNSActivity extends Activity  {
	private EditText mEdtText = null;
	private Button mBtnSend = null;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	LinearLayout mainLayout = new LinearLayout(this);
    	LinearLayout.LayoutParams layoutPram = new LinearLayout.LayoutParams(
    			LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
    	mainLayout.setOrientation(LinearLayout.VERTICAL);
    	setContentView(mainLayout);
    	
    	TapGirlActivity mainActivity = TapGirlActivity.getInstance();
    	mEdtText = new EditText(this);
    	int pixels = (int)(100.0f * TapGirlActivity.getDensity());
    	mEdtText.setHeight(pixels);
    	mEdtText.setWidth(mainActivity.getPixelsWidth());
    	mEdtText.setText(TapGirlActivity.getSNSString());
    	
    	mBtnSend = new Button(this);
    	mBtnSend.setText("送信");
//    	pixels = (int)(50.0f * CBaseActivity.getDensity());
//    	mBtnSend.setWidth(pixels);
    	pixels = (int)(25.0f * CBaseActivity.getDensity());
    	mBtnSend.setHeight(pixels);
    	
    	mainLayout.addView(mEdtText);
    	mainLayout.addView(mBtnSend);
    }

}
