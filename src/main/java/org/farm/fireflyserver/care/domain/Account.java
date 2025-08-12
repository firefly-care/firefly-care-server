package org.farm.fireflyserver.care.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;


@Entity
@Getter(AccessLevel.PROTECTED)
@NoArgsConstructor
public class Account {

    @Id
    @Comment("계정 아이디")
    private String id;

    @Column(nullable = false)
    private String pwd;

    @Comment("계정 권한")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Authority authority;
}
