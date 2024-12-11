class Kexercise1 {
    fun main() {
        val number: MutableList<Int> = ArrayList()
        number.add(0)
        number.add(1)
        number.add(2)
        number.add(3)
        printAll(number)
    }

    private fun printAll(list: List<Int>) {
        println(list)

        for (i in list.indices) {
            println(list[i])
        }

        for (integer in list) {
            println(integer)
        }
    }
}