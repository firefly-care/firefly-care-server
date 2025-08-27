package org.farm.fireflyserver.domain.care.persistence;

import org.farm.fireflyserver.domain.care.persistence.entity.Care;
import org.farm.fireflyserver.domain.care.web.dto.CareDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

public interface CareRepository extends JpaRepository<Care, Long> {
    @Query("SELECT c FROM Care c " +
            "LEFT JOIN c.managerAccount m " +
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
            "s.homePhoneNum LIKE %:#{#req.searchTerm}% OR " +
            "s.zipCode LIKE %:#{#req.searchTerm}% OR " +
            "s.address LIKE %:#{#req.searchTerm}% OR " +
            "s.town LIKE %:#{#req.searchTerm}% OR " +
            //Account 정보
            "m.name LIKE %:#{#req.searchTerm}% OR " +
            "m.phoneNum LIKE %:#{#req.searchTerm}%)")
    List<Care> search(@Param("req") CareDto.SearchRequest dto);

    List<Care> findAllByDateBetween(LocalDateTime start, LocalDateTime end);

    List<Care> findAllBySeniorSeniorIdAndDateBetween(Long seniorId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT c.type, count(c) FROM Care c WHERE c.senior.seniorId = :seniorId AND YEAR(c.date) = :year AND MONTH(c.date) = :month GROUP BY c.type")
    List<Object[]> countCareByTypePerMonth(@Param("seniorId") Long seniorId, @Param("year") int year, @Param("month") int month);
}