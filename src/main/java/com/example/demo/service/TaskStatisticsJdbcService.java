package com.example.demo.service;

import com.example.demo.dto.TaskPriorityCountDto;
import com.example.demo.model.Priority;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class TaskStatisticsJdbcService {

    private static final RowMapper<TaskPriorityCountDto> PRIORITY_COUNT_ROW_MAPPER =
            new RowMapper<>() {
                @Override
                public TaskPriorityCountDto mapRow(ResultSet resultSet, int rowNum)
                        throws SQLException {
                    return new TaskPriorityCountDto(
                            Priority.valueOf(resultSet.getString("priority")),
                            resultSet.getLong("tasks_count")
                    );
                }
            };

    private final JdbcTemplate jdbcTemplate;

    public TaskStatisticsJdbcService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<TaskPriorityCountDto> getTasksCountByPriority() {
        return jdbcTemplate.query("""
                select priority, count(*) as tasks_count
                from tasks
                group by priority
                order by priority
                """, PRIORITY_COUNT_ROW_MAPPER);
    }
}
