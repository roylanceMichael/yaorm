package org.roylance.yaorm.utilities

import org.roylance.yaorm.models.EntityCollection
import org.roylance.yaorm.models.IEntity
import org.roylance.yaorm.models.entity.EntityDefinitionModel
import java.util.*

object ClassEqualityUtils {
    fun areBothObjectsEqual(firstObject:IEntity<*>?, secondObject:IEntity<*>?):Boolean {
        return this.areBothObjectsEqualInternal(firstObject, secondObject, HashSet())
    }

    fun areBothCollectionsEqual(
            firstCollection: EntityCollection<*, *>,
            secondCollection: EntityCollection<*, *>): Boolean {
        return this.areBothCollectionsEqualInternal(firstCollection, secondCollection, HashSet())
    }

    private fun areBothObjectsEqualInternal(
            firstObject:IEntity<*>?,
            secondObject:IEntity<*>?,
            seenObjects:HashSet<String>):Boolean {
        val firstIsNull = firstObject == null
        val secondIsNull = secondObject == null
        if ((firstIsNull && !secondIsNull) ||
                (!firstIsNull && secondIsNull)) {
            return false
        }
        if (firstIsNull && secondIsNull) {
            return true
        }

        if (firstObject!!.javaClass != secondObject!!.javaClass) {
            return false
        }

        val combinedComparisonHash = "${firstObject.javaClass.name} ${firstObject.id}~${secondObject.id}"
        if (seenObjects.contains(combinedComparisonHash)) {
            return true
        }

        // keep track of comparison tuple
        seenObjects.add(combinedComparisonHash)

        return EntityUtils.getProperties(firstObject)
            .all {
                val firstObjectProperty = it.getMethod.invoke(firstObject)
                val secondObjectProperty = it.getMethod.invoke(secondObject)
                if (this.bothItemsNull(firstObjectProperty, secondObjectProperty)) {
                    true
                }
                else if (!this.bothItemsNotNull(firstObjectProperty, secondObjectProperty)) {
                    false
                }
                else if (EntityDefinitionModel.Single.equals(it.type)) {
                    firstObjectProperty.equals(secondObjectProperty)
                }
                else {
                    // handle case where we're comparing lists...
                    if (this.areBothCollectionsEqualInternal(
                            firstObjectProperty as EntityCollection<*, *>,
                            secondObjectProperty as EntityCollection<*, *>,
                            seenObjects)) {
                        true
                    }
                    else {
                        false
                    }
                }
            }
    }

    private fun areBothCollectionsEqualInternal(
            firstCollection: EntityCollection<*, *>,
            secondCollection: EntityCollection<*, *>,
            seenObjects: HashSet<String>): Boolean {
        if (firstCollection.entityDefinition != secondCollection.entityDefinition) {
            return false
        }

        val firstMap = firstCollection.toMapBy { it.id }
        val secondMap = secondCollection.toMapBy { it.id }

        if (firstMap.size != secondMap.size) {
            return false
        }

        var matchNumber = 0
        firstMap
            .keys
            .forEach {
                if (secondMap.containsKey(it)) {
                    matchNumber++
                    if (!this.areBothObjectsEqualInternal(firstMap[it], secondMap[it], seenObjects)) {
                        return false
                    }
                }
                else {
                    return false
                }
            }

        if (matchNumber != firstCollection.size) {
            return false
        }

        return true
    }

    private fun bothItemsNull(firstObject:Any?, secondObject: Any?):Boolean {
        return firstObject == null && secondObject == null
    }

    private fun bothItemsNotNull(firstObject:Any?, secondObject: Any?):Boolean {
        return firstObject != null && secondObject != null
    }
}
