package org.roylance.yaorm.services.sqlite

import org.junit.Test
import org.roylance.yaorm.ComplexModel
import org.roylance.yaorm.services.jdbc.JDBCGranularDatabaseService
import org.roylance.yaorm.services.EntityMessageService
import org.roylance.yaorm.services.EntityService
import org.roylance.yaorm.utilities.ComplexModelBuilder
import java.io.File
import java.util.*

class SQLiteWeakTypeTests {
    @Test
    fun shouldNotSaveChildMarkedAsWeakAfterAlreadySavedThrough() {
        // arrange
        val database = File(UUID.randomUUID().toString().replace("-", ""))
        try {
            val sourceConnection = SQLiteConnectionSourceFactory(database.absolutePath)
            val granularDatabaseService = JDBCGranularDatabaseService(
                    sourceConnection,
                    false)
            val sqliteGeneratorService = SQLiteGeneratorService()
            val entityService = EntityService(granularDatabaseService, sqliteGeneratorService)
            val entityMessageService = EntityMessageService(ComplexModelBuilder, entityService, HashMap())

            val beacon = ComplexModel.Beacon.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setId1("1")
                .setId2("2")
                .setId3("3")
                .build()

            val clientBeacon = ComplexModel.ClientBeacon.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setUserId("monkey")
                .setBeacon(beacon)
                .build()

            entityMessageService.createEntireSchema(ComplexModel.getDescriptor())

            entityMessageService.merge(beacon)
            entityMessageService.merge(clientBeacon)

            // act

            val newClientBeacon = clientBeacon.toBuilder()
            newClientBeacon.beacon = beacon.toBuilder().setId1("hollywood").build()
            entityMessageService.merge(newClientBeacon.build())

            // assert
            val actualClientBeacon = entityMessageService.get(ComplexModel.ClientBeacon.getDefaultInstance(), clientBeacon.id)!!
            assert(actualClientBeacon.beacon.id1 == beacon.id1)
        }
        finally {
            database.deleteOnExit()
        }
    }

    @Test
    fun directChildRemove() {
        // arrange
        val database = File(UUID.randomUUID().toString().replace("-", ""))
        try {
            val sourceConnection = SQLiteConnectionSourceFactory(database.absolutePath)
            val granularDatabaseService = JDBCGranularDatabaseService(
                    sourceConnection,
                    false)
            val sqliteGeneratorService = SQLiteGeneratorService()
            val entityService = EntityService(granularDatabaseService, sqliteGeneratorService)
            val entityMessageService = EntityMessageService(ComplexModelBuilder, entityService, HashMap())

            val beacon = ComplexModel.Beacon.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setId1("1")
                    .setId2("2")
                    .setId3("3")
                    .build()

            val clientBeacon = ComplexModel.ClientBeacon.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setUserId("monkey")
                    .setBeacon(beacon)
                    .build()

            entityMessageService.createEntireSchema(ComplexModel.getDescriptor())

            entityMessageService.merge(beacon)
            entityMessageService.merge(clientBeacon)

            // act
            val newClientBeacon = clientBeacon.toBuilder().clearBeacon()
            entityMessageService.merge(newClientBeacon.build())

            // assert
            val actualClientBeacon = entityMessageService.get(ComplexModel.ClientBeacon.getDefaultInstance(), clientBeacon.id)!!
            println(actualClientBeacon.beacon.id1)
            assert(actualClientBeacon.beacon.id1 != beacon.id1)
            assert(actualClientBeacon.beacon.id1 == "")
        }
        finally {
            database.deleteOnExit()
        }
    }

    @Test
    fun shouldNotSaveChildMarkedAsWeakEvenIfNotExists() {
        // arrange
        val database = File(UUID.randomUUID().toString().replace("-", ""))
        try {
            val sourceConnection = SQLiteConnectionSourceFactory(database.absolutePath)
            val granularDatabaseService = JDBCGranularDatabaseService(
                    sourceConnection,
                    false)
            val sqliteGeneratorService = SQLiteGeneratorService()
            val entityService = EntityService(granularDatabaseService, sqliteGeneratorService)
            val entityMessageService = EntityMessageService(ComplexModelBuilder, entityService, HashMap())

            val beacon = ComplexModel.Beacon.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setId1("1")
                    .setId2("2")
                    .setId3("3")
                    .build()

            val clientBeacon = ComplexModel.ClientBeacon.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setUserId("monkey")
                    .setBeacon(beacon)
                    .build()

            entityMessageService.createEntireSchema(ComplexModel.getDescriptor())

            entityMessageService.merge(clientBeacon)

            // act

            val newClientBeacon = clientBeacon.toBuilder()
            newClientBeacon.beacon = beacon.toBuilder().setId1("hollywood").build()
            entityMessageService.merge(newClientBeacon.build())

            // assert
            val actualClientBeacon = entityMessageService.get(ComplexModel.ClientBeacon.getDefaultInstance(), clientBeacon.id)
            assert(actualClientBeacon != null)
            assert(actualClientBeacon!!.beacon.id == beacon.id)
            assert(actualClientBeacon.beacon.id1 != beacon.id1)
        }
        finally {
            database.deleteOnExit()
        }
    }

    @Test
    fun saveDeleteSaveNotRepeated() {
        // arrange
        val database = File(UUID.randomUUID().toString().replace("-", ""))
        try {
            val sourceConnection = SQLiteConnectionSourceFactory(database.absolutePath)
            val granularDatabaseService = JDBCGranularDatabaseService(
                    sourceConnection,
                    false)
            val sqliteGeneratorService = SQLiteGeneratorService()
            val entityService = EntityService(granularDatabaseService, sqliteGeneratorService)
            val entityMessageService = EntityMessageService(ComplexModelBuilder, entityService, HashMap())

            val beacon = ComplexModel.Beacon.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setId1("1")
                    .setId2("2")
                    .setId3("3")
                    .build()

            val clientBeacon = ComplexModel.ClientBeacon.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setUserId("monkey")
                    .setBeacon(beacon)
                    .build()

            entityMessageService.createEntireSchema(ComplexModel.getDescriptor())
            entityMessageService.merge(beacon)
            entityMessageService.merge(clientBeacon)

            // act
            val newClientBeacon = clientBeacon.toBuilder().clearBeacon()
            entityMessageService.merge(newClientBeacon.build())

            var actualClientBeacon = entityMessageService.get(ComplexModel.ClientBeacon.getDefaultInstance(), clientBeacon.id)
            assert(actualClientBeacon != null)
            assert(actualClientBeacon!!.beacon.id != beacon.id)
            assert(actualClientBeacon.beacon.id1 != beacon.id1)

            entityMessageService.merge(newClientBeacon.setBeacon(beacon).build())

            // assert
            actualClientBeacon = entityMessageService.get(ComplexModel.ClientBeacon.getDefaultInstance(), clientBeacon.id)
            assert(actualClientBeacon != null)
            assert(actualClientBeacon!!.beacon.id == beacon.id)
            assert(actualClientBeacon.beacon.id1 == beacon.id1)
        }
        finally {
            database.deleteOnExit()
        }
    }

    @Test
    fun shouldNotSaveChildMarkedAsWeakAfterAlreadySavedThroughRepeated() {
        // arrange
        val database = File(UUID.randomUUID().toString().replace("-", ""))
        try {
            val sourceConnection = SQLiteConnectionSourceFactory(database.absolutePath)
            val granularDatabaseService = JDBCGranularDatabaseService(
                    sourceConnection,
                    false)
            val sqliteGeneratorService = SQLiteGeneratorService()
            val entityService = EntityService(granularDatabaseService, sqliteGeneratorService)
            val entityMessageService = EntityMessageService(ComplexModelBuilder, entityService, HashMap())

            val beacon = ComplexModel.Beacon.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setId1("1")
                    .setId2("2")
                    .setId3("3")
                    .build()

            val weakChild = ComplexModel.WeakChild.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setSomeField("cool beans")
                    .build()

            val clientBeacon = ComplexModel.ClientBeacon.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setUserId("monkey")
                    .setBeacon(beacon)
                    .addWeakChildren(weakChild)
                    .build()

            entityMessageService.createEntireSchema(ComplexModel.getDescriptor())

            entityMessageService.merge(weakChild)
            entityMessageService.merge(beacon)
            entityMessageService.merge(clientBeacon)

            // act
            val newClientBeacon = clientBeacon.toBuilder()
            val foundWeakChild = newClientBeacon.weakChildrenList.first().toBuilder()
            foundWeakChild.someField = "hot beans"

            newClientBeacon.clearWeakChildren()
            newClientBeacon.addWeakChildren(foundWeakChild)
            entityMessageService.merge(newClientBeacon.build())

            // assert
            val actualWeakChild = entityMessageService.get(ComplexModel.WeakChild.getDefaultInstance(), weakChild.id)!!
            assert(actualWeakChild.someField == weakChild.someField)

            val actualClientBeacon = entityMessageService.get(ComplexModel.ClientBeacon.getDefaultInstance(), clientBeacon.id)!!
            assert(actualClientBeacon.weakChildrenList.first().id == weakChild.id)
            assert(actualClientBeacon.weakChildrenList.first().someField == weakChild.someField)
            assert(actualClientBeacon.weakChildrenList.first().someField != foundWeakChild.someField)
        }
        finally {
            database.deleteOnExit()
        }
    }

    @Test
    fun repeatedAddRemoveTest() {
        // arrange
        val database = File(UUID.randomUUID().toString().replace("-", ""))
        try {
            val sourceConnection = SQLiteConnectionSourceFactory(database.absolutePath)
            val granularDatabaseService = JDBCGranularDatabaseService(
                    sourceConnection,
                    false)
            val sqliteGeneratorService = SQLiteGeneratorService()
            val entityService = EntityService(granularDatabaseService, sqliteGeneratorService)
            val entityMessageService = EntityMessageService(ComplexModelBuilder, entityService, HashMap())

            val beacon = ComplexModel.Beacon.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setId1("1")
                    .setId2("2")
                    .setId3("3")
                    .build()

            val weakChild = ComplexModel.WeakChild.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setSomeField("cool beans")
                    .build()

            val clientBeacon = ComplexModel.ClientBeacon.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setUserId("monkey")
                    .setBeacon(beacon)
                    .addWeakChildren(weakChild)
                    .build()

            entityMessageService.createEntireSchema(ComplexModel.getDescriptor())

            entityMessageService.merge(weakChild)
            entityMessageService.merge(beacon)
            entityMessageService.merge(clientBeacon)

            // act
            val newClientBeacon = clientBeacon.toBuilder().clearWeakChildren()
            entityMessageService.merge(newClientBeacon.build())

            // assert
            val actualWeakChild = entityMessageService.get(ComplexModel.WeakChild.getDefaultInstance(), weakChild.id)!!
            assert(actualWeakChild.someField == weakChild.someField)

            val actualClientBeacon = entityMessageService.get(ComplexModel.ClientBeacon.getDefaultInstance(), clientBeacon.id)!!
            assert(actualClientBeacon.weakChildrenCount == 0)
        }
        finally {
            database.deleteOnExit()
        }
    }
}