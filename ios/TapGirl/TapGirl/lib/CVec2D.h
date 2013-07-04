//
//  CVec2D.h
//  TapGirl
//
//  Created by nakano_michiharu on 7/4/13.
//  Copyright (c) 2013 oono. All rights reserved.
//

#ifndef TapGirl_CVec2D_h
#define TapGirl_CVec2D_h
#import <assert.h>
#import <math.h>

class CMatrix;

class CVec2D {
public:
	union {
		float element[2];
		struct {
			float x;
			float y;
		};
		struct {
			float width;
			float height;
		}
	};
	
public:
	CVec2D() {
		x = y = 0.0f;
	}
	CVec2D(float initX, float initY) {
		x = initX;
		y = initY;
	}
	CVec2D(const CVec2D& v) {
		x = v.x; y = v.y;
	}
	CVec2D(const CVec2D& vTo, const CVec2D& vFrom ) {
		x = vTo.x - vFrom.x;
		y = vTo.y - vFrom.y;
	}
	
	~CVec2D() {}
	//getメソッドの{の前のconst は、メンバーを変更しない事をコンパイラに知らせる。これによって
	//const CVec2D v;
	//float x = v.getElement(1);
	//などが、コンパイルエラーを起こさなくなるようだ
	//要素の取得（行指定）
	float getElement(int row) const{assert(0<=row && row<2);return element[row];}
	//要素の取得（GL用）
	const float* getElement() const{return element;}
	//xの取得
	float getX() const {return x;}
	//yの取得
	float getY() const {return y;}
	//widthの取得
	float getWidth() const {return width;}
	//heightの取得
	float getHeight() const {return height;}
	//要素の設定
	void setElement(int row, float val){assert(0<=row && row<2);element[row] = val;}
	//xの設定
	void setX(float val) {x = val;}
	//yの設定
	void setY(float val) {y = val;}
	//widthの設定
	void setWidth(float val) {width = val;}
	//heightの設定
	void setHeight(float val) {height = val;}
	//x,yの設定
	inline void set(float X,float Y);
	//x,yの設定
	inline void set(const Vector& vec);
	//ベクトルの長さ
	inline float getLength() const;
	//ベクトルの長さの２乗
	inline float getLengthSq() const;
	//単位ベクトル化(長さ０のときはx軸方向の単位ベクトル）
	//@return : 長さ０でコールしたときfalse
	inline bool normalize();
	//長さを設定（長さ０のときはx軸方向のベクトルになる）
	inline bool setLength(float v){
		bool ret = normalize();
		*this *= v;
		return ret;
	}
	//ベクトルを回転させる
	//@param deg 単位はラジアン
	inline void rotate(float rad){
		float fCos = cosf(rad), fSin = sinf(rad);
		float tx = fCos * x + fSin * y;
		float ty = -fSin * x + fCos * y;
		x = tx;
		y = ty;
	}
	//XY平面での角度を求める
	//内部はatan2であり、x=+0, y=+0のときは0度が返る。
	//参考：http://linuxjm.sourceforge.jp/html/LDP_man-pages/man3/atan2.3.html
	//@return 角度（単位：degree）。範囲は、[-π,π]
	inline float getAngle() const { return atan2f(y,x); }
	//XY平面での２つのベクトル角度を求める(v1-v2)
	//@return 角度（単位：ラジアン）。
	static inline float getAngle(const Vector& v1, const Vector& v2){
		return v1.getAngle() - v2.getAngle();
	}
	//単位ベクトルの取得(長さ０のときはx軸方向の単位ベクトル）
	//@param outSuccess 長さ０でコールした場合false。NULL可
	inline Vector getNormal(bool* outSuccess) const;
	//代入
	inline Vector& operator=(const Vector& vctr);
	//加算
	inline Vector operator+(const Vector& vctr) const;
	//加算（代入）
	inline Vector& operator+=(const Vector& vctr);
	//加算（代入）
	inline void add(float X, float Y){x+=X; y+=Y;}
	//-1倍
	inline Vector operator-() const;
	//減算
	inline Vector operator-(const Vector& vctr) const;
	//減算（代入）
	inline Vector& operator-=(const Vector& vctr);
	//減算（代入）
	inline void subtract(float X, float Y){x-=X; y-=Y;}
	//スカラー乗算
	inline Vector operator*(float val) const;
	//スカラー乗算（代入）
	inline Vector& operator*=(float val);
	//スカラー商
	inline Vector operator/(float val) const;
	//スカラー商（代入）
	inline Vector& operator/=(float val);
	//比較
	inline bool operator==(const Vector& vctr) const;
	//比較（NOT）
	inline bool operator!=(const Vector& vctr) const;
	//内積
	inline float dot(const Vector& vctr) const;
	//外積（2次元の外積はスカラー）
	inline float exterior(const Vector& vctr) const {
		return x*vctr.y-y*vctr.x;
	}
	//行列との乗算
	inline Vector operator*(const Matrix& mtrx) const;
	//行列との乗算（代入）
	inline Vector& operator*=(const Matrix& mtrx);
	//スカラー×ベクトル
	friend Vector operator*(float val, const Vector& vctr);
	//このベクトルと指定した位置ベクトルとの距離
	float getDistance(const Vector& v) const{
		float dx = v.x - x, dy = v.y - y;
		return sqrtf(dx * dx + dy * dy);
	}
	//このベクトルと指定した位置ベクトルとの距離の２乗
	float getDistanceSq(const Vector& v) const{
		float dx = v.x - x, dy = v.y - y;
		return dx * dx + dy * dy;
	}
	//２つの位置ベクトルの距離
	static inline float getDistance(const Vector& v1,const Vector& v2){
		return (v2-v1).getLength();
	}
	//２つの位置ベクトルの距離の２乗
	static inline float getDistanceSq(const Vector& v1, const Vector& v2){
		return (v2-v1).getLengthSq();
	}
	//２つの位置ベクトルの間を補完した位置を求める
	//@param t 補完パラメータ。0を指定するとv1。1を指定するとv2。(0～1)を指定するとその間
	static inline Vector getMix(const Vector& v1, const Vector v2, float t){
		return v1 * (1 - t) + v2 * t;
	}
	
};
//x,y,zで設定
inline void Vector::set(float X,float Y){
	x=X;
	y=Y;
}

//x,y,zで設定
inline void Vector::set(const Vector& vec){
	x=vec.x;
	y=vec.y;
}

//ベクトルの長さ
inline float Vector::getLength() const{
	return sqrtf(x*x +y*y);
}

//ベクトルの長さの２乗
inline float Vector::getLengthSq() const{
	return x*x + y*y;
}

//単位ベクトル化(長さ０のときはx軸方向の単位ベクトル）
inline bool Vector::normalize(){
	float	len = getLength();
	if(len!=float(0.0)){
		x /= len;
		y /= len;
		return true;
	} else {
		x = 1.0f;
		y = 0.0f;
		return false;
	}
}

//単位ベクトルの取得（長さ０のときはx軸方向のベクトル）
inline Vector Vector::getNormal(bool* outSuccess) const
{
	float	len = getLength();
	if(len!=float(0.0)){
		if(outSuccess){
			*outSuccess=true;
		}
		return Vector(x/len,y/len);
	} else {
		if(outSuccess){
			*outSuccess=false;
		}
		return Vector(1.0f,0.0f);
	}
}

//代入
inline Vector& Vector::operator=(const Vector& vctr)
{
	x = vctr.x;
	y = vctr.y;
	return *this;
}

//加算
inline Vector Vector::operator+(const Vector& vctr) const
{
	return Vector(
				  x + vctr.x,
				  y + vctr.y);
}

//加算（代入）
inline Vector& Vector::operator+=(const Vector& vctr){
	x+=vctr.x;
	y+=vctr.y;
	return *this;
}

//-1倍
inline Vector Vector::operator-() const
{
	return Vector(-x, -y);
}

//減算
inline Vector Vector::operator-(const Vector& vctr) const //fixed by kamiyan
{
	return Vector(
				  x - vctr.x,
				  y - vctr.y);
}

//減算（代入）
inline Vector& Vector::operator-=(const Vector& vctr){
	x-=vctr.x;
	y-=vctr.y;
	return *this;
}

//スカラー乗算
inline Vector Vector::operator*(float val) const
{
	return Vector(
				  x * val,
				  y * val);
}

//スカラー乗算（代入）
inline Vector& Vector::operator*=(float val)
{
	x*=val;
	y*=val;
	return *this;
}

//スカラー商
inline Vector Vector::operator/(float val) const
{
	return Vector(
				  x / val,
				  y / val);
}

//スカラー商（代入）
inline Vector& Vector::operator/=(float val)
{
	x/=val;
	y/=val;
	return *this;
}

//比較
inline bool Vector::operator==(const Vector& vctr) const
{
	for(int row=0; row<2; row++){
		if(element[row]!=vctr.element[row]){
			return false;
		}
	}
	return true;
}

//比較（NOT）
inline bool Vector::operator!=(const Vector& vctr) const
{
	return !((*this)==vctr);
}

//内積
inline float Vector::dot(const Vector& vctr) const
{
	return x*vctr.x +y*vctr.y;
}

//スカラー×ベクトル
inline Vector operator*(float val, const Vector& vctr)
{
	return Vector(
				  val * vctr.x,
				  val * vctr.y);
}



#endif
