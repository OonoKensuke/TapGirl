//
//  CMatrix.h
//  TapGirl
//
//  Created by nakano_michiharu on 7/4/13.
//  Copyright (c) 2013 oono. All rights reserved.
//

#ifndef __TapGirl__CMatrix__
#define __TapGirl__CMatrix__

#include <iostream>
#import "CVec2D.h"

//float型3x3行列
class CMatrix {
	friend class CVec2D;
private:
	float element[3][3];
	
public:
	CMatrix();
	CMatrix(const CMatrix& mtrx);
	~CMatrix() {};
	
	//要素の取得
	float getElement(int row, int col) const {
		assert(0<=row && row<3);
		assert(0<=col && col<3);
		return element[row][col];
	}
	//要素の設定
	void setElement(int row, int col, float val){
		assert(0<=row && row<3);
		assert(0<=col && col<3);
		element[row][col] = val;
	}
	//要素の取得
	float operator()(int row, int col){
		return getElement(row,col);
	}
	//比較
	bool operator==(const CMatrix& mtrx) const;
	//比較（NOT）
	bool operator!=(const CMatrix& mtrx) const;
	//代入
	CMatrix& operator=(const CMatrix& mtrx);
	//乗算
	CMatrix operator*(const CMatrix& mtrx) const;
	//乗算（代入）
	CMatrix& operator*=(const CMatrix& mtrx){
		return *this=(*this)*mtrx;
	}
	//加算
	CMatrix operator+(const CMatrix& mtrx) const;
	//加算（代入）
	CMatrix& operator+=(const CMatrix& mtrx){
		return *this=(*this)+mtrx;
	}
	//減算
	CMatrix operator-(const CMatrix& mtrx) const;
	//減算（代入）
	CMatrix& operator-=(const CMatrix& mtrx){
		return *this=(*this)-mtrx;
	}
	//ベクトルとの乗算
	CVec2D operator*(const CVec2D& mtrx) const;
	//単位ベクトル化
	CMatrix& setUnit();
	//単位ベクトルか？
	bool IsUnit() const;
	
	//Z軸周りに回転（角度はdegree）
	CMatrix& addZRotation(float rot);
	//Z軸周りの回転行列（角度はdegree）
	CMatrix& setZRotation(float rot);
	//平行移動
	CMatrix& addTranslation(float x, float y);
	//平行移動
	CMatrix& addTranslation(const CVec2D& v){
		return addTranslation(v.x,v.y);
	}
	//平行移動行列
	CMatrix& setTranslation(float x, float y);
	//平行移動行列
	CMatrix& setTranslation(const CVec2D& v){
		return setTranslation(v.x,v.y);
	}
	//拡大縮小（各軸方向ごと倍率）
	CMatrix& addScale(float x, float y);
	//拡大縮小（各軸方向ごと倍率）
	CMatrix& addScale(const CVec2D& v){
		return addScale(v.x,v.y);
	}
	//拡大縮小（全軸共通倍率）
	CMatrix& addScale(float val){
		return addScale(val,val);
	}
	//拡大縮小行列（各軸方向ごと倍率）
	CMatrix& setScale(float x, float y);
	//拡大縮小行列（各軸方向ごと倍率）
	CMatrix& setScale(const CVec2D& v){
		return setScale(v.x,v.y);
	}
	//拡大縮小行列（全軸共通倍率）
	CMatrix& setScale(float val){
		return setScale(val,val);
	}
	//逆行列化
	CMatrix& inverse();
	//行列式
	float det();
	//４次元行列化してカラムごとにもらう
	void getVec4(int column, float* dataOut);
	//4x4行列に展開する
	void getMat4(float dataOut[16]);

};

#endif /* defined(__TapGirl__CMatrix__) */
