package com.kry.triage.service;

import com.kry.triage.model.Recommendation;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RecommendationServiceTest {

    private final RecommendationService recommendationService = new RecommendationService();

    @Test
    void shouldMapScoresToRecommendations() {
        assertEquals(Recommendation.CHAT, recommendationService.calculate(5));
        assertEquals(Recommendation.CHAT, recommendationService.calculate(7));
        assertEquals(Recommendation.NURSE, recommendationService.calculate(8));
        assertEquals(Recommendation.NURSE, recommendationService.calculate(11));
        assertEquals(Recommendation.DOCTOR, recommendationService.calculate(12));
        assertEquals(Recommendation.DOCTOR, recommendationService.calculate(15));
    }

    @Test
    void shouldRejectOutOfRangeScores() {
        assertThrows(ResponseStatusException.class, () -> recommendationService.calculate(4));
        assertThrows(ResponseStatusException.class, () -> recommendationService.calculate(16));
    }
}
