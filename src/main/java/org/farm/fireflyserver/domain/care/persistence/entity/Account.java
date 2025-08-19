package org.farm.fireflyserver.domain.care.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

@Table(name = "account")
@Entity
@Getter
@NoArgsConstructor
public class Account {

    @Id
    private String accountId;

    @Comment("이름")
    private String name;

    @Comment("전화 번호")
    private String phoneNum;

    @Comment("계정 아이디")
    @Column(nullable = false, unique = true)
    private String id;

    @Comment("비밀번호")
    @Column(nullable = false)
    private String pwd;

    @Comment("계정 권한")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Authority authority;


}
