import util.InputReader
import java.io.BufferedReader
import java.io.FileReader

//region lettura input, DA MODIFICARE PER FAR FUNZIONARE LA SOLUTION
data class InputData(
        //region inserire qui sotto la struttura dell'input
        //endregion
        val numberOfProviders : Int,
        val numberOfServices : Int,
        val numberOfCountries : Int,
        val numberOfProjects : Int,
        val serviceName : Array<String>,
        val countryName : Array<String>,
        val providerRegion : Array<Array<Region>>,
        val project : Array<Project>
        //region fine struttura dell'input
        //endregion
)

data class Project(val prjID : Int,
                   val penalty : Int,
                   val countryName : String,
                   val units : Array<Int>)


/* OUTPUT


 */

data class Region(val name : String,
                  val packetComposition : Array<Int>,
                  val costoUnitario : Double,
                  val availableUnit : Int,
                  val latency : Array<Int>)

object Input{

    fun readInput() : InputData{
        /* esempio di lettura
            val (args1, args2, args3) = read3<Int, Int, Int>(" ")
            val grid = readGrid(2,2,"",MyType::mapper)
            val array = readArray<MyType.Enumerativo>(delimiters = " ",size =2,mapper = MyType::mapper)
         */
        //region Inserire qui sotto la lettura dell'input
        //endregion

        /*
    3 3 3 5
    cpu memory disk
    Italy Germany Spain
    Amazon 4
    Milan
    60 0.32 10 5 1
    50 75 52
    London
    100 0.8 8 8 8
    75 60 35
    Madrid
    10 6 3 5 10
    60 80 85
    Moscow
    10 0.1 1 10 5
    50 25 70
    Microsoft 2
    Madrid
    75 0.70 15 50 100
    90 49 10
    Dublin
    25 1.5 12 8 24
    80 45 30
    Google 3
    Berlin
    30 1.5 40 100 500
    35 10 42
    Dublin
    15 1 25 25 0
    48 25 35
    Sidney
    5000 2.5 10 10 3
    100 170 130
    10000 Italy 1000 0 0
    1000 Spain 100 60 0
    255000 Italy 20 0 555
    30000 Italy 250 300 780
    5000000 Germany 5000 300 10000
         */


        val inputFile = FileReader("inputFile")
        val input = InputReader(BufferedReader(inputFile))

        val (numberOfProviders, numberOfServices, numberOfCountries, numberOfProjects) = input.read4<Int,Int,Int,Int>(" ")
        val serviceName = input.readArray(" ", numberOfServices, { it })
        val countryName = input.readArray(" ", numberOfCountries, { it })


        val arrayCountryRegion = mutableListOf<Array<Region>>()

        for (i in 0 until numberOfProviders){
            val (provName, numberOfRegions) = input.read2<String,Int>(" ")
            val arrayOfRegion = mutableListOf<Region>()
            for(j in 0 until numberOfRegions){
                // nome regione
                val (regionName) = input.read1<String>()
                //println("costi")
                val dati = input.readArray<Double>(" ",numberOfServices + 2)
                val disponiblita = dati[0].toInt()
                val costoUnitario = dati[1]
                //println("numCount")
                val latenze = input.readArray<Int>(" ",numberOfCountries)
                arrayOfRegion.add(Region(regionName,
                        dati.sliceArray(2 until dati.size).map { it.toInt() }.toTypedArray(),
                        costoUnitario,
                        disponiblita,
                        latenze))
            }
            arrayCountryRegion.add(arrayOfRegion.toTypedArray())
        }

        val prj = mutableListOf<Project>()
        //println("!! " + numberOfProjects)
        for (i in 0 until numberOfProjects){
            val datiProgetto = input.readArray<String>(" ", numberOfServices+2, {it})
            val costoPenalità = datiProgetto[0].toInt()
            val nomeCountry = datiProgetto[1]
            prj.add(Project(i,costoPenalità,nomeCountry,datiProgetto.sliceArray(2 until datiProgetto.size).map { it.toInt() }.toTypedArray()))
        }

        inputFile.close()
        //region fine lettura dell'input
        //endregion
        return InputData(numberOfProviders,
                numberOfServices,
                numberOfCountries,
                numberOfProjects,
                serviceName,
                countryName,
                arrayCountryRegion.toTypedArray(),
                prj.toTypedArray()
        )
    }
}


//endregion