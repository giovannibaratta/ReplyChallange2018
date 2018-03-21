package util

//region Funzioni con cache
class Function1Cache<T,R>(function: Function1<T,R>) {

    private val cache = Cache<T,R>(function)
    fun functionCall(arg: T): R = cache.retrieve(arg)
}

class Function2Cache<T,K,R>(function: Function2<T,K,R>) {

    private val cache =  Cache<Pair<T,K>,R>({function(it.first,it.second)})
    fun functionCall(arg1 : T, arg2 : K): R = cache.retrieve(Pair(arg1,arg2))
}

class Cache<T,R>(private val function : (T) -> R){

    private val cache = HashMap<T,R>()

    fun retrieve(args : T) : R {
        val cached = cache[args]
        if(cached == null){
            val valueToCache = function(args)
            cache.put(args, valueToCache)
            return valueToCache
        }

        return cached
    }
}

object CachableFunction{

    fun <T,R> buildCache(function : Function1<T,R>) : Function1<T,R> =
            Function1Cache<T,R>(function)::functionCall

    fun <T,K,R> buildCache(function : Function2<T,K,R>) : Function2<T,K,R> =
            Function2Cache<T,K,R>(function)::functionCall
}
//endregion