package org.roylance.yaorm.utilities

import org.junit.Assert
import org.junit.Test
import org.roylance.yaorm.testmodels.ChildTestModel
import org.roylance.yaorm.testmodels.RootTestModel

class ClassEqualityUtilsTest {
    @Test
    fun rootModelWithTwoIdenticalChildrenEqualityTest() {
        // arrange
        val firstChildName = "firstChild"
        val secondChildName = "secondChild"
        val rootName = "mike"

        val firstChildEntity = ChildTestModel(1, firstChildName)
        val firstChildEntity1 = ChildTestModel(2, secondChildName)
        val firstRootModel = RootTestModel(1, rootName)
        firstRootModel.childTests.add(firstChildEntity)
        firstRootModel.childTests.add(firstChildEntity1)

        val secondChildEntity = ChildTestModel(1, firstChildName)
        val secondChildEntity1 = ChildTestModel(2, secondChildName)
        val secondRootModel = RootTestModel(1, rootName)
        secondRootModel.childTests.add(secondChildEntity)
        secondRootModel.childTests.add(secondChildEntity1)

        // act
        val result = ClassEqualityUtils.areBothObjectsEqual(firstRootModel, secondRootModel)

        // assert
        Assert.assertEquals(true, result)
    }

    @Test
    fun rootModelWithNonIdenticalChildrenEqualityTest() {
        // arrange
        val firstChildName = "firstChild"
        val secondChildName = "secondChild"
        val secondChildNameDifferent = "secondChild1"
        val rootName = "mike"

        val firstChildEntity = ChildTestModel(1, firstChildName)
        val firstChildEntity1 = ChildTestModel(2, secondChildName)
        val firstRootModel = RootTestModel(1, rootName)
        firstRootModel.childTests.add(firstChildEntity)
        firstRootModel.childTests.add(firstChildEntity1)

        val secondChildEntity = ChildTestModel(1, firstChildName)
        val secondChildEntity1 = ChildTestModel(2, secondChildNameDifferent)
        val secondRootModel = RootTestModel(1, rootName)
        secondRootModel.childTests.add(secondChildEntity)
        secondRootModel.childTests.add(secondChildEntity1)

        // act
        val result = ClassEqualityUtils.areBothObjectsEqual(firstRootModel, secondRootModel)

        // assert
        Assert.assertEquals(false, result)
    }

    @Test
    fun circularReferenceEqualityTest() {
        // arrange
        val firstChildName = "firstChild"
        val secondChildName = "secondChild"
        val rootName = "mike"

        val firstChildEntity = ChildTestModel(1, firstChildName)
        val firstChildEntity1 = ChildTestModel(2, secondChildName)
        val firstRootModel = RootTestModel(1, rootName)
        firstChildEntity.rootModel = firstRootModel
        firstChildEntity1.rootModel = firstRootModel
        firstRootModel.childTests.add(firstChildEntity)
        firstRootModel.childTests.add(firstChildEntity1)

        val secondChildEntity = ChildTestModel(1, firstChildName)
        val secondChildEntity1 = ChildTestModel(2, secondChildName)
        val secondRootModel = RootTestModel(1, rootName)
        secondChildEntity.rootModel = secondRootModel
        secondChildEntity1.rootModel = secondRootModel
        secondRootModel.childTests.add(secondChildEntity)
        secondRootModel.childTests.add(secondChildEntity1)

        // act
        val result = ClassEqualityUtils.areBothObjectsEqual(firstRootModel, secondRootModel)

        // assert
        Assert.assertEquals(false, result)
    }
}
