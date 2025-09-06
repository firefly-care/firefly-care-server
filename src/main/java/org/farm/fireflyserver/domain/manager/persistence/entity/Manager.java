package org.farm.fireflyserver.domain.manager.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.farm.fireflyserver.domain.care.persistence.entity.Care;
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

    @Column(length = 25)
    @Comment("연락처")
    private String phoneNum;

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

    @Comment("돌봄 건수")
    private Long seniorCnt;

    @Comment("대상자 수")
    private Long careCnt;

    @Comment("최근 돌봄 일자")
    private LocalDate recentCareDate;

    @OneToMany(mappedBy = "manager")
    private List<Care> careList = new ArrayList<>();
}
