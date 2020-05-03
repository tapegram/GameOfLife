package com.gameoflife.version2

import arrow.core.some
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.specs.StringSpec
import org.junit.Assert.assertEquals

class UniverseGen(private val center: Cell, private val aliveNeighbors: Int) : Gen<Universe> {

    override fun constants(): Iterable<Universe> = emptyList()

    override fun random(): Sequence<Universe> = generateSequence {
        val alive = (1..aliveNeighbors).map { Cell.Alive() }.toList()
        val dead = (1..(8 - aliveNeighbors)).map { Cell.Dead() }.toList()
        val all = alive + dead

        all.shuffled().toGrid(center)
    }

    private fun List<Cell>.toGrid(center: Cell): List<List<Cell>> =
        listOf(
            listOf(get(0), get(1), get(2)),
            listOf(get(3), center, get(4)),
            listOf(get(5), get(6), get(7))
        )
}

fun universeGenWith2or3AliveNeighbours(cell: Cell) =
    Gen.choose(2, 3).flatMap { UniverseGen(cell, it) }

fun universeGenFewerThan2AliveNeighbours(cell: Cell) =
    Gen.choose(0, 1).flatMap { UniverseGen(cell, it) }

fun universeGenMoreThan3AliveNeighbours(cell: Cell) =
    Gen.choose(4, 8).flatMap { UniverseGen(cell, it) }

class GameOfLifeSpec : StringSpec({

    "cell position is accurate" {
        val cell = Cell.Dead()
        val universe = listOf(
            listOf(Cell.Dead(), Cell.Alive(), Cell.Dead()),
            listOf(Cell.Alive(), Cell.Alive(), Cell.Dead()),
            listOf(Cell.Dead(), cell, Cell.Dead())
        )

        assertEquals(Position(2, 1).some(), universe.cellPosition(cell))
    }

    "cell neighbours are accurate" {
        val cell = Cell.Dead()

        val universe = listOf(
            listOf(Cell.Dead(), Cell.Dead(), Cell.Dead()),
            listOf(Cell.Alive(), Cell.Alive(), Cell.Alive()),
            listOf(Cell.Alive(), cell, Cell.Alive())
        )

        assertEquals(
            listOf(Position(1, 0), Position(1, 1), Position(1, 2), Position(2, 0), Position(2, 2)).map { it.some() },
            cell.neighbours(universe).map { universe.cellPosition(it) }
        )
    }

    "Any live cell with two or three neighbors survives" {
        forAll(universeGenWith2or3AliveNeighbours(Cell.Alive())) { universe ->
            val finalState = gameOfLife(Finite(1)).run(IO.monad(), universe).fix().unsafeRunSync().a

            finalState[1][1].isAlive()
        }
    }

    "Any dead cell with three live neighbors becomes a live cell" {
        forAll(UniverseGen(center = Cell.Dead(), aliveNeighbors = 3)) { universe ->
            val finalState = gameOfLife(Finite(1)).run(IO.monad(), universe).fix().unsafeRunSync().a

            finalState[1][1].isAlive()
        }
    }

    "Any live cell with fewer than two live neighbours dies, as if by underpopulation." {
        forAll(universeGenFewerThan2AliveNeighbours(Cell.Alive())) { universe ->
            val finalState = gameOfLife(Finite(1)).run(IO.monad(), universe).fix().unsafeRunSync().a

            finalState[1][1].isDead()
        }
    }

    "Any live cell with more than three live neighbours dies, as if by overpopulation." {
        forAll(universeGenMoreThan3AliveNeighbours(Cell.Alive())) { universe ->
            val finalState = gameOfLife(Finite(1)).run(IO.monad(), universe).fix().unsafeRunSync().a

            finalState[1][1].isDead()
        }
    }
})