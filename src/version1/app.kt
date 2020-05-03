package version1

import kotlin.random.Random
/*
Things that could be improved:
1) Relationship between cell position and its alive or dead status.
    Maybe cells should know there position?
    Maybe a board should have spaces which hold cells, and those spaces know their position?
2) Should make illegal coords unrepresentable (impossible to make coords off the board)
3) The `value` method is sloppy, should that even live on the cell?
4) Coord's xy orientation is confusing.
5) Could be much more configurable
 */

sealed class Cell {
    abstract fun next(neighborsAlive: Int): Cell
    abstract fun value(): Int

    object Alive: Cell() {
        override fun next(neighborsAlive: Int): Cell = when (neighborsAlive) {
            2 -> Alive
            3 -> Alive
            else -> Dead
        }
        override fun value(): Int = 1
        override fun toString() = "A"
    }

    object Dead: Cell() {
        override fun next(neighborsAlive: Int): Cell = when (neighborsAlive) {
            3 -> Alive
            else -> Dead
        }
        override fun value(): Int = 0
        override fun toString() = "_"
    }
}

data class Coord(val x: Int, val y: Int) {
    fun getBorderingCoords(): List<Coord> {
        return listOf(
            Coord(x - 1, y),
            Coord(x + 1, y),
            Coord(x - 1, y - 1),
            Coord(x - 1, y + 1),
            Coord(x + 1, y - 1),
            Coord(x + 1, y + 1),
            Coord(x, y + 1),
            Coord(x, y - 1)
        )
    }
}
data class Board(
    val grid: List<List<Cell>>,
    val iteration: Int = 0
) {
    companion object {
        fun createRandom(size: Int): Board = Board(
            List(size) {
                List(size) {
                    listOf(Cell.Alive, Cell.Dead)[Random.nextInt(0,2)]
                }
            }
        )
    }
    fun next(): Board = this.copy(
        grid = grid.mapIndexed { x, row ->
            row.mapIndexed { y, cell ->
                cell.next(
                    this.countBorderingLivingCells(
                        Coord(x, y)
                    )
                )
            }
        },
        iteration = iteration + 1
    )

    fun printState(): Board =
        this
            .also { println("ITERATION ${it.iteration}")}
            .also { grid.map { println(it) } }
            .also {
                grid.mapIndexed { x, row ->
                    row.mapIndexed { y, cell ->
                        this.countBorderingLivingCells(Coord(x, y))
                    }
                }
            }

    private fun get(coord: Coord): Cell = grid[coord.x][coord.y]

    // TODO: Should try to make illegal coords unrepresentable
    private fun countBorderingLivingCells(coord: Coord): Int =
        coord
            .getBorderingCoords()
            .filter { it.x >= 0 }
            .filter { it.x < grid.count() }
            .filter { it.y >= 0 }
            .filter { it.y < grid[it.x].count() }
            .sumBy { get(it).value() }
}

fun main() {
    var board = Board
        .createRandom(20)

    while (board.iteration < 50) {
        // Fake clear the output so it looks animated
        print("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n")
        board = board
            .printState()
            .next()
        Thread.sleep(300)
    }
}
