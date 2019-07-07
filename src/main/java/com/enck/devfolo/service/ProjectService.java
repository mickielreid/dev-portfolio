package com.enck.devfolo.service;

import com.enck.devfolo.domain.Project;
import com.enck.devfolo.domain.User;
import com.enck.devfolo.exception.ProjectException;
import com.enck.devfolo.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

@Service
@Slf4j
public class ProjectService {

    ///
    // public final static String ImageDir = "C:\\Users\\micki\\OneDrive\\Desktop\\javatrashprojects";

    private final static String ImageDir = "projectImages";
    //rember to remove the project repository

    private UserRepository userRepository;


    @Autowired
    public ProjectService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Mono<User> tester( String imageDir){
        return userRepository.findById(imageDir);
    }
    


    //TITLE --- []service methods///
    //this is responsible for saving the image
    public Mono<ResponseEntity<?>> saveProjectImage(String userId, String title, FilePart filePart) {

        String userFolderPath = ImageDir + "/" + userId;

        Mono<User> foundedUser = userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new ProjectException("Could not find a user with id of  " + userId)));

        //this creates a folder if the user is present
        foundedUser.hasElement().subscribe(s -> {
            if (s) {
                //creating the folder
                File folder = new File(userFolderPath);

                boolean isCreated = false;
                if (!folder.exists())
                    isCreated = folder.mkdir();
            }
        });

        //if the title exits the image will be saved else a error is returned
        return checkIfTitleExits(title, foundedUser).flatMap(decision -> {

            Mono<ResponseEntity<?>> rDecision = Mono.empty();

            if (decision) {

                //creating a new image name
                String newImageName = createNewImageName(title, filePart.filename());

                //saving the image in the created folder
                Mono<Void> ddd = filePart.transferTo(new File(userFolderPath + "/" +
                        newImageName).toPath()).then();

                //saving the image name for the project in the database
                //the image is named after the project title
                foundedUser.map(usr -> {

                    usr.getProjects().forEach(project -> {
                        if (project.getTitle().equals(title)) {
                            project.setImageName(newImageName);
                        }
                    });
                    // log.warn("user updated " + usr);

                    userRepository.save(usr).subscribe();
                    return usr;
                }).subscribe();

                rDecision = Mono.just(new ResponseEntity<String>("The image was saved", HttpStatus.ACCEPTED));

            } else {

                //if title is not found exception is thrown
                rDecision = Mono.error(new ProjectException("could now find a project by the title of " + title));

            }
            return rDecision;
        });


    }//end crete image


    public Mono<ResponseEntity<String>> deleteImageProject(String userId, String projectTitle) {
        String userFolderPath = ImageDir + "/" + userId;

        Mono<User> foundUser = userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new ProjectException("Could not find a user with id of  " + userId)));


        return checkIfTitleExits(projectTitle, foundUser).flatMap(decision -> {
            Mono<ResponseEntity<String>> finalResponce = Mono.empty();

            if (decision) {
                finalResponce = foundUser.flatMap(usr -> {

                    String fileName = "";
                    ResponseEntity<String> rResponce = null;
                    boolean isDeleted = false;

                    //getting the image name
                    fileName = getImageName(usr.getProjects().iterator(), projectTitle);

//                    Flux.fromIterable(u.getProjects()).filter(s -> s.getTitle().equals(projectTitle)).next()
//                            .map(Project::getImageName).block();
                    //saving what is saved



                    //deleting the image based on the file name from above
                    try {
                            isDeleted = Files.deleteIfExists(Paths.get(userFolderPath, fileName));

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    //returning a custom message based on the bool form delete if exist
                    if (isDeleted) {

                       boolean did = usr.getProjects().removeIf(s -> s.getTitle().equals(projectTitle));

                      // log.warn("did it " + did);

                        //[] subscribe was removed from the save unitTestMod
                       userRepository.save(usr);

                        rResponce = new ResponseEntity<>("Image and project was deleted", HttpStatus.NO_CONTENT);
                    } else {
                        rResponce = new ResponseEntity<>("Could not delete it at this moment ", HttpStatus.NOT_FOUND);
                    }

                    return Mono.just(rResponce);
                });


            } else {

                finalResponce = Mono.just(new ResponseEntity<>("Could not find a Project with title  " + projectTitle, HttpStatus.NOT_FOUND));
            }

            return finalResponce;
        });


    }//end delete image and data method


    //this will get a image by the name
    public Mono<ResponseEntity<?>> findOneImage(String userId, String title) {
        String userFolderPath = ImageDir + "/" + userId;


        return userRepository.findById(userId).flatMap(usr -> {
            Mono<ResponseEntity<?>> foundImage = Mono.empty();


            String imageName = getImageName(usr.getProjects().iterator(), title);

            //if we can t find a project with the title
            if (imageName.equals(" ")) {
                return Mono.error(new ProjectException("sorry could not find a project with a title of  " + title));
            }


            try {

                // String path = "C:/Users/micki/OneDrive/Pictures/head.jpg";
                ///the constructed path to the image
                String path = userFolderPath + "/" + imageName;


                InputStream image = new FileInputStream(path);

                //i used BufferedInputStream because it is a bit faster
                foundImage = Mono.just(new BufferedInputStream(image))
                        .map(imageStream -> {

                            InputStream imgStream = imageStream;

                            int contentLength = -1;

                            //getting the length of the stream for the content length
                            try {
                                contentLength = imageStream.available();
                            } catch (IOException e) {
                                e.printStackTrace();

                            }


                            return ResponseEntity.ok()
                                    .contentLength(contentLength)
                                    .body(new InputStreamResource(imgStream));
                        });


            } catch (IOException e) {

                foundImage = Mono.just(ResponseEntity
                        .badRequest()
                        .body("Could not find " + imageName));
            }

            return foundImage;

        }).switchIfEmpty(Mono.error(new ProjectException("Could not find a user with id of  " + userId)));


    }//find one image


    //this will update the project data and change the image name if need be
    //title should be named old title
    public Mono<User> updateProjectData(Project project, String userId, String title) {

        String userFolderPath = ImageDir + "/" + userId;

        Mono<User> foundedUser = userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new ProjectException("Could not find a user accicouated with id  " + userId)));


        return checkIfTitleExits(title, foundedUser).flatMap(decition -> {

            //will be returned to the alient
            Mono<User> finalResponce;

            //if the title exist
            if (decition) {
                finalResponce = foundedUser.map(usr -> {
                    //updating the project data if it is present

                    //looping through the projects
                    for (Project pro : usr.getProjects()) {

                        //if the title in the database matches the one provided by the client
                        if (pro.getTitle().equals(title)) {

                            String nTitle = project.getTitle();
                            String description = project.getDescription();

                            boolean titleBpp = !nTitle.equals("");

                            //renaming the title and the associated image
                            if (titleBpp) {
                                File oldImage = new File(userFolderPath + "/" + pro.getImageName());
                                String newImageName = createNewImageName(nTitle, pro.getImageName());
                                File newImage = new File(userFolderPath + "/" + newImageName);

                                if (!newImage.exists()) {
                                    oldImage.renameTo(newImage);

                                }

                                pro.setTitle(nTitle);
                                pro.setImageName(newImageName);
                                //log.warn("new Title" + pro.getImageName);

                            }

                            //renaming the image
                            boolean decBoo = !description.equals("");

                            if (decBoo) {
                                pro.setDescription(description);
                            }


                        }

                    }


                    return usr;
                }).flatMap(s -> {

                    //saving the image
                    return  userRepository.save(s).flatMap(Mono::just);
                });

                // finalResponce = userRepository.findById(userId);
            } else {
                finalResponce = Mono.error(new ProjectException("Could not find a project with title " + title));
            }

            return finalResponce;
        });


    }


    //TITLE --- [] utility methods///
    //check if  the title given title exits in the one of the projects
     Mono<Boolean> checkIfTitleExits(String title, Mono<User> user) {
        return user.map(u -> {
            boolean decition = false;

            for (Project fUser : u.getProjects()) {
                if (fUser.getTitle().equals(title)) {
                    decition = true;
                    break;
                } else {
                    decition = false;
                }
            }
            return decition;
        });
    }

    //this will create a image name from title, it will also extract the extension from the current image name
    //title html , oldImage- phono.jpg return - html.jpg
     String createNewImageName(String title, String oldImage) {

        String extension = FilenameUtils.getExtension(oldImage);

        return title + "." + extension;

    }

    //this will get a the image name based on the title from the list of images
    // projects -list of project, projectTitle - html, return - html.jpg/png/map
     String getImageName(Iterator projects, String projectTitle) {
        String name = " ";

        while (projects.hasNext()) {

            Project p = (Project) projects.next();

            if (p.getTitle().equals(projectTitle)) {

                name = p.getImageName();

                break;
            }
            log.warn("current project " + p);
        }

        return name;
    }
}
