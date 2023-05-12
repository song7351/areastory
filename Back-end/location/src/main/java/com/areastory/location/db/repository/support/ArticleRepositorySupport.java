package com.areastory.location.db.repository.support;


import com.areastory.location.dto.common.LocationDto;
import com.areastory.location.dto.response.LocationResp;

import java.util.List;

public interface ArticleRepositorySupport {

    List<LocationResp> getUserArticleList(Long userId, List<LocationDto> locationList);

    List<LocationResp> getDongeupmyeon();

    List<LocationResp> getSigungu();

    List<LocationResp> getDosi();

    LocationResp getDailyLikeCountData(String type, Long articleId, LocationDto locationDto, Long dailyLikeCount);
}