package com.enck.devfolo.service;

import com.enck.devfolo.domain.Project;
import com.enck.devfolo.domain.Sections;
import com.enck.devfolo.domain.User;
import com.enck.devfolo.repository.UserRepository;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProjectServiceTest {


//    UserRepository repository = Mockito.mock( UserRepository.class);
//
//    ProjectService service = new ProjectService(repository);


    @Mock
    UserRepository repository;

    ProjectService service;

    @Before
    public void init() {
        service = new ProjectService(repository);
    }

    @Test
    public void testTheTesting() {

        User user = returnNewUser("jack", "Bruse");

        System.out.println(user);
        Mono<User> expectedUser = Mono.just(user);

        when(repository.findById("1")).thenReturn(expectedUser);

        Mono<User> restlt = service.tester("1");


        System.out.println("Mono " + restlt);

        assertEquals(expectedUser, restlt);

    }


    @Test
    public void testSaveProjectImage() {
        Project project = returnNewProject("html", "html.jpg");
        project.setImageName("html.jpg");

        User user = returnNewUserWithProject("bruce", "banner", "html", "about html");
        user.setId("1");
        user.addNewProject(project);

        FilePart filePart = Mockito.mock(FilePart.class);

        when(repository.findById(anyString())).thenReturn(Mono.just(user));

        when(filePart.filename()).thenReturn("randomName.jpg");

       // when(filePart.transferTo(new File("image.jpg"))).thenReturn(Mono.empty());
        when(filePart.transferTo(new File(anyString()))).thenReturn(Mono.empty());

        Mono<ResponseEntity<?>> actual = service.saveProjectImage("1", "html", filePart);

       // ResponseEntity expected = new ResponseEntity("The image was saved ", HttpStatus.ACCEPTED);

        actual.subscribe(s -> {
            System.out.println(s.getBody());
        });




        // System.out.println(ss.getStatusCode().toString());
        //assertEquals(Mono.just(expected) , actual);
    }

    @Test
    public void testDeleteImageProject(){

        //proprities
        User user = null;
        Project project = null;


        //their defitions
        project = returnNewProject("html", "html.jpg");
        project.setImageName("html.jpg");

        user = returnNewUserWithProject("bill" , "jones" , project);

        //cretes a image in the folder where the unit code will look for it
        createImage();


        when(repository.findById("1")).thenReturn(Mono.just(user));

        when(repository.save(any()))
                .thenReturn(Mono.just(returnNewUserWithProject("bill" , "jones" , project)));

        Mono<ResponseEntity<String>> actual = service.deleteImageProject("1", "html");



       // assertEquals(expected , actual);
        actual.subscribe(s -> {
            String expected = "Image and project was deleted";
            assertEquals(expected, s.getBody());
        });

    }

    @Test
    public void testFindOneImage(){

        User user  = returnNewUserWithProject();

        //cretes a image in the folder where the unit code will look for it
        createImage();

        when(repository.findById(anyString())).thenReturn(Mono.just(user));

        Mono<ResponseEntity<?>> expected = service.findOneImage("1", "html");

        expected.subscribe(s -> {
            assertEquals(200 , s.getStatusCodeValue());
        });

    }

    @Test
    public void testUpdateProjectData(){

        final Project newProject = returnNewProject("java" , "about java");


        User user = returnNewUserWithProject();
        User expectedUser = returnNewUserWithProject("john" , "soup", newProject);

        when(repository.findById(anyString())).thenReturn(Mono.just(user));
        when(repository.save(any())).thenReturn(Mono.just(expectedUser));

        Mono<User> expected = service.updateProjectData(newProject, "1", "html");

        expected.subscribe(s -> {

            //System.out.println(s.getProjects().getFirst().getTitle());

            assertEquals("java" ,s.getProjects().getFirst().getTitle());
        });


    }

    //[]testing the utility methods[]//
    @Test
    public void testCheckIfTitleExits() {
        User expectedUser = returnNewUserWithProject("bruce", "gold", "html", "html dsc");
        Mono<Boolean> actualUser = service.checkIfTitleExits("html", Mono.just(expectedUser));
        assertEquals(true, actualUser.block());
    }

    @Test
    public void testCreateNewImageName() {

        String expected = "html.jpg";
        String projectTitle = "html";
        String oldImageName = "Photo.jpg";

        String actual = service.createNewImageName(projectTitle, oldImageName);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetImageName() {
        String projectTitle = "php";
        String expectedImageName = "php.jpg";

        Project expectedProject = returnNewProject("html", "about html");
        expectedProject.setImageName("html.jpg");

        Project expectedProject1 = returnNewProject("css", "about css");
        expectedProject1.setImageName("css.jpg");

        Project expectedProject3 = returnNewProject("php", "about php");
        expectedProject3.setImageName("php.jpg");

        Project expectedProject2 = returnNewProject("node", "about node");
        expectedProject2.setImageName("node.jpg");

        System.out.println(expectedProject3);

        List<Project> listProject = Arrays.asList(
                expectedProject, expectedProject1, expectedProject2, expectedProject3
        );


        String actualImageName = service.getImageName(listProject.iterator(), projectTitle);


        assertEquals(expectedImageName, actualImageName);
    }


    //[] Utility Methods []//

    private User returnNewUser(String fName, String lName) {
        Sections sections = new Sections("Being a Boss", Arrays.asList("Java, Html , Make Money"));

        return new User(fName + "@gmail.com", lName + "123", fName, lName,
                "june 11 , 2019", sections);
    }

    private User returnNewUserWithProject(String fName, String lName, String title, String descriptiom) {
        Sections sections = new Sections("Being a Boss", Arrays.asList("Java, Html , Make Money"));

        return new User(fName + "@gmail.com", lName + "123", fName, lName,
                "june 11 , 2019", sections, new Project(title, descriptiom));
    }

    private User returnNewUserWithProject(String fName, String lName, Project project) {
        Sections sections = new Sections("Being a Boss", Arrays.asList("Java, Html , Make Money"));

        return new User(fName + "@gmail.com", lName + "123", fName, lName,
                "june 11 , 2019", sections, project);
    }

    private User returnNewUserWithProject(){
        Sections sections = new Sections("Being a Boss", Arrays.asList("Java, Html , Make Money"));

        Project project = new Project("html" , "about html");
        project.setImageName("html.jpg");

        return new User("john@gmail.com","john123", "john", "soup",
                "june 11 , 2019", sections , project);
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


    private Project returnNewProject(String title, String descriptiom) {
        return new Project(title, descriptiom);
    }
}