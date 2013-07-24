package jp.lolipop.dcc.TapGirl;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import jp.lolipop.dcc.lib.MyGLUtil;

import android.opengl.GLES20;
import android.util.Log;

import jp.lolipop.dcc.lib.IGLRenderer;

public class MyRenderer extends IGLRenderer {
	// 頂点シェーダのプログラム
	private static final String VSHADER_SOURCE =
		"attribute vec4 a_Position;\n" + // attribute変数
		"void main() {\n" +
		"  gl_Position = a_Position;\n" + // 点の座標を設定する
		"  gl_PointSize = 20.0;\n" +      // 点のサイズを設定する
		"}\n";

	// フラグメントシェーダのプログラム
	private static final String FSHADER_SOURCE =
		"void main() {\n" +
		"    gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);\n" + // 点の色を設定する
		"}\n";
	
	private boolean mTest = true;
	private MyGLShader mShader = null;
	@Override
	public void onDrawFrame(GL10 gl) {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
		GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		Log.v("info", "MyRenderer#onSurfaceChanged width " + width + " height " + height);
		GLES20.glViewport(0, 0, width, height);
		
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		Log.v("info", "MyRenderer#onSurfaceCreated ");
		if (mTest) {
			// シェーダを初期化する
			int program = MyGLUtil.initShaders(VSHADER_SOURCE, FSHADER_SOURCE);

			// attribute変数の格納場所を取得する
			int a_Position = GLES20.glGetAttribLocation(program, "a_Position");
			if (a_Position == -1) {
				throw new RuntimeException("attribute変数の格納場所の取得に失敗");
			}
			// attribute変数に点の座標を代入する
			//GLES20.glVertexAttrib3f(a_Position, 0.0f, 0.0f, 0.0f);
			// 表示されない場合は、
			MyGLUtil.glVertexAttrib3f(a_Position, 0.0f, 0.0f, 0.0f);

			// 画面をクリアする色を設定する
			GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		}
		else {
			GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
			mShader = new MyGLShader();
			mShader.build(TapGirlActivity.getInstance());
		}
		
	}

}
