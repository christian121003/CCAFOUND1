package com.example.ccafound1;

public class StudentRanking {
    private String name;
    private String profileImageUrl;
    private int reportCount;

    public StudentRanking() {}

    public StudentRanking(String name, String profileImageUrl, int reportCount) {
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.reportCount = reportCount;
    }

    public String getName() {
        return name;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public int getReportCount() {
        return reportCount;
    }
}
