import util.ASearch
import kotlin.collections.HashMap
import kotlin.concurrent.thread
import kotlin.math.max
import kotlin.math.min

//region oggetto soluzione, DA MODIFICARE IN BASE AL PROBLEMA
class Solution(val input: InputData) {

    data class State(val neededServicesQuantity: Array<Int>,
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
        val availableUnitsFromProviderRegion = HashMap<Pair<Int, Int>, Int>()

        // popolo la lista iniziale
        for (providerIndex in input.providerRegion.indices)
            for (regionIndex in input.providerRegion[providerIndex].indices)
                availableUnitsFromProviderRegion
                        .put( Pair(providerIndex, regionIndex),
                              input.providerRegion[providerIndex][regionIndex].availableUnit)

        println("Numero di provider X regione = ${availableUnitsFromProviderRegion.size}")

        val availableService = Array(input.numberOfServices, {0L})

        for(key in availableUnitsFromProviderRegion.keys)
            for(i in 0 until availableService.size)
                availableService.set(i, availableService[i] + input.providerRegion[key.first][key.second].packetComposition[i] * availableUnitsFromProviderRegion.get(key)!!)

        var searchedProjects = 0

        // ordina i progetti
        val sortedProject = sort()

        // key : IDProgetto value : Stringa di output>
        val projectResult = HashMap<Int, String>()

        // per ogni progetto faccio una ricerca per trovare i componenti da prendere
        for (prj in input.project) {
            var res: ASearch.Node<State>? = null
            try {
                val startState = State(Array(input.numberOfServices, { prj.units[it] }), HashMap(), 0.0, 0.0, 0.0, 0.0)
                val startTime = System.nanoTime()
                res = ASearch.search<State>(startState,
                        stateCost = { 1/it.score },
                        isGoal = {
                            if((System.nanoTime() - startTime)/1000000 > 300) {
                                println("Timeout ${searchedProjects}")
                                return@search true
                            }

                            val mapped = availableService.mapIndexed {
                                index, aQ -> aQ - (it.usedProviders.map {
                                val packet = input.providerRegion[it.key.first][it.key.second].packetComposition[index]
                                val quantity = it.value
                                packet * it.value
                                }.sum())
                            }

                            val filtered = mapped.filterIndexed { index, value -> it.neededServicesQuantity[index] > 0 && value - it.neededServicesQuantity[index] > 0 }

                            it.neededServicesQuantity.sum() == 0
                            || !filtered.any()/*!availableService.mapIndexed {
                                index, aQ -> aQ - it.usedProviders.map {
                                                input.providerRegion[it.key.first][it.key.second].packetComposition[index] * it.value
                                                }.sum()
                                    }.filterIndexed { index, value -> value - it.neededServicesQuantity[index] > 0 }.any()*/
                            // possibile miglioramento, controllo se non ci sono più abbastanza pezzi
                        },
                        heuristic = {
                            val invScore = 1/it.score
                            val pesoNeeded = when{
                                invScore > 1000 -> 10000.0
                                invScore > 100 -> 1000.0
                                invScore > 10 -> 100.0
                                invScore > 1 -> 10.0
                                invScore > 0.1 -> 1.0
                                invScore > 0.01 -> 0.1
                                invScore > 0.001 -> 0.01
                                invScore > 0.0001 -> 0.001
                                invScore > 0.00001 -> 0.0001
                                else -> 1.0
                            }
                            val pesoScore = 0//0.0001
                            //if(searchedProjects == 3690)
                            //    println("w : ${pesoNeeded} s : ${1/it.score} q : ${pesoNeeded * it.neededServicesQuantity.sum()} s : ${pesoScore/it.score}")
                            pesoNeeded * it.neededServicesQuantity.sum() + pesoScore/it.score },
                        nextState = {
                            //println("s : ${it.score} , need : ${it.neededServicesQuantity.sum()}, prov : ${it.usedProviders}")
                            val listaNext = mutableListOf<State>()
                            val state = it

                            for (providerRegionPair in availableUnitsFromProviderRegion.keys.filterNot { state.usedProviders.containsKey(it) }) {
                                // providerRegion ancora non utilizzato
                                val quantity = howMuchICanBuy(prj, state.neededServicesQuantity, providerRegionPair, availableUnitsFromProviderRegion)
                                if(quantity <= 0)
                                    continue // niente da comprare

                                assert(quantity <= availableUnitsFromProviderRegion.get(providerRegionPair)!!, {"Sto comprando più del dovuto"})
                                val newUsedService = HashMap<Pair<Int,Int>,Int>()
                                newUsedService.putAll(state.usedProviders)
                                newUsedService.put(providerRegionPair, quantity)

                                val newNeededServiceQuantity : Array<Int> =
                                        state.neededServicesQuantity
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
                res.state.usedProviders.forEach {
                    val newValue = availableUnitsFromProviderRegion.get(it.key)!! - it.value
                    if (newValue > 0)
                        availableUnitsFromProviderRegion.put(it.key, newValue)
                    else
                        availableUnitsFromProviderRegion.remove(it.key)

                    for(i in availableService.indices){
                        availableService.set(i, availableService[i] -
                            input.providerRegion[it.key.first][it.key.second].packetComposition[i] * it.value)
                    }
                }


                availableService.forEach { assert(it >= 0, {"Uno dei servizi è negativo"}) }
                // assertion
                availableUnitsFromProviderRegion.forEach { assert(it.component2() >= 0, { "Una delle componenti rimanenti è < 0" }) }

                if(searchedProjects % 10 == 0)
                    println("Fine progetto ${searchedProjects} ${availableUnitsFromProviderRegion.size}")
            } catch (e: ASearch.NoSolutionException) {
                // Non c'è soluzione, stampo una stringa vuota
                projectResult.put(prj.prjID, "\n")
                println("Fine Progetto ${searchedProjects} con fallimento ${availableUnitsFromProviderRegion.size}")
            }finally {
                searchedProjects++
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