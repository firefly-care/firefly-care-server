package org.farm.fireflyserver.domain.senior.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.farm.fireflyserver.common.util.BaseCreatedTimeEntity;
import org.farm.fireflyserver.domain.care.persistence.entity.Care;
import org.farm.fireflyserver.domain.led.persistence.entity.LedState;
import org.farm.fireflyserver.domain.manager.persistence.entity.Manager;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Table(name = "senior")
@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@AttributeOverride(name = "createdAt", column = @Column(columnDefinition = "TIMESTAMP COMMENT '서비스 시작일'"))
public class Senior extends BaseCreatedTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seniorId;

    @Column(nullable = false, length = 10)
    @Comment("이름")
    private String name;

    @Enumerated(EnumType.STRING)
    @Comment("성별")
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Comment("보장 유형")
    private BenefitType benefitType;

    @Comment("생년월일")
    private LocalDate birthday;

    @Column(length = 25)
    @Comment("주 연락처")
    private String phoneNum;

    @Column(length = 25)
    @Comment("부 연락처")
    private String subPhoneNum;

    @Column(length = 25)
    @Comment("우편번호")
    private String zipCode;

    @Column(length = 50)
    @Comment("주소")
    private String address;

    @Column(length = 10)
    @Comment("보호자 이름")
    private String guardianName;

    @Column(length = 25)
    @Comment("보호자 전화번호")
    private String guardianPhoneNum;

    @Column(columnDefinition = "TEXT")
    @Comment("특이사항")
    private String memo;

    @Column(nullable = false)
    @Comment("서비스 이용 상태")
    private boolean isActive = true;

    @Comment("LED 사용 여부")
    private boolean isLedUse;

    @Comment("LED 식별 번호")
    private String ledMtchnSn;

    @OneToMany(mappedBy = "senior")
    private List<Care> careList = new ArrayList<>();

    @OneToOne(mappedBy = "senior")
    private SeniorStatus seniorStatus;

    @OneToOne
    @JoinColumn(name = "manager_id", nullable = false)
    private Manager manager;

    @OneToMany(mappedBy = "senior")
    private List<LedState> ledStates = new ArrayList<>();

    public void deactivate() {
        this.isActive = false;
    }
    public void assignManager(Manager manager) {
        this.manager = manager;
    }
}