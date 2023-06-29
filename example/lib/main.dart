import 'dart:async';

import 'package:flutter/material.dart';
import 'package:hgg_app_upgrade/hgg_app_upgrade.dart';

void main() => runApp(const MyApp());

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  _MyAppState createState() => _MyAppState();
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
        appBar: AppBar(title: const Text('App 升级测试')),
        body: const SingleChildScrollView(
            child: Center(
          child: Home(),
        )),
      ),
    );
  }
}

class Home extends StatefulWidget {
  const Home({Key? key}) : super(key: key);

  @override
  State<StatefulWidget> createState() => _HomeState();
}

class _HomeState extends State<Home> {
  late AppInfo _appInfo;

  // List<AppMarketInfo> _appMarketList = [];
  String _installMarkets = '';

  @override
  void initState() {
    // _checkAppUpgrade();
    _getInstallMarket();
    _getAppInfo();
    super.initState();
  }

  _checkAppUpgrade() {
    AppUpgrade.appUpgrade(
      context,
      _checkAppInfo(),
      cancelText: '以后再说',
      okText: '马上升级',
      iosAppId: 'id88888888',
      // appMarketInfo: AppMarket.tencent,
      okBackgroundColors: [const Color(0xFF765CFE), const Color(0xFF765CFE)],
      progressBarColor: const Color(0xFF5A46BE).withOpacity(.4),
      isDark: true,
      onCancel: () {
        debugPrint('onCancel');
      },
      onOk: () {
        debugPrint('onOk');
      },
      downloadProgress: (count, total) {
        // debugPrint('count:$count,total:$total');
      },
      downloadStatusChange: (DownloadStatus status, {dynamic error}) {
        debugPrint('status:$status,error:$error');
      },
    );
  }

  Future<AppUpgradeInfo> _checkAppInfo() async {
    //这里一般访问网络接口，将返回的数据解析成如下格式
    return Future.delayed(const Duration(seconds: 1), () {
      return AppUpgradeInfo(
        title: '新版本V1.1.1',
        apkDownloadUrl:
            'https://larp-gbo.oss-cn-chengdu.aliyuncs.com/2021-09-07/tYgyjZfradPoTLXPl38nMdkNigW5mqWa',
        contents: [
          '1、支持立体声蓝牙耳机，同时改善配对性能',
          '2、提供屏幕虚拟键盘',
          '3、更简洁更流畅，使用起来更快',
          '4、修复一些软件在使用时自动退出bug',
          '5、新增加了分类查看功能'
        ],
        force: false,
      );
    });
  }

  _getAppInfo() async {
    var appInfo = await HggUpgrade.appInfo;
    setState(() {
      _appInfo = appInfo;
    });
  }

  _getInstallMarket() async {
    List<String> marketList = await HggUpgrade.getInstallMarket();
    for (var f in marketList) {
      _installMarkets += '$f,';
    }
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: <Widget>[
        Text('packageName:${_appInfo.packageName}'),
        Text('versionName:${_appInfo.versionName}'),
        Text('versionCode:${_appInfo.versionCode}'),
        Text('安装的应用商店:$_installMarkets'),
        ElevatedButton(onPressed: _checkAppUpgrade, child: const Text('打开对话框')),
      ],
    );
  }
}
