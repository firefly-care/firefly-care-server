package org.farm.fireflyserver.care.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter(AccessLevel.PROTECTED)
@NoArgsConstructor
public class Care {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_id")
    private CareManager manager;

    private LocalDateTime date;

    //private LocalDate reg_date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "senior_id")
    private Senior senior;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Result result;
}
