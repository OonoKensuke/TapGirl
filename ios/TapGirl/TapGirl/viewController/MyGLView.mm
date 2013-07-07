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
//目標タッチ距離（cm）
#define _OBJECTIVE_LENGTH 1000

typedef struct {
	// 位置
	CVec2D positions[4];
	// テクスチャ座用
	CVec2D texCoords[4];
}_PRIMITIVE;


// プライベート
@interface MyGLView() {
	//タッチ距離を測るための座標
	CGPoint _posTouch;
	
	_PRIMITIVE _primCurrent;
	_PRIMITIVE _primNext;
}
// メインテクスチャ
//@property(strong, nonatomic) IGLImage* picture;
@property(assign, nonatomic) IGLImage* textureCurrent;
@property(assign, nonatomic) IGLImage* textureNext;
// シェーダー
//@property(strong, nonatomic) MyGLShader* shader;
//
@property(strong, nonatomic) NSMutableArray* arrayShader;
@end


@implementation MyGLView
//@synthesize shader = _shader;
//@synthesize picture = _picture;
@synthesize textureCurrent = _textureCurrent;
@synthesize textureNext = _textureNext;
@synthesize touchLength = _touchLength;
@synthesize arrayShader = _arrayShader;

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
		[self loadTextureFromIndex:1 toCurrent:true];
		self.touchLength = 0.0f;
		NSLog(@"pix per cm = %f", _PIX_PER_CM);
    }
    return self;
}

- (void)dealloc
{
	[self.arrayShader release];
	[super dealloc];
}

- (BOOL)loadTextureFromIndex:(int)index toCurrent:(BOOL)bLoadCurrent
{
	BOOL result = false;
	@try {
		NSString* nameFile = [NSString stringWithFormat:@"image%02d.jpg", index];
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
	// 位置
	/*
	 float w = fminf(self.width, self.height);
	 positions[0] = Vector(0, 0);
	 positions[1] = Vector(w, 0);
	 positions[2] = Vector(0, w);
	 positions[3] = Vector(w, w);
	 */
	[self drawTextureCurrent:true withAlpha:alpha withColor:color];
	[self drawFps];
}

- (void)drawTextureCurrent:(BOOL)isCurrent withAlpha:(float)alpha withColor:(CColor&)color
{
	_PRIMITIVE *prim = nil;
	if (isCurrent) {
		prim = &_primCurrent;
	}
	else {
		prim = &_primNext;
	}
	float drawWidth = _GR_WIDTH;
	float drawHeight = _GR_HEIGHT;
	// テクスチャ座標
	float u = (float)_GR_WIDTH / (float)self.textureCurrent.width;
	float v = (float)_GR_HEIGHT / (float)self.textureCurrent.height;
	
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
	MyGLShader* shader = [self.arrayShader objectAtIndex:(int)FRSH_NORMAL];
	[shader drawArraysMy:GL_TRIANGLE_STRIP positions:(float*)(prim->positions)
				 glImage:self.textureCurrent texCoords:(float*)(prim->texCoords) count:4
				   color:color alpha:alpha];
	
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
			_posTouch = posUpdate;
			int iLength = _OBJECTIVE_LENGTH - (int)(self.touchLength);
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
