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
#import "NADView.h"

@interface MyGLViewController : UIViewController<NADViewDelegate>
{
}
@property (retain, nonatomic) IBOutlet MyGLView *glView;
@property (retain, nonatomic) IBOutlet UILabel *countLabel;
@property (retain, nonatomic) IBOutlet ToggleButton *btnToggleSound;
@property (retain, nonatomic) IBOutlet UIButton *btnMoreApps;
@property (retain, nonatomic) IBOutlet UIButton *btnTweet;
@property (retain, nonatomic) IBOutlet UIButton *btnFacebook;


@property (nonatomic, retain) NADView* nadView;

+(MyGLViewController*) getInstance;
- (IBAction)onPushButton:(id)sender;

@end
