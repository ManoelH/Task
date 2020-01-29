# Task

*This app is used to book tasks, this project use Firebase's authentication to login and how database the Firebase Cloud Firestore.

*This project is used to show my work and my knowledge with Kotlin language with Android,
if you want contact me call me for linkedin, email or my phone number, for security motives this project
doesn't have the file google-services.json, but I have the apk project and all files.


# run

if you want to run this project you need to create a firebase account, to follow the Firebase instructions
and after putting the file google-services.json in the project after it you will create a Cloud Firestore
database following the instructions in the file: database.zip and to run this project if you
don't need doing the next step.

This project run in Android studio with Java 11 version, but if you need changing you can change it in Build.gradle (Module: app):

kotlinOptions {
        jvmTarget = JavaVersion.VERSION_11.toString()
    }


# Note
For while the function of notifications works when the app is running in background or not, the app send notifications about the tasks that have the due date equal "today" every 15 minutes using JobScheduler. In the future this implementation will be improvemented to verify and to send notifications in second plane blocking the stop of send notification when the app is closed.

This app will get some improvemnts how: notifications in second plane, login with google and image to profile 
