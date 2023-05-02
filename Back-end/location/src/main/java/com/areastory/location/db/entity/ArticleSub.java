package com.areastory.location.db.entity;

import com.areastory.location.dto.common.LocationDto;
import lombok.Getter;

@Getter
public class ArticleSub extends LocationDto {
    private final Long dailyLikeCount;

    public ArticleSub(Long dailyLikeCount, String doName, String si, String gun, String gu, String dong, String eup, String myeon) {
        super(doName, si, gun, gu, dong, eup, myeon);
        this.dailyLikeCount = dailyLikeCount;
    }
}
