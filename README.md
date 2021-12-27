## Table of Contents

- [About the Project](#about-the-project)
  - [Built With](#built-with)
- [Features](#features)
- [Usage](#usage)
- [Roadmap](#roadmap)
  <!-- - [Musafir Database](#musafir-database) -->
- [Contact](#contact)
- [Project Link](#project-link)

<!-- ABOUT THE PROJECT -->

## About The Project

The project is done for an event named 'Softablitz' under the technical festival 'Avishkar 2021' of Motilal Nehru National Institute of Technology, Allahabad. The project CodeDock is an app that provides a medium to user for typing some code, compile it, run it and share with others (In layman terms). Through this app, User can easily type code in various language and provide the access of that document to fellow users and all of them can simultaneously update the code. Not only this, User can access, create and manage local files with Local Editor. Users can interact with other users thorugh Video Call as well as Chats ( Personally or in a Room). Users can create a Room and share a common document with other fellow users. Read the following text to know more magic that CodeDock can do!!!

### Built With

The project is wholly based on JAVA with a MYSQL database. 

#### The concepts of JAVA used are:

- Basics Of JAVA
- Object-Oriented Programming Concepts
- Socket Programming in JAVA
- JAVA GUI (e.g., Swing, AWT, etc.)
- Java JDBC

#### Techs used to create and manage database:

- Mysql Command-line
- LucidChart 
- Mysql Workbench

#### IDEs used are:

- IntelliJ Idea
- VSCode

<!-- FEATURES -->

## Features

- Users can signup/login/logout.
- Users can work on CodeDock globally as well as locally.
- By working globally user can create and work on documents which are managed by CodeDock server.
- By working locally user can create and work on documents which are managed locally on user end.
- User can open a directory and see its composition (In local editor).
- User can surf google (in global editor).
- User are able to fetch previous records
- Users can create/edit/save Document.
- Users can allow other users to edit/see in their document and after giving permission can block the user from further updating the code.
- When any user updates in the document the same change is reflected in every other user's window, working on the same document.
- Coding area has autocomplete/recommendation according to the language (Implemented from scratch).
- User can write and compile/run in cpp, c, java, python.
- User can also create and edit a txt file.
- Coding area shows line numbers.
- Users can run programs by providing input and can have output.
- Separate Chat section where users can send/receive messages both privately and in room.
- Write cursors for users are displayed in the panel (like Google Docs).
- Video communication between users is implemented from scratch.
- Users are allowed to open Multiple tabs for each session is Local Editor.

<!-- USAGE -->

## Usage

To use the application, one will first have to set up the 'CodeDock Database'. After that, he would have to clone 'CodeDockServer' repo (Link provided in Project link section) and setup JDK 8 in project structure. Then he would have to add mysql-connector-java jar file to module section in Server project. After that, one will have to clone CodeDock repo (Link provided in Project link section) and setup JDK 8 in project structure. Then, he would have to add jar files of webcam-capture-0.3.12-dist in module in modules of project structure. Now to run CodeDock you have to run Server.java file in CodeDockServer Project, then you will have to run Main.java file in CodeDock project.

<!-- CONTACT -->

## Contact

Aditya Darji - [https://github.com/aditya-darji](https://github.com/aditya-darji) - darjiaditya2000@gmail.com

Deepesh Rathi - [https://github.com/mrpirated](https://github.com/mrpirated) - deepeshrathi5050@gmail.com

Anubhav Rajput - [https://github.com/anubhav180400](https://github.com/anubhav180400) - anubhav.20194037@mnnit.ac.in

## Project Link

- CodeDock Repo: [https://github.com/aditya-darji/CodeDock](https://github.com/aditya-darji/CodeDock)
- CodeDockServer Repo: [https://github.com/aditya-darji/CodeDockServer](https://github.com/aditya-darji/CodeDockServer)
