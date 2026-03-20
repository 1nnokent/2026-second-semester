package com.example.demo.mapper;

import com.example.demo.dto.TaskResponseDto;
import com.example.demo.dto.TaskUpdateDto;
import com.example.demo.model.Task;
import com.example.demo.dto.TaskCreateDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public interface TaskMapper {
    Task toEntity(TaskCreateDto dto);
    Task updateEntity(TaskUpdateDto dto, @MappingTarget Task task);
    TaskResponseDto toResponseDto(Task task);
}
