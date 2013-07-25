package jp.lolipop.dcc.TapGirl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import jp.lolipop.dcc.lib.AndroidFileIO;
import jp.lolipop.dcc.lib.MyGLUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import jp.lolipop.dcc.lib.IGLRenderer;

public class MyRenderer extends IGLRenderer {
	
	  // 頂点シェーダのプログラム
	  private static String VSHADER_SOURCE =null;

	  // フラグメントシェーダのプログラム
	  private static String FSHADER_SOURCE = null;
	
	@Override
	public void onDrawFrame(GL10 gl) {
	    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);          // 描画領域をクリアする
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		Log.v("info", "MyRenderer#onSurfaceChanged width " + width + " height " + height);
		GLES20.glViewport(0, 0, width, height);
		
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		Log.v("info", "MyRenderer#onSurfaceCreated ");
		GLES20.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
        {
        	VSHADER_SOURCE = MyGLShader.loadTextAsset("vshader.txt", TapGirlActivity.getInstance());
        	FSHADER_SOURCE = MyGLShader. loadTextAsset("fshader_normal.txt", TapGirlActivity.getInstance());
        	Log.v("info", FSHADER_SOURCE);
        }
		int program = MyGLUtil.initShaders(VSHADER_SOURCE, FSHADER_SOURCE);
		initVertexBuffers(program);
		initTextures(program);
	
	}
	  private void initTextures(int program) {
			// 画像ファイルを読み込む
			Bitmap bmp = BitmapFactory.decodeResource(TapGirlActivity.getInstance().getResources(), R.drawable.sky);
			if (bmp == null) {
			  throw new RuntimeException("画像の読み込みに失敗");
			}

		    // テクスチャオブジェクトを作成する
		    int[] textures = new int[1];
		    GLES20.glGenTextures(1, textures, 0);

		    // u_Samplerの格納場所を取得する
		    int u_texture = GLES20.glGetUniformLocation(program, "u_texture");
		    if (u_texture == -1) {
		      throw new RuntimeException("u_textureの格納場所の取得に失敗");
		    }

		    // テクスチャユニット0を有効にする
		    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		    // テクスチャオブジェクトをターゲットにバインドする
		    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);

		    // テクスチャパラメータを設定する
		    GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
		    // テクスチャ画像を書き込む
		    GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);

		    // サンプラにテクスチャユニット0を設定する
		    GLES20.glUniform1i(u_texture, 0);
		  }
	  private int initVertexBuffers(int program) {
		    FloatBuffer verticesTexCoords = MyGLUtil.makeFloatBuffer(new float[] {
		      // 点の座標，テクスチャ座標
		      -1.0f,  1.0f,   0.0f, 0.0f, // 1番目の頂点座標とテクスチャ座標
		      -1.0f, -1.0f,   0.0f, 1.0f, // 2番目の頂点座標とテクスチャ座標
		       1.0f,  1.0f,   1.0f, 0.0f, // 3番目の頂点座標とテクスチャ座標
		       1.0f, -1.0f,   1.0f, 1.0f, // 4番目の頂点座標とテクスチャ座標
		    });
		    final int n = 4; // 頂点数
		    final int FSIZE = Float.SIZE / Byte.SIZE; // floatのバイト数

		    // バッファオブジェクトを作成する
		    int[] vertexTexCoord = new int[1];
		    GLES20.glGenBuffers(1, vertexTexCoord, 0);

		    // 頂点の座標とテクスチャ座標をバッファオブジェクトに書き込む
		    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexTexCoord[0]);
		    GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, FSIZE * verticesTexCoords.limit(), verticesTexCoords, GLES20.GL_STATIC_DRAW);

		    // a_Positionの格納場所を取得し、バッファオブジェクトを割り当て、有効化する
		    int a_Position = GLES20.glGetAttribLocation(program, "a_Position");
		    if (a_Position == -1) {
		      throw new RuntimeException("a_Positionの格納場所の取得に失敗");
		    }
		    GLES20.glVertexAttribPointer(a_Position, 2, GLES20.GL_FLOAT, false, FSIZE * 4, 0);
		    GLES20.glEnableVertexAttribArray(a_Position);  // バッファオブジェクトの割り当ての有効化

		    // a_TexCoordの格納場所を取得し、バッファオブジェクトを割り当て、有効化する
		    int a_TexCoord = GLES20.glGetAttribLocation(program, "a_TexCoord");
		    if (a_TexCoord == -1) {
		      throw new RuntimeException("a_TexCoordの格納場所の取得に失敗");
		    }
		    GLES20.glVertexAttribPointer(a_TexCoord, 2, GLES20.GL_FLOAT, false, FSIZE * 4, FSIZE * 2);
		    GLES20.glEnableVertexAttribArray(a_TexCoord);  // バッファオブジェクトの割り当ての有効化

		    return n;
		  }

}
