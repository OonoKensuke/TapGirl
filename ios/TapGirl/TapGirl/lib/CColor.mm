//
//  CColor.mm
//  TapGirl
//
//  Created by nakano_michiharu on 7/4/13.
//  Copyright (c) 2013 oono. All rights reserved.
//

#include "CColor.h"

void CColor::setHue(float degree, float alpha) {
	// 0 <= degree <= 360 になるように変換
	if (degree < 0.f) {	// 角度がマイナスのとき
		degree = 360 + degree + 360 * (int)-degree / 360;
	} else if (360 < degree) {	// 角度が360度を超えているとき
		degree = degree - (360 * ((int)degree / 360));
	}
	if (degree <= 60.f) {
		r = 1.f;
		g = degree / 60.f;
		b = 0.f;
	} else if (degree <= 120.f) {
		r = (1.f - (degree - 60.f) / 60.f);
		g = 1.f;
		b = 0;
	} else if (degree <= 180.f) {
		r = 0.f;
		g = 1.f;
		b = (degree - 120.f) / 60.f;
	} else if (degree <= 240.f) {
		r = 0.f;
		g = 1.f - (degree - 180.f) / 60.f;
		b = 1.f;
	} else if (degree <= 300.f) {
		r = (degree - 240.f) / 60.f;
		g = 0.f;
		b = 1.f;
	} else {
		r = 1.f;
		g = 0.f;
		b = 1.f - (degree - 300.f) / 60.f;
	}
	a = alpha;
}