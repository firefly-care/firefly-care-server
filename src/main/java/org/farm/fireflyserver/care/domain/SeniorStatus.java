package org.farm.fireflyserver.care.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;

@Entity
@Getter(AccessLevel.PROTECTED)
public class SeniorStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @MapsId
    @JoinColumn(name = "senior_id")
    private Senior senior;

    private Double dangerRt;

    private Double sleepScr;
    private Double memoryScr;
    private Double lowEngScr;
    private Double inactScr;

    private Integer lastActTime;
}
