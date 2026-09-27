package com.cubeai.app

import com.weijiekeji.kociemba.twophase.Search

/**
 * Solver API foundation. It already guarantees correct solutions for any state
 * created from a known move history; camera-state solving plugs into the same API next.
 */
class CubeSolver {
    data class Result(val moves: List<String>, val valid: Boolean, val message: String)

    fun solveFacelets(facelets:String): Result {
        if (!CubeEngine.validateFacelets(facelets)) return Result(emptyList(), false, "Invalid facelets")
        val raw = try { Search.solution(facelets, 24, 10, false) } catch (e:Exception) {
            return Result(emptyList(), false, e.message ?: "Solver failed")
        }
        if (raw.startsWith("Error")) return Result(emptyList(), false, raw)
        val moves=raw.trim().split(Regex("\\s+")).filter{it.matches(Regex("[URFDLB](2|')?"))}
        return if (verifySolution(facelets,moves)) Result(moves,true,"Solution ready")
        else Result(emptyList(),false,"Solution verification failed")
    }

    fun solveKnownScramble(scramble: String): Result {
        val tokens = scramble.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
        if (tokens.any { !it.matches(Regex("[URFDLB](2|')?")) })
            return Result(emptyList(), false, "Invalid scramble notation")
        val inverse = tokens.asReversed().map(::inverse)
        return Result(inverse, true, "Solution ready")
    }

    fun verifySolution(start: String, moves: List<String>): Boolean {
        val cube = CubeEngine()
        if (!cube.load(start)) return false
        return try {
            moves.forEach(cube::move)
            cube.isSolved()
        } catch (_: IllegalArgumentException) { false }
    }

    private fun inverse(m:String)=when {
        m.endsWith("2") -> m
        m.endsWith("'") -> m.dropLast(1)
        else -> "$m'"
    }
}
