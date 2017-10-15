# squash

This app consumes the GitHub API to display information and recent commit history for user's favorite repositories. It uses **Retrofit 2** for HTTP requests and **Gson** for JSON deserialization.
The favorite repos are stored in a **Room** database, part of the new **Architecture Components** framework. This database is accessed directly for building new commit notifications and
through **LiveData** and **ViewModel** for displaying the favorites list in UI.

## How to run
You will need an up-to-date version of Android Studio and a phone running Android 5.0 (Marshmallow) or higher. After cloning the repo, just build and run the app - simple as that!

## TODO
* Docs
