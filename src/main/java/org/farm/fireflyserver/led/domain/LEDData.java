package org.farm.fireflyserver.led.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Getter(AccessLevel.PROTECTED)
@NoArgsConstructor
public class LEDData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private SensorGbn sensorGbn;

    private Timestamp regDt;

    @Column(length = 20)
    private String ledMtchnSn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "led_state_id")
    private LEDState ledState;

}
