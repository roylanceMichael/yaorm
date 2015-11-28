package org.roylance.yaorm.utilities

import org.roylance.yaorm.models.EntityCollection
import org.roylance.yaorm.models.entity.EntityDefinitionModel

object ClassEqualityUtils {
    fun areBothObjectsEqual(firstObject:Any?, secondObject:Any?):Boolean {
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

        val commonProperties = EntityUtils.getProperties(firstObject)
        commonProperties
            .any {
                val firstObjectProperty = it.getMethod.invoke(firstObject)
                val secondObjectProperty = it.getMethod.invoke(secondObject)
                if (this.bothItemsNull(firstObject, secondObject)) {
                    true
                }
                else if (!this.bothItemsNotNull(firstObject, secondObject)) {
                    false
                }
                else if (EntityDefinitionModel.Single.equals(it.type)) {
                    firstObjectProperty.equals(secondObjectProperty)
                }
                else {
                    // handle case where we're comparing lists...
                    false
                }
            }

        return false
    }

    fun areBothCollectionsEqual(
            firstCollection: EntityCollection<*, *>,
            secondCollection: EntityCollection<*, *>): Boolean {
        if (firstCollection.entityDefinition != secondCollection.entityDefinition) {
            return false
        }

        // assuming that it is type IEntity
        return false
    }

    private fun bothItemsNull(firstObject:Any?, secondObject: Any?):Boolean {
        return firstObject == null && secondObject == null
    }

    private fun bothItemsNotNull(firstObject:Any?, secondObject: Any?):Boolean {
        return firstObject != null && secondObject != null
    }
}
