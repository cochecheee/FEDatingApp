package com.example.fedatingapp.entities;


public class SearchCriteria {
    private Long id;

    private String datingPurpose;
    private int minAge;
    private int maxAge;
    private int distance;
    private String interests;
    private String zodiacSign;
    private String personalityType;

    public SearchCriteria() {
    }

    public SearchCriteria(Long id, String datingPurpose, int minAge, int maxAge, int distance, String interests, String zodiacSign, String personalityType) {
        this.id = id;
        this.datingPurpose = datingPurpose;
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.distance = distance;
        this.interests = interests;
        this.zodiacSign = zodiacSign;
        this.personalityType = personalityType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDatingPurpose() {
        return datingPurpose;
    }

    public void setDatingPurpose(String datingPurpose) {
        this.datingPurpose = datingPurpose;
    }

    public int getMinAge() {
        return minAge;
    }

    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public String getZodiacSign() {
        return zodiacSign;
    }

    public void setZodiacSign(String zodiacSign) {
        this.zodiacSign = zodiacSign;
    }

    public String getPersonalityType() {
        return personalityType;
    }

    public void setPersonalityType(String personalityType) {
        this.personalityType = personalityType;
    }
}
