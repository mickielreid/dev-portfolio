package com.enck.devfolo.integration;

import com.enck.devfolo.domain.Project;
import com.enck.devfolo.domain.Sections;
import com.enck.devfolo.domain.User;
import com.enck.devfolo.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class UserControllerINTG {

    @Autowired
     private WebTestClient client;



    @Test
    public void  testUserStructure(){
        User expectedUser = new User(
                "mike@gmail.com", "mike123", "mike", "van",
                "may 11 , 2019", new Sections("" , null)
        );


        client.get().uri("/api/v1/user/structure")
                .exchange()
                .expectStatus().isOk()
                .expectBody(User.class)
                .isEqualTo(expectedUser);
    }

    @Test
    public void testGetAll(){


        client.get().uri("/api/v1/user")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[?(@.firstName == 'john')].email").isEqualTo("john@gmail.com");
    }

    @Test
    public void createNewUser(){
        User user =returnNewUser("Bruce" , "Golding");
        user.setId("22");

        client.post().uri("/api/v1/user")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(user) , User.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(User.class)
                .isEqualTo(user);
    }


    @Test
    public void  getById(){

         client.get().uri("/api/v1/user/findById?user-id=2")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                 .jsonPath("$[?(@.firstName == 'john')].email").isEqualTo("john@gmail.com");



    }

    @Test
    public void testDeleteById(){

        client.delete().uri("/api/v1/user/{userId}" , "1")
                .exchange()
                .expectStatus().isNoContent()
                .expectBody()
                .isEmpty();
    }


    @Test
    public void updateUser(){


        User user1 = new User(
                "mike@gmail.com","", "not john", "",
                "may 11 , 1988", null, null
        );

        client.patch().uri("/api/v1/user?user-id=2")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(user1) , User.class)
                .exchange()
                .expectStatus().isAccepted()
                .expectBody()
                .jsonPath("$[?(@.firstName == 'not john')].firstName").isEqualTo("not john");



    }


    //[] Utility Methods []//

    public User returnNewUser(String fName , String lName){
        Sections sections = new Sections("Being a Boss" , Arrays.asList("Java, Html , Make Money"));

        return new User( fName + "@gmail.com",lName + "123", fName, lName,
                "june 11 , 2019", sections);
    }


}
