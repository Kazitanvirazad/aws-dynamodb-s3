package com.serverless.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
public class ProfileCredential {
    private String profile_name;
    private String accessKey;
    private String secretKey;

    public ProfileCredential() {
        super();
    }

    public ProfileCredential(String profile_name, String accessKey, String secretKey) {
        this.profile_name = profile_name;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    @DynamoDbPartitionKey
    public String getProfile_name() {
        return profile_name;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setProfile_name(String profile_name) {
        this.profile_name = profile_name;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public boolean isValid() {
        if (this.profile_name != null && this.accessKey != null && this.secretKey != null) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "ProfileCredential{" +
                "profile_name='" + profile_name + '\'' +
                ", accessKey='" + accessKey + '\'' +
                ", secretKey='" + secretKey + '\'' +
                '}';
    }
}
