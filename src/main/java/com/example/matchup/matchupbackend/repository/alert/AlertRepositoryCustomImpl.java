package com.example.matchup.matchupbackend.repository.alert;

import com.example.matchup.matchupbackend.dto.request.alert.AlertFilterRequest;
import com.example.matchup.matchupbackend.entity.Alert;
import com.example.matchup.matchupbackend.entity.AlertType;
import com.example.matchup.matchupbackend.entity.QAlert;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.matchup.matchupbackend.entity.QAlert.*;

@Repository
@RequiredArgsConstructor
public class AlertRepositoryCustomImpl implements AlertRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private QAlert qAlert = alert;

    @Override
    public Slice<Alert> findAlertSliceByAlertRequest(Long userId, AlertFilterRequest alertRequest, Pageable pageable) {
        List<Alert> content = queryFactory.selectFrom(alert)
                .where(alert.isDeleted.eq(false),
                        alertTypeEq(alertRequest.getAlertType()))
                .orderBy(alert.createTime.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = false;
        if (content.size() > pageable.getPageSize()) {
            hasNext = true;
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    private BooleanExpression alertTypeEq(AlertType alertType) {
        return (alertType != null) ? alert.alertType.eq(alertType) : null;
    }
}
