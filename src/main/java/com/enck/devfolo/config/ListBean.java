package com.enck.devfolo.config;

import com.enck.devfolo.domain.Project;
import org.springframework.context.annotation.Bean;

import java.util.LinkedList;

public class ListBean {


    @Bean
    public LinkedList<Project> crateList(){
        return new LinkedList<>();
    }
}
