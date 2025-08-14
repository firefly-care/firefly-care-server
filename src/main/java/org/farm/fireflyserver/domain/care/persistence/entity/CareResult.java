package org.farm.fireflyserver.domain.care.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.farm.fireflyserver.domain.care.CareAnswer;
import org.hibernate.annotations.Comment;

@Table(name = "care_result")
@Entity
@Getter(AccessLevel.PROTECTED) @Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long careResultId;

    @OneToOne
    @JoinColumn(name = "care_id")
    private Care care;

    @Comment("건강 상태")
    @Enumerated(EnumType.STRING)
    private CareAnswer health;

    @Comment("식사 기능")
    @Enumerated(EnumType.STRING)
    private CareAnswer eating;

    @Comment("인지 기능")
    @Enumerated(EnumType.STRING)
    private CareAnswer cognition;

    @Comment("의사소통")
    @Enumerated(EnumType.STRING)
    private CareAnswer communication;
}
