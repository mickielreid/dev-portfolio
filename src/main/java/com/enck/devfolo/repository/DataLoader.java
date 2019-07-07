package com.enck.devfolo.repository;

import com.enck.devfolo.domain.Project;
import com.enck.devfolo.domain.Sections;
import com.enck.devfolo.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.LinkedList;

@Configuration
@Slf4j
public class DataLoader  {


    @Autowired
    UserRepository userRepository;





   // @Bean
    CommandLineRunner dataloader(){
        return  e -> {

            userRepository.deleteAll().subscribe();

            Sections sections1 = new Sections(
                    "i am just a man",
                    Arrays.asList("Java" , "Mysql")

            );

            Sections sections2 = new Sections(
                    "i am just a man2",
                    Arrays.asList("Java" , "Mysql")
            );

            Sections sections3 = new Sections(
                    "i am just a man3",
                    Arrays.asList("Java" , "Mysql")
            );

            //user classes

           Project project = new Project("footnet" , "you can buy food on it");
           project.setImageName("footnet.jpg");

            User user1 = new User(
                    "mike@gmail.com","mike123", "mike", "van",
                    "may 11 , 2019", sections1, project
            );
           // user1.setId("1");

            User user2 = new User(
                    "john@gmail.com","john123", "john", "soup",
                    "june 11 , 2019", sections2
            );
            //user2.setId("2");

            User user3 = new User(
                    "jess@gmail.com","jess123", "jess", "fun",
                    "december 11 , 2019", sections3
            );



            //saving the data
            userRepository.save(user1).subscribe();
            userRepository.save(user2).subscribe();
            userRepository.save(user3).subscribe();

        };


    }
}
