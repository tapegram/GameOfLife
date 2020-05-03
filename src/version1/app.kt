package version1

import kotlin.random.Random


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
    fun getBoarderingCoords(): List<Coord> {
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
                    this.countBoarderingLivingCells(
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
                        this.countBoarderingLivingCells(Coord(x, y))
                    }
                }
            }

    private fun get(coord: Coord): Cell = grid[coord.x][coord.y]

    // TODO: Should try to make illegal coords unrepresentable
    private fun countBoarderingLivingCells(coord: Coord): Int =
        coord
            .getBoarderingCoords()
            .filter { it.x >= 0 }
            .filter { it.x < grid.count() }
            .filter { it.y >= 0 }
            .filter { it.y < grid[it.x].count() }
            .sumBy { get(it).value() }
}

fun main() {
    var board = Board
        .createRandom(15)

    while (board.iteration < 50) {
        print("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n")
        board = board
            .printState()
            .next()
        Thread.sleep(300)
    }
}