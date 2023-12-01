package com.serverless.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.serverless.dao.AnimalDAO;


public final class Animal {

    private String key;

    private String name;

    @JsonProperty(value = "isMammal")
    private boolean isMammal;

    private String livingPlace;
    private String food;

    public Animal(String key, String name, boolean isMammal, String livingPlace, String food) {
        this.key = key;
        this.name = name;
        this.isMammal = isMammal;
        this.livingPlace = livingPlace;
        this.food = food;
    }

    public Animal(AnimalDAO animalDAO) {
        if (animalDAO != null) {
            this.key = animalDAO.getInfo_id();
            this.name = animalDAO.getName();
            this.isMammal = animalDAO.isMammal();
            this.livingPlace = animalDAO.getLivingPlace();
            this.food = animalDAO.getFood();
        }
    }

    public Animal() {
        super();
    }

    public String getName() {
        return name;
    }

    @JsonIgnore
    public boolean isMammal() {
        return isMammal;
    }

    public String getLivingPlace() {
        return livingPlace;
    }

    public String getFood() {
        return food;
    }

    public String getKey() {
        return key;
    }
}
