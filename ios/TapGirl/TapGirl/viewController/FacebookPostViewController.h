//
//  FacebookPostViewController.h
//  TapGirl
//
//  Created by nakano_michiharu on 7/12/13.
//  Copyright (c) 2013 oono. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <FacebookSDK/FacebookSDK.h>


@interface FacebookPostViewController : UIViewController
{
	
}
@property (retain, nonatomic) IBOutlet UIButton *btnPostMessage;
@property (retain, nonatomic) IBOutlet UILabel *labelFirstName;
@property (retain, nonatomic) IBOutlet FBProfilePictureView *profilePic;
@property (retain, nonatomic) IBOutlet FBLoginView *loginFbView;
@property (retain, nonatomic) IBOutlet UITextView *textPost;

- (IBAction)onPushButton:(id)sender;

@end
