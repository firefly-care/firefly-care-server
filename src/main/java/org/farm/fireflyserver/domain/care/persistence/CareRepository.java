package org.farm.fireflyserver.domain.care.persistence;

import org.farm.fireflyserver.domain.account.persistence.entity.Account;
import org.farm.fireflyserver.domain.care.persistence.entity.Care;
import org.farm.fireflyserver.domain.care.web.dto.CareDto;
import org.farm.fireflyserver.domain.manager.persistence.entity.Manager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CareRepository extends JpaRepository<Care, Long>, CareRepositoryCustom {
    @Query("SELECT c FROM Care c " +
            "LEFT JOIN c.manager m " +
            "LEFT JOIN c.senior s WHERE " +
            "(:#{#req.type} IS NULL OR c.type = :#{#req.type}) AND " +
            "(:#{#req.result} IS NULL OR c.result = :#{#req.result}) AND " +
            "(:#{#req.startDate} IS NULL OR c.date >= :#{#req.startDate}) AND " +
            "(:#{#req.endDate} IS NULL OR c.date <= :#{#req.endDate}) AND " +
            "(:#{#req.searchTerm} IS NULL OR " +
            //돌봄 정보
            "c.content LIKE %:#{#req.searchTerm}% OR " +
            //Senior 정보
            "s.name LIKE %:#{#req.searchTerm}% OR " +
            "CAST(s.birthday AS string) LIKE %:#{#req.searchTerm}% OR " +
            "s.phoneNum LIKE %:#{#req.searchTerm}% OR " +
            "s.subPhoneNum LIKE %:#{#req.searchTerm}% OR " +
            "s.zipCode LIKE %:#{#req.searchTerm}% OR " +
            "s.address LIKE %:#{#req.searchTerm}% OR " +
            //Account 정보
            "m.name LIKE %:#{#req.searchTerm}% OR " +
            "m.phoneNum LIKE %:#{#req.searchTerm}%)")
    List<Care> search(@Param("req") CareDto.SearchRequest dto);

    List<Care> findAllByDateBetween(LocalDateTime start, LocalDateTime end);
    List<Care> findAllByDateBetweenOrderByDateDesc(LocalDateTime start, LocalDateTime end);

    List<Care> findAllBySeniorSeniorIdAndDateBetween(Long seniorId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT c.type, count(c) FROM Care c WHERE c.senior.seniorId = :seniorId AND YEAR(c.date) = :year AND MONTH(c.date) = :month GROUP BY c.type")
    List<Object[]> countCareByTypePerMonth(@Param("seniorId") Long seniorId, @Param("year") int year, @Param("month") int month);
    @Query("SELECT DATE(c.date), COUNT(c) FROM Care c WHERE c.date BETWEEN :start AND :end GROUP BY DATE(c.date) ORDER BY DATE(c.date)")
    List<Object[]> countByDateBetweenGroupByDate(LocalDateTime start, LocalDateTime end);

    // 특정 담당자가 수행한 돌봄 건수
    long countByManager(Manager manager);

    // 특정 담당자가 돌본 대상자 수
    @Query("select count(distinct c.senior) from Care c where c.manager = :manager")
    long countDistinctSeniorByManager(Manager manager);

    // 특정 담당자가 최근에 수행한 돌봄 기록
    Care findTopByManagerOrderByDateDesc(Manager manager);

}