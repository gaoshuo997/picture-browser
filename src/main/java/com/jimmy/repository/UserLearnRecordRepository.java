package com.jimmy.repository;

import com.jimmy.entity.UserLearnRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface UserLearnRecordRepository extends JpaRepository<UserLearnRecord, String> ,
        JpaSpecificationExecutor<UserLearnRecord> {
    UserLearnRecord findByDayAndUserId(LocalDate day,Long userId);

    List<UserLearnRecord> findByUserId(Long userId);

    List<UserLearnRecord> findByUserIdAndDayBetween(Long userId,LocalDate startDate, LocalDate endDate);
}
