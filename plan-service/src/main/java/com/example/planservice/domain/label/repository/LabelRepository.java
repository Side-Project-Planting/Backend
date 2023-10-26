package com.example.planservice.domain.label.repository;

import com.example.planservice.domain.label.Label;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LabelRepository extends JpaRepository<Label, Long> {
}
