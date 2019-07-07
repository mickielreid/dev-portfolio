package com.enck.devfolo.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedList;

@Document
@Data
public class User {

    @Id
    private String id;

    private String email;

    @JsonIgnore
    private String password;

    private String firstName;

    private String lastName;

    private String dob;

    private Sections sections;


    private LinkedList<Project> projects = new LinkedList<>();


    public User(String email, String password, String firstName, String lastName, String dob, Sections sections,
                Project projects ) {
       // this.projects = new LinkedList<>();
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.sections = sections;
        this.addNewProject(projects);
    }

    public User(String email, String password, String firstName, String lastName, String dob, Sections sections) {
        //this.projects = new LinkedList<>();
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
        this.sections = sections;
    }

    public User(){}

    public void addNewProject(Project project){
        this.projects.add(project);
    }
}
