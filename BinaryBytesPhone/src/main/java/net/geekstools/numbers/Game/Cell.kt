package net.geekstools.numbers.Game

open class Cell {
    var x: Int = 0
        internal set
    var y: Int = 0
        internal set

    init {
        this.x = x
        this.y = y
    }

    constructor(x: Int, y: Int) {
        this.x = x
        this.y = y
    }
}
