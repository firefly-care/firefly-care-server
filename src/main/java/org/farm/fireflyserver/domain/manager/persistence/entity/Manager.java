package org.farm.fireflyserver.domain.manager.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.farm.fireflyserver.domain.account.persistence.entity.Account;
import org.farm.fireflyserver.domain.care.persistence.entity.Care;
import org.farm.fireflyserver.domain.senior.persistence.entity.Gender;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Table(name = "manager")
@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Manager {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("담당자 PK")
    private Long managerId;

    @Column(nullable = false, length = 25)
    @Comment("이름")
    private String name;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(length = 25)
    @Comment("연락처")
    private String phoneNum;

    @Column(length = 25)
    @Comment("부연락처")
    private String subPhoneNum;

    @Comment("생년월일")
    private LocalDate birth;

    @Column(length = 25)
    @Comment("소속")
    private String affiliation;

    @Column(length = 25)
    @Comment("이메일")
    private String email;

    @Column(length = 50)
    @Comment("주소")
    private String address;

    @Comment("우편번호")
    private String zipCode;

    @Comment("특이사항")
    @Column(columnDefinition = "TEXT")
    private String note;

    @Comment("프로필 이미지 URL")
    private String imageUrl;

    @Comment("돌봄 건수")
    private Long seniorCnt;

    @Comment("대상자 수")
    private Long careCnt;

    @Comment("최근 돌봄 일자")
    private LocalDate recentCareDate;

    @OneToMany(mappedBy = "manager")
    private List<Care> careList = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    //돌봄 등록 시 careCnt 1 증가, recentCareDate 오늘 날짜로 변경
    public void addCare() {
        this.careCnt++;
        this.recentCareDate = LocalDate.now();
    }
}
