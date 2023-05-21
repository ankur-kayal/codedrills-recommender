package com.codedrills.service.db;

import com.codedrills.model.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProblemsDao extends JpaRepository<Problem, String> {}