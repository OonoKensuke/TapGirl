//
//  FacebookPostViewController.m
//  TapGirl
//
//  Created by nakano_michiharu on 7/12/13.
//  Copyright (c) 2013 oono. All rights reserved.
//

#import "FacebookPostViewController.h"
#import "MyGLView.h"
#include "gameDefs.h"

@interface FacebookPostViewController () <FBLoginViewDelegate>
@property (strong, nonatomic) id<FBGraphUser> loggedInUser;
@property (strong, nonatomic) NSMutableDictionary *postParams;

@end

@implementation FacebookPostViewController
@synthesize loggedInUser = _loggedInUser;
@synthesize postParams = _postParams;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    FBLoginView *loginview = [[FBLoginView alloc] init];
    
    loginview.frame = CGRectOffset(loginview.frame, 5, 5);
    loginview.delegate = self;
	loginview.readPermissions = @[@"basic_inof"];
	
    [self.view addSubview:loginview];
    
    [loginview sizeToFit];
	{
        // Custom initialization
        self.postParams = [@{
						   @"link" : SNS_URL,
						   //@"picture" : @"https://developers.facebook.com/attachment/iossdk_logo.png",
						   @"name" : SNS_APP_NAME,
						   @"caption" : @"キャプション",
						   @"description" : @"簡単な説明"
						   } mutableCopy];
	}
	[self.navigationController setNavigationBarHidden:NO];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)dealloc {
    [_btnPostMessage release];
	[_labelFirstName release];
	[_profilePic release];
    [super dealloc];
}
- (void)viewDidUnload {
    [self setBtnPostMessage:nil];
	[self setLabelFirstName:nil];
	[self setProfilePic:nil];
    [super viewDidUnload];
}


#pragma mark - FBLoginViewDelegate

- (void)loginViewShowingLoggedInUser:(FBLoginView *)loginView {
    // first get the buttons set for login mode
	self.btnPostMessage.enabled = true;
	
//    self.buttonPostPhoto.enabled = YES;
//    self.buttonPostStatus.enabled = YES;
//    self.buttonPickFriends.enabled = YES;
//    self.buttonPickPlace.enabled = YES;
//	
//    // "Post Status" available when logged on and potentially when logged off.  Differentiate in the label.
//    [self.buttonPostStatus setTitle:@"Post Status Update (Logged On)" forState:self.buttonPostStatus.state];
}

- (void)loginViewFetchedUserInfo:(FBLoginView *)loginView
                            user:(id<FBGraphUser>)user {
    // here we use helper properties of FBGraphUser to dot-through to first_name and
    // id properties of the json response from the server; alternatively we could use
    // NSDictionary methods such as objectForKey to get values from the my json object
    self.labelFirstName.text = [NSString stringWithFormat:@"Hello %@!", user.first_name];
    // setting the profileID property of the FBProfilePictureView instance
    // causes the control to fetch and display the profile picture for the user
    self.profilePic.profileID = user.id;
    self.loggedInUser = user;
}

- (void)loginViewShowingLoggedOutUser:(FBLoginView *)loginView {
    // test to see if we can use the share dialog built into the Facebook application
    FBShareDialogParams *p = [[FBShareDialogParams alloc] init];
    p.link = [NSURL URLWithString:@"http://developers.facebook.com/ios"];
#ifdef DEBUG
    [FBSettings enableBetaFeatures:FBBetaFeaturesShareDialog];
#endif
//    BOOL canShareFB = [FBDialogs canPresentShareDialogWithParams:p];
//    BOOL canShareiOS6 = [FBDialogs canPresentOSIntegratedShareDialogWithSession:nil];
	
//    self.buttonPostStatus.enabled = canShareFB || canShareiOS6;
//    self.buttonPostPhoto.enabled = NO;
//    self.buttonPickFriends.enabled = NO;
//    self.buttonPickPlace.enabled = NO;
    
    // "Post Status" available when logged on and potentially when logged off.  Differentiate in the label.
//    [self.buttonPostStatus setTitle:@"Post Status Update (Logged Off)" forState:self.buttonPostStatus.state];
    
    self.profilePic.profileID = nil;
    self.labelFirstName.text = nil;
    self.loggedInUser = nil;
}

- (void)loginView:(FBLoginView *)loginView handleError:(NSError *)error {
    // see https://developers.facebook.com/docs/reference/api/errors/ for general guidance on error handling for Facebook API
    // our policy here is to let the login view handle errors, but to log the results
    NSLog(@"FBLoginView encountered an error=%@", error);
}
#pragma mark -event
- (IBAction)onPushButton:(id)sender
{
	MyGLView* glView = [MyGLView getInstance];
	int len = (int)glView.touchLength;
	NSString *str = [NSString stringWithFormat:SNS_TWEET_FORMAT, len, SNS_APP_NAME, SNS_HASHTAG];

	self.postParams[@"message"] = str;
    if ([FBSession.activeSession.permissions
         indexOfObject:@"publish_actions"] == NSNotFound) {
        // Permission hasn't been granted, so ask for publish_actions
        [FBSession openActiveSessionWithPublishPermissions:@[@"publish_actions"]
                                           defaultAudience:FBSessionDefaultAudienceFriends
                                              allowLoginUI:YES
                                         completionHandler:^(FBSession *session, FBSessionState state, NSError *error) {
											 if (FBSession.activeSession.isOpen && !error) {
												 // Publish the story if permission was granted
												 [self publishStory];
											 }
											 else {
												 NSLog(@"session open erro:%@", error);
											 }
										 }];
    } else {
        // If permissions present, publish the story
        [self publishStory];
    }
}

#pragma mark -
- (void)publishStory
{
	[FBRequestConnection
	 startWithGraphPath:@"me/feed"
	 parameters:self.postParams
	 HTTPMethod:@"POST"
	 completionHandler:^(FBRequestConnection *connection,
						 id result,
						 NSError *error) {
		 NSString *alertText;
		 if (error) {
			 alertText = [NSString stringWithFormat:
						  @"error: domain = %@, code = %d",
						  error.domain, error.code];
		 }
		 else {
			 alertText = [NSString stringWithFormat:
						  @"Posted action, id:%@",
						  result[@"id"]];
		 }
		 [[[UIAlertView alloc] initWithTitle:@"Result"
									 message:alertText
									delegate:self
						   cancelButtonTitle:@"Ok"
						   otherButtonTitles:nil, nil]
		  show];
	 }];
}

- (void)alertView:(UIAlertView*)alertView
{
	[[self presentedViewController]
	 dismissModalViewControllerAnimated:YES];
}


@end
