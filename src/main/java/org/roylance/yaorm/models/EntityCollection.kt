package org.roylance.yaorm.models

import java.util.*

class EntityCollection<T> (val entityDefinition: Class<T>) : MutableCollection<T> {
    private val internalCollection = ArrayList<T>()
    override fun iterator(): MutableIterator<T> {
        return this.internalCollection.iterator()
    }

    override fun add(element: T): Boolean {
        return this.internalCollection.add(element)
    }

    override fun remove(element: T): Boolean {
        return this.internalCollection.remove(element)
    }

    override fun addAll(elements: Collection<T>): Boolean {
        return this.internalCollection.addAll(elements)
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        return this.internalCollection.removeAll(elements)
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        return this.internalCollection.retainAll(elements)
    }

    override fun clear() {
        this.internalCollection.clear()
    }

    override val size: Int
        get() = this.internalCollection.size

    override fun isEmpty(): Boolean {
        return this.internalCollection.isEmpty()
    }

    override fun contains(element: T): Boolean {
        return this.internalCollection.contains(element)
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        return this.internalCollection.containsAll(elements)
    }
}
