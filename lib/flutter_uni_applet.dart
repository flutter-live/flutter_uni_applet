
import 'dart:async';

import 'package:flutter/services.dart';

class FlutterUniApplet {
  static const MethodChannel _channel = MethodChannel('flutter_uni_applet');

  static Future<void> openUniMP(Map<String, dynamic> arg) async {
    await _channel.invokeMethod('openUniMP', arg);
  }
}
