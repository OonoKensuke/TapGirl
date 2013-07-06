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
// プライベート
@interface MyGLView() {
	// 位置
	CVec2D positions[4];
	// テクスチャ座用
	CVec2D texCoords[4];
}
// メインテクスチャ
@property(strong, nonatomic) IGLImage* picture;
@end


@implementation MyGLView
@synthesize shader = _shader;
@synthesize picture = _picture;

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
    }
    return self;
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
				CGPoint clickPos = [touch locationInView:self];
				{
					NSLog(@"x:%f, y:%f", clickPos.x, clickPos.y);
					NSLog(@"cange to x:%f, y:%f", clickPos.x, clickPos.y);
				}
				
				break;
			}
			NSLog(@"touch count:%d", count);
		}
		[MyGLViewController getInstance].countLabel.text = @"touchBegan";
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
					NSLog(@"x:%f, y:%f", clickPos.x, clickPos.y);
					NSLog(@"cange to x:%f, y:%f", clickPos.x, clickPos.y);
				}
				
				break;
			}
			NSLog(@"touch count:%d", count);
		}
		[MyGLViewController getInstance].countLabel.text = @"touchEnd";
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
					NSLog(@"x:%f, y:%f", clickPos.x, clickPos.y);
					NSLog(@"cange to x:%f, y:%f", clickPos.x, clickPos.y);
				}
				
				break;
			}
			NSLog(@"touch count:%d", count);
		}
		[MyGLViewController getInstance].countLabel.text = @"touchMove";
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
