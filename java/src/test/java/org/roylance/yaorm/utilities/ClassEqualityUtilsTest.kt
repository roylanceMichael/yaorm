package org.roylance.yaorm.utilities

import org.junit.Assert
import org.junit.Test
import org.roylance.yaorm.testmodels.ChildTestModel
import org.roylance.yaorm.testmodels.RootTestModel

class ClassEqualityUtilsTest {
    private val One = "1"
    private val Two = "2"

    @Test
    fun rootModelWithTwoIdenticalChildrenEqualityTest() {
        // arrange
        val firstChildName = "firstChild"
        val secondChildName = "secondChild"
        val rootName = "mike"

        val firstChildEntity = ChildTestModel(One, firstChildName)
        val firstChildEntity1 = ChildTestModel(Two, secondChildName)
        val firstRootModel = RootTestModel(One, rootName)
        firstRootModel.commonChildTests.add(firstChildEntity)
        firstRootModel.commonChildTests.add(firstChildEntity1)

        val secondChildEntity = ChildTestModel(One, firstChildName)
        val secondChildEntity1 = ChildTestModel(Two, secondChildName)
        val secondRootModel = RootTestModel(One, rootName)
        secondRootModel.commonChildTests.add(secondChildEntity)
        secondRootModel.commonChildTests.add(secondChildEntity1)

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

        val firstChildEntity = ChildTestModel(One, firstChildName)
        val firstChildEntity1 = ChildTestModel(Two, secondChildName)
        val firstRootModel = RootTestModel(One, rootName)
        firstRootModel.commonChildTests.add(firstChildEntity)
        firstRootModel.commonChildTests.add(firstChildEntity1)

        val secondChildEntity = ChildTestModel(One, firstChildName)
        val secondChildEntity1 = ChildTestModel(Two, secondChildNameDifferent)
        val secondRootModel = RootTestModel(One, rootName)
        secondRootModel.commonChildTests.add(secondChildEntity)
        secondRootModel.commonChildTests.add(secondChildEntity1)

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

        val firstChildEntity = ChildTestModel(One, firstChildName)
        val firstChildEntity1 = ChildTestModel(Two, secondChildName)
        val firstRootModel = RootTestModel(One , rootName)
        firstChildEntity.commonRootModel = firstRootModel
        firstChildEntity1.commonRootModel = firstRootModel
        firstRootModel.commonChildTests.add(firstChildEntity)
        firstRootModel.commonChildTests.add(firstChildEntity1)

        val secondChildEntity = ChildTestModel(One, firstChildName)
        val secondChildEntity1 = ChildTestModel(Two, secondChildName)
        val secondRootModel = RootTestModel(One, rootName)
        secondChildEntity.commonRootModel = secondRootModel
        secondChildEntity1.commonRootModel = secondRootModel
        secondRootModel.commonChildTests.add(secondChildEntity)
        secondRootModel.commonChildTests.add(secondChildEntity1)

        // act
        val result = ClassEqualityUtils.areBothObjectsEqual(firstRootModel, secondRootModel)

        // assert
        Assert.assertEquals(false, result)
    }
}
