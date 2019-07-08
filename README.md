# Developer Portfolio
### What this Api does?
This API serves as the back for a frontend. When the API is combined with the front end it will allow a backend developer 
to have a portfolio landing page with out any knowledge of HTML, CSS and JavaScript. The developer will only be required 
to input their information in the different section such as: First Name, Last Name, DOB, About Me, Skills and their projects.



* Forgive me if I did not follow some of the reactive programming guidelines, this is my first project using Web Flux 
and MongoDb

## Stack:
  * Java 8
  * WebFlux
  * Lombok
  * Reactive-Mongodb
  
  
  ## Endpoints And Their Meanings:
  * `api/v1/users` - Get all users
  * `api/v1/users/findById?user-id=id` - Get user by id
  * `api/v1/users` - Post on this end point will create a user
  * `api/v1/users?user-id=id` - Patch on this endpoint will allow you to update this user 
  * `api/v1/users/user-id` - Delete on this endpoint will allow you to delete this user 
  
#### For The Projects
  * `api/v1/pro/save` - Post on this end point will create a new project
  * `api/v1/pro/save/save/image/{userId}/{proTitle}` - will save the project image 
  * `api/v1/pro/save/{userId}/{title}` - Patch on this endpoint will allow you to update this project 
  * `api/v1/pro/save/{userId}/{proTitle}` - Delete on this endpoint will allow you to delete this project and Image 
  * `api/v1/pro/save/find/{userId}/{proTitle}` - find Project Image



## User Stories:
   * As a user I should be able to create my account.
   * As a user I should be able to view my account.
   * As a user I should be able to delete my account.
   * As a user I should be able to update my account.
   * As a user I should be able to add a project to my project section.
   * As a user I should be able to add a project image for a project I created.
   * As a user I should be able to delete a project which in turn will delete the associated project image 
   * As a user I should be able to update my project information which includes the project image.

    
      
 **  Feel free if you want to contact me if you have any questions
 
 
      
      
      
      
      
