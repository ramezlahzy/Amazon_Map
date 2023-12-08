
# Amazon Map

A map that visualizes and stores the search locations of all users.

# App Setup

amazon Map is an Android application developed with Android Studio, Firebase Firestore, and Amazon Amplify. This guide outlines the steps to connect the Android app to Firebase and Amazon Amplify SDKs.

## Firebase Firestore Integration:

1. **Firebase Project Setup:**
   - Create a project on [Firebase Console](https://console.firebase.google.com/).
   - Follow the prompts to set up your project.

2. **Android App Configuration:**
   - Register your Android app on Firebase Console.
   - Download `google-services.json` and place it in the app module.

3. **Firebase SDK Integration:**
   - Add Firebase SDK dependencies to your app's build.gradle file.

4. **Firestore Database:**
   - Set up Firestore on Firebase Console.
   - Define the data structure for user searches.

5. **Authentication (Optional):**
   - If needed, configure Firebase Authentication.

## Amazon Amplify Integration:

Guide to set up an Android app with Amazon Location Service.

## 1. Creating Amazon Location Resources:
### 1.1 Choose a map style
- In Amazon Location console, create a new map.
- Provide Name and Description.
- Choose a map style and agree to Terms.
- Note the Amazon Resource Name (ARN) for your map.

### 1.2 Choose a place index
- In Amazon Location console, create a new place index.
- Add Name and Description.
- Choose a data provider and storage option.
- Agree to Terms and note the ARN for your place index.

## 2. Setting up Authentication:
### 2.1 Create an Amazon Cognito identity pool
- Go to Amazon Cognito console.
- Choose Manage Identity Pools and create a new pool.
- Enable access to unauthenticated identities.
- Configure IAM roles.
- Note down the IdentityPoolId.

### 2.2 Create IAM roles and policies
- Under View Details, enter a role name.
- Edit the policy document to add policies.
- Allow to create your identity pools.




For detailed documentation:
- Firebase Documentation: [https://firebase.google.com/docs](https://firebase.google.com/docs)
- Amplify Documentation: [https://docs.amplify.aws/](https://docs.amplify.aws/)

## Features

- Realtime data
- Light mode


## Tech Stack

**Client:** Android Studio

**Server:** Amazon Amplify, Firebase

##  Database Schema:


#### Collection: users
- **userId**: String
- **birthDate**: Timestamp

#### Collection: search
- **userId**: Reference(users)
- **searchWord**: String
- **latitude**: Double
- **longitude**: Double
