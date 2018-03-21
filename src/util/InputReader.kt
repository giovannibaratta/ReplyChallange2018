package util

import java.io.BufferedReader

class InputReader(val input : BufferedReader){

    //region Classi per la lettura di argomenti da input
    data class Read1<A>(val arg1 : A)
    data class Read2<A,B>(val arg1 : A, val arg2 : B)
    data class Read3<A,B,C>(val arg1 : A, val arg2 : B, val arg3 : C)
    data class Read4<A,B,C,D>(val arg1 : A, val arg2 : B, val arg3 : C, val arg4 : D)
    data class Read6<A,B,C,D,E,F>(val arg1 : A, val arg2 : B, val arg3 : C, val arg4 : D, val arg5 : E, val arg6 :F)
    //endregion

    //region Funzione per il cast da stringa ad altro tipo
    inline fun <reified T> String.cast() : T = when(T::class){
        Int::class -> this.toInt() as T
        Long::class -> this.toLong() as T
        Double::class -> this.toDouble() as T
        Float::class -> this.toFloat() as T
        Char::class -> this[0] as T
        String::class -> this as T
        else -> throw Exception("Impossibile effettuare la conversione")
    }
    //endregion

    //region Funzioni per la lettura di argomenti da input
    inline fun <reified A> convert(input : String) : A = input.cast<A>()
    inline fun <reified A> read1() : Read1<A> = Read1(convert<A>(input.readLine()
            ?: throw Exception("Non è stato possibile leggere 1 parametro")))
    inline fun <reified A, reified B> read2(delimiters : String = " ") : Read2<A, B> {
        val line = input.readLine() ?: throw Exception("Non è stato possibile leggere 1 parametro")
        val token = line.split(delimiters)
        if(token.size < 2)
            throw Exception("Mancano degli argomenti")
        return Read2(convert(token[0]), convert(token[1]))
    }

    inline fun <reified A, reified B, reified C> read3(delimiters : String = " ") : Read3<A, B, C> {
        val line = input.readLine() ?: throw Exception("Non è stato possibile leggere 1 parametro")
        val token = line.split(delimiters)
        if(token.size < 3)
            throw Exception("Mancano degli argomenti")
        return Read3(convert(token[0]), convert(token[1]), convert(token[2]))
    }

    inline fun <reified A, reified B, reified C, reified D> read4(delimiters : String = " ") : Read4<A, B, C, D> {
        val line = input.readLine() ?: throw Exception("Non è stato possibile leggere 1 parametro")
        val token = line.split(delimiters)
        if(token.size < 4)
            throw Exception("Mancano degli argomenti")
        return Read4(convert(token[0]), convert(token[1]), convert(token[2]), convert(token[3]))
    }

    inline fun <reified A, reified B, reified C, reified D,reified E,reified F> read6(delimiters : String = " ") : Read6<A, B, C, D,E,F> {
        val line = input.readLine() ?: throw Exception("Non è stato possibile leggere 1 parametro")
        val token = line.split(delimiters)
        if(token.size < 6)
            throw Exception("Mancano degli argomenti")
        return Read6(convert(token[0]), convert(token[1]), convert(token[2]), convert(token[3]),convert(token[4]),convert(token[5]))
    }
//endregion

    //region Funzione per la lettura di griglie NxM
    inline fun <reified T> readGrid(row : Int, column : Int, delimiters: String, convertFunction : (String) -> T) : Array<Array<T>>{
        val grid = Array(row,{ emptyArray<T>()})
        for( i in 0 until row){
            val line = input.readLine() ?: throw Exception("Impossibile leggere la riga ${i+1}")
            var token = line.split(delimiters)
            if(delimiters == "")
                token = token.subList(1,token.size-1)
            if(token.size != column)
                throw Exception("La riga ${i+1} ha un numero di elementi sbagliato")
            val temp = mutableListOf<T>()
            token.forEach { temp.add(convertFunction(it)) }
            grid[i] = temp.toTypedArray()
        }
        return grid
    }
    //endregion

    //region Funzione per la lettura di array
    inline fun<reified T>  readArray(delimiters: String = " ",
                                     size : Int = 0,
                                     mapper : (String) -> T = {
                                         convert<T>(it)
                                     }) : Array<T>{
        require(size >= 0)
        val line = input.readLine() ?: throw Exception("Non è stato possibile leggere la linea")
        var token = line.split(delimiters)
        if(delimiters == "")
            token = token.subList(1,token.size-1)
        if(size != 0 && token.size != size)
            throw IllegalStateException("Il numero di elementi dell'array non corrisponde a $size")
        return Array(token.size,{mapper(token[it])})
    }
    //endregion

}