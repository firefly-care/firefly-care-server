package org.farm.fireflyserver.domain.care.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Table(name = "senior")
@Entity
@Getter
@NoArgsConstructor
public class Senior {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seniorId;

    @Column(nullable = false, length = 25)
    @Comment("이름")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Comment("성별")
    private Gender gender;

    @Comment("생년월일")
    private LocalDate birthday;

    @Column(length = 50)
    @Comment("주소")
    private String address;

    @Column(length = 25)
    @Comment("읍면동")
    private String town;

    @Column(length = 25)
    @Comment("휴대 전화번호")
    private String phoneNum;

    @Column(length = 25)
    @Comment("집 전화번호")
    private String homePhoneNum;

    @Column(length = 25)
    @Comment("보호자 전화번호")
    private String guardianPhoneNum;

    @ColumnDefault("true")
    @Column(nullable = false)
    @Comment("서비스 이용 상태")
    private boolean isActive;

    @OneToMany(mappedBy = "senior")
    private List<Care> careList = new ArrayList<>();
}