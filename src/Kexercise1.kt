class Kexercise1 {

    fun printAll(list: List<Int>) {
        println(list)

        for (i in list.indices) {
            println(list[i])
        }

        for (integer in list) {
            println(integer)
        }
    }
}

fun main(){
    val number = listOf(0,1,2,3)
    val a = Kexercise1()
    a.printAll(number)
}