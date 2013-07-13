//
//  MoreGamesViewController.m
//  TapGirl
//
//  Created by nakano_michiharu on 7/10/13.
//  Copyright (c) 2013 oono. All rights reserved.
//

#import "MoreGamesViewController.h"

@interface MoreGamesViewController ()

@end

@implementation MoreGamesViewController

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
	NSString* strUrl = @"http://apple.com";
	NSURL *url = [NSURL URLWithString:strUrl];
	NSURLRequest *requestObj = [NSURLRequest requestWithURL:url];
	[self.webView loadRequest:requestObj];
	[self.navigationController setNavigationBarHidden:false];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)dealloc {
	[_webView release];
	[super dealloc];
}
- (void)viewDidUnload {
	[self setWebView:nil];
	[super viewDidUnload];
}
@end