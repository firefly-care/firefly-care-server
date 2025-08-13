package org.farm.fireflyserver.senior.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import org.hibernate.annotations.Comment;

@Table(name = "senior_status")
@Entity
@Getter(AccessLevel.PROTECTED)
public class SeniorStatusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @MapsId
    @JoinColumn(name = "senior_id")
    private SeniorEntity seniorEntity;

    private Double dangerRt;

    private Double sleepScr;
    private Double memoryScr;
    private Double lowEngScr;
    private Double inactScr;

    @Comment("마지막 활동 시간")
    private Integer lastActTime;
}
