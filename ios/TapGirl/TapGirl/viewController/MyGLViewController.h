//
//  MyGLViewController.h
//  TapGirl
//
//  Created by nakano_michiharu on 7/3/13.
//  Copyright (c) 2013 oono. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "MyGLView.h"
#import "ToggleButton.h"

@interface MyGLViewController : UIViewController
{
	
}
@property (retain, nonatomic) IBOutlet MyGLView *glView;
@property (retain, nonatomic) IBOutlet UILabel *countLabel;
@property (retain, nonatomic) IBOutlet ToggleButton *btnToggleSound;
@property (retain, nonatomic) IBOutlet UIButton *btnMoreApps;

+(MyGLViewController*) getInstance;
- (IBAction)onPushButton:(id)sender;

@end
