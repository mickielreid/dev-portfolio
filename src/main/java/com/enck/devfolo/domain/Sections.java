package com.enck.devfolo.domain;

import lombok.Data;


@Data
public class Sections {



    private String aboutMe;
    private Iterable<String> skills;


    public Sections(String aboutMe, Iterable<String> skills) {
        this.aboutMe = aboutMe;
        this.skills = skills;
    }

    public Sections(){};
}
