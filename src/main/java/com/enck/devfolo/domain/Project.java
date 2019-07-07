package com.enck.devfolo.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class Project {


    @JsonIgnore
    private String id = "No not set the id";

    private String title;

    private String description;

    @JsonIgnore
    private String imageName = "do not set the image name";


    public Project(String title, String dsc){
        this.title = title;
        this.description = dsc;
    }

    public Project(){}
}
