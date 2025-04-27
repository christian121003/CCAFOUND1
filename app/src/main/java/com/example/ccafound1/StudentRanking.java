package com.example.ccafound1;

public class StudentRanking {
    private final String name;
    private final String profileImageUrl;
    private final long reportCount;

    public StudentRanking(String name, String profileImageUrl, long reportCount) {
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.reportCount = reportCount;
    }

    public String getName() { return name; }
    public String getProfileImageUrl() { return profileImageUrl; }
    public long getFoundCount() { return reportCount; }
}

