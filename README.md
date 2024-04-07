
# Get Together 2.0

This app is made for Android using Kotlin and Firebase as the backend. We organize our code using MVVM (Model-View-ViewModel) and use Dagger Hilt for easier dependency management.

There are two main parts to our app: posts and chats. Posts are for everyone to see, but chatting is only for people who are connected.

We store all the posts and chat messages securely in Firebase Firestore, so everything stays up-to-date and safe.

Besides posts and chats, users can do other things like reacting to posts, commenting, and saving their favorite posts. They can also edit their profile details.

In chats, users can react to messages and share things like videos and pictures, making conversations more fun and interactive.

Overall, our app is a friendly place for people to connect, share, and chat with each other easily.


## Project Description

├── data                # For data handling.   
|   ├── AuthData        # For all the functionality related Autentication 
│   └── repository      # For all the functionality related Post, chats , upload photo , etc .
|
├── model               # Model classes
|
├── di                  # Dependency Injection               
│   └── module          # DI Modules
|
├── ui                  # Activity/View layer
│   ├── auth            # Autnetication  Screen  View 
│   ├── chat            # Chat Screen View, Adapters
|   ├── post		     #	Post Screens ,Adapters 
│   └── sideMenu         # Detail Screen Activity 
|
├── utils               # Utility Classes / Kotlin extensions
|
└──viewModels 			 # AuthViewModel ,firebaseViewModel
