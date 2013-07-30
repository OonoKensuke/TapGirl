package jp.lolipop.dcc.TapGirl;

public class CChangeData {
	/**********  データ部分  ***********/
	//初期値。これだけの距離で最終画面にいくものとする
	static final float _INIT_LENGTH =	100.0f;
	//テスト用の切り替え距離
	static final float _TEST_DIGIT =	10.0f;
	final static CChangeParam [] s_ChangeParams = {
		//初期データ
		new CChangeParam(
		//切り替えのきっかけとなる残りの距離
		_INIT_LENGTH,
		//切り替えるイメージの番号
		1,
		//*****初期データは、上記２つのみ有効*****
		//切り替える時のSE
		0,
		//シェーダー
		MyRenderer.FRSH_FADE,
		//切り替え処理前半の時間（単位は秒）
		1.0f,
		//切り替え処理後半の時間（単位は秒）
		0.1f
		),
	//このあとは切り替えデータ
		new CChangeParam(
		//切り替えのきっかけとなる残りの距離
		_INIT_LENGTH - (_TEST_DIGIT),
		//切り替えるイメージの番号
		2,
		//切り替える時のSE
		1,
		//シェーダー
		MyRenderer.FRSH_FADE,
		//切り替え処理前半の時間（単位は秒）
		1.0f,
		//切り替え処理後半の時間（単位は秒）
		0.5f
		),
		new CChangeParam(
		//切り替えのきっかけとなる残りの距離
		_INIT_LENGTH - (_TEST_DIGIT * 2),
		//切り替えるイメージの番号
		3,
		//切り替える時のSE
		2,
		//シェーダー
		MyRenderer.FRSH_FADE,
		//切り替え処理前半の時間（単位は秒）
		1.0f,
		//切り替え処理後半の時間（単位は秒）
		0.5f
		),
		new CChangeParam(
		//切り替えのきっかけとなる残りの距離
		_INIT_LENGTH - (_TEST_DIGIT * 3),
		//切り替えるイメージの番号
		1,
		//切り替える時のSE
		3,
		//シェーダー
		MyRenderer.FRSH_FADE,
		//切り替え処理前半の時間（単位は秒）
		1.0f,
		//切り替え処理後半の時間（単位は秒）
		0.5f
		),
		new CChangeParam(
		//切り替えのきっかけとなる残りの距離 最後のデータは必ず０で終わる事
		0.0f,
		//切り替えるイメージの番号*** ラストはcongratulationsに固定される。この値は使われない
		3,
		//切り替える時のSE
		4,
		//シェーダー
		MyRenderer.FRSH_FADE,
		//切り替え処理前半の時間（単位は秒）
		1.0f,
		//切り替え処理後半の時間（単位は秒）
		0.5f
		),
		
	};
	/**********  コード部分  ***********/
	private float mRestLength = s_ChangeParams[0].restLength;
	public float getRestLength() {
		return mRestLength;
	}
	private boolean mIsChange = false;
	public boolean isChange() {
		return mIsChange;
	}
	public void setIsChange(boolean bChange)
	{
		mIsChange = bChange;
	}
	private int mIndexData = 0;
	
	static CChangeData s_InstanceChangeData = null;
	public static CChangeData getInstance()
	{
		return s_InstanceChangeData;
	}
	//保存用データ：タッチ距離
	public int getTouchLength()
	{
		int iLength = (int)(getObjectiveLength() - getRestLength());
		return iLength;
	}
	//保存用データ：周回数
	private int mLoopNumber = 1;
	
	public int getLoopNumber() {
		return mLoopNumber;
	}
	public void setLoopNumber(int loopNumber) {
		mLoopNumber = loopNumber;
	}
	
	public CChangeData(float length)
	{
		//最後のデータは必ず０で終わる事
		assert(s_ChangeParams[s_ChangeParams.length - 1].restLength == 0.0f);
		float restLength = requestAddTouchLength(length);
		while (restLength > 0.0f)
		{
			restLength = requestAddTouchLength(restLength);
		}
		mIsChange = false;
		s_InstanceChangeData = this;
	}
	
	public float requestAddTouchLength(float length)
	{
		//lengthが複数の変更にまたがった時、1つめの変更のみを処理して、残りを返す
		float len = 0.0f;
		if (mIndexData < (s_ChangeParams.length - 1))
		{
			mRestLength -= length;
			float curOffset = s_ChangeParams[mIndexData].restLength - getRestLength();
			float nextRestLength = s_ChangeParams[mIndexData + 1].restLength;
			float distance = s_ChangeParams[mIndexData].restLength - nextRestLength;
			if (curOffset >= distance)
			{
				mIndexData++;
				mRestLength = nextRestLength;
				len = curOffset - distance;
				mIsChange = true;
			}
		}
		return len;
	}
	
	static public float getObjectiveLength()
	{
		return s_ChangeParams[0].restLength;
	}
	
	public final CChangeParam getChangeParam()
	{
		return s_ChangeParams[mIndexData];
	}
	
	public int getNextIndexOfSE()
	{
		int result = 0;
		if (mIndexData < (s_ChangeParams.length - 1))
		{
			result = s_ChangeParams[mIndexData + 1].indexSE;
		}
		return result;
	}

}
