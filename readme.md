## yaorm

Welcome to the readme/faq for yaorm.


***


## What is this?

This is **y**et **a**nother **o**bject **r**elational **m**apping library. 


***


## Why was this made?

I've been doing a lot of Android and iOS development lately (with RoboVM), and didn't find an ORM that I could use. So I wrote one for my own needs.


***


## How do I use it?


In gradle, reference the following URL for your repository:
```groovy
repositories {
    mavenCentral()
    maven { url 'http://mikeapps.org:8081/artifactory/libs-snapshot-local' }
}
```

Maven:
```xml
<repositories>
    <repository>
        <snapshots />
        <id>mike-MacBook</id>
        <name>mike-MacBook-snapshots</name>
        <url>http://mikeapps.org:8081/artifactory/libs-snapshot-local</url>
    </repository>
</repositories>
```