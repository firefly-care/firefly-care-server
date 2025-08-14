package org.farm.fireflyserver.domain.led.persistence.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;

@Entity
@Getter(AccessLevel.PROTECTED)
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name="led_data")
public class LedData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ledDataId;

    @Enumerated(EnumType.STRING)
    private SensorGbn sensorGbn;

    private Timestamp regDt;

    @Column(length = 20)
    private String ledMtchnSn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "led_state_id")
    private LedState ledState;

}
