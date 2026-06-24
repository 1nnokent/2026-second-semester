package com.example.demo.repository;

import com.example.demo.model.Priority;
import com.example.demo.model.Task;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByCompletedAndPriority(boolean completed, Priority priority);

    @Query("""
            select t
            from Task t
            where t.dueDate between :startDate and :endDate
            order by t.dueDate asc
            """)
    List<Task> findTasksDueWithinNext7Days(@Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    @Query("""
            select distinct t
            from Task t
            left join fetch t.attachments
            """)
    List<Task> findAllWithAttachments();
}
