package org.farm.fireflyserver.led.domain;

import jakarta.persistence.*;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter(AccessLevel.PROTECTED)
@NoArgsConstructor
@Entity
public class LEDHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean isOn;

    @Enumerated(EnumType.STRING)
    private SensorGbn sensorGbn;

    //점등 시간 추가

}
