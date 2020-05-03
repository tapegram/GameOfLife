package com.gameoflife.version2
/*
https://www.47deg.com/blog/conway-kotlin/
 */

import arrow.core.Tuple2
import arrow.core.k
import arrow.core.toT
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.io.monad.monad
import arrow.mtl.State
import arrow.mtl.StateT
import java.util.UUID


fun generateId(): String = UUID.randomUUID().toString()

sealed class Cell {
    abstract val id: String

    data class Alive(override val id: String) : Cell() {
        constructor() : this(generateId())
    }
    data class Dead(override val id: String) : Cell() {
        constructor() : this(generateId())
    }
}
fun Cell.neighbours(universe: Universe): List<Cell> {
    val allDeltaCombinations = listOf(-1, 0, 1) * listOf(-1, 0, 1) // ListK cartesian product to get all combinations
    val deltas = allDeltaCombinations - Tuple2(0, 0)
    return deltas
        .filterMap { universe.cellPosition(this).shiftBy(universe, it) }
        .map { universe[it.x][it.y] }
}

typealias Universe = List<List<Cell>>
data class Position(val x: Int, val y: Int)

private fun Universe.tick(): Tuple2<Universe, Unit> {
    val newGeneration = this.map { column ->
        column.map { cell ->
            val aliveNeighbors = cell.aliveNeighbours(this).size
            when {
                aliveNeighbors < 2 || aliveNeighbors > 3 -> Cell.Dead()
                aliveNeighbors == 3 && cell.isDead() -> Cell.Alive()
                else -> Cell.Alive()
            }
        }.k()
    }.k()
    return newGeneration toT Unit
}

fun gameOfLife(maxGenerations: GenerationCount = Infinite, currentGeneration: Int = 0): StateT<ForIO, Universe, Unit> =
    StateT(IO.monad()) { universe: Universe ->
        IO {
            println(universe)
            universe.tick()
        }
    }.flatMap(IO.monad()) {
        when (maxGenerations) {
            is Infinite -> gameOfLife(maxGenerations, currentGeneration + 1)
            is Finite -> if (currentGeneration < maxGenerations.count - 1) {
                gameOfLife(maxGenerations, currentGeneration + 1)
            } else {
                StateT.just(IO.monad(), it)
            }
        }
    }
