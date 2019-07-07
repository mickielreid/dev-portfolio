package com.enck.devfolo.controller;

import com.enck.devfolo.domain.Project;
import com.enck.devfolo.domain.Sections;
import com.enck.devfolo.domain.User;
import com.enck.devfolo.repository.UserRepository;
import com.enck.devfolo.service.ProjectService;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class ProjectControllerTest {

   private UserRepository repository = Mockito.mock(UserRepository.class);


   private ProjectService projectService  = new ProjectService(repository);


   private WebTestClient client = WebTestClient.bindToController(new ProjectController(projectService , repository)).build();


    @Test
    public void getStructure(){


        client.get().uri("/api/v1/pro/structure")
                .exchange()
                .expectStatus()
                .isOk();


    }

    @Test
    public void TestSaveProjectData(){

        Project project = returnNewProject("html" , "about html");
        User user = returnNewUser("jack" , "bag");

        when(repository.findById(anyString())).thenReturn(Mono.just(user));
        when(repository.save(any())).thenReturn(Mono.just(returnNewUserWithProject(user , project)));

        client.post().uri("/api/v1/pro/save?user-id=1")
                .body(Mono.just(project) , Project.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[?(@.title == 'html')].title").isEqualTo("html");

    }

    @Test
    public void TestSaveProjectDataDuplicateTitle(){

        Project project = returnNewProject("html" , "about html");
        Project projectDuplicate = returnNewProject("html" , " css is the boss");
        User user = returnNewUserWithProject("jack" , "bag", projectDuplicate);

        when(repository.findById(anyString())).thenReturn(Mono.just(user));
        when(repository.save(any())).thenReturn(Mono.just(returnNewUserWithProject(user , project)));

        client.post().uri("/api/v1/pro/save?user-id=1")
                .body(Mono.just(project) , Project.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[?(@.title == 'html')].title").isEqualTo("html");

    }
    @Test
    public void testUpdateProject(){
        final Project newProject = returnNewProject("java" , "about java");


        User user = returnNewUserWithProject();
        User expectedUser = returnNewUserWithProject("john" , "soup", newProject);

        when(repository.findById(anyString())).thenReturn(Mono.just(user));
        when(repository.save(any())).thenReturn(Mono.just(expectedUser));

        client.put().uri("/api/v1/pro/{userId}/{title}", "1", "html")
                .body(Mono.just(newProject) , Project.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody(User.class)
                .isEqualTo(expectedUser);

    }

    @Test
    public void testRemoveProjetandImage(){

        Project project = returnNewProject("html", "html.jpg");
        project.setImageName("html.jpg");

        User user = returnNewUserWithProject("bill" , "jones" , project);

        //cretes a image in the folder where the unit code will look for it
        createImage();


        when(repository.findById("1")).thenReturn(Mono.just(returnNewUserWithProject()));

        when(repository.save(any()))
                .thenReturn(Mono.just(user));


        client.delete().uri("/api/v1/pro/{userId}/{proTitle}", "1" , "html")
                .exchange()
                .expectStatus()
                .isNoContent();

    }

    @Test
    public void  testFindoneImage(){
        User user  = returnNewUserWithProject();

        //cretes a image in the folder where the unit code will look for it
        createImage();

        when(repository.findById(anyString())).thenReturn(Mono.just(user));

        client.get().uri("/api/v1/pro/find/{userId}/{proTitle}" , "1" , "html")
                .exchange()
                .expectStatus()
                .isOk();
    }

    //not working
    @Test
    public void testSaveImage(){
        Project project = returnNewProject("html", "html about");
       // project.setImageName("html.jpg");

        User user = returnNewUserWithProject("bruce", "banner", project);
        user.setId("5");

        FilePart filePart = Mockito.mock(FilePart.class);

        when(repository.findById(anyString())).thenReturn(Mono.just(user));

        when(filePart.filename()).thenReturn("randomName.jpg");

        // when(filePart.transferTo(new File("image.jpg"))).thenReturn(Mono.empty());
        when(filePart.transferTo(Mockito.any(File.class))).thenReturn(Mono.empty());


        client.post().uri("/api/v1/pro/{userId}/{proTitle}", "5" , "html")
                //.accept(MediaType.MULTIPART_FORM_DATA_VALUE)
               // .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                .body(Mono.just(filePart), FilePart.class)
                .exchange()
                .expectStatus().isOk();
    }





    //[] Utility Methods []//

    private User returnNewUser(String fName , String lName){
        Sections sections = new Sections("Being a Boss" , Arrays.asList("Java, Html , Make Money"));

        return new User( fName + "@gmail.com",lName + "123", fName, lName,
                "june 11 , 2019", sections);
    }

    private User returnNewUserWithProject(String fName , String lName, String title, String descriptiom){
        Sections sections = new Sections("Being a Boss" , Arrays.asList("Java, Html , Make Money"));

        return new User( fName + "@gmail.com",lName + "123", fName, lName,
                "june 11 , 2019", sections, new Project(title , descriptiom));
    }


    private User returnNewUserWithProject(User user, Project project){
        Sections sections = new Sections("Being a Boss" , Arrays.asList("Java, Html , Make Money"));

        return new User( user.getFirstName() + "@gmail.com",user.getLastName() + "123", user.getFirstName(),
                user.getLastName(),
                "june 11 , 2019", sections, project);


    }

    private User returnNewUserWithProject(){
        Sections sections = new Sections("Being a Boss", Arrays.asList("Java, Html , Make Money"));

        Project project = new Project("html" , "about html");
        project.setImageName("html.jpg");

        return new User("john@gmail.com","john123", "john", "soup",
                "june 11 , 2019", sections , project);
    }

    private User returnNewUserWithProject(String fName, String lName, Project project) {
        Sections sections = new Sections("Being a Boss", Arrays.asList("Java, Html , Make Money"));

        return new User(fName + "@gmail.com", lName + "123", fName, lName,
                "june 11 , 2019", sections, project);
    }

    //creates the image in the specfied path
    private void createImage(){
        String imagePath = "projectImages/1/html.jpg";
        List<String> lines = Arrays.asList("code i got from stack overflow");
        Path file = Paths.get(imagePath);
        try {
            Files.write(file, lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Project returnNewProject(String title, String descriptiom){
        return  new Project(title , descriptiom);
    }

}