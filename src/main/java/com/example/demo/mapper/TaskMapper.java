package com.example.demo.mapper;

import com.example.demo.dto.TaskCreateDto;
import com.example.demo.dto.TaskResponseDto;
import com.example.demo.dto.TaskUpdateDto;
import com.example.demo.model.Task;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface TaskMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "completed", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    Task toEntity(TaskCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Task updateEntity(TaskUpdateDto dto, @MappingTarget Task task);

    TaskResponseDto toResponseDto(Task task);
}
