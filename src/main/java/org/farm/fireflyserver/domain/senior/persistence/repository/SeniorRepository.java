package org.farm.fireflyserver.domain.senior.persistence.repository;

import org.farm.fireflyserver.domain.senior.persistence.entity.Senior;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SeniorRepository extends JpaRepository<Senior, Long> {

    List<Senior> findAllByIsActiveTrue();
    int countByIsActiveTrue();
    List<Senior> findByIsActiveTrueAndIsLedUseTrue();
    int countByIsActiveTrueAndIsLedUseTrue();
    int countByIsActiveTrueAndIsAmiUseTrue();

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

    @Query("""
    SELECT s.town, COUNT(s)
    FROM Senior s
    WHERE s.isActive = true
    GROUP BY s.town
""")
    List<Object[]> countByTown();

    @Query("""
    SELECT s.seniorStatus.dangerLevel, COUNT(s)
    FROM Senior s
    WHERE s.isActive = true AND s.isLedUse = true
    GROUP BY s.seniorStatus.dangerLevel
""")
    List<Object[]> countByDangerLevel();

    // 위기 등급 기준
    @Query("""
        SELECT s.town, ss.dangerLevel, COUNT(s)
        FROM Senior s
        JOIN s.seniorStatus ss
        WHERE s.isActive = true
        GROUP BY s.town, ss.dangerLevel
    """)
    List<Object[]> countByTownAndDangerLevel();

    // 마지막 활동 기준
    @Query("""
        SELECT s.town, ss.lastActTime, COUNT(s)
        FROM Senior s
        JOIN s.seniorStatus ss
        WHERE s.isActive = true
        GROUP BY s.town, ss.lastActTime
    """)
    List<Object[]> countByTownAndLastActTime();

    // 이상 징후 기준
    @Query("""
        SELECT s.town, ss.state, COUNT(s)
        FROM Senior s
        JOIN s.seniorStatus ss
        WHERE s.isActive = true
        GROUP BY s.town, ss.state
    """)
    List<Object[]> countByTownAndState();

    @Query("SELECT s FROM Senior s " +
           "LEFT JOIN FETCH s.seniorStatus " +
           "LEFT JOIN FETCH s.careList c " +
           "LEFT JOIN FETCH c.managerAccount " +
           "WHERE s.seniorId = :seniorId")
    Optional<Senior> findSeniorDetailById(@Param("seniorId") Long seniorId);

    Optional<Senior> findByLedMtchnSn(String ledMtchnSn);
}