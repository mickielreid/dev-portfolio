package com.enck.devfolo.controller;

import com.enck.devfolo.domain.Sections;
import com.enck.devfolo.domain.User;
import com.enck.devfolo.exception.ProjectException;
import com.enck.devfolo.repository.UserRepository;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


public class UserControllerTest {

    private UserRepository repository = Mockito.mock(UserRepository.class);

    WebTestClient client = WebTestClient.bindToController(new UserController(repository)).build();


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
        List allUsers = Arrays.asList(
                returnNewUser("Bruce" , "mack"),
                returnNewUser("John" , "Crow"),
                returnNewUser("jab", "Juice")
        );

        Flux<User> userFlux = Flux.fromIterable(allUsers);

        when(repository.findAll()).thenReturn(userFlux);

        client.get().uri("/api/v1/user")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[?(@.firstName == 'John')].email").isEqualTo("John@gmail.com");

    }//end get all


    @Test
    public void  getById(){
        Mono<User> user = Mono.just(returnNewUser("Bruce" , "Golding"));

        when(repository.findById("1")).thenReturn(user);

        client.get().uri("/api/v1/user/findById?user-id=1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(User.class)
                .isEqualTo(user.block());

    }

    @Test
    public void TestCreateNewUser(){

        User user =returnNewUser("Bruce" , "Golding");
        Mono<User> userToBeCreated = Mono.just(user);

        when(repository.save(user)).thenReturn(userToBeCreated);

        client.post().uri("/api/v1/user")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(userToBeCreated , User.class)
                .exchange()
                .expectStatus().isCreated();



    }

    @Test
    public void testDeleteById(){

        when(repository.deleteById("1")).thenReturn(Mono.empty());

        client.delete().uri("/api/v1/user/{userId}" , "1")
                .exchange()
                .expectStatus().isNoContent()
                .expectBody()
                .isEmpty();
    }


//    @Test
//    public void testDefaultGetMetod(){
//
//        Mono<Object> expectedError = Mono.error(new ProjectException("The path that you are looking for does not exist"));
//
//
//        client.get().uri("/api/v1/user/hhj")
//                .exchange()
//                .expectStatus().isBadRequest();
//    }


    @Test
    public void updateUser(){
        User oldUser  = returnNewUser("Bruce" , "Golding");
        User newUser = returnNewUser("Bruce22" , "be");

        when(repository.findById("1")).thenReturn(Mono.just(oldUser));
        when(repository.save(any())).thenReturn(Mono.just(newUser));


        client.patch().uri("/api/v1/user?user-id=1")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .body(Mono.just(newUser) , User.class)
                .exchange()
                .expectStatus().isAccepted();


    }








    //[] Utility Methods []//

    public User returnNewUser(String fName , String lName){
        Sections sections = new Sections("Being a Boss" , Arrays.asList("Java, Html , Make Money"));

        return new User( fName + "@gmail.com",lName + "123", fName, lName,
                "june 11 , 2019", sections);
    }

}