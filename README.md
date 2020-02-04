# Task
<div>
        <image src="screenshots/login.jpg" width="200"/>
        <image src="screenshots/register-user.jpg" width="200"/>
        <image src="screenshots/task-uncompleted.jpg" width="200"/>
        <image src="screenshots/task-completed.jpg" width="200"/>
        <image src="screenshots/menu.jpg" width="200"/>
        <image src="screenshots/register-task.jpg" width="200"/>
        <image src="screenshots/edit-task.jpg" width="200"/>
        <image src="screenshots/deleting-task.jpg" width="200"/>
        <image src="screenshots/notification.jpg" width="200"/>
</div>

*This app is used to book tasks, this project use Firebase's authentication to login and how database the Firebase Cloud Firestore.

*This project was created initially during a conclusion of a kotlin course (commit 31, Jan / 11 /2020, caafbfaf39e3971a1bb3f25652add31c57ff39f9) and it had a crud of tasks using SQLite and login. After this commit, I decide to use this project to get more knowledge in Android and Kotlin besides showing my work. Now this project is getting improvements almost dayly, this project have now crud of tasks and login with Firebase Cloud Firestore, implementation of Observer, notification of due date using jobScheduler (notification still needs some improvements) and others few improvements.
if you want contact me call me for linkedin, email or my phone number.


# run

If you want to run this project you need to create a firebase account, to follow the Firebase instructions
and after putting the file google-services.json in the project after it you will create a Cloud Firestore
database following the instructions in the file: database.zip and to run this project if you
don't need doing the next step.

This project run in Android studio with Java 11 version, but if you need changing you can change it in Build.gradle (Module: app):

kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }


# Note
For while the function of notifications works when the app is running in background or not, the app send notifications about the tasks that have the due date equal "today" every 15 minutes using JobScheduler, and in some phones it's sending notification once. 

This app will get some improvemnts how: notifications system improvements, verification of email, login with google and image to profile 
