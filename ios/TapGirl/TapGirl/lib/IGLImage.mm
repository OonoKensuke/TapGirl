//
//  IGLImage.mm
//  TapGirl
//
//  Created by nakano_michiharu on 7/4/13.
//  Copyright (c) 2013 oono. All rights reserved.
//

#import "IGLImage.h"
#import "IGLKit.h"

@interface IGLImage(){
}
// バッファサイズを求める
-(size_t)getBufferSize:(UIImage*)uiImage;
// CGImageの内容をコピーしてピクセルデータを得る
-(void)loadImage:(CGImageRef)image output:(unsigned char*)data;
// テクスチャ作成
-(void)createTexture:(unsigned char*)data mipmap:(BOOL)createMipmap;
// バッファ削除
-(void)deleteBuffer;
@end



@implementation IGLImage

@synthesize width = _width;
@synthesize height = _height;
@synthesize textureId = _textureId;

// 初期化
-(id)initWithUIImage:(UIImage*)uiImage{
    self = [super init];
    if (self) {
		BOOL ret = [self loadImage:uiImage];
		if(!ret){// エラーのとき
			self.width = self.height = 0;
			return nil;
		}
	}
	return self;
}

// 破棄
-(void)dealloc {
	[self deleteBuffer];
	[super dealloc];
}


// バッファ削除
-(void)deleteBuffer {
	if(self.textureId){
		glDeleteTextures(1, &_textureId);CHECK_GL_ERROR();
		self.textureId = 0;
	}
	self.width = self.height = 0;
}


// バッファサイズを求める
-(size_t)getBufferSize:(UIImage*)uiImage {
	CGImageRef image = uiImage.CGImage;
	self.width = CGImageGetWidth(image);	//画像の幅と高さは2のべき乗であること
	self.height = CGImageGetHeight(image);
	return self.width * self.height * 4 * sizeof(unsigned char);
}


// ロード
-(BOOL)loadImage:(UIImage*)uiImage {
	[self deleteBuffer];
	CGImageRef image = uiImage.CGImage;
	if(image == nil){
		return NO;
	}
	size_t length = [self getBufferSize:uiImage];
	unsigned char* data = (unsigned char*)malloc(length);//malloc
	if(data == nil){
		return NO;
	}
	[self loadImage:image output:data];
	[self createTexture:data mipmap:true];
	free(data);//free
	return YES;
}

// CGImageの内容をコピーしてピクセルデータを得る
-(void)loadImage:(CGImageRef)image output:(unsigned char*)data {
	//ビットマップコンテキスト作成
	CGContextRef memContext = CGBitmapContextCreate(data, self.width, self.height, 8/*8bit/要素*/,
													self.width * 4/*row bytes*/, CGImageGetColorSpace(image),
													kCGImageAlphaPremultipliedLast);
	//コンテキストに画像を描画(これでdataに描画される）
	CGContextDrawImage(memContext,
					   CGRectMake(0.0f, 0.0f, (CGFloat)self.width, (CGFloat)self.height), image);
	CGContextRelease(memContext);
	return;
}

// テクスチャ作成
-(void)createTexture:(unsigned char*)data mipmap:(BOOL)createMipmap {
	glGenTextures(1, &_textureId);CHECK_GL_ERROR();	//テクスチャ作成
	glBindTexture(GL_TEXTURE_2D, self.textureId);CHECK_GL_ERROR();	//テクスチャのバインド
	glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
	glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, self.width, self.height, 0, GL_RGBA,
				 GL_UNSIGNED_BYTE, data);CHECK_GL_ERROR();//ピクセルデータをGPUへ転送
	if (createMipmap) {
		glGenerateMipmap(GL_TEXTURE_2D);CHECK_GL_ERROR();//ミップマップ生成
	}
}


@end
