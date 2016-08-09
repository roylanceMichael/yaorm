## yaorm

Welcome to the readme/faq for yaorm.


***


## What is this?

This is **Y**et **A**nother **O**bject **R**elational **M**apping library. 


***


## Why was this made?

I've been doing a lot of Android and iOS development lately (with RoboVM), and didn't find an ORM that I could use. So I wrote one for my own needs.

I am also using this library for Hive as well, they support CRUD operations in Hadoop.


***


## How do I get it?


In gradle, reference the following URL for your repository:
```groovy
repositories {
    mavenCentral()
    maven { url 'http://mikeapps.org:8081/artifactory/libs-snapshot-local' }
}

dependencies {
    compile(group: 'org.roylance', name: 'yaorm', version: '0.48-SNAPSHOT')
}
```

Maven:
```xml
<repositories>
    <repository>
        <snapshots />
        <id>mikeapps</id>
        <name>mikeapps-snapshots</name>
        <url>http://mikeapps.org:8081/artifactory/libs-snapshot-local</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>org.roylance</groupId>
        <artifactId>yaorm</artifactId>
        <version>0.48-SNAPSHOT</version>
    </dependency>
</dependencies>
```


***

## How do I use it?


First, in your code, implement the IEntity interface for your model. For example, this model

```java
public class TestModel implements IEntity {
    private String id;
    private String name;

    @Override
    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public TestModel setId(String value) {
        this.id = value;
        return this;
    }

    public TestModel setName(String value) {
        this.name = value;
        return this;
    }
}
```

implements the IEntity interface, and adds a new property as well. 

Make sure that your names are nouns, not verbs like "get" or "is".

There are a few service dependencies needed to save this to a data store. Here is a simple test to show them all in action:

```java
// arrange
// create a unique file
final File databaseFile = new File(UUID.randomUUID().toString().replace("-", ""));
// it shouldn't exist, but delete if it does, for some reason...
if (databaseFile.exists()) {
    databaseFile.delete();
}

final String testName = "NameToTest";

// this is the factory for the SQLite connection. Note, on Android, you can implement this interface and hook it in
final IConnectionSourceFactory sourceConnection =
        new SQLiteConnectionSourceFactory(databaseFile.getAbsolutePath());

// this is in charge of converting the results into the model you'd like. Using JDBC for now, but on Android, just implement this interface
final IGranularDatabaseService granularDatabaseService =
        new JDBCGranularDatabaseService(
        sourceConnection.getConnectionSource(),
        false);

try  {
    // this is the service that generates the sql for SQLite.
    final ISqlGeneratorService sqlGeneratorService =
            new SQLiteGeneratorService();

    // this entity access service uses the previous dependencies to do common CRUD operations against the data store
    final IEntityService<TestModel> entityAccessService =
            new EntityService<TestModel>(
                    TestModel.class,
                    granularDatabaseService,
                    sqlGeneratorService,
                    null);

    // create a new model to test
    final TestModel newModel = new TestModel()
            .setName(testName);
    newModel.setId("test");

    // act
    // create the sqlite table, we know it doesn't exist yet
    entityAccessService.createTable();

    // create the entity in the data store
    entityAccessService.create(newModel);

    // assert
    // let's get them all, be careful with this, obviously. there is also a filtering method
    final List<TestModel> foundTestModels = entityAccessService.getMany(1000);

    // verify we're greater than 0
    assert foundTestModels.size() > 0;

    final TestModel foundTestModel = foundTestModels.get(0);

    // verify that we incremented the id
    assert foundTestModel.getId().equals("test");

    // verify that the name is the same one we are expecting
    assert testName.equals(foundTestModel.getName());
}
finally {
    granularDatabaseService.close();

    // clean up after ourselves
    databaseFile.deleteOnExit();
}
```


***

### Anything else you can tell me?

This is still in development! I'm adding features and functionality (that I need/want) constantly.

I currently have SQLite and Hive implemented, as those are both back-ends that I currently use. I'll do more as I need them. I also just implemented Phoenix (Apache), but I'm still ironing that out.

For Hive, make sure that you have ACID compliance turned on. This will not work otherwise.

For Android, I implemented the IGranularDatabaseService and ICursor with Android specific libraries (they do things outside of JDBC).

This isn't even alpha yet, but I am using this code in my libraries (and there are tests). So use with caution. Pull requests welcome, I will review. 

I wrote this mostly in Kotlin because I really love that language (I view it as the next step in Java, personally). Kotlin has 100% compatibility with Java.