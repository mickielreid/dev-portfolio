package com.enck.devfolo.exception;

import com.enck.devfolo.domain.User;
import reactor.core.publisher.Mono;

public class UserNotCreated extends RuntimeException{

    public UserNotCreated(Mono<User> userMono) {
        super("Could Not Create User with values " + userMono);
    }
}
