package com.cubeai.app

/** Converts scanner colors into CubeEngine URFDLB facelets using center stickers. */
object ScanStateMapper {
    data class Result(val facelets:String?, val message:String)

    fun map(faces:List<List<CubeColorDetector.Sample>>):Result {
        if(faces.size!=6 || faces.any{it.size!=9}) return Result(null,"Scan all 6 faces")
        val centers=faces.map{it[4].label}
        if(centers.toSet().size!=6) return Result(null,"Center colors must all be different. Rescan a face.")
        val counts=faces.flatten().groupingBy{it.label}.eachCount()
        if(centers.any{(counts[it]?:0)!=9}) return Result(null,"Each cube color must appear exactly 9 times.")
        // Scanner order is FRONT, RIGHT, BACK, LEFT, TOP, BOTTOM.
        // Engine order is U,R,F,D,L,B; centers define color -> engine face.
        val colorToFace=mapOf(
            faces[4][4].label to 'U', faces[1][4].label to 'R',
            faces[0][4].label to 'F', faces[5][4].label to 'D',
            faces[3][4].label to 'L', faces[2][4].label to 'B'
        )
        val order=listOf(4,1,0,5,3,2)
        val s=buildString {
            order.forEach { i -> faces[i].forEach { append(colorToFace[it.label] ?: '?') } }
        }
        return if(CubeEngine.validateFacelets(s)) Result(s,"Scan valid")
        else Result(null,"Invalid cube scan. Correct colors or rescan.")
    }
}
