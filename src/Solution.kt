import util.ASearch
import kotlin.collections.HashMap
import kotlin.math.max
import kotlin.math.min

//region oggetto soluzione, DA MODIFICARE IN BASE AL PROBLEMA
class Solution(val input: InputData) {

    data class State(val NeededServicesQuantity: Array<Int>,
                     // key : (providerIndex,regionIndex) value : quanity
                     val usedProviders: HashMap<Pair<Int,Int>,Int>,
                     val servicesCost: Double,
                     val availabilityScore: Double,
                     val slaScore : Double,
                     val latency: Double){

        val score =
                1.0e9 / (slaScore + ((servicesCost * latency)/availabilityScore) )

    }

    fun getSolution(): String {

        val printer = StringBuilder()

        // key : (providerIndex,regionIndex) value : quanity
        val availableServicesQuantity = HashMap<Pair<Int, Int>, Int>()

        // popolo la lista iniziale
        for (providerIndex in input.providerRegion.indices)
            for (regionIndex in input.providerRegion[providerIndex].indices)
                availableServicesQuantity
                        .put( Pair(providerIndex, regionIndex),
                              input.providerRegion[providerIndex][regionIndex].availableUnit)

        println("Numero di provider X regione = ${availableServicesQuantity.size}")
        var searchedProjects = 1

        // ordina i progetti
        val sortedProject = sort()

        // key : IDProgetto value : Stringa di output>
        val projectResult = HashMap<Int, String>()

        // per ogni progetto faccio una ricerca per trovare i componenti da prendere
        for (prj in input.project) {
            var res: ASearch.Node<State>? = null
            try {
                val startState = State(Array(input.numberOfServices, { prj.units[it] }), HashMap(), 0.0, 0.0, 0.0, 0.0)

                res = ASearch.search<State>(startState,
                        stateCost = { 1/it.score },
                        isGoal = { it.NeededServicesQuantity.sum() == 0
                            // possibile miglioramento, contorllo se non ci sono più abbastanza pezzi
                        },
                        heuristic = {
                            //println("q : ${it.NeededServicesQuantity.sum()} s : ${10000/it.score}")
                            it.NeededServicesQuantity.sum()/1.0 + 10000/it.score },
                        nextState = {
                            //println("s : ${it.score} , need : ${it.NeededServicesQuantity.sum()}, prov : ${it.usedProviders}")
                            val listaNext = mutableListOf<State>()
                            val state = it

                            for (providerRegionPair in availableServicesQuantity.keys.filterNot { state.usedProviders.containsKey(it) }) {
                                // providerRegion ancora non utilizzato
                                val quantity = howMuchICanBuy(prj, state.NeededServicesQuantity, providerRegionPair, availableServicesQuantity)
                                if(quantity <= 0)
                                    continue // niente da comprare

                                assert(quantity <= availableServicesQuantity.get(providerRegionPair)!!, {"Sto comprando più del dovuto"})
                                val newUsedService = HashMap<Pair<Int,Int>,Int>()
                                newUsedService.putAll(state.usedProviders)
                                newUsedService.put(providerRegionPair, quantity)

                                val newNeededServiceQuantity : Array<Int> =
                                        state.NeededServicesQuantity
                                                .mapIndexed{ index, value->
                                                    max(0,value-(input.providerRegion[providerRegionPair.first][providerRegionPair.second].packetComposition[index]*quantity))}
                                                .toTypedArray()

                                val newCost = state.servicesCost + quantity * input.providerRegion[providerRegionPair.first][providerRegionPair.second].costoUnitario

                                listaNext.add(State(newNeededServiceQuantity,
                                        newUsedService, newCost, calcolaDisponibilita(newUsedService),slaScore = 0.0,latency = latenza(prj, newUsedService)))

                            }
                            listaNext
                        }
                )


                // Costruzione della linea di output
                val outputString = StringBuilder()
                res.state.usedProviders.forEach { outputString.append("${it.key.first} ${it.key.second} ${it.value} ") }
                outputString.append("\n")
                projectResult.put(prj.prjID, outputString.toString())

                // Aggiorna le unità di pacchetti rimanenti
                res.state.usedProviders.forEach { availableServicesQuantity.put(it.key, availableServicesQuantity.get(it.key)!! - it.value) }
                // assertion
                availableServicesQuantity.forEach { assert(it.component2() >= 0, { "Una delle componenti rimanenti è < 0" }) }

                println("Fine progetto ${searchedProjects++}")
            } catch (e: ASearch.NoSolutionException) {
                // Non c'è soluzione, stampo una stringa vuota
                projectResult.put(prj.prjID, "\n")
                println("Fine Progetto ${searchedProjects++} con fallimento")
            }

        }

        // stampo i risultati in ordine di lettura dell'input
        projectResult.keys.sorted().forEach { printer.append(projectResult.get(it)) }

        return printer.toString()
    }

    fun latenza(prj: Project, coseComprate: HashMap<Pair<Int, Int>, Int>): Double {

        var num: Double = 0.0
        var den = 0
        coseComprate.forEach {
            val sum = it.value * input.providerRegion[it.key.first][it.key.second].packetComposition.sum()
            num += sum * input.providerRegion[it.key.first][it.key.second].latency[input.countryName.indexOf(prj.countryName)]
            den += sum
        }
        return num / den
    }

    fun calcolaDisponibilita(coseComprate: HashMap<Pair<Int, Int>, Int>): Double {

        var sum = 0
        var quadSum = 0

        coseComprate.forEach {
            sum += it.value
            quadSum += it.value * it.value
        }
        return sum.toDouble() / quadSum
    }

    fun howMuchICanBuy(prj: Project, neededServicesQuantity: Array<Int>, providerRegionPair: Pair<Int, Int>, availableServicesQuantity: HashMap<Pair<Int, Int>, Int>) : Int {

        val availableQuanity = availableServicesQuantity.get(providerRegionPair)!!

        var quantity = 0

        for (i in neededServicesQuantity.indices) {
            if(neededServicesQuantity[i] > 0)
                quantity = max(quantity, Math.ceil(neededServicesQuantity[i].toDouble()%input.providerRegion[providerRegionPair.first][providerRegionPair.second].packetComposition[i]).toInt())
        }

        return min(quantity, availableQuanity)
    }

    fun sort(): Unit {
        input.project.sortWith(object : Comparator<Project> {
            override fun compare(o1: Project?, o2: Project?): Int {
                return (o1!!.penalty * o1.units.sum()) - (o2!!.penalty * o2.units.sum())
            }
        })
    }

}
//endregion