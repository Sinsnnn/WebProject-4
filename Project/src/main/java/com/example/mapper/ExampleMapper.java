package com.example.mapper;

import com.example.entity.ExampleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExampleMapper extends JpaRepository<ExampleEntity, Long> {
}