package org.roylance.yaorm.utilities.common

import org.roylance.common.service.IBuilder
import org.roylance.yaorm.ComplexModel
import org.roylance.yaorm.services.EntityMessageService
import org.roylance.yaorm.services.IEntityService
import java.util.*

object WeakTypeTestUtilities {
  fun shouldNotSaveChildMarkedAsWeakAfterAlreadySavedThrough(entityService: IEntityService,
      cleanup: IBuilder<Boolean>? = null) {
    // arrange
    try {
      val entityMessageService = EntityMessageService(entityService, HashMap())

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
      val actualClientBeacon = entityMessageService.get(
          ComplexModel.ClientBeacon.getDefaultInstance(), clientBeacon.id)!!
      assert(actualClientBeacon.beacon.id1 == beacon.id1)
    } finally {
      entityService.close()
      cleanup?.build()
    }
  }

  fun directChildRemove(entityService: IEntityService, cleanup: IBuilder<Boolean>? = null) {
    // arrange
    try {
      val entityMessageService = EntityMessageService(entityService, HashMap())

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
      val actualClientBeacon = entityMessageService.get(
          ComplexModel.ClientBeacon.getDefaultInstance(), clientBeacon.id)!!
      println(actualClientBeacon.beacon.id1)
      assert(actualClientBeacon.beacon.id1 != beacon.id1)
      assert(actualClientBeacon.beacon.id1 == "")
    } finally {
      entityService.close()
      cleanup?.build()
    }
  }

  fun shouldNotSaveChildMarkedAsWeakEvenIfNotExists(entityService: IEntityService,
      cleanup: IBuilder<Boolean>? = null) {
    // arrange
    try {
      val entityMessageService = EntityMessageService(entityService, HashMap())

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
      val actualClientBeacon = entityMessageService.get(
          ComplexModel.ClientBeacon.getDefaultInstance(), clientBeacon.id)!!
      assert(actualClientBeacon.beacon.id == beacon.id)
      assert(actualClientBeacon.beacon.id1 != beacon.id1)
    } finally {
      entityService.close()
      cleanup?.build()
    }
  }

  fun saveDeleteSaveNotRepeated(entityService: IEntityService, cleanup: IBuilder<Boolean>? = null) {
    // arrange
    try {
      val entityMessageService = EntityMessageService(entityService, HashMap())

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

      var actualClientBeacon = entityMessageService.get(
          ComplexModel.ClientBeacon.getDefaultInstance(), clientBeacon.id)
      assert(actualClientBeacon != null)
      assert(actualClientBeacon!!.beacon.id != beacon.id)
      assert(actualClientBeacon.beacon.id1 != beacon.id1)

      entityMessageService.merge(newClientBeacon.setBeacon(beacon).build())

      // assert
      actualClientBeacon = entityMessageService.get(ComplexModel.ClientBeacon.getDefaultInstance(),
          clientBeacon.id)
      assert(actualClientBeacon != null)
      assert(actualClientBeacon!!.beacon.id == beacon.id)
      assert(actualClientBeacon.beacon.id1 == beacon.id1)
    } finally {
      entityService.close()
      cleanup?.build()
    }
  }

  fun shouldNotSaveChildMarkedAsWeakAfterAlreadySavedThroughRepeated(entityService: IEntityService,
      cleanup: IBuilder<Boolean>? = null) {
    // arrange
    try {
      val entityMessageService = EntityMessageService(entityService, HashMap())

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
      val actualWeakChild = entityMessageService.get(ComplexModel.WeakChild.getDefaultInstance(),
          weakChild.id)!!
      assert(actualWeakChild.someField == weakChild.someField)

      val actualClientBeacon = entityMessageService.get(
          ComplexModel.ClientBeacon.getDefaultInstance(), clientBeacon.id)!!
      assert(actualClientBeacon.weakChildrenList.first().id == weakChild.id)
      assert(actualClientBeacon.weakChildrenList.first().someField == weakChild.someField)
      assert(actualClientBeacon.weakChildrenList.first().someField != foundWeakChild.someField)
    } finally {
      entityService.close()
      cleanup?.build()
    }
  }

  fun repeatedAddRemoveTest(entityService: IEntityService, cleanup: IBuilder<Boolean>? = null) {
    // arrange
    try {
      val entityMessageService = EntityMessageService(entityService, HashMap())

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
      val actualWeakChild = entityMessageService.get(ComplexModel.WeakChild.getDefaultInstance(),
          weakChild.id)!!
      assert(actualWeakChild.someField == weakChild.someField)

      val actualClientBeacon = entityMessageService.get(
          ComplexModel.ClientBeacon.getDefaultInstance(), clientBeacon.id)!!
      assert(actualClientBeacon.weakChildrenCount == 0)
    } finally {
      entityService.close()
      cleanup?.build()
    }
  }

  fun shouldBeAbleToSaveDeepNestedTree(entityService: IEntityService, cleanup: IBuilder<Boolean>? = null) {
    // arrange
    try {
      val entityMessageService = EntityMessageService(entityService, HashMap())

      val firstValidation = ComplexModel.Validation.newBuilder()
          .setId(UUID.randomUUID().toString())
          .setName("first_name")
          .setValue("first_name")

      val secondValidation = ComplexModel.Validation.newBuilder()
          .setId(UUID.randomUUID().toString())
          .setName("second_name")
          .setValue("second_name")

      val thirdValidation = ComplexModel.Validation.newBuilder()
          .setId(UUID.randomUUID().toString())
          .setName("third_name")
          .setValue("third_name")

      secondValidation.addChildren(thirdValidation)
      firstValidation.addChildren(secondValidation)

      val view = ComplexModel.View.newBuilder()
          .setId(UUID.randomUUID().toString())
      view.addValidations(firstValidation)

      val request = ComplexModel.Request.newBuilder()
          .setId(UUID.randomUUID().toString())

      request.setView(view)

      entityMessageService.createEntireSchema(ComplexModel.getDescriptor())

      // act
      entityMessageService.merge(request.build())

      // assert
      val allRequests = entityMessageService.getMany(ComplexModel.Request.getDefaultInstance())

      println(allRequests)
      assert(true)
    }
    finally {
      entityService.close()
      cleanup?.build()
    }
  }

}