package util

//region ASearch
object ASearch{
    /**
     * @param startState stato iniziale della ricerca
     * @param stateCost funzione che dato due stati <b>s1</b> e <b>s2</b> calcolo il servicesCost per passare da s1 a s2
     * @param heuristic funzione che dato uno stato <b>s</b> stima il servicesCost per concludere la ricerca
     * @param nextState funzione che dato uno stato <b>s</b> calcola gli stati futuri
     * @param isGoal funzione che dato uno stato restituisce vero se è lo stato desiderato
     * @tparam T
     * @return
     */
    fun <T> search(startState : T,
                   stateCost : (T) -> Double,
                   heuristic : (T) -> Double,
                   nextState : (T) -> List<T>,
                   isGoal : (T) -> Boolean) : Node<T>{
        require(startState != null)
        // Inizializzo una cosa contenente lo stato iniziale e la ordino per servicesCost in ordine crescente

        val queue = util.FibonacciHeap<Node<T>>()

        queue.enqueue(NodeInstance(startState,0.0,null),0.0)

        while(!queue.isEmpty){
            // estraggo il primo elemento con servicesCost minore
            val currentState = queue.dequeueMin().value
            // se ho trovato lo stato goal mi fermo altrimenti costruisco i nuovi stati
            if( isGoal(currentState.state) )
                return currentState

            // genero i nuovi stati e li aggiungo alla coda
            nextState(currentState.state).forEach {
                val newNode = NodeInstance(it, stateCost(it),currentState)
                queue.enqueue(newNode,(newNode.cost+heuristic(it)))
            }
        }

        throw NoSolutionException("Non ho trovato il goal finale")
    }

    class NoSolutionException(msg : String) : Exception(msg)

    interface Node<T>{
        val state : T
        val cost : Double
        val prevNode : Node<T>?
    }

    private class NodeInstance<T>(override val state : T,
                                  override val cost : Double,
                                  override val prevNode : Node<T>?) : Node<T>

}



//endregion