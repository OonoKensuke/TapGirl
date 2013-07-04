//
//  CColor.h
//  TapGirl
//
//  Created by nakano_michiharu on 7/4/13.
//  Copyright (c) 2013 oono. All rights reserved.
//

#ifndef __TapGirl__CColor__
#define __TapGirl__CColor__

#include <iostream>

class CColor {
public:
	float r;
	float g;
	float b;
	float a;
	
public:
	CColor() {r = g = b = a = 1.0f;}
	// コンストラクタ(RGB指定）
	CColor(float R, float G, float B){r=R;g=G;b=B;a=1.f;}
	// コンストラクタ(RGBA指定）
	CColor(float R, float G, float B, float A){r=R;g=G;b=B;a=A;}
	// コンストラクタ(uint型 0xRRGGBBAA指定）
	CColor(unsigned int rgba){
		a = (rgba & 0xff) / 255.f; rgba >>=8;
		b = (rgba & 0xff) / 255.f; rgba >>=8;
		g = (rgba & 0xff) / 255.f; rgba >>=8;
		r = (rgba & 0xff) / 255.f; rgba >>=8;
	}
	// コピーコンストラクタ
	CColor(const CColor& c){r=c.r;g=c.g;b=c.b;a=c.a;}
	// 設定(uchar型 RGBA指定）
	void setByInt(unsigned char R, unsigned char G, unsigned char B, unsigned char A){
		r=(float)R / 255.f;
		g=(float)G / 255.f;
		b=(float)B / 255.f;
		a=(float)A / 255.f;
	}
	// 色相から設定
	void setHue(float degree, float alpha);
};

#endif /* defined(__TapGirl__CColor__) */
