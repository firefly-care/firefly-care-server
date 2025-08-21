package org.farm.fireflyserver.domain.senior.persistence.repository;

import org.farm.fireflyserver.domain.senior.persistence.entity.Senior;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SeniorRepository extends JpaRepository<Senior, Long> {

    @Query("""
       SELECT DISTINCT s FROM Senior s
       LEFT JOIN s.careList c
       LEFT JOIN c.managerAccount m
       LEFT JOIN s.seniorStatus ss
       WHERE (:isActive IS NULL OR s.isActive = :isActive)
       AND (
           :keywordType IS NULL OR :keyword IS NULL OR
           (:keywordType = 'name' AND LOWER(s.name) LIKE %:keyword%) OR
           (:keywordType = 'phone' AND LOWER(s.phoneNum) LIKE %:keyword%) OR
           (:keywordType = 'town' AND LOWER(s.town) LIKE %:keyword%) OR
           (:keywordType = 'address' AND LOWER(s.address) LIKE %:keyword%) OR
           (:keywordType = 'managerName' AND LOWER(m.name) LIKE %:keyword%) OR
           (:keywordType = 'managerPhone' AND LOWER(m.phoneNum) LIKE %:keyword%) OR
           (:keywordType = 'state' AND LOWER(ss.state) LIKE %:keyword%)
   )
""")
    List<Senior> searchSeniors(
            @Param("isActive") Boolean isActive,
            @Param("keywordType") String keywordType,
            @Param("keyword") String keyword
    );

}
