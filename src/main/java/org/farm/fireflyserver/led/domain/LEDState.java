package org.farm.fireflyserver.led.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.farm.fireflyserver.common.util.BaseUpdatedTimeEntity;

@Entity
@Getter(AccessLevel.PROTECTED)
@NoArgsConstructor
@Table(name="led_state")
public class LEDState extends BaseUpdatedTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean isOn;

    @Enumerated(EnumType.STRING)
    private SensorGbn sensorGbn;



}
