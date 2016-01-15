package org.roylance.yaorm.services.readme;

import org.roylance.yaorm.services.*;
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseService;
import org.roylance.yaorm.services.sqlite.SQLiteConnectionSourceFactory;
import org.roylance.yaorm.services.sqlite.SQLiteGeneratorService;
import org.roylance.yaorm.testmodels.TestModel;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class JavaEntityAccessServiceTest {
//    @Test
    public void readmeTest() throws Exception {
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
            final IEntityService<Integer, TestModel> entityAccessService =
                    new EntityService<>(
                            TestModel.class,
                            granularDatabaseService,
                            sqlGeneratorService,
                            null);

            // create a new model to test
            final TestModel newModel = new TestModel()
                    .setName(testName);

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
            assert foundTestModel.getId() > 0;

            // verify that the name is the same one we are expecting
            assert testName.equals(foundTestModel.getName());
        }
        finally {
            granularDatabaseService.close();

            // clean up after ourselves
            databaseFile.deleteOnExit();
        }
    }
}
