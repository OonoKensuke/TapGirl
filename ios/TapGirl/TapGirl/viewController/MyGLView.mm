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


// プライベート
@interface MyGLView() {
	// 位置
	CVec2D positions[4];
	// テクスチャ座用
	CVec2D texCoords[4];
	//タッチ距離を測るための座標
	CGPoint _posTouch;
}
// メインテクスチャ
@property(strong, nonatomic) IGLImage* picture;
// シェーダー
@property(strong, nonatomic) MyGLShader* shader;
@end


@implementation MyGLView
@synthesize shader = _shader;
@synthesize picture = _picture;
@synthesize touchLength = _touchLength;

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
		self.shader = [[MyGLShader alloc] init];
		UIImage *img = [UIImage imageNamed:@"hiroin1_texture.jpg"];
		self.picture = [[IGLImage alloc] initWithUIImage:img];
		self.touchLength = 0.0f;
		NSLog(@"pix per cm = %f", _PIX_PER_CM);
    }
    return self;
}
#pragma mark -public Method
-(NSString*)buildShader
{
	NSString *error = [self.shader build];
	return error;
}


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
	float drawWidth = _GR_WIDTH;
	float drawHeight = _GR_HEIGHT;
	// テクスチャ座標
	float u = (float)_GR_WIDTH / (float)self.picture.width;
	float v = (float)_GR_HEIGHT / (float)self.picture.height;
	
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
	
	positions[0] = CVec2D(0, 0);
	positions[1] = CVec2D(drawWidth, 0);
	positions[2] = CVec2D(0, drawHeight);
	positions[3] = CVec2D(drawWidth, drawHeight);
	texCoords[0] = CVec2D(0,0);
	texCoords[1] = CVec2D(u,0);
	texCoords[2] = CVec2D(0,v);
	texCoords[3] = CVec2D(u,v);
	[self.shader drawArraysMy:GL_TRIANGLE_STRIP positions:(float*)positions
					  glImage:self.picture texCoords:(float*)texCoords count:4
						color:color alpha:alpha];
	[self drawFps];
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
