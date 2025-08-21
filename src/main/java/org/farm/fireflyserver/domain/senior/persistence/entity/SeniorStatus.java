package org.farm.fireflyserver.domain.senior.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import org.hibernate.annotations.Comment;

@Table(name = "senior_status")
@Entity
@Getter(AccessLevel.PROTECTED)
public class SeniorStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seniorStatusid;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @MapsId
    @JoinColumn(name = "senior_id")
    private Senior seniorEntity;

    @Comment("위험 지수")
    private Double dangerRt;

    @Comment("수면 장애 점수")
    private Double sleepScr;

    @Comment("인지 저하 점수")
    private Double memoryScr;

    @Comment("무기력증 점수")
    private Double lowEngScr;

    @Comment("미활동 점수")
    private Double inactScr;

    @Comment("마지막 활동 시간")
    private Integer lastActTime;
}
