package org.farm.fireflyserver.led.domain;

import jakarta.persistence.*;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter(AccessLevel.PROTECTED)
@NoArgsConstructor
@Entity
@Table(name="led_history")
public class LEDHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean isOn;

    @Enumerated(EnumType.STRING)
    private SensorGbn sensorGbn;

    private LocalDateTime OnDate;


}
