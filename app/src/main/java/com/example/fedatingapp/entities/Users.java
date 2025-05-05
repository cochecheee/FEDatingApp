package com.example.fedatingapp.entities;

import com.google.gson.annotations.SerializedName;

import java.util.Date;


public class Users {
    @SerializedName("id")
    private Long id;
    @SerializedName("name")
    private String name;
    @SerializedName("phone")
    private String phone;
    @SerializedName("gender")
    private String gender;
    @SerializedName("sexualOrientation")
    private String sexualOrientation;
    @SerializedName("birthday")
    private Date birthday;
    @SerializedName("biography")
    private String biography;
    @SerializedName("height")
    private double height;
    @SerializedName("weight")
    private int weight;
    @SerializedName("zodiacSign")
    private String zodiacSign;
    @SerializedName("personalityType")
    private String personalityType;
    @SerializedName("interests")
    private String interests;
    @SerializedName("address")
    private String address;
    @SerializedName("job")
    private String job;

    public Users() {
    }

    public Users(Long id, String name, String phone, String gender, String sexualOrientation, Date birthday, String biography, double height, int weight, String zodiacSign, String personalityType, String interests, String address, String job) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.gender = gender;
        this.sexualOrientation = sexualOrientation;
        this.birthday = birthday;
        this.biography = biography;
        this.height = height;
        this.weight = weight;
        this.zodiacSign = zodiacSign;
        this.personalityType = personalityType;
        this.interests = interests;
        this.address = address;
        this.job = job;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSexualOrientation() {
        return sexualOrientation;
    }

    public void setSexualOrientation(String sexualOrientation) {
        this.sexualOrientation = sexualOrientation;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
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

    public String getInterests() {
        return interests;
    }

    public void setInterests(String interests) {
        this.interests = interests;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }


    @Override
    public String toString() {
        return "Users{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + phone + '\'' +
                ", gender='" + gender + '\'' +
                ", sexualOrientation='" + sexualOrientation + '\'' +
                ", birthday=" + birthday +
                ", biography='" + biography + '\'' +
                ", height=" + height +
                ", weight=" + weight +
                ", zodiacSign='" + zodiacSign + '\'' +
                ", personalityType='" + personalityType + '\'' +
                ", interests='" + interests + '\'' +
                ", address='" + address + '\'' +
                ", job='" + job + '\'' +
                '}';
    }
}
