import 'package:flutter/material.dart';
import 'package:flutter_uni_applet/flutter_uni_applet.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Flutter Plugin example app'),
        ),
        body: Center(
          child: Column(
            children: [
              const Padding(padding: EdgeInsets.all(20)),
              MaterialButton(
                color: Colors.blue,
                textColor: Colors.white,
                child: const Text('打开小程序'),
                onPressed: () async {
                  const arg = {
                    "appId": "__UNI__5E20470",
                    "version": "1.0.0",
                    "appName": "__UNI__5E20470",
                    "url":
                        "https://gitee.com/lzdjack-docs/assets/raw/master/__UNI__5E20470.wgt",
                    "redirectPath":
                        "pages/component/scroll-view/scroll-view?a=1&b=2&c=3"
                  };
                  await FlutterUniApplet.openUniMP(arg);
                },
              )
            ],
          ),
        ),
      ),
    );
  }
}
