Avery Astorino 101295020

The project requires the postgresql-42.7.8.jar in the root folder to compile and run the Java application.

I have included two files in the docs folder, which show the ER diagram and the mapping to the schema. All tables in the database are normalized to 3NF.

Compilation: 
  cd app
  javac -cp postgresql-42.7.8.jar MemberService.java TrainerService.java AdminService.java GymManagement.java

To run: 
  java -cp .:postgresql-42.7.8.jar GymManagement

Video Link: https://youtu.be/mGOlUVkDSmo
