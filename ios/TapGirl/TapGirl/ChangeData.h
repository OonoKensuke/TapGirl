//
//  ChangeData.h
//  TapGirl
//
//  Created by nakano_michiharu on 7/7/13.
//  Copyright (c) 2013 oono. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "MyGLShader.h"
typedef struct {
	//切り替えのきっかけとなる残りの距離
	float restLength;
	//切り替えるイメージの番号
	int indexImage;
	//切り替える時のSE
	int indexSE;
	//シェーダー
	FRAG_SHADER shader;
	//切り替え処理前半の時間（単位はミリ秒）
	int delayIn;
	//切り替え処理後半の時間（単位はミリ秒）
	int delayOut;
}CHANGE_PARAM;

@interface ChangeData : NSObject
{
	
}
@property (readonly, nonatomic) float restLength;
@property (readonly, nonatomic) float objectiveLength;
@property (assign, nonatomic, setter = setIsChange:) BOOL isChange;

- (id)initWithTouchLength:(float)length;
- (float)requestAddTouchLength:(float)length;
- (const CHANGE_PARAM*)getChangeParam;

@end
