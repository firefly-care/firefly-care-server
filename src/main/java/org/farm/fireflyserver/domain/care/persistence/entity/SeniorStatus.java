package org.farm.fireflyserver.domain.care.persistence.entity;

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
    private Long seniorStatusId;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @MapsId
    @JoinColumn(name = "senior_id")
    private Senior senior;

    private Double dangerRt;

    private Double sleepScr;
    private Double memoryScr;
    private Double lowEngScr;
    private Double inactScr;

    @Comment("마지막 활동 시간")
    private Integer lastActTime;
}
