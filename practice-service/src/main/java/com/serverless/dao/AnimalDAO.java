package com.serverless.dao;

import com.serverless.dto.Animal;
import com.serverless.service.AnimalService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class AnimalDAO {
    private static final Logger LOGGER = LogManager.getLogger(AnimalService.class);
    private String info_id;
    private String name;
    private Boolean isMammal;
    private String livingPlace;
    private String food;

    public AnimalDAO(Animal animal) {
        if (animal != null) {
            this.name = animal.getName();
            this.isMammal = animal.isMammal();
            this.livingPlace = animal.getLivingPlace();
            this.food = animal.getFood();
        }
        generateHashedInfoId();
    }

    public AnimalDAO(Map<String, AttributeValue> attributeValueMap) {
        if (attributeValueMap != null) {
            this.name = attributeValueMap.get("name").s();
            this.isMammal = attributeValueMap.get("isMammal").bool();
            this.livingPlace = attributeValueMap.get("livingPlace").s();
            this.food = attributeValueMap.get("food").s();
            this.info_id = attributeValueMap.get("info_id").s();
        }
    }

    public AnimalDAO(String name, boolean isMammal, String livingPlace, String food) {
        this.name = name;
        this.isMammal = isMammal;
        this.livingPlace = livingPlace;
        this.food = food;
        generateHashedInfoId();
    }

    public AnimalDAO() {
        super();
        generateHashedInfoId();
    }

    private void generateHashedInfoId() {
        if (this.info_id == null) {
            Date date = Calendar.getInstance().getTime();
            //Generating random UUID number
            String generateUUIDNo = String.format("%010d",
                    new BigInteger(UUID.randomUUID().toString().replace("-", ""), 16));
            String id = date.toInstant() + generateUUIDNo.substring(generateUUIDNo.length() - 9);
            try {
                //Hashing the randomly generated UUID number
                MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                messageDigest.update(id.getBytes());
                byte[] hashedId = messageDigest.digest();
                this.info_id = Base64.getEncoder().encodeToString(hashedId);
            } catch (NoSuchAlgorithmException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    public static List<AnimalDAO> getAnimalDAOListFromAttributeMapList(List<Map<String, AttributeValue>> attributeValueMapList) {
        List<AnimalDAO> animalDAOList = new ArrayList<>();
        if (attributeValueMapList != null && attributeValueMapList.size() > 0) {
            for (Map<String, AttributeValue> attributeValueMap : attributeValueMapList) {
                animalDAOList.add(new AnimalDAO(attributeValueMap));
            }
        }
        return animalDAOList;
    }

    public Map<String, AttributeValue> getAttributeValueMap() {
        Map<String, AttributeValue> map = new HashMap<>() {{
            put("name", AttributeValue.builder().s(getName()).build());
            put("isMammal", AttributeValue.builder().bool(isMammal()).build());
            put("livingPlace", AttributeValue.builder().s(getLivingPlace()).build());
            put("food", AttributeValue.builder().s(getFood()).build());
            put("info_id", AttributeValue.builder().s(getInfo_id()).build());
        }};
        return map;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isMammal() {
        return isMammal;
    }

    public void setMammal(boolean mammal) {
        isMammal = mammal;
    }

    public String getLivingPlace() {
        return livingPlace;
    }

    public void setLivingPlace(String livingPlace) {
        this.livingPlace = livingPlace;
    }

    public String getFood() {
        return food;
    }

    public void setFood(String food) {
        this.food = food;
    }

    public String getInfo_id() {
        return info_id;
    }
}
