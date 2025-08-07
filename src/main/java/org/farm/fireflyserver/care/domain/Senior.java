package org.farm.fireflyserver.care.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter(AccessLevel.PROTECTED)
@NoArgsConstructor
public class Senior {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 25)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    private LocalDate birthday;

    @Column(length = 50)
    private String address;

    @Column(length = 25)
    private String town;

    @Column(length = 25)
    private String phoneNum;

    @Column(length = 25)
    private String homePhoneNum;

    @Column(length = 25)
    private String guardianPhoneNum;

    @ColumnDefault("true")
    @Column(nullable = false)
    private boolean isActive;

    @OneToMany(mappedBy = "senior")
    private List<Care> careList = new ArrayList<>();
}