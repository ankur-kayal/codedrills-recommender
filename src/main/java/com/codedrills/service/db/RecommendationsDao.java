package com.codedrills.service.db;

import com.codedrills.model.recommendation.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecommendationsDao extends JpaRepository<Recommendation, String> {}
