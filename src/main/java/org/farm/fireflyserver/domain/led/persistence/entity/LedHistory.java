package org.farm.fireflyserver.domain.led.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
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

    @Comment("LED ON/OFF 정보")
    private boolean isOn;

    @Enumerated(EnumType.STRING)
    @Comment("LED 센서 구분")
    private SensorGbn sensorGbn;

    @Comment("LED 점등 시각")
    private LocalDateTime onDate;
}