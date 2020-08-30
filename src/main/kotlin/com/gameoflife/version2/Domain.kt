package com.gameoflife.version2

import arrow.core.MapK
import java.util.UUID

/*
Domain
 */
sealed class Cell {
    object Alive: Cell()
    object Dead: Cell()
}

data class World(
    val nodes: MapK<NodeId, Node>,
    val edges: MapK<NodeId, NodeId>
)

typealias NodeId = UUID
data class Node(
    val id: NodeId,
    val cell: Cell
)

typealias LivingNeighbors = Int // Could be Natural or something fancy with generic type params
typealias NextGenCell = (Cell, LivingNeighbors) -> Cell
typealias NextGenWorld = (World, NextGenCell) -> World

/*
Implementations
 */

// Creating a graph from a concrete structure is a little messy.
// 2D can be implemented by mapping the list<list<cell>> to list<list<node>
// and then doing another parse to build the map of edges.
fun createWorldFrom2DList(world: List<List<Cell>>): World = TODO()

fun nextGen(world: World, nextGenCell: NextGenCell): World =
    world.copy(
        nodes = world.nodes.map { node ->
            node.copy(
                cell = nextGenCell(node.cell, countLivingNeighbors(world, node.id))
            )
        }
    )

// Example implementation of a rule.
fun standardGenRule(cell: Cell, livingNeighbors: LivingNeighbors): Cell =
    when (cell) {
        Cell.Alive -> when (livingNeighbors) {
            2, 3 -> Cell.Alive
            else -> Cell.Dead
        }
        Cell.Dead -> when (livingNeighbors) {
            3 -> Cell.Alive
            else -> Cell.Dead
        }
    }

private fun countLivingNeighbors(world: World, nodeId: NodeId): LivingNeighbors =
    world.edges.filterKeys { it == nodeId }
        .map { edge -> world.nodes[edge.value]!! }
        .map { it.cell }
        .sumBy {
            when(it) {
                Cell.Alive -> 1
                Cell.Dead -> 0
            }
        }
