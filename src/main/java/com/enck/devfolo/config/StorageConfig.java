package com.enck.devfolo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StorageConfig {

    private String location = "C:\\Users\\micki\\OneDrive\\Desktop\\storageImage";

    public String getLocation(){
        return location;
    }

    public void setLocation(String location){
        this.location = location;
    }

}
