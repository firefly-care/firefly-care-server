package org.farm.fireflyserver.domain.led.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter(AccessLevel.PROTECTED)
@NoArgsConstructor
@Table(name = "led_data")
public class LedData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ledDataId;

    @Column(name = "TRG_SN")
    private String trgSn;
    @Column(name = "SNSR_SN")
    private String snsrSn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "led_state_id")
    private LedState ledState;
}
