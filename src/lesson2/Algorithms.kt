@file:Suppress("UNUSED_PARAMETER")

package lesson2

import kotlin.math.sqrt

/**
 * Получение наибольшей прибыли (она же -- поиск максимального подмассива)
 * Простая
 *
 * Во входном файле с именем inputName перечислены цены на акции компании в различные (возрастающие) моменты времени
 * (каждая цена идёт с новой строки). Цена -- это целое положительное число. Пример:
 *
 * 201
 * 196
 * 190
 * 198
 * 187
 * 194
 * 193
 * 185
 *
 * Выбрать два момента времени, первый из них для покупки акций, а второй для продажи, с тем, чтобы разница
 * между ценой продажи и ценой покупки была максимально большой. Второй момент должен быть раньше первого.
 * Вернуть пару из двух моментов.
 * Каждый момент обозначается целым числом -- номер строки во входном файле, нумерация с единицы.
 * Например, для приведённого выше файла результат должен быть Pair(3, 4)
 *
 * В случае обнаружения неверного формата файла бросить любое исключение.
 */
fun optimizeBuyAndSell(inputName: String): Pair<Int, Int> {
    TODO()
}

/**
 * Задача Иосифа Флафия.
 * Простая
 *
 * Образовав круг, стоят menNumber человек, пронумерованных от 1 до menNumber.
 *
 * 1 2 3
 * 8   4
 * 7 6 5
 *
 * Мы считаем от 1 до choiceInterval (например, до 5), начиная с 1-го человека по кругу.
 * Человек, на котором остановился счёт, выбывает.
 *
 * 1 2 3
 * 8   4
 * 7 6 х
 *
 * Далее счёт продолжается со следующего человека, также от 1 до choiceInterval.
 * Выбывшие при счёте пропускаются, и человек, на котором остановился счёт, выбывает.
 *
 * 1 х 3
 * 8   4
 * 7 6 Х
 *
 * Процедура повторяется, пока не останется один человек. Требуется вернуть его номер (в данном случае 3).
 *
 * 1 Х 3
 * х   4
 * 7 6 Х
 *
 * 1 Х 3
 * Х   4
 * х 6 Х
 *
 * х Х 3
 * Х   4
 * Х 6 Х
 *
 * Х Х 3
 * Х   х
 * Х 6 Х
 *
 * Х Х 3
 * Х   Х
 * Х х Х
 *
 * Общий комментарий: решение из Википедии для этой задачи принимается,
 * но приветствуется попытка решить её самостоятельно.
 */
fun josephTask(menNumber: Int, choiceInterval: Int): Int {
    TODO()
}

/**
 * Наибольшая общая подстрока.
 * Средняя
 *
 * Дано две строки, например ОБСЕРВАТОРИЯ и КОНСЕРВАТОРЫ.
 * Найти их самую длинную общую подстроку -- в примере это СЕРВАТОР.
 * Если общих подстрок нет, вернуть пустую строку.
 * При сравнении подстрок, регистр символов *имеет* значение.
 * Если имеется несколько самых длинных общих подстрок одной длины,
 * вернуть ту из них, которая встречается раньше в строке first.
 */

// память O(m*n), m - длина first, n - длина second
// производительность O(m*n)

fun longestCommonSubstring(first: String, second: String): String {
    var maximumLength = 0
    val simularitiesArray = List(first.length) { MutableList(second.length) { 0 } }
    var lastIndex = -1
    for (i in first.indices) {
        for (j in second.indices) {
            if (first[i] == second[j]) {
                if (i == 0 || j == 0) {
                    simularitiesArray[i][j] = 1
                } else {
                    simularitiesArray[i][j] = simularitiesArray[i - 1][j - 1] + 1
                }
                if (maximumLength < simularitiesArray[i][j]) {
                    maximumLength = simularitiesArray[i][j]
                    lastIndex = i
                }
            }
        }
    }
    lastIndex++
    return first.substring(lastIndex - maximumLength, lastIndex)
}

/**
 * Число простых чисел в интервале
 * Простая
 *
 * Рассчитать количество простых чисел в интервале от 1 до limit (включительно).
 * Если limit <= 1, вернуть результат 0.
 *
 * Справка: простым считается число, которое делится нацело только на 1 и на себя.
 * Единица простым числом не считается.
 */

// память O(n)
// производительность O(n*loglog(n))

fun calcPrimesNumber(limit: Int): Int {
    if (limit <= 1) return 0
    val erathosphen: MutableList<Boolean> = MutableList(limit + 1) { true }
    erathosphen[0] = false
    erathosphen[1] = false
    for (i in 2 until erathosphen.size) {
        if (erathosphen[i]) {
            var j = 2
            while (i * j < erathosphen.size) {
                erathosphen[i * j] = false
                j++
            }
        }
    }
    return erathosphen.count { it }
}

// более оптимальное решение

fun optimalErathosphen(limit: Int): Int {
    if (limit <= 1) return 0
    val erathosphen: MutableList<Int?> = MutableList(limit + 1) { it }
    erathosphen[0] = null
    erathosphen[1] = null
    for (i in 2..sqrt(erathosphen.size.toFloat()).toInt()) {
        for (k in 2..erathosphen.size) {
            if (k * i >= erathosphen.size) break
            erathosphen[k * i] = null
        }
    }
    return erathosphen.count { it != null }
}