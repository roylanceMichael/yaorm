## yaorm

Welcome to the readme/faq for yaorm.


***


## What is this?

This is **Y**et **A**nother **O**bject **R**elational **M**apping library. 


***


## Why was this made?

I did not find any good ORMs based on protobufs, so I built one that I use myself.


***

## How do I understand what this is doing quickly?

The best way is to look at the integration tests. MySQL, SQLite, and Postgres all have the same tests with the only difference being the service implementation that's passed on. Be sure to edit the properties files under resources with your appropriate settings. 

A lot of the services are similar to what EntityFramework (ActiveRecord, etc) do. 

***

## How does the system handle migrations? 

The migrations are handled automatically when the EntityProtoContext handleMigrations() method is called. This handles instantiating the model in the database if it doesn't exist, and making schema changes what the previous model saved to the current model saved. Yaorm does not generate a migration file that can be edited. 
  
There are pros and cons to both approaches (one where you specify every migration manually). You can still do that, but that framework isn't provided.

Also, because this is dealing with protobufs, non-backwards compatible changes are frowned upon in general (you might have a customer with an old copy of your code trying to communicate with an updated endpoint). In this sense, migrations that require the data to be put into a temp table and then re-added into new column names are discouraged. 

***

## Hold on, there are not many comments throughout the code base. Why not? 

I strive to make my code readable in the sense that it is self explanatory. A developer should be able to look at it and understand what is going on. If English comments make the code more readable, then something is wrong with either the code or the developer reading it. Not everything that the code does can be translated to natural language. It does require the developer to understand Java and Kotlin development (or just strongly typed OOP in general).

This is not a universally held point of view, I know, but it is a point of view held by many. The goal of this library is to serialize protobuf messages to a relational store in a clean and predictable way. To understand the library does require understanding how to code. 

***

## How do I get it?


In gradle, reference the following URL for your repository:
```groovy
repositories {
    mavenCentral()
    maven { url 'https://bintray.com/roylancemichael/maven' }
}

dependencies {
    compile(group: 'org.roylance.yaorm', name: 'api', version: '0.179')
}
```

Maven:
```xml
<repositories>
    <repository>
        <snapshots />
        <id>roylanceBintray</id>
        <name>roylanceBintray</name>
        <url>https://bintray.com/roylancemichael/maven</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>org.roylance.yaorm</groupId>
        <artifactId>api</artifactId>
        <version>0.179</version>
    </dependency>
</dependencies>
```

***

### What are the different projects? Which should I use?

I also developed another framework called yaclib (yet another cross language interface builder) that I use in yaorm as well. It contains all the protobuf models in the api project (as well as any logic needed). It will then programmatically build out an n-tier solution based on the model and controller definitions. This will build Java, CSharp, Python, and TypScript models and communication implementations with the rest endpoint (currently all it does is report meta information about the service). Here is a description of the projections:

api - the core model definition and service implementation of yaorm. For most projects, this is all you need.

csv - a way to convert Records into a CSV file (and back again).

android - android library for instantiating the yaorm proto context.

plugin - a way to automatically generate the IProtoGeneratedMessageBuilder based on current model project. Instantiating protobuf messages in Java through reflection alone does not work, so this type of builder is needed.

sapi - server api, this is the sample rest endpoint project created. 

***

### Anything else you can tell me?

This is still in development, although I have used this in many projects (many of which are in production for paying customers)! I'm adding features and functionality (that I need/want) constantly to new versions.

I currently have MySQL, Postgres, and SQLite implemented, as those are back-ends that I currently use. I'll do more as I need them.

Yaorm uses JDBC connections for everything but Android, I have a separate library for that (also on bintray and in this repo). RoboVM connections can use the JDBC SQLite one.


