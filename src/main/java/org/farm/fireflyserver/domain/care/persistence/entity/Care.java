package org.farm.fireflyserver.domain.care.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.farm.fireflyserver.domain.care.Result;
import org.farm.fireflyserver.domain.care.Type;
import org.farm.fireflyserver.domain.senior.persistence.entity.Senior;

import org.farm.fireflyserver.common.util.BaseCreatedTimeEntity;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Table(name = "care")
@Entity
@Getter(AccessLevel.PROTECTED)
@NoArgsConstructor @AllArgsConstructor
@Builder
@AttributeOverride(name = "createdAt", column = @Column(name = "reg_date", columnDefinition = "TIMESTAMP COMMENT '등록일'"))
public class Care extends BaseCreatedTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long careId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    @Comment("돌봄 담당자")
    private Account managerAccount;

    @Comment("돌봄 일시")
    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Comment("돌봄 유형")
    private Type type;

    @Comment("돌봄 내용")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "senior_id")
    @Comment("돌봄 대상자")
    private Senior senior;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Comment("돌봄 결과")
    private Result result;
}
