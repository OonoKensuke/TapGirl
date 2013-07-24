package jp.lolipop.dcc.TapGirl;

import jp.lolipop.dcc.lib.MyGLUtil;

import jp.lolipop.dcc.lib.IGLRenderer;
import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;

public class TapGirlActivity extends Activity {
	private GLSurfaceView mGLSurfaceView = null;
	private MyRenderer mRenderer = null;
	private static TapGirlActivity s_Instance = null;
	
	public static TapGirlActivity getInstance()
	{
		return s_Instance;
	}
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        assert(s_Instance == null);
        s_Instance = this;
        mRenderer = new MyRenderer();
        mGLSurfaceView = MyGLUtil.initGLES20(this, mRenderer);
        mGLSurfaceView.setDebugFlags(GLSurfaceView.DEBUG_CHECK_GL_ERROR	 | 	GLSurfaceView.DEBUG_LOG_GL_CALLS);
        setContentView(mGLSurfaceView);
    }
}