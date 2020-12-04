@file:Suppress("UNUSED_PARAMETER", "unused")

package lesson6

import lesson6.impl.GraphBuilder
import org.jetbrains.annotations.NotNull
import java.util.*


/**
 * Эйлеров цикл.
 * Средняя
 *
 * Дан граф (получатель). Найти по нему любой Эйлеров цикл.
 * Если в графе нет Эйлеровых циклов, вернуть пустой список.
 * Соседние дуги в списке-результате должны быть инцидентны друг другу,
 * а первая дуга в списке инцидентна последней.
 * Длина списка, если он не пуст, должна быть равна количеству дуг в графе.
 * Веса дуг никак не учитываются.
 *
 * Пример:
 *
 *      G -- H
 *      |    |
 * A -- B -- C -- D
 * |    |    |    |
 * E    F -- I    |
 * |              |
 * J ------------ K
 *
 * Вариант ответа: A, E, J, K, D, C, H, G, B, C, I, F, B, A
 *
 * Справка: Эйлеров цикл -- это цикл, проходящий через все рёбра
 * связного графа ровно по одному разу
 */
fun Graph.findEulerLoop(): List<Graph.Edge> {
    TODO()
}

/**
 * Минимальное остовное дерево.
 * Средняя
 *
 * Дан связный граф (получатель). Найти по нему минимальное остовное дерево.
 * Если есть несколько минимальных остовных деревьев с одинаковым числом дуг,
 * вернуть любое из них. Веса дуг не учитывать.
 *
 * Пример:
 *
 *      G -- H
 *      |    |
 * A -- B -- C -- D
 * |    |    |    |
 * E    F -- I    |
 * |              |
 * J ------------ K
 *
 * Ответ:
 *
 *      G    H
 *      |    |
 * A -- B -- C -- D
 * |    |    |
 * E    F    I
 * |
 * J ------------ K
 */

// решил алгоритмом Прима
// производительность O(e*log(v)) , e-к-во ребер, v-к-во вершин
// память - O(e+v)

fun Graph.minimumSpanningTree(): Graph {
    val gb = GraphBuilder()
    val root = vertices.firstOrNull() ?: return gb.build()
    val vertexes = mutableSetOf(root)
    span(gb, vertexes)
    return gb.build()
}

private fun Graph.span(gb: GraphBuilder, vertexes: MutableSet<Graph.Vertex>) {
    var minimalEgde: Graph.Edge? = null
    vertexes.forEach { vertex ->
        val probablyMinEdge = getConnections(vertex)
            .filter { it.value.begin !in vertexes || it.value.end !in vertexes }
            .minByOrNull { it.value.weight }?.value
        if (minimalEgde == null) minimalEgde = probablyMinEdge
        if (minimalEgde != null &&
            probablyMinEdge != null &&
            minimalEgde!!.weight > probablyMinEdge.weight
        ) minimalEgde = probablyMinEdge
    }
    // Завел дополнительную переменную для решения !! проблемы
    val foundME = minimalEgde ?: return
    if (foundME.begin in vertexes) {
        vertexes.add(foundME.end)
        gb.addVertex(foundME.end.name)
    } else {
        vertexes.add(foundME.begin)
        gb.addVertex(foundME.begin.name)
    }
    gb.addConnection(foundME)
    return span(gb, vertexes)
}

/**
 * Максимальное независимое множество вершин в графе без циклов.
 * Сложная
 *
 * Дан граф без циклов (получатель), например
 *
 *      G -- H -- J
 *      |
 * A -- B -- D
 * |         |
 * C -- F    I
 * |
 * E
 *
 * Найти в нём самое большое независимое множество вершин и вернуть его.
 * Никакая пара вершин в независимом множестве не должна быть связана ребром.
 *
 * Если самых больших множеств несколько, приоритет имеет то из них,
 * в котором вершины расположены раньше во множестве this.vertices (начиная с первых).
 *
 * В данном случае ответ (A, E, F, D, G, J)
 *
 * Если на входе граф с циклами, бросить IllegalArgumentException
 *
 * Эта задача может быть зачтена за пятый и шестой урок одновременно
 */
fun Graph.largestIndependentVertexSet(): Set<Graph.Vertex> {
    TODO()
}

/**
 * Наидлиннейший простой путь.
 * Сложная
 *
 * Дан граф (получатель). Найти в нём простой путь, включающий максимальное количество рёбер.
 * Простым считается путь, вершины в котором не повторяются.
 * Если таких путей несколько, вернуть любой из них.
 *
 * Пример:
 *
 *      G -- H
 *      |    |
 * A -- B -- C -- D
 * |    |    |    |
 * E    F -- I    |
 * |              |
 * J ------------ K
 *
 * Ответ: A, E, J, K, D, C, H, G, B, F, I
 */

// в функции перебираются всевозможные перестановки элементов,
// для множества из 1 элемента (для каждой вершины, если возможно),
// из 2, ... из n, где n - к-во вершин
// Приблизительно можно оценить производительность как O(n!)
// Аналогично память - O(n!)

fun Graph.longestSimplePath(): Path {
    var answer = Path()
    val possiblePaths = Stack<Path>()
    vertices.forEach { possiblePaths.push(Path(it)) }
    while (possiblePaths.isNotEmpty()) {
        val currentPath = possiblePaths.pop()
        if (answer.length < currentPath.length) answer = currentPath
        val neighbours = getNeighbors(currentPath.vertices[currentPath.length])
        neighbours.forEach { if (it !in currentPath) possiblePaths.push(Path(currentPath, this, it)) }
    }
    return answer
}

/**
 * Балда
 * Сложная
 *
 * Задача хоть и не использует граф напрямую, но решение базируется на тех же алгоритмах -
 * поэтому задача присутствует в этом разделе
 *
 * В файле с именем inputName задана матрица из букв в следующем формате
 * (отдельные буквы в ряду разделены пробелами):
 *
 * И Т Ы Н
 * К Р А Н
 * А К В А
 *
 * В аргументе words содержится множество слов для поиска, например,
 * ТРАВА, КРАН, АКВА, НАРТЫ, РАК.
 *
 * Попытаться найти каждое из слов в матрице букв, используя правила игры БАЛДА,
 * и вернуть множество найденных слов. В данном случае:
 * ТРАВА, КРАН, АКВА, НАРТЫ
 *
 * И т Ы Н     И т ы Н
 * К р а Н     К р а н
 * А К в а     А К В А
 *
 * Все слова и буквы -- русские или английские, прописные.
 * В файле буквы разделены пробелами, строки -- переносами строк.
 * Остальные символы ни в файле, ни в словах не допускаются.
 */
fun baldaSearcher(inputName: String, words: Set<String>): Set<String> {
    TODO()
}