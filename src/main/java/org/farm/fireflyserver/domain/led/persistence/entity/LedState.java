package org.farm.fireflyserver.domain.led.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.farm.fireflyserver.common.util.BaseUpdatedTimeEntity;
import org.farm.fireflyserver.domain.senior.persistence.entity.Senior;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Table(name="led_state")
public class LedState extends BaseUpdatedTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ledStateId;

    @Comment("LED 식별 번호")
    private String ledMtchnSn;

    @Enumerated(EnumType.STRING)
    @Comment("LED 센서 구분")
    private SensorGbn sensorGbn;

    @Comment("LED ON/OFF 정보")
    @Enumerated(EnumType.STRING)
    private OnOff onOff;

    @ManyToOne
    @JoinColumn(name = "senior_id", nullable = false)
    @Comment("대상자 식별 정보")
    private Senior senior;

    public void updateState(OnOff onOff) {
        this.onOff = onOff;
    }

    public void updateTime(LocalDateTime updatedTime) {
        super.updateTime(updatedTime);
    }

}
