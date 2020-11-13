@file:Suppress("UNUSED_PARAMETER")

package lesson7

import java.lang.StringBuilder
import kotlin.math.max

/**
 * Наибольшая общая подпоследовательность.
 * Средняя
 *
 * Дано две строки, например "nematode knowledge" и "empty bottle".
 * Найти их самую длинную общую подпоследовательность -- в примере это "emt ole".
 * Подпоследовательность отличается от подстроки тем, что её символы не обязаны идти подряд
 * (но по-прежнему должны быть расположены в исходной строке в том же порядке).
 * Если общей подпоследовательности нет, вернуть пустую строку.
 * Если есть несколько самых длинных общих подпоследовательностей, вернуть любую из них.
 * При сравнении подстрок, регистр символов *имеет* значение.
 */

// память O(m*n), где m - длина 1 строки, n - длина 2 строки
// производительность O(m*n)

fun longestCommonSubSequence(first: String, second: String): String {
    if (first == second) return first
    val firstLength = first.length
    val secondLength = second.length
    if (firstLength == 0 || secondLength == 0) return ""
    val data = List(firstLength + 1) { IntArray(secondLength + 1) }
    for (row in 0..firstLength) {
        for (column in 0..secondLength) {
            if (row == 0 || column == 0) data[row][column] = 0
            else if (first[row - 1] == second[column - 1]) data[row][column] = data[row - 1][column - 1] + 1
            else data[row][column] = max(data[row - 1][column], data[row][column - 1])
        }
    }
    var currentIndex = data[firstLength][secondLength]
    val sb = StringBuilder()
    var i = firstLength
    var j = secondLength
    while (i > 0 && j > 0) {
        when {
            first[i - 1] == second[j - 1] -> {
                sb.append(first[i - 1])
                currentIndex--
                i--
                j--
            }
            data[i - 1][j] > data[i][j - 1] -> i--
            else -> j--
        }
    }
    return sb.reverse().toString()
}

/**
 * Наибольшая возрастающая подпоследовательность
 * Сложная
 *
 * Дан список целых чисел, например, [2 8 5 9 12 6].
 * Найти в нём самую длинную возрастающую подпоследовательность.
 * Элементы подпоследовательности не обязаны идти подряд,
 * но должны быть расположены в исходном списке в том же порядке.
 * Если самых длинных возрастающих подпоследовательностей несколько (как в примере),
 * то вернуть ту, в которой числа расположены раньше (приоритет имеют первые числа).
 * В примере ответами являются 2, 8, 9, 12 или 2, 5, 9, 12 -- выбираем первую из них.
 */

// память O(n), где n - длина последовательности
// производительность O(n^2)

fun longestIncreasingSubSequence(list: List<Int>): List<Int> {
    if (list.size <= 1) return list
    val listSize = list.size
    var subsequenceLength = -1
    val subsequence = IntArray(listSize) { Int.MAX_VALUE }
    val indices = IntArray(listSize)
    subsequence[0] = list[0]
    for (i in 1 until listSize) {
        indices[i] = ceilIndex(subsequence, i, list[i])
        if (subsequenceLength < indices[i]) subsequenceLength = indices[i]
    }
    // для восстановления последовательности сделаю следующее:
    // очевидно, что может возникнуть проблемная ситуация: 2, 8, 5, 9, 12, 6
    // indices = { 0 1 1 2 3 2 }. Для начала буду брать элемнт с наибольшим
    // индексом (3) - 12. Далее буду продвигаться справа налево. Случай с indices[2] = 1
    // показателен. Выбираю не первое значение, где indices[2] = 1, а самое левое.
    // Ввел дополнительные оптимизации для сдвига индекса, а также прекращения
    // обхода при достижении предела искомых чисел (subsequenceLength)
    // в массиве для повышения производительности.
    val answer = mutableListOf<Int>()
    var currentIndex = indices.indexOfFirst { it == subsequenceLength }
    while (currentIndex > -1) {
        if (indices[currentIndex] == subsequenceLength) {
            var toAdd = list[currentIndex]
            var temp: Int? = null
            for (innerIndex in currentIndex - 1 downTo 0) {
                if (indices[innerIndex] == subsequenceLength && list[innerIndex] < answer.last()) {
                    toAdd = list[innerIndex]
                    temp = innerIndex
                }
            }
            if (temp != null) currentIndex = temp
            answer.add(toAdd)
            subsequenceLength--
            if (subsequenceLength == -1) break
        }
        currentIndex--
    }
    return answer.reversed()
}

private fun ceilIndex(subsequence: IntArray, startRight: Int, key: Int): Int {
    var left = 0
    var ceilIndex = 0
    var right = startRight
    var indexFound = false
    var mid = (left + right) / 2
    while (left <= right && !indexFound) {
        if (subsequence[mid] > key) {
            right = mid - 1
        } else if (subsequence[mid] == key) {
            ceilIndex = mid
            indexFound = true
        } else if (mid + 1 <= right && subsequence[mid + 1] >= key) {
            subsequence[mid + 1] = key
            ceilIndex = mid + 1
            indexFound = true
        } else {
            left = mid + 1
        }
        mid = (left + right) / 2
    }
    if (!indexFound) {
        if (mid == left) {
            subsequence[mid] = key
            ceilIndex = mid
        } else {
            subsequence[mid + 1] = key
            ceilIndex = mid + 1
        }
    }
    return ceilIndex
}

/**
 * Самый короткий маршрут на прямоугольном поле.
 * Средняя
 *
 * В файле с именем inputName задано прямоугольное поле:
 *
 * 0 2 3 2 4 1
 * 1 5 3 4 6 2
 * 2 6 2 5 1 3
 * 1 4 3 2 6 2
 * 4 2 3 1 5 0
 *
 * Можно совершать шаги длиной в одну клетку вправо, вниз или по диагонали вправо-вниз.
 * В каждой клетке записано некоторое натуральное число или нуль.
 * Необходимо попасть из верхней левой клетки в правую нижнюю.
 * Вес маршрута вычисляется как сумма чисел со всех посещенных клеток.
 * Необходимо найти маршрут с минимальным весом и вернуть этот минимальный вес.
 *
 * Здесь ответ 2 + 3 + 4 + 1 + 2 = 12
 */
fun shortestPathOnField(inputName: String): Int {
    TODO()
}

// Задачу "Максимальное независимое множество вершин в графе без циклов"
// смотрите в уроке 5