package com.enck.devfolo.integration;

import com.enck.devfolo.domain.Project;
import com.enck.devfolo.domain.Sections;
import com.enck.devfolo.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.test.context.junit4.SpringRunner;
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

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class ProjectControllerINTG {

    @Autowired
    private WebTestClient client;


    @Test
    public void TestSaveProjectData(){

        Project project = returnNewProject("html" , "about html");
        User user = returnNewUser("jack" , "bag");

        client.post().uri("/api/v1/pro/save?user-id=2")
                .body(Mono.just(project) , Project.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[?(@.title == 'html')].title").isEqualTo("html");

    }


    @Test
    public void testUpdateProject(){
        final Project newProject = returnNewProject("java" , "about java");
        Project trys = returnNewProject("random" , "about java");

        User expectedUser = returnNewUserWithProject("mike" , "van", newProject);
        expectedUser.setId("1");


        client.put().uri("/api/v1/pro/{userId}/{title}", "1", "footnet")
                .body(Mono.just(newProject) , Project.class)
                .exchange()
                .expectStatus().isOk();



        //"$.A[?(@.B[?(@.id == 1)])]"
    }

    @Test
    public void testRemoveProjetandImage(){



        client.delete().uri("/api/v1/pro/{userId}/{proTitle}", "1" , "footnet")
                .exchange()
                .expectStatus()
                .isNoContent();

    }

    @Test
    public void  testFindoneImage(){

        //cretes a image in the folder where the unit code will look for it
        createImage();


        client.get().uri("/api/v1/pro/find/{userId}/{proTitle}" , "1" , "footnet")
                .exchange()
                .expectStatus()
                .isOk();
    }


    //not working
    @Test
    public void testSaveImage(){
        Project project = returnNewProject("html", "html.jpg");
        project.setImageName("html.jpg");

        User user = returnNewUserWithProject("bruce", "banner", "html", "about html");
        user.setId("1");
        user.addNewProject(project);

        FilePart filePart = Mockito.mock(FilePart.class);


        when(filePart.filename()).thenReturn("randomName.jpg");

        // when(filePart.transferTo(new File("image.jpg"))).thenReturn(Mono.empty());
        when(filePart.transferTo(Mockito.any(File.class))).thenReturn(Mono.empty());


        client.post().uri("/api/v1/pro/{userId}/{proTitle}", "1" , "html")
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
        String imagePath = "projectImages/1/footnet.jpg";
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
