//
//  ChangeData.mm
//  TapGirl
//
//  Created by nakano_michiharu on 7/7/13.
//  Copyright (c) 2013 oono. All rights reserved.
//

#import "ChangeData.h"

const static CHANGE_PARAM s_ChangeParams[] = {
	//初期データ
	{
		//切り替えのきっかけとなる残りの距離
		100.0f,
		//切り替えるイメージの番号
		1,
		//初期データは、上記２つのみ有効
		//切り替える時のSE
		0,
		//シェーダー
		FRSH_FADE,
		//切り替え処理前半の時間（単位はミリ秒）
		1000,
		//切り替え処理後半の時間（単位はミリ秒）
		500,
	},
	//このあとは切り替えデータ
	{
		//切り替えのきっかけとなる残りの距離
		90.0f,
		//切り替えるイメージの番号
		2,
		//切り替える時のSE
		1,
		//シェーダー
		FRSH_FADE,
		//切り替え処理前半の時間（単位はミリ秒）
		1000,
		//切り替え処理後半の時間（単位はミリ秒）
		500,
	},
	{
		//切り替えのきっかけとなる残りの距離
		80.0f,
		//切り替えるイメージの番号
		3,
		//切り替える時のSE
		1,
		//シェーダー
		FRSH_FADE,
		//切り替え処理前半の時間（単位はミリ秒）
		1000,
		//切り替え処理後半の時間（単位はミリ秒）
		500,
	},
	{
		//切り替えのきっかけとなる残りの距離
		70.0f,
		//切り替えるイメージの番号
		1,
		//切り替える時のSE
		1,
		//シェーダー
		FRSH_FADE,
		//切り替え処理前半の時間（単位はミリ秒）
		1000,
		//切り替え処理後半の時間（単位はミリ秒）
		500,
	},
	{
		//切り替えのきっかけとなる残りの距離 最後のデータは必ず０で終わる事
		0.0f,
		//切り替えるイメージの番号
		3,
		//切り替える時のSE
		1,
		//シェーダー
		FRSH_FADE,
		//切り替え処理前半の時間（単位はミリ秒）
		1000,
		//切り替え処理後半の時間（単位はミリ秒）
		500,
	},
};

// プライベート
@interface ChangeData() {
	
}
@property (assign, nonatomic) int indexOfData;
@property (assign, readonly, nonatomic) int countData;
@end

@implementation ChangeData
@synthesize indexOfData = _indexOfData;
@synthesize restLength = _restLength;
@synthesize countData = _countData;

- (id)initWithTouchLength:(float)length
{
	self = [super init];
	if (self != nil) {
		_restLength = 1000.0f;
		_countData = sizeof(s_ChangeParams) / sizeof(s_ChangeParams[0]);
		const CHANGE_PARAM *pLast = &s_ChangeParams[self.countData - 1];
		//最後のデータは必ず０で終わる事
		assert(pLast->restLength == 0.0f);
		self.indexOfData = 0;

		float restLength = [self requestAddTouchLength:length];
		while (restLength) {
			restLength = [self requestAddTouchLength:restLength];
		}
	}
	return self;
}

- (void)dealloc
{
	NSLog(@"%s", __PRETTY_FUNCTION__);
	[super dealloc];
}
- (float)requestAddTouchLength:(float)length
{
	//lengthが複数の変更にまたがった時、1つめの変更のみを処理して、残りを返す
	float len = 0.0f;
	if (self.indexOfData < (self.countData - 1)) {
		if (length > 0.0f) {
			const CHANGE_PARAM *pCur = &s_ChangeParams[self.indexOfData];
			float curOffset = self.restLength - pCur->restLength;
			const CHANGE_PARAM *pNext = &s_ChangeParams[self.indexOfData + 1];
			float distance = pCur->restLength - pNext->restLength;
			if (curOffset > distance) {
				_indexOfData++;
				_restLength = pNext->restLength;
				len = curOffset - distance;
			}
		}
	}
	return len;
}

- (const CHANGE_PARAM*)getChangeParam
{
	return &s_ChangeParams[self.indexOfData];
}

- (float)objectiveLength
{
	return s_ChangeParams[0].restLength;
}



@end
