package org.farm.fireflyserver.domain.led.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import java.sql.Timestamp;

@Entity
@Getter(AccessLevel.PROTECTED)
@NoArgsConstructor
@Table(name = "led_data")
public class LedData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ledDataId;

    private Long trgSn;
    private Long snsrSn;

//    @Enumerated(EnumType.STRING)
//    @Comment("LED 센서 구분")
//    private SensorGbn sensorGbn;
//
//    @Comment("등록 일시")
//    private Timestamp regDt;
//
//    @Column(length = 20)
//    @Comment("대상 가구 식별 코드")
//    private String ledMtchnSn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "led_state_id")
    private LedState ledState;

}
