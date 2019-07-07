package com.enck.devfolo.controller;


import com.enck.devfolo.domain.Sections;
import com.enck.devfolo.domain.User;
import com.enck.devfolo.exception.ProjectException;
import com.enck.devfolo.exception.UserNotFound;
import com.enck.devfolo.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/user")
@Slf4j
public class UserController {

    private UserRepository repository;

    @Autowired
    public UserController(UserRepository repository) {
        this.repository = repository;
    }


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Flux<User> allUsers() {


        return repository.findAll().switchIfEmpty(
                Mono.error(new UserNotFound("Sorry counld not find any User  "))

        );
    }


    @GetMapping("/structure")
    @ResponseStatus(HttpStatus.OK)
    public Mono<User> userStructure(){

        return Mono.just(new User(
                "mike@gmail.com", "mike123", "mike", "van",
                "may 11 , 2019", new Sections("" , null)
        ));
    }



    @GetMapping("/findById")
    @ResponseStatus(HttpStatus.OK)
    public Mono<User> findById(@RequestParam(name = "user-id") String id) {

        // log.warn("I am in the find by id");

        return repository.findById(id).switchIfEmpty(
                Mono.error(new UserNotFound("Sorry counld not find a User for id " + id))

        );

    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<User> createUser(@RequestBody User userMono) {

       return repository.save(userMono);
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<User> UpdateUser(@RequestBody User user, @RequestParam("user-id") String id) {

        //getting user if user does not exist then error
        Mono<User> oldUser = repository.findById(id).switchIfEmpty(
                Mono.error(new UserNotFound("Could Not Find a user for id " + id))
        );

        return oldUser.map(s -> {

            if (!user.getFirstName().equals(""))
                s.setFirstName(user.getFirstName());


            if (!user.getDob().equals(""))
                s.setDob(user.getDob());


            if (!user.getLastName().equals(""))
                s.setLastName(user.getLastName());


            if (user.getSections() != null)
                s.setSections(user.getSections());


            return s;
            //return repository.save(s).subscribe();
        }).flatMap(s ->
             repository.save(s).flatMap(Mono::just)
        );




    }



    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteUser(@PathVariable("userId") String id) {
            return  repository.deleteById(id);
    }


    //default for this path
//    @GetMapping("/**")
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    Mono<Void> defaultGetMetod(){
//        return Mono.error( new ProjectException("The path that you are looking for does not exist"));
//    }



}




