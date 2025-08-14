package org.farm.fireflyserver.domain.care.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.farm.fireflyserver.common.util.BaseCreatedTimeEntity;
import org.farm.fireflyserver.domain.senior.persistence.entity.Senior;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity
@Getter(AccessLevel.PROTECTED)
@NoArgsConstructor
public class Care extends BaseCreatedTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    @Comment("돌봄 담당자")
    private CareManager manager;

    @Comment("돌봄 일시")
    private LocalDateTime date;

    //private LocalDate reg_date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Comment("돌봄 유형")
    private Type type;

    @Comment("돌봄 내용")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "senior_id")
    @Comment("돌봄 대상자")
    private Senior seniorEntity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Comment("돌봄 결과")
    private Result result;
}
