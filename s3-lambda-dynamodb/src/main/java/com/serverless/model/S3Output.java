package com.serverless.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.util.List;

@DynamoDbBean
public class S3Output {

    private String fileName;
    private List<String> output;
    private String itemCreationTimeStamp;
    private String itemUpdateTimeStamp;

    public S3Output() {
        super();
    }

    public S3Output(String fileName, List<String> output, String itemCreationTimeStamp, String itemUpdateTimeStamp) {
        this.fileName = fileName;
        this.output = output;
        this.itemCreationTimeStamp = itemCreationTimeStamp;
        this.itemUpdateTimeStamp = itemUpdateTimeStamp;
    }

    @DynamoDbPartitionKey
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<String> getOutput() {
        return output;
    }

    public void setOutput(List<String> output) {
        this.output = output;
    }

    public String getItemCreationTimeStamp() {
        return itemCreationTimeStamp;
    }

    public void setItemCreationTimeStamp(String itemCreationTimeStamp) {
        this.itemCreationTimeStamp = itemCreationTimeStamp;
    }

    public String getItemUpdateTimeStamp() {
        return itemUpdateTimeStamp;
    }

    public void setItemUpdateTimeStamp(String itemUpdateTimeStamp) {
        this.itemUpdateTimeStamp = itemUpdateTimeStamp;
    }
}
