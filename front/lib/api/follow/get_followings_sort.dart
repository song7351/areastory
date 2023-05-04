import 'dart:convert';
import 'package:dio/dio.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

Future<List<Followings>> getFollowingsSort({
  required int page,
  required int type,
}) async {
  final dio = Dio(BaseOptions(
    baseUrl: '${dotenv.get('BASE_URL')}/api',
  ));

  final storage = FlutterSecureStorage();
  final userId = await storage.read(key: 'userId');

  final response = await dio.get('/following/$userId', queryParameters: {
    'page': page,
    'type': type,
  });

  if (response.statusCode == 200) {
    final jsonData = json.decode(response.toString());
    final List<dynamic> followingsList = jsonData['data'];
    final List<Followings> followings = followingsList
        .map((followingsJson) => Followings.fromJson(followingsJson))
        .toList();

    print('성공');
    return followings;
  } else {
    print('실패');
    throw Exception('Failed to load followers');
  }
}

class Followings {
  final int userId;
  final String nickname;
  final String profile;

  Followings({
    required this.userId,
    required this.nickname,
    required this.profile,
  });

  factory Followings.fromJson(Map<String, dynamic> json) {
    return Followings(
      userId: json['userId'],
      nickname: json['nickname'],
      profile: json['profile'],
    );
  }
}
