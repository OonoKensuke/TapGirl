package jp.lolipop.dcc.TapGirl;

import jp.lolipop.dcc.lib.MyGLUtil;

import jp.lolipop.dcc.lib.IGLRenderer;
import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class TapGirlActivity extends Activity {
	private GLSurfaceView mGLSurfaceView = null;
	private MyRenderer mRenderer = null;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRenderer = new MyRenderer();
        mGLSurfaceView = MyGLUtil.initGLES20(this, mRenderer);
        setContentView(mGLSurfaceView);
    }
}