package com.example.planservice.domain.label.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.planservice.domain.label.Label;

@Repository
public interface LabelRepository extends JpaRepository<Label, Long> {
}
