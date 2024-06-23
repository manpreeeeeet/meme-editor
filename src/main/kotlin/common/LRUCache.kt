package common

class LRUCache<K, V>(private val capacity: Int) {
    private val map = mutableMapOf<K, Node<K, V>>()
    private val head = Node<K, V>(key = null, value = null)
    private val tail = Node<K, V>(key = null, value = null)

    init {
        head.next = tail
        tail.prev = head
    }

    fun get(key: K): V? {
        val node = map[key] ?: return null
        moveToHead(node)
        return node.value
    }

    fun put(key: K, value: V) {
        val node = map[key]
        if (node != null) {
            node.value = value
            moveToHead(node)
        } else {
            val newNode = Node(key, value)
            map[key] = newNode
            addToHead(newNode)
            if (map.size > capacity) {
                val tailNode = removeTail()
                tailNode?.key?.let { map.remove(it) }
            }
        }
    }

    private fun addToHead(node: Node<K, V>) {
        node.prev = head
        node.next = head.next
        head.next?.prev = node
        head.next = node
    }

    private fun removeNode(node: Node<K, V>) {
        val prevNode = node.prev
        val nextNode = node.next
        prevNode?.next = nextNode
        nextNode?.prev = prevNode
    }

    private fun moveToHead(node: Node<K, V>) {
        removeNode(node)
        addToHead(node)
    }

    private fun removeTail(): Node<K, V>? {
        val tailNode = tail.prev
        if (tailNode != null && tailNode != head) {
            removeNode(tailNode)
            return tailNode
        }
        return null
    }

    private data class Node<K, V>(
        val key: K?,
        var value: V?,
        var prev: Node<K, V>? = null,
        var next: Node<K, V>? = null
    )
}
