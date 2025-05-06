package com.example.fedatingapp.models;

import com.google.gson.annotations.SerializedName;

public class UserSettings {
    // ID might not be needed when sending data TO the backend for saving,
    // as the backend identifies the user by userId and likely manages the criteria ID itself.
    // Keep it if the GET request returns it and you need it for some reason.
    // private Long id;

    @SerializedName("datingPurpose") // Match JSON key from backend if different
    private String datingPurpose;

    @SerializedName("minAge") // Match JSON key
    private Integer minAge;

    @SerializedName("maxAge") // Match JSON key
    private Integer maxAge;

    @SerializedName("distance") // Match JSON key
    private Double distance; // Use Double or Integer based on backend (SeekBar gives int)

    @SerializedName("interests") // Match JSON key
    private String interests;

    @SerializedName("zodiacSign") // Match JSON key
    private String zodiacSign;

    @SerializedName("personalityType") // Match JSON key
    private String personalityType;

    // Default constructor (often needed by libraries like Gson)
    public UserSettings() {
    }

    // Constructor (optional if you use setters)
    public UserSettings(String datingPurpose, Integer minAge, Integer maxAge, Double distance, String interests, String zodiacSign, String personalityType) {
        this.datingPurpose = datingPurpose;
        this.minAge = minAge;
        this.maxAge = maxAge;
        this.distance = distance;
        this.interests = interests;
        this.zodiacSign = zodiacSign;
        this.personalityType = personalityType;
    }

    // --- Getters ---
    public String getDatingPurpose() { return datingPurpose; }
    public Integer getMinAge() { return minAge; }
    public Integer getMaxAge() { return maxAge; }
    public Double getDistance() { return distance; }
    public String getInterests() { return interests; }
    public String getZodiacSign() { return zodiacSign; }
    public String getPersonalityType() { return personalityType; }

    // --- Setters ---
    public void setDatingPurpose(String datingPurpose) { this.datingPurpose = datingPurpose; }
    public void setMinAge(Integer minAge) { this.minAge = minAge; }
    public void setMaxAge(Integer maxAge) { this.maxAge = maxAge; }
    public void setDistance(Double distance) { this.distance = distance; }
    public void setInterests(String interests) { this.interests = interests; }
    public void setZodiacSign(String zodiacSign) { this.zodiacSign = zodiacSign; }
    public void setPersonalityType(String personalityType) { this.personalityType = personalityType; }
}