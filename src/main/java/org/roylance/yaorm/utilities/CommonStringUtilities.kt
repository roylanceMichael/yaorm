package org.roylance.yaorm.utilities

object CommonStringUtilities {
    fun getLastWord(item:String):String {
        val splitItems = item.split(".")
        if (splitItems.size > 0) {
            return splitItems[splitItems.size - 1]
        }
        return item
    }
}
