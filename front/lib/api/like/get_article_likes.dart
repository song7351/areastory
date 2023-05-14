import 'dart:convert';
import 'package:dio/dio.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

Future<UserData> getArticleLikes({
  required int articleId,
}) async {
  final dio = Dio(BaseOptions(
    baseUrl: '${dotenv.get('BASE_URL')}/api/articles/like',
  ));

  final storage = new FlutterSecureStorage();
  final userId = await storage.read(key: 'userId');

  final response = await dio.get('/$articleId', queryParameters: {
    'userId': userId,
  });

  if (response.statusCode == 200) {
    final jsonData = json.decode(response.toString());
    final userData = UserData.fromJson(jsonData);
    print('게시글 좋아요 유저 요청 성공');
    return userData;
  } else {
    print('실패');
    throw Exception('Failed to load articles');
  }
}

class UserData {
  final int pageSize;
  final int totalPageNumber;
  final int totalCount;
  final int pageNumber;
  final bool nextPage;
  final bool previousPage;
  final List<User> users;

  UserData({
    required this.pageSize,
    required this.totalPageNumber,
    required this.totalCount,
    required this.pageNumber,
    required this.nextPage,
    required this.previousPage,
    required this.users,
  });

  factory UserData.fromJson(Map<String, dynamic> json) {
    final userList = json['users'] as List<dynamic>;
    final users = userList
        .map((userJson) => User.fromJson(userJson))
        .toList();

    return UserData(
      pageSize: json['pageSize'],
      totalPageNumber: json['totalPageNumber'],
      totalCount: json['totalCount'],
      pageNumber: json['pageNumber'],
      nextPage: json['nextPage'],
      previousPage: json['previousPage'],
      users: users,
    );
  }
}

class User {
  final int userId;
  final String nickname;
  final String? profile;
  final bool followYn;

  User({
    required this.userId,
    required this.nickname,
    this.profile,
    required this.followYn,
  });

  factory User.fromJson(Map<String, dynamic> json) {
    return User(
      userId: json['userId'],
      nickname: json['nickname'],
      profile: json['profile'],
      followYn: json['followYn'],
    );
  }
}