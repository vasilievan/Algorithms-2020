package lesson5

/**
 * Множество(таблица) с открытой адресацией на 2^bits элементов без возможности роста.
 */
class KtOpenAddressingSet<T : Any>(private val bits: Int) : AbstractMutableSet<T>() {
    init {
        require(bits in 2..31)
    }

    // 2^bits
    private val capacity = 1 shl bits

    private val storage = Array<Any?>(capacity) { null }

    override var size: Int = 0

    /**
     * Индекс в таблице, начиная с которого следует искать данный элемент
     */

    // Int.MAX_VALUE = (2^31)-1 moved to some
    // Int.MAX_VALUE shr 30 = (2^30)-1
    private fun T.startingIndex(): Int {
        return hashCode() and (0x7FFFFFFF shr (31 - bits))
    }

    /**
     * Проверка, входит ли данный элемент в таблицу
     */
    override fun contains(element: T): Boolean {
        val startIndex = element.startingIndex()
        var index = startIndex
        var current = storage[index]
        while (current != null) {
            if (current == element) {
                return true
            }
            index = calcIndex(index)
            current = storage[index]
        }
        return false
    }

    /**
     * Добавление элемента в таблицу.
     *
     * Не делает ничего и возвращает false, если такой же элемент уже есть в таблице.
     * В противном случае вставляет элемент в таблицу и возвращает true.
     *
     * Бросает исключение (IllegalStateException) в случае переполнения таблицы.
     * Обычно Set не предполагает ограничения на размер и подобных контрактов,
     * но в данном случае это было введено для упрощения кода.
     */
    override fun add(element: T): Boolean {
        val startingIndex = element.startingIndex()
        var index = startingIndex
        var current = storage[index]
        while (current != null) {
            // если ячейка уже была занята раньше, а потом данные из нее удалили
            // запишу в нее данные снова
            if (current == Deleted) break
            if (current == element) {
                return false
            }
            index = calcIndex(index)
            check(index != startingIndex) { "Table is full" }
            current = storage[index]
        }
        storage[index] = element
        size++
        return true
    }

    /**
     * Удаление элемента из таблицы
     *
     * Если элемент есть в таблица, функция удаляет его из дерева и возвращает true.
     * В ином случае функция оставляет множество нетронутым и возвращает false.
     * Высота дерева не должна увеличиться в результате удаления.
     *
     * Спецификация: [java.util.Set.remove] (Ctrl+Click по remove)
     *
     * Средняя
     */

    // производительность O(1), если сразу нашел искомую ячейку
    // если же, допустим, таблица сначала была полностью заполнена,
    // а затем в ней оставлен только искомый элемент (или не оставлен),
    // а остальные - стали deleted, сложность O(n), n - к-во элементов
    // память O(1)

    // создам особый флаг, чтоб помечать ячейку, из которой удалил информацию,
    // для сохранения доступа к последующим ячейкам
    private object Deleted

    override fun remove(element: T): Boolean {
        val start = element.startingIndex()
        var index = start
        var current = storage[index]
        do {
            if (current == element) {
                storage[index] = Deleted
                size--
                return true
            }
            index = calcIndex(index)
            if (index == start) return false
            current = storage[index]
        } while (current != null)
        return false
    }

    private fun calcIndex(index: Int): Int = (index + 1) % capacity

    /**
     * Создание итератора для обхода таблицы
     *
     * Не забываем, что итератор должен поддерживать функции next(), hasNext(),
     * и опционально функцию remove()
     *
     * Спецификация: [java.util.Iterator] (Ctrl+Click по Iterator)
     *
     * Средняя (сложная, если поддержан и remove тоже)
     */
    override fun iterator(): MutableIterator<T> = HashIter()

    inner class HashIter : MutableIterator<T> {
        var currentPosition = 0
        var next: Any? = null

        // производительность O(n) худший случай - попали в самом конце на не-null в массиве
        // производительность O(1) лучший случай - попали сразу на не-null
        // память O(1)

        override fun hasNext(): Boolean {
            for (i in currentPosition until capacity) {
                if (storage[i] != null && storage[i] != Deleted) {
                    currentPosition = i
                    return true
                }
            }
            return false
        }

        // производительность O(n) худший случай - попали в самом конце на не-null в массиве
        // производительность O(1) лучший случай - попали сразу на не-null
        // память O(1)

        override fun next(): T {
            if (!hasNext()) throw IllegalStateException()
            val toBeReturned = storage[currentPosition] as T
            next = toBeReturned
            currentPosition++
            for (i in currentPosition until capacity) {
                if (storage[i] != null && storage[i] != Deleted) {
                    currentPosition = i
                    break
                }
            }
            return toBeReturned
        }

        // память O(1)
        // производительность O(1)

        override fun remove() {
            if (next == null) throw IllegalStateException()
            remove(next)
            next = null
        }
    }
}