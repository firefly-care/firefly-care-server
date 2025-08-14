package org.farm.fireflyserver.domain.led.persistence.entity;

import jakarta.persistence.*;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter(AccessLevel.PROTECTED)
@NoArgsConstructor
@Entity
@Table(name="led_history")
public class LedHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean isOn;

    @Enumerated(EnumType.STRING)
    private SensorGbn sensorGbn;

    private LocalDateTime OnDate;


}
