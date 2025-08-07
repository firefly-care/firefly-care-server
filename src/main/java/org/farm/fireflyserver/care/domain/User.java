package org.farm.fireflyserver.care.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter(AccessLevel.PROTECTED)
@NoArgsConstructor
public class User {

    @Id
    private String id;

    @Column(nullable = false)
    private String pwd;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Authority authority;
}
