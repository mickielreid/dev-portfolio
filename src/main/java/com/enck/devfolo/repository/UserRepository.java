package com.enck.devfolo.repository;

import com.enck.devfolo.domain.Project;
import com.enck.devfolo.domain.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;


@Repository
public interface UserRepository extends ReactiveMongoRepository<User , String> {

    Mono<User> findByEmail(String s);
    Mono<Project> findByProjects(String title);
}

