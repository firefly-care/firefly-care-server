package org.farm.fireflyserver.domain.led.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.farm.fireflyserver.common.util.BaseCreatedTimeEntity;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
@Builder
@AllArgsConstructor
@Table(name = "led_history")
public class LedHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ledHistoryId;

    @Comment("LED 식별 번호")
    private String ledMtchnSn;

    @Enumerated(EnumType.STRING)
    @Comment("LED 센서 구분")
    private SensorGbn sensorGbn;

    @Comment("LED ON/OFF 정보")
    @Enumerated(EnumType.STRING)
    private OnOff onOff;

    @Comment("LED ON/OFF 시각")
    private LocalDateTime eventTime;
}