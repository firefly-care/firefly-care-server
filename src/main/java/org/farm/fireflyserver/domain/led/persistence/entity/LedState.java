package org.farm.fireflyserver.domain.led.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.farm.fireflyserver.common.util.BaseUpdatedTimeEntity;
import org.farm.fireflyserver.domain.senior.persistence.entity.Senior;
import org.hibernate.annotations.Comment;

@Entity
@Getter
@NoArgsConstructor
@Table(name="led_state")
public class LedState extends BaseUpdatedTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ledStateId;

    @Comment("LED ON/OFF 정보")
    private boolean isOn;

    @Enumerated(EnumType.STRING)
    @Comment("LED 센서 구분")
    private SensorGbn sensorGbn;

    @ManyToOne
    @JoinColumn(name = "senior_id", nullable = false)
    @Comment("대상자 식별 코드")
    private Senior senior;

}
