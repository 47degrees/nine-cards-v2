---
layout: docs
title: Database
section: docs
---

# Database

**Table of Contents**

* [1. Collections](#collections)
  * [1.1. Fields](#fields)
* [2. Cards](#cards)
  * [2.1. Fields](#fields-1)
* [3. Apps](#apps)
  * [3.1. Fields](#fields-2)
* [4. DockApps](#dockapps)
  * [4.1. Fields](#fields-3)
* [5. Moments](#moments)
  * [5.1. Fields](#fields-4)
* [6. Users](#users)
  * [6.1. Fields](#fields-5)
* [7. Widgets](#widgets)
  * [7.1. Fields](#fields-6)

## 1. Collections

This table stores all the information related to the collections created in 9 cards

### 1.1. Fields

* Position: `Int` - The position of the collection within the app
* Name: `String` - The name of the collection
* Type: `String` - The type of elements that the collection contains (e.g: apps, contacts...)
* Icon: `String` - The icon of the collection
* AppsCategory: `String` - The name of  the category of Google Play to which the collection is associated
* OriginalSharedCollectionId: `String` - Id of the original collection shared by other user
* SharedCollectionId: `String` - Id assigned to the collection when is shared to other users
* SharedCollectionSubscribed: `Boolean` - Field which indicates if the user want to receive notifications when the shared collection changes

## 2. Cards

This table stores all the information related to the cards created in 9 cards. Each card could represent an app, a phone number, an email address...

### 2.1. Fields

* Position: `Int` - Position of the card within the collection
* CollectionId: `Int` - The foreign key of  the collection to which the card is associated
* Term: `String` - The name which describes the element represented by the card
* PackageName: `String` - If the card represents an app, the package name of the app
* Type: `String` - The type of element that the card represents (e.g: app, phone, email...)
* Intent: `String` - If the card represents an app, the intent which will be launched when the app starts
* ImagePath: `String` - The path of the icon shown inside the card
* Notification: `String` - Generic text used for showing notifications

## 3. Apps

This table stores all apps installed in your cellphone

### 3.1. Fields

* Name: `String` - Name of the app
* PackageName: `String` - Package name of the application
* ClassName: `String` - Main class main in order to launch the app
* Category: `String` - Category of the app in Google Play
* dateInstalled: `Long` - Date installed
* dateUpdate: `Long` - Date updated
* version: `String` - Version of the app
* installedFromGooglePlay: `Boolean` - If the app was installed from Google Play

## 4. DockApps

This table stores the apps added to bottom dock in the launcher

### 4.1. Fields

* Name: `String` - Name of the app
* PackageName: `String` - Package name of the application
* DockType: `String` - The kind of dock (e.g: apps, contacts...)
* Intent: `String` - The intent which will be launched when the app starts
* imagePath: `String` - The path of the icon shown inside the card
* position: `Int` - Position of the card within the dock layout

## 5. Moments

This table stores the list of moments of the user and the information of them

### 5.1. Fields

* CollectionId: `String` - The foreign key of  the collection to which the card is associated
* Timeslot: `String` - Information about the time that the moment can be activated
* Wifi: `String` - List of wifi that the moment can be activated
* MomentType: `String` - The kind of moment: home. night, work, music...

## 6. Users

This table stores the information about the user using the app

### 6.1. Fields

* Email: `String` - Email of the user
* ApiKey: `String` - Api key of the user that we use for backend
* SessionToken: `String` - Session token of the user that we use for backend
* DeviceToken: `String` - Device token of the user that we use for backend
* MarketToken: `String` - Market token of the user that we use for backend
* Name: `String` - Name of the user in Google Plus
* Avatar: `String` - Avatar of the user in Google Plus
* Cover: `String` - Cover of the user in Google Plus
* DeviceName: `String` - Device name that the user is using in Google Drive
* DeviceCloudId: `String` - Device Cloud Id that the user is using in Google Drive

## 7. Widgets

This table stores the widgets that the user have added in every moment

### 7.1. Fields

* MomentId: `Int` - The foreign key of  the moment to which the widget is associated
* PackageName: `String` - Package name of the application of the widget
* ClassName: `String` - Main class of the widget selected
* AppWidgetId: `Int` - Android Widget Id given by AppWidget provider
* StartX: `Int` - Position where the widget starts in X
* StartY: `Int` - Position where the widget starts in Y
* SpanX: `Int` - Number of positions where the widget is expanded in X
* SpanY: `Int` - Number of positions where the widget is expanded in Y
* widgetType: `String` - Type of widget (e.g: app)
* label: `String` - Label of widget for specifics type of widgets
* imagePath: `String` - Image path of widget for specifics type of widgets
* intent: `String` -  Intent of widget for specifics type of widgets