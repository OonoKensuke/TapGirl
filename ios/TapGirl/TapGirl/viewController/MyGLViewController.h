//
//  MyGLViewController.h
//  TapGirl
//
//  Created by nakano_michiharu on 7/3/13.
//  Copyright (c) 2013 oono. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MyGLView.h"

@interface MyGLViewController : UIViewController
{
	
}
@property (retain, nonatomic) IBOutlet MyGLView *glView;
@property (retain, nonatomic) IBOutlet UILabel *countLabel;

+(MyGLViewController*) getInstance;
@end
