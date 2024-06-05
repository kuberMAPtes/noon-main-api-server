package com.kube.noon.building.repository;

public interface BuildingSummaryRepository {
    String findFeedAISummary(String title, String feedText);
}
