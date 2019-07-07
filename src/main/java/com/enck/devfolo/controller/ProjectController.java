package com.enck.devfolo.controller;

import com.enck.devfolo.domain.Project;
import com.enck.devfolo.domain.User;
import com.enck.devfolo.exception.ProjectException;
import com.enck.devfolo.repository.UserRepository;
import com.enck.devfolo.service.ProjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/pro")
@Slf4j
public class ProjectController {


   private ProjectService service;
   private UserRepository userRepository;

    @Autowired
    public ProjectController(ProjectService service, UserRepository userRepository) {
        this.service = service;
        this.userRepository = userRepository;
    }

    @GetMapping("/structure")
    @ResponseStatus(HttpStatus.OK)
    Project getTheClassStructure() {
        return new Project("title" , "description of the project");
    }

    //adding the data for the project data based on the user id
    @PostMapping(value = "/save", consumes = "Application/Json")
    @ResponseStatus(HttpStatus.OK)
    Mono<Iterable<Project>> saveProjectData(@RequestBody Project project, @RequestParam(name = "user-id") String userId) {

        //checking if there is user accicated with the id
        Mono<User> foundedUser = userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new ProjectException("Could not find a user accicouated with id  " + userId)));


        //setting the projects in the user document
        return foundedUser.flatMap(user -> {

            Mono<User> innerUser;


           if(user.getProjects() != null){
               for(Project pro : user.getProjects()){
                   if(pro.getTitle().equals(project.getTitle()))
                       return  Mono.error(new ProjectException("Sorry 2 projects cannot have the same name"));
               }
           }

            user.addNewProject(project);
            return innerUser = Mono.just(user);
        }).flatMap(user -> {
            Mono<User> savedUser = userRepository.save(user).flatMap(Mono::just);

             return  savedUser.flatMap(s -> Mono.just(s.getProjects()));

        });


    }


    //saving the image for the project
    @PostMapping(value = "/save/image/{userId}/{proTitle}" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity<?>> saveProjectImage(@RequestPart(name = "file") FilePart files,
                                         @PathVariable("userId") String id,
                                         @PathVariable("proTitle") String title) {
        return service.saveProjectImage(id,title, files);
    }


   // updating
   // updaing the data
    @PutMapping("/{userId}/{title}")
    @ResponseStatus(HttpStatus.OK)
   public Mono<User> updateProject(@RequestBody Project project, @PathVariable String userId, @PathVariable String title ){

       // log.warn("oldtitle " + title);
        return service.updateProjectData(project, userId, title);
    }



    @DeleteMapping(value = "/{userId}/{proTitle}")
    Mono<ResponseEntity<String>> removeProjetandImage(@PathVariable("userId") String userId,
                                                      @PathVariable("proTitle") String title){
        return service.deleteImageProject(userId, title);

    }

    //@GetMapping(value = "/image" , )
    @GetMapping(value = "/find/{userId}/{proTitle}" , produces = MediaType.IMAGE_JPEG_VALUE)
    Mono<ResponseEntity<?>> findoneImage(@PathVariable String userId , @PathVariable String proTitle){

       return service.findOneImage(userId , proTitle);
    }


    //default for this path
//    @GetMapping("/**")
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    Mono<Void> defaultGetMetod(){
//       return Mono.error( new ProjectException("The path that you are looking for does not exist"));
//    }
}
