//
//  IGL.m
//  TapGirl
//
//  Created by nakano_michiharu on 7/4/13.
//  Copyright (c) 2013 oono. All rights reserved.
//

#import "IGL.h"
#import "CMatrix.h"


//プライベート
@interface IGL() {
	//ビュー行列(4x4)
	float viewMatrix[16];
}
//初期化
-(id)init;

//ビューサイズ
@property(nonatomic) CGSize viewSize;
@end

//グローバル変数
static IGL* g_instance = nil;



@implementation IGL
@synthesize  viewSize = _viewSize;

//インスタンス取得（シングルトン）
+(IGL*)sharedGL {
	if(g_instance == nil){//マルチスレッドでこのメソッドに入ってくることは考量していない
		g_instance = [[IGL alloc] init];
	}
	return g_instance;
}
//初期化
-(id)init {
	self = [super init];
	if(self){
		
	}
	return self;
}


//ビューサイズ設定
-(void)setViewSize:(CGSize)size {
	_viewSize = size;
	CMatrix m;
	m.addScale(CVec2D(2.f/size.width, -2.f/size.height));
	m.addTranslation(CVec2D(-1.f, 1.f));
	m.getMat4(viewMatrix);
}

//ビュー行列取得
-(const float*)viewMatrix {
	return viewMatrix;
}


@end
