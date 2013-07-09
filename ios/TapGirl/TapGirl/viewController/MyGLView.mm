//
//  MyGLView.mm
//  TapGirl
//
//  Created by nakano_michiharu on 7/4/13.
//  Copyright (c) 2013 oono. All rights reserved.
//

#import <math.h>
#import "MyGLView.h"
#import "MyGLViewController.h"
#import "IGLImage.h"
#import "CColor.h"
#import "CVec2D.h"
#import "IGLKit.h"
#import "ChangeData.h"
#import <AudioToolbox/AudioToolbox.h>
#import <AVFoundation/AVFoundation.h>

#define _GR_WIDTH 603
#define _GR_HEIGHT 820
//iphone(非RetinaのPPI　タッチ座標はRetinaでもこの値)
#define _PPI	163
//１インチ＝2.54cm
#define _CM_PER_INCH	2.54
//1cm当たりのピクセル数
#define _PIX_PER_CM	((float)_PPI / _CM_PER_INCH)
//スコアに換算する最低距離(cm)
#define _MIN_LEN	0.3
//浮動小数点で誤差が生じる可能性があるとき、十分小さな値として使う
#define _NEAR0	(float)(1.0f / 65536.0f)

typedef struct {
	// 位置
	CVec2D positions[4];
	// テクスチャ座用
	CVec2D texCoords[4];
}_PRIMITIVE;

typedef enum  {
	//初期化
	STEP_INIT = 0,
	//通常表示
	STEP_NORMAL,
	//画像変更前半
	STEP_CHANGE_IN,
	//画像変更後半
	STEP_CHANGE_OUT,
	//音声終了待ち
	STEP_WAIT_SE_END,
}_STEP;

typedef struct {
	const CHANGE_PARAM *pChangeParam;
	//変更処理が開始される時刻（実際には１フレーム前）
	double timeBegin;
}_CHANGE_WORK;


// プライベート
@interface MyGLView() {
	//タッチ距離を測るための座標
	CGPoint _posTouch;
	
	_PRIMITIVE _primCurrent;
	_PRIMITIVE _primNext;
	//切り替え処理のワークエリア
	_CHANGE_WORK _changeWork;
	//切り替えのSEを鳴らすプレイヤー
	AVAudioPlayer* _player;
}
// メインテクスチャ
//@property(strong, nonatomic) IGLImage* picture;
@property(assign, nonatomic) IGLImage* textureCurrent;
@property(assign, nonatomic) IGLImage* textureNext;
// シェーダー
//@property(strong, nonatomic) MyGLShader* shader;
//
@property(strong, nonatomic) NSMutableArray* arrayShader;

@property(assign, nonatomic) ChangeData* changeData;

@property(assign, nonatomic) _STEP step;
@end


@implementation MyGLView
//@synthesize shader = _shader;
//@synthesize picture = _picture;
@synthesize textureCurrent = _textureCurrent;
@synthesize textureNext = _textureNext;
@synthesize touchLength = _touchLength;
@synthesize arrayShader = _arrayShader;
@synthesize changeData = _changeData;
@synthesize step = _step;

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
    }
    return self;
}


// 初期化
- (id)initWithCoder:(NSCoder*)coder {
	NSLog(@"%s", __PRETTY_FUNCTION__);
    self = [super initWithCoder:coder];
    if (self) {
		//self.shader = [[MyGLShader alloc] init];
		self.arrayShader = [[NSMutableArray alloc] init];
		{
			for (int index = 0; index < (int)FRSH_MAX; index++) {
				MyGLShader *shader = [[MyGLShader alloc] init];
				[self.arrayShader addObject:shader];
			}
		}
		self.touchLength = 0.0f;
		//NSLog(@"pix per cm = %f", _PIX_PER_CM);
		self.changeData = [[ChangeData alloc] initWithTouchLength:self.touchLength];
		const CHANGE_PARAM *pChangeParam = [self.changeData getChangeParam];
		[self loadTextureFromIndex:pChangeParam->indexImage toCurrent:true];
		_player = nil;
		self.step = STEP_INIT;
    }
    return self;
}

- (void)dealloc
{
	[self.textureCurrent release];
	[self.textureNext release];
	[self.changeData release];
	[self.arrayShader release];
	[super dealloc];
}

- (BOOL)loadTextureFromIndex:(int)index toCurrent:(BOOL)bLoadCurrent
{
	BOOL result = false;
	@try {
		NSString* nameFile = [NSString stringWithFormat:@"image%02d.jpg", index];
		debug_NSLog(@"%@ をテクスチャとして読み込みます", nameFile);
		NSString* strPath = [[NSBundle mainBundle] pathForResource:nameFile ofType:nil];
		if ([[NSFileManager defaultManager] fileExistsAtPath:strPath]) {
			UIImage *img = [UIImage imageNamed:nameFile];
			if (bLoadCurrent) {
				self.textureCurrent = [[IGLImage alloc] initWithUIImage:img];
			}
			else {
				self.textureNext = [[IGLImage alloc] initWithUIImage:img];
			}
			result = true;
		}
	}
	@catch (NSException *exception) {
		debug_NSLog(@"%@", [exception reason]);
		assert(false);
	}
	return result;
}
#pragma mark -public Method
-(NSString*)buildShader
{
	NSString *error = nil;
	for (int index = 0; index < (int)FRSH_MAX; index++) {
		MyGLShader* shader = [self.arrayShader objectAtIndex:(FRAG_SHADER)index];
		error = [shader build];
		if (error != nil) {
			break;
		}
	}
	return error;
}

-(BOOL)loadShaders
{
	BOOL result = false;
	@try {
		for (int index = 0; index < (int)FRSH_MAX; index++) {
			MyGLShader* shader = [self.arrayShader objectAtIndex:index];
			BOOL tmpResult = [shader loadFragShader:(FRAG_SHADER)index];
			assert(tmpResult);
		}
		result = true;
		
	}
	@catch (NSException *exception) {
	}
	return result;
}

#pragma mark -draw
// 描画
-(void)drawMain {
	//NSLog(@"%s", __PRETTY_FUNCTION__);
	// 色の計算
	double time = [NSDate timeIntervalSinceReferenceDate];
	float alpha = (float)((sin(M_PI * time / 2.0) + 1.0) / 2.0);
	CColor color;
	color.setHue((float)fmod(time / 8.0, 1.0) * 360.f, 1.f);

	switch (self.step) {
		case STEP_INIT:
		{
			[self drawTextureCurrent:true withAlpha:alpha withColor:color withShader:FRSH_NORMAL];
			self.step = STEP_NORMAL;
		}
			break;
		case STEP_NORMAL:
		{
			float processedLenght = self.changeData.objectiveLength - self.changeData.restLength;
			float restTemp = self.touchLength - processedLenght;
			//いきなりSEを読むと音が途切れる可能性があるので、前もって読んでおく
			if (_player == nil) {
				int indexSE = [self.changeData getNextIndexOfSE];
				if (indexSE > 0) {
					[self loadAudioByIndex:indexSE];
				}
			}
			if (restTemp > _NEAR0) {
				[self.changeData requestAddTouchLength:restTemp];
				if (self.changeData.isChange) {
					self.changeData.isChange = false;
					memset(&_changeWork, 0, sizeof(_changeWork));
					//アルファなどの変更に使う基準となる現在時刻
					_changeWork.timeBegin = time;
					//次のテクスチャを読み込む
					_changeWork.pChangeParam = [self.changeData getChangeParam];
					[self loadTextureFromIndex:_changeWork.pChangeParam->indexImage
									 toCurrent:false];
					self.step = STEP_CHANGE_IN;
					//
					_player.numberOfLoops = 0;
					if (![MyGLViewController getInstance].btnToggleSound.highlighted) {
						[_player play];
					}
				}
			}
			[self drawTextureCurrent:true withAlpha:alpha withColor:color withShader:FRSH_NORMAL];
		}
			break;
		case STEP_CHANGE_IN:
		{
			//切り替える時間なのかのチェック
			BOOL bChangeStep = false;
			double timeDelta = time - _changeWork.timeBegin;
			if (timeDelta >= _changeWork.pChangeParam->delayIn) {
				bChangeStep = true;
			}
			float theta = (M_PI_2 * timeDelta) / _changeWork.pChangeParam->delayIn;
			alpha = sinf(theta);
			if (bChangeStep) {
				//切り替え時点の値に修正
				alpha = 1.0f;
			}
			color.setByInt(0, 0, 0, 255);
			//debug_NSLog(@"time : %1.3f, theta : %1.4f, alpha = %1.3f", timeDelta, theta, alpha);
			[self drawTextureCurrent:true
						   withAlpha:alpha
						   withColor:color
						  withShader:_changeWork.pChangeParam->shader];
			//debug_NSLog(@"%f", timeDelta);
			if (bChangeStep) {
				self.step = STEP_CHANGE_OUT;
				_changeWork.timeBegin = time;
			}
			
		}
			break;
		case STEP_CHANGE_OUT:
		{
			//切り替える時間なのかのチェック
			BOOL bChangeStep = false;
			double timeDelta = time - _changeWork.timeBegin;
			if (timeDelta >= _changeWork.pChangeParam->delayOut) {
				bChangeStep = true;
			}
			
			float theta = (M_PI_2 * timeDelta) / _changeWork.pChangeParam->delayOut;
			alpha = sinf(theta + M_PI_2);
			if (bChangeStep) {
				//切り替え時点の値に修正
				alpha = 0.0f;
			}
			color.setByInt(0, 0, 0, 255);
			//debug_NSLog(@"time : %1.3f, theta : %1.4f, alpha = %1.3f", timeDelta, theta, alpha);
			[self drawTextureCurrent:false
						   withAlpha:alpha
						   withColor:color
						withShader:_changeWork.pChangeParam->shader];
			
			if (bChangeStep) {
				self.step = STEP_WAIT_SE_END;
				_changeWork.timeBegin = time;
				debug_NSLog(@"rc:%d", [self.textureCurrent retainCount]);
				[self.textureCurrent release];
				self.textureCurrent = self.textureNext;
				self.textureNext = nil;
			}
			
		}
			break;
			//音声終了待ち
		case STEP_WAIT_SE_END:
		{
			//double pathTime = time - _changeWork.timeBegin;
			//debug_NSLog(@"wait se end %f", pathTime);
			if (!_player.isPlaying) {
				[_player release];
				_player = nil;
				self.step = STEP_NORMAL;
			}
			[self drawTextureCurrent:true withAlpha:alpha withColor:color withShader:FRSH_NORMAL];
		}
			break;
			
		default:
			break;
	}
	[self drawFps];
}

- (void)drawTextureCurrent:(BOOL)isCurrent withAlpha:(float)alpha withColor:(CColor&)color withShader:(FRAG_SHADER)shaderType
{
	_PRIMITIVE *prim = nil;
	IGLImage *texture = nil;
	if (isCurrent) {
		prim = &_primCurrent;
		texture = self.textureCurrent;
	}
	else {
		prim = &_primNext;
		texture = self.textureNext;
	}
	float drawWidth = _GR_WIDTH;
	float drawHeight = _GR_HEIGHT;
	// テクスチャ座標
	float u = (float)_GR_WIDTH / (float)texture.width;
	float v = (float)_GR_HEIGHT / (float)texture.height;
	
	BOOL bResize = false;
	if (drawWidth > self.width) {
		bResize = true;
	}
	else if (drawHeight > self.height) {
		bResize = true;
	}
	if (bResize) {
		float ratioW = (float)self.width / drawWidth;
		float ratioH = (float)self.height / drawHeight;
		if (ratioW > ratioH) {
			drawWidth *= ratioH;
			drawHeight *= ratioH;
		}
		else {
			drawWidth *= ratioW;
			drawHeight *= ratioW;
		}
	}
	assert(prim != nil);
	prim->positions[0] = CVec2D(0, 0);
	prim->positions[1] = CVec2D(drawWidth, 0);
	prim->positions[2] = CVec2D(0, drawHeight);
	prim->positions[3] = CVec2D(drawWidth, drawHeight);
	prim->texCoords[0] = CVec2D(0,0);
	prim->texCoords[1] = CVec2D(u,0);
	prim->texCoords[2] = CVec2D(0,v);
	prim->texCoords[3] = CVec2D(u,v);
	MyGLShader* shader = [self.arrayShader objectAtIndex:(int)shaderType];
	[shader drawArraysMy:GL_TRIANGLE_STRIP positions:(float*)(prim->positions)
				 glImage:texture texCoords:(float*)(prim->texCoords) count:4
				   color:color alpha:alpha];
	
}
#pragma mark -Audio
- (void)loadAudioByIndex:(int)indexSE
{
	NSString *fileName = [NSString stringWithFormat:@"%02d", indexSE];
	NSString *path = [[NSBundle mainBundle] pathForResource:fileName ofType:@"wav"];
	NSURL *url = [NSURL fileURLWithPath:path];
	_player = [[AVAudioPlayer alloc] initWithContentsOfURL:url error:nil];
	[_player prepareToPlay];
}

#pragma mark -Game Method
- (void)clearPosTouch
{
	_posTouch.x = _posTouch.y = -1.0f;
}

- (void)reqUpdateTouchPos:(CGPoint)posUpdate
{
	if ((_posTouch.x < 0.0f) && (_posTouch.y < 0.0f)) {
		_posTouch = posUpdate;
	}
	else {
		float curLen = 0.0f;
		{
			float curLenX = _posTouch.x - posUpdate.x;
			curLenX *= curLenX;
			float curLenY = _posTouch.y - posUpdate.y;
			curLenY *= curLenY;
			curLen = sqrtf(curLenX + curLenY);
			curLen /= _PIX_PER_CM;
		}
		if (_MIN_LEN < curLen) {
			self.touchLength += curLen;
			self.touchLength = MIN(self.touchLength, self.changeData.objectiveLength);
			_posTouch = posUpdate;
			int iLength = self.changeData.objectiveLength - (int)(self.touchLength);
			NSString* strTouch = [NSString stringWithFormat:@"%i", iLength];
			[MyGLViewController getInstance].countLabel.text = strTouch;
		}
		else {
			//debug_NSLog(@"距離が短すぎる:%f", curLen);
		}
	}
}
#pragma mark -touch events
- (void) touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
	debug_NSLog(@"%s", __PRETTY_FUNCTION__);
	@try {
		int count = 0;
		{
			for (UITouch *touch in touches)
			{
				count++;
				_posTouch = [touch locationInView:self];
				{
					//NSLog(@"x:%f, y:%f", _posTouch.x, _posTouch.y);
				}
				
				break;
			}
			//NSLog(@"touch count:%d", count);
		}
	}
	@catch (NSException * e) {
		NSLog(@"%s Caught %@: %@", __FUNCTION__,[e name], [e reason]);
	}
}

- (void)touchesMoved:(NSSet *)touches withEvent:(UIEvent *)event
{
	@try {
		int count = 0;
		{
			for (UITouch *touch in touches)
			{
				count++;
				CGPoint clickPos = [touch locationInView:self];
				{
					//NSLog(@"x:%f, y:%f", clickPos.x, clickPos.y);
					//NSLog(@"change to x:%f, y:%f", clickPos.x, clickPos.y);
				}
				[self reqUpdateTouchPos:clickPos];
				break;
			}
			//NSLog(@"touch count:%d", count);
		}
	}
	@catch (NSException * e) {
		NSLog(@"%s Caught %@: %@", __FUNCTION__,[e name], [e reason]);
	}
}
- (void) touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event
{
	debug_NSLog(@"%s", __PRETTY_FUNCTION__);
	@try {
		int count = 0;
		{
			for (UITouch *touch in touches)
			{
				count++;
				CGPoint clickPos = [touch locationInView:self];
				{
					//NSLog(@"x:%f, y:%f", clickPos.x, clickPos.y);
				}
				[self reqUpdateTouchPos:clickPos];
				[self clearPosTouch];
				break;
			}
			//NSLog(@"touch count:%d", count);
		}
	}
	@catch (NSException * e) {
		NSLog(@"%s Caught %@: %@", __FUNCTION__,[e name], [e reason]);
	}
}


/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect
{
    // Drawing code
}
*/

@end
