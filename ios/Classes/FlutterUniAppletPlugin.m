#import "FlutterUniAppletPlugin.h"
#if __has_include(<flutter_uni_applet/flutter_uni_applet-Swift.h>)
#import <flutter_uni_applet/flutter_uni_applet-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "flutter_uni_applet-Swift.h"
#endif

@implementation FlutterUniAppletPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterUniAppletPlugin registerWithRegistrar:registrar];
}
@end
