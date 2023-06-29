#import "HggAppUpgradePlugin.h"
#if __has_include(<hgg_app_upgrade/hgg_app_upgrade-Swift.h>)
#import <hgg_app_upgrade/hgg_app_upgrade-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "hgg_app_upgrade-Swift.h"
#endif

@implementation HggAppUpgradePlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftHggAppUpgradePlugin registerWithRegistrar:registrar];
}
@end
