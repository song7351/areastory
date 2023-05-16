package com.areastory.location.db.repository.support;

import com.areastory.location.db.entity.ArticleSub;
import com.areastory.location.dto.common.LocationDto;
import com.areastory.location.dto.response.LocationResp;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.areastory.location.db.entity.QArticle.article;
import static com.querydsl.jpa.JPAExpressions.select;


@Repository
@RequiredArgsConstructor
public class ArticleRepositorySupportImpl implements ArticleRepositorySupport {
    private final JPAQueryFactory query;

    @Override
    public LocationResp getDailyLikeCountData(String type, Long articleId, LocationDto locationDto, Long dailyLikeCount) {
        SubQueryExpression<Tuple> subQuery;
        Tuple tuple;
        if (type.equals("dongeupmyeon")) {
            subQuery = select(article.dosi, article.sigungu, article.dongeupmyeon, article.dailyLikeCount.max())
                    .from(article)
                    .where(getWhereAnd(locationDto)
                            .and(article.dailyLikeCount.gt(dailyLikeCount))
                            .and(article.articleId.ne(articleId)))
                    .groupBy(article.dosi, article.sigungu, article.dongeupmyeon);

            tuple = query
                    .select(article.dosi, article.sigungu, article.dongeupmyeon, article.dailyLikeCount, article.image, article.articleId)
                    .from(article)
                    .where(
                            Expressions.list(article.dosi, article.sigungu, article.dongeupmyeon, article.dailyLikeCount)
                                    .in(subQuery)
                                    .and(getWhereAnd(locationDto))
                                    .and(article.articleId.ne(articleId))
                    )
                    .groupBy(article.dosi, article.sigungu, article.dongeupmyeon)
                    .fetchOne();

            return tupleToDongeupmyeonResp(tuple);
        } else if (type.equals("sigungu")) {
            subQuery = select(article.dosi, article.sigungu, article.dailyLikeCount.max())
                    .from(article)
                    .where(getWhereAnd(locationDto)
                            .and(article.dailyLikeCount.gt(dailyLikeCount))
                            .and(article.articleId.ne(articleId)))
                    .groupBy(article.dosi, article.sigungu);

            tuple = query
                    .select(article.dosi, article.sigungu, article.dailyLikeCount, article.image, article.articleId)
                    .from(article)
                    .where(
                            Expressions.list(article.dosi, article.sigungu, article.dailyLikeCount)
                                    .in(subQuery)
                                    .and(getWhereAnd(locationDto))
                                    .and(article.articleId.ne(articleId))
                    )
                    .groupBy(article.dosi, article.sigungu)
                    .fetchOne();
            return tupleToSigunguResp(tuple);
        } else {
            subQuery = select(article.dosi, article.dailyLikeCount.max())
                    .from(article)
                    .where(getWhereAnd(locationDto)
                            .and(article.dailyLikeCount.gt(dailyLikeCount))
                            .and(article.articleId.ne(articleId)))
                    .groupBy(article.dosi);

            tuple = query
                    .select(article.dosi, article.dailyLikeCount, article.image, article.articleId)
                    .from(article)
                    .where(
                            Expressions.list(article.dosi, article.dailyLikeCount)
                                    .in(subQuery)
                                    .and(getWhereAnd(locationDto))
                                    .and(article.articleId.ne(articleId))
                    )
                    .groupBy(article.dosi)
                    .fetchOne();
            return tupleToDosiResp(tuple);

        }
    }

    @Override
    public List<LocationResp> getInitDongeupmyeon() {

        SubQueryExpression<Tuple> subQuery = JPAExpressions
                .select(article.dosi, article.sigungu, article.dongeupmyeon, article.dailyLikeCount.max())
                .from(article)
                .groupBy(article.dosi, article.sigungu, article.dongeupmyeon);

        List<Tuple> tuples = query
                .select(article.dosi, article.sigungu, article.dongeupmyeon, article.dailyLikeCount, article.image, article.articleId)
                .from(article)
                .where(
                        Expressions.list(article.dosi, article.sigungu, article.dongeupmyeon, article.dailyLikeCount).in(subQuery)
                )
                .groupBy(article.dosi, article.sigungu, article.dongeupmyeon)
                .fetch();

        return tuples.stream().map(this::tupleToDongeupmyeonResp).collect(Collectors.toList());
    }


    @Override
    public List<LocationResp> getInitSigungu() {
        SubQueryExpression<Tuple> subQuery = JPAExpressions
                .select(article.dosi, article.sigungu, article.dailyLikeCount.max())
                .from(article)
                .groupBy(article.dosi, article.sigungu);

        List<Tuple> tuples = query
                .select(article.dosi, article.sigungu, article.dailyLikeCount, article.image, article.articleId)
                .from(article)
                .where(
                        Expressions.list(article.dosi, article.sigungu, article.dailyLikeCount).in(subQuery)
                )
                .groupBy(article.dosi, article.sigungu)
                .fetch();

        return tuples.stream().map(this::tupleToSigunguResp).collect(Collectors.toList());
    }

    @Override
    public List<LocationResp> getInitDosi() {
        SubQueryExpression<Tuple> subQuery = JPAExpressions
                .select(article.dosi, article.dailyLikeCount.max())
                .from(article)
                .groupBy(article.dosi);

        List<Tuple> tuples = query
                .select(article.dosi, article.dailyLikeCount, article.image, article.articleId)
                .from(article)
                .where(
                        Expressions.list(article.dosi, article.dailyLikeCount).in(subQuery)
                )
                .groupBy(article.dosi)
                .fetch();
        return tuples.stream().map(this::tupleToDosiResp).collect(Collectors.toList());
    }

    private BooleanExpression getWhereOr(List<? extends LocationDto> locationList) {
        BooleanExpression whereBoolean = null;
        for (LocationDto locationDto : locationList) {
            BooleanExpression whereAnd = getWhereAnd(locationDto);
            if (whereBoolean == null)
                whereBoolean = whereAnd;
            if (whereAnd == null)
                continue;
            whereBoolean = whereBoolean.or(whereAnd);
        }
        return whereBoolean;
    }

    private BooleanExpression getWhereAnd(LocationDto locationDto) {
        BooleanExpression booleanExpression = null;
        booleanExpression = eqDosi(booleanExpression, locationDto.getDosi());
        booleanExpression = eqSigungu(booleanExpression, locationDto.getSigungu());
        booleanExpression = eqDongeupmyeon(booleanExpression, locationDto.getDongeupmyeon());
        if (locationDto instanceof ArticleSub)
            booleanExpression = eqLikeCount(booleanExpression, ((ArticleSub) locationDto).getDailyLikeCount());
        return booleanExpression;
    }

    private BooleanExpression eqLikeCount(BooleanExpression be, Long likeCount) {
        BooleanExpression eq = article.dailyLikeCount.eq(likeCount);
        if (be == null)
            return eq;
        if (eq == null)
            return be;
        return be.and(eq);
    }

    private BooleanExpression eqDosi(BooleanExpression be, String dosi) {
        BooleanExpression eq = null;
        if (StringUtils.hasText(dosi)) {
            eq = article.dosi.eq(dosi);
        }
        if (be == null)
            return eq;
        if (eq == null)
            return be;
        return be.and(eq);
    }

    private BooleanExpression eqSigungu(BooleanExpression be, String sigungu) {
        BooleanExpression eq = null;
        if (StringUtils.hasText(sigungu)) {
            eq = article.sigungu.eq(sigungu);
        }
        if (be == null)
            return eq;
        if (eq == null)
            return be;
        return be.and(eq);
    }

    private BooleanExpression eqDongeupmyeon(BooleanExpression be, String dongeupmyeon) {
        BooleanExpression eq = null;
        if (StringUtils.hasText(dongeupmyeon)) {
            eq = article.dongeupmyeon.eq(dongeupmyeon);
        }
        if (be == null)
            return eq;
        if (eq == null)
            return be;
        return be.and(eq);
    }

    private LocationResp tupleToDosiResp(Tuple tuples) {
        if (tuples == null) {
            return null;
        }
        return LocationResp.builder()
                .dosi(tuples.get(article.dosi))
                .likeCount(tuples.get(article.dailyLikeCount))
                .image(tuples.get(article.image))
                .articleId(tuples.get(article.articleId))
                .build();
    }

    private LocationResp tupleToSigunguResp(Tuple tuples) {
        if (tuples == null) {
            return null;
        }
        return LocationResp.builder()
                .dosi(tuples.get(article.dosi))
                .sigungu(tuples.get(article.sigungu))
                .likeCount(tuples.get(article.dailyLikeCount))
                .image(tuples.get(article.image))
                .articleId(tuples.get(article.articleId))
                .build();
    }


    private LocationResp tupleToDongeupmyeonResp(Tuple tuples) {
        if (tuples == null) {
            return null;
        }
        return LocationResp.builder()
                .dosi(tuples.get(article.dosi))
                .sigungu(tuples.get(article.sigungu))
                .dongeupmyeon(tuples.get(article.dongeupmyeon))
                .likeCount(tuples.get(article.dailyLikeCount))
                .image(tuples.get(article.image))
                .articleId(tuples.get(article.articleId))
                .build();
    }
}
