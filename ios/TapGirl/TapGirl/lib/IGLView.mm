//
//  IGLView.m
//  TapGirl
//
//  Created by nakano_michiharu on 7/4/13.
//  Copyright (c) 2013 oono. All rights reserved.
//

#import <QuartzCore/CAEAGLLayer.h>
#import <QuartzCore/CADisplayLink.h>
#import "IGLView.h"
#include "IGLKit.h"
#import "IGL.h"

// プライベート
@interface IGLView(){
	// アニメーションのフレーム間隔
    NSInteger animationFrameInterval;
	// DisplayLinkのインスタンス
    CADisplayLink* displayLink;
	// コンテキスト
	EAGLContext* context;
    // レンダバッファ
    uint defaultFramebuffer;
	// カラーレンダバッファ
	uint colorRenderbuffer;
	// １秒以内に描画されたフレーム数
	int frames;
	// 前回描画フレーム数をクリアした時刻
	double beforeTime;
	// アニメーション中か
	BOOL animating;
}
// ビューを描画する
// 　sender：メッセージ送信インスタンス
- (void)drawView:(id)sender;
// ビューサイズ設定
- (void) setViewRect:(CGRect)rect;
@end



@implementation IGLView

@synthesize width = _width;
@synthesize height = _height;

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
    }
    return self;
}


// レイヤークラス
+ (Class)layerClass {
    return [CAEAGLLayer class];
}

// 初期化
- (id)initWithCoder:(NSCoder*)coder {
	NSLog(@"%s", __PRETTY_FUNCTION__);
    self = [super initWithCoder:coder];
    if (self) {
        // Get the layer
        CAEAGLLayer *eaglLayer = (CAEAGLLayer *)self.layer;
        eaglLayer.opaque = TRUE;
        eaglLayer.drawableProperties = [NSDictionary dictionaryWithObjectsAndKeys:
                                        [NSNumber numberWithBool:FALSE],
										kEAGLDrawablePropertyRetainedBacking,
										kEAGLColorFormatRGBA8,
										kEAGLDrawablePropertyColorFormat, nil];
		// コンテキストを取得
		context = [[EAGLContext alloc] initWithAPI:kEAGLRenderingAPIOpenGLES2];
        if (!context || ![EAGLContext setCurrentContext:context]) {
            return nil;
        }
		
		glGenFramebuffers(1, &defaultFramebuffer);CHECK_GL_ERROR();
		glGenRenderbuffers(1, &colorRenderbuffer);CHECK_GL_ERROR();
		glBindFramebuffer(GL_FRAMEBUFFER, defaultFramebuffer);CHECK_GL_ERROR();
		glBindRenderbuffer(GL_RENDERBUFFER, colorRenderbuffer);CHECK_GL_ERROR();
		glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0,
								  GL_RENDERBUFFER, colorRenderbuffer);CHECK_GL_ERROR();
        animating = FALSE;
        animationFrameInterval = 1;
        displayLink = nil;
		frames = 0;
		beforeTime = 0;
	}
    return self;
}

// 破棄
- (void)dealloc {
	NSLog(@"%s", __PRETTY_FUNCTION__);
	[EAGLContext setCurrentContext:nil];
	glDeleteFramebuffers(1, &defaultFramebuffer);CHECK_GL_ERROR();
	defaultFramebuffer = 0;
	glDeleteRenderbuffers(1, &colorRenderbuffer);CHECK_GL_ERROR();
	colorRenderbuffer = 0;
	[super dealloc];
}


// サブビューをレイアウト
- (void)layoutSubviews {
	NSLog(@"%s", __PRETTY_FUNCTION__);
	glBindRenderbuffer(GL_RENDERBUFFER, colorRenderbuffer);CHECK_GL_ERROR();
	[context renderbufferStorage:GL_RENDERBUFFER fromDrawable:(CAEAGLLayer*)self.layer];
	glGetRenderbufferParameteriv(GL_RENDERBUFFER, GL_RENDERBUFFER_WIDTH, &_width);CHECK_GL_ERROR();
	glGetRenderbufferParameteriv(GL_RENDERBUFFER, GL_RENDERBUFFER_HEIGHT, &_height);CHECK_GL_ERROR();
	if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
		NSLog(@"Failed to make complete framebuffer object %x", glCheckFramebufferStatus(GL_FRAMEBUFFER));
		return;
	}
	[self setViewRect:CGRectMake(0, 0, self.width, self.height)];
}


// ビューサイズ設定
- (void) setViewRect:(CGRect)rect {
	NSLog(@"%s", __PRETTY_FUNCTION__);
	glViewport(rect.origin.x, rect.origin.y, rect.size.width, rect.size.height);CHECK_GL_ERROR();
	[[IGL sharedGL] setViewSize:rect.size];
}

// 描画メイン
- (void)drawMain {
	NSLog(@"%s", __PRETTY_FUNCTION__);
	//オーバーライドして使う
}

// ビューを描画
- (void)drawView:(id)sender {
	//NSLog(@"%s", __PRETTY_FUNCTION__);
	[EAGLContext setCurrentContext:context];
	glBindFramebuffer(GL_FRAMEBUFFER, defaultFramebuffer);CHECK_GL_ERROR();
	glClearColor(1.0f, 1.0f, 1.0f, 1.0f);CHECK_GL_ERROR();
	glClear(GL_COLOR_BUFFER_BIT);CHECK_GL_ERROR();
	[self drawMain];
	//[self drawFps];
	glFlush();CHECK_GL_ERROR();
	// バックバッファをフロントバッファへ
	glBindRenderbuffer(GL_RENDERBUFFER, colorRenderbuffer);CHECK_GL_ERROR();
	[context presentRenderbuffer:GL_RENDERBUFFER];
}

// fps描画（現バージョンでは、debug_NSLog()で出力のみ）
- (void)drawFps {
	//NSLog(@"%s", __PRETTY_FUNCTION__);
	double nowTime = CFAbsoluteTimeGetCurrent();
	double elapsedTime = nowTime - beforeTime;
	static float frameRate = 0.0f;
	frames++;
	if (elapsedTime > 1.0f) {
		frameRate = (float)((double) frames / elapsedTime);
		beforeTime = nowTime;
		frames = 0;
		debug_NSLog(@"fps=%3.1f", frameRate);
	}
}

// アニメーションを開始する
- (void)startAnimation {
	NSLog(@"%s", __PRETTY_FUNCTION__);
	if (animating) {
		return;
	}
	displayLink = [self.window.screen displayLinkWithTarget:self
												   selector:@selector(drawView:)];
	[displayLink setFrameInterval:animationFrameInterval];
	[displayLink addToRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
	animating = TRUE;
}

// アニメーションを停止する
- (void)stopAnimation {
	NSLog(@"%s", __PRETTY_FUNCTION__);
	if (!animating) {
		return;
	}
	[displayLink invalidate];
	displayLink = nil;
	animating = FALSE;
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
