package com.gameoflife.version1

fun main(args: Array<String>) {
    var board = Board
        .createRandom(100)

    while (board.iteration < 1000) {
        // Fake clear the output so it looks animated
        println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n")
        board = board
            .printState()
            .next()
        Thread.sleep(200)
    }
}
