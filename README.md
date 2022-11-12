# inventoryApp
My inventory app is a simple app designed to keep track of inventory. It was created so I could gain exposure to Android development.

# Features
The app launches into a login screen in which users must enter a username and password to access the app. Once logged in, the app will display the main inventory screen. There is a bottom navigation bar with three options: inventory, notifications, and settings. The inventory screen contains a scrolling list of items and their quantities. Users can add new items, adjust current item's quantites, and delete items. The notifications screen allows users to enable push notifications for when an item's quantity reaches 0. The settings screen allows the user to log out and return to the login screen. 

![Screenshot_1668220036](https://user-images.githubusercontent.com/86049959/201452493-0b03a159-1c57-4ce0-8580-4a7ed1eccb18.png)

# Development and Design
The entire app was created with Android Studio. The app has 5 screens which were designed with the IDE's Layout Editor. The UI designs were simple and intuitive so users can easily navigate the app. The user and item information are stored in a local SQLite database. 

# Testing
I ran my app on the AVD emulator to test the functionality. The emulator uses a Pixel 5 running on Android Pie. I made sure all features could be properly used and no crashes occured. 
