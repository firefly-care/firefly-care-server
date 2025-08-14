package org.farm.fireflyserver.domain.care.persistence.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Table(name = "absent_result")
@Entity
@Getter(AccessLevel.PROTECTED) @Builder
@NoArgsConstructor
@AllArgsConstructor
public class AbsentResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long absentResultId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "care_id")
    private Care care;

    @Comment("대상자 부재 첫번째 질문")
    private Boolean first;
    @Comment("대상자 부재 두번째 질문")
    private Boolean second;
    @Comment("대상자 부재 세번째 질문")
    private Boolean third;
    @Comment("대상자 부재 네번째 질문")
    private Boolean fourth;
}
