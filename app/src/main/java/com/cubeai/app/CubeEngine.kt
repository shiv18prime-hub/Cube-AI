package com.cubeai.app

/**
 * Logical 3x3 Rubik's Cube engine.
 * Face order: U, R, F, D, L, B. Each face has 9 stickers row-major.
 */
class CubeEngine {
    enum class Face(val ch: Char) { U('U'), R('R'), F('F'), D('D'), L('L'), B('B') }
    private val s = CharArray(54) { i -> Face.entries[i / 9].ch }

    fun reset() {
        for (i in s.indices) s[i] = Face.entries[i / 9].ch
    }

    fun copyState(): String = String(s)
    fun load(facelets: String): Boolean {
        if (!validateFacelets(facelets)) return false
        facelets.toCharArray().copyInto(s)
        return true
    }

    fun isSolved(): Boolean = (0 until 6).all { f ->
        val c = s[f * 9 + 4]
        (0 until 9).all { s[f * 9 + it] == c }
    }

    fun apply(sequence: String) {
        sequence.trim().split(Regex("\\s+")).filter { it.isNotBlank() }.forEach(::move)
    }

    fun move(token: String) {
        require(token.matches(Regex("[URFDLB](2|')?"))) { "Invalid move: $token" }
        val turns = when {
            token.endsWith("2") -> 2
            token.endsWith("'") -> 3
            else -> 1
        }
        repeat(turns) { quarter(token[0]) }
    }

    private fun quarter(m: Char) {
        val old = s.copyOf()
        // Geometry-derived permutation: sticker positions/normals rotate with the selected layer.
        for (i in 0 until 54) {
            val st = sticker(i)
            if (!inLayer(st, m)) continue
            val p = rotate(st.p, m)
            val n = rotate(st.n, m)
            val j = indexOf(p, n)
            s[j] = old[i]
        }
    }

    data class V(val x:Int,val y:Int,val z:Int)
    data class St(val p:V,val n:V)

    private fun sticker(i:Int): St {
        val f=i/9; val k=i%9; val r=k/3; val c=k%3
        return when(f) {
            0 -> St(V(c-1,-1,r-1),V(0,-1,0))       // U
            1 -> St(V(1,r-1,1-c),V(1,0,0))         // R
            2 -> St(V(c-1,r-1,1),V(0,0,1))         // F
            3 -> St(V(c-1,1,1-r),V(0,1,0))         // D
            4 -> St(V(-1,r-1,c-1),V(-1,0,0))       // L
            else -> St(V(1-c,r-1,-1),V(0,0,-1))    // B
        }
    }

    private fun inLayer(st:St,m:Char)=when(m) {
        'U' -> st.p.y==-1; 'D' -> st.p.y==1
        'R' -> st.p.x==1; 'L' -> st.p.x==-1
        'F' -> st.p.z==1; else -> st.p.z==-1
    }

    private fun rotate(v:V,m:Char):V = when(m) {
        'R' -> V(v.x,-v.z,v.y)
        'L' -> V(v.x,v.z,-v.y)
        'U' -> V(v.z,v.y,-v.x)
        'D' -> V(-v.z,v.y,v.x)
        'F' -> V(-v.y,v.x,v.z)
        else -> V(v.y,-v.x,v.z)
    }

    private fun indexOf(p:V,n:V):Int {
        for(i in 0 until 54) {
            val a=sticker(i)
            if(a.p==p && a.n==n) return i
        }
        error("Invalid sticker geometry")
    }

    companion object {
        fun validateFacelets(v:String):Boolean {
            if(v.length!=54) return false
            val allowed=setOf('U','R','F','D','L','B')
            if(v.any{it !in allowed}) return false
            if(allowed.any{ch->v.count{it==ch}!=9}) return false
            // Fixed centers are required for our scanner/solver orientation.
            return v[4]=='U' && v[13]=='R' && v[22]=='F' &&
                   v[31]=='D' && v[40]=='L' && v[49]=='B'
        }
    }
}
