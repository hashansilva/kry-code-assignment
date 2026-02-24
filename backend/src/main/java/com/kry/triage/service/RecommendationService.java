package com.kry.triage.service;

import com.kry.triage.model.Recommendation;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RecommendationService {

    public Recommendation calculate(int score) {
        if (score < 5 || score > 15) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "score must be between 5 and 15");
        }

        if (score <= 7) {
            return Recommendation.CHAT;
        }
        if (score <= 11) {
            return Recommendation.NURSE;
        }
        return Recommendation.DOCTOR;
    }
}
