package org.farm.fireflyserver.domain.led.persistence.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.farm.fireflyserver.common.util.BaseUpdatedTimeEntity;

@Entity
@Getter(AccessLevel.PROTECTED)
@NoArgsConstructor
public class LedState extends BaseUpdatedTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean isOn;

    @Enumerated(EnumType.STRING)
    private SensorGbn sensorGbn;


}
