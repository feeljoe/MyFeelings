package valenzuela.joel.myfeelings

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import utilities.CustomBarDrawable
import utilities.CustomCircleDrawable
import utilities.Emociones
import utilities.JSONFile

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    var jsonFile: JSONFile? = null
    var veryHappy = 0.0F
    var happy = 0.0F
    var neutral = 0.0F
    var sad = 0.0F
    var verySad = 0.0F
    var data:Boolean =false
    var lista = ArrayList<Emociones>()

    fun parseJson (jsonArray: JSONArray): ArrayList<Emociones>{
        var lista = ArrayList<Emociones>()
        for(i in 0..jsonArray.length()){
            try{
                val nombre = jsonArray.getJSONObject(i).getString("nombre")
                val porcentaje = jsonArray.getJSONObject(i).getDouble("porcentaje").toFloat()
                val color = jsonArray.getJSONObject(i).getInt("color")
                val total = jsonArray.getJSONObject(i).getDouble("total").toFloat()
                var emocion = Emociones(nombre, porcentaje, color, total)
                lista.add(emocion)
            }catch(e:JSONException){
                e.printStackTrace()
            }
        }
        return lista
    }

    fun fetchingData(){
        try{
            var json: String = jsonFile?.getData(this)?:""
            if(json!= ""){
                this.data = true
                var jsonArray = JSONArray(json)

                this.lista = parseJson(jsonArray)

                for(i in lista!!){
                    when(i.nombre){
                        "Muy Feliz" -> veryHappy = i.total
                        "Feliz" -> happy = i.total
                        "Neutral" -> neutral = i.total
                        "Triste" -> sad = i.total
                        "Muy Triste" -> verySad = i.total
                    }
                }
            }else{
                this.data = false
            }
        }catch(e: JSONException){
            e.printStackTrace()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val guardarButton: Button = findViewById(R.id.guardarButton)
        val veryHappyButton: ImageButton = findViewById(R.id.veryHappyButton)
        val happyButton: ImageButton = findViewById(R.id.happyButton)
        val neutralButton: ImageButton = findViewById(R.id.neutralButton)
        val sadButton: ImageButton = findViewById(R.id.sadButton)
        val verySadButton: ImageButton = findViewById(R.id.verySadButton)
        val graph:ConstraintLayout = this.findViewById(R.id.graph)
        val icon: ImageView = findViewById(R.id.icon)
        val graphVeryHappy:View = findViewById(R.id.graphVeryHappy)
        val graphHappy:View = findViewById(R.id.graphHappy)
        val graphNeutral:View = findViewById(R.id.graphNeutral)
        val graphVerySad:View = findViewById(R.id.graphVerySad)
        val graphSad:View = findViewById(R.id.graphSad)

        jsonFile = JSONFile()

        fetchingData()

        fun iconoMayoria(){
            if(happy>veryHappy && happy>neutral && happy>sad && happy>verySad){
                icon.setImageResource(R.drawable.ic_happy)
            }
            if(veryHappy>happy && veryHappy>neutral && veryHappy>sad && veryHappy>verySad){
                icon.setImageResource(R.drawable.ic_veryhappy)
            }
            if(neutral>veryHappy && neutral>happy && neutral>sad && neutral>verySad){
                icon.setImageResource(R.drawable.ic_neutral)
            }
            if(sad>happy && sad>neutral && sad>veryHappy && sad>verySad){
                icon.setImageResource(R.drawable.ic_sad)
            }
            if(verySad>happy && verySad>neutral && verySad>sad  && veryHappy<verySad){
                icon.setImageResource(R.drawable.ic_verysad)
            }
        }

        fun actualizarGrafica(){
            val total = veryHappy + happy + neutral + sad + verySad

            var pVH:Float = (veryHappy * 100 / total).toFloat()
            var pH:Float = (happy * 100 / total).toFloat()
            var pN:Float = (neutral * 100 / total).toFloat()
            var pS:Float = (sad * 100 / total).toFloat()
            var pVS:Float = (verySad * 100 / total).toFloat()

            Log.d("porcentajes", "Very Happy " +pVH)
            Log.d("porcentajes", "Happy " +pH)
            Log.d("porcentajes", "Neutral " +pN)
            Log.d("porcentajes", "Sad " +pS)
            Log.d("porcentajes", "Very Sad " +pVS)

            lista?.clear()
            lista?.add(Emociones("Muy Feliz", pVH, R.color.mustard, veryHappy))
            lista?.add(Emociones("Feliz", pH, R.color.orange, happy))
            lista?.add(Emociones("Neutral", pN, R.color.greenie, neutral))
            lista?.add(Emociones("Triste", pS, R.color.blue, sad))
            lista?.add(Emociones("Muy Triste", pVS, R.color.deepBlue, verySad))

            val fondo = CustomCircleDrawable(this, this.lista!!)

            graphVeryHappy.background = CustomBarDrawable(this, Emociones("Muy Feliz", pVH, R.color.mustard, veryHappy))
            graphHappy.background = CustomBarDrawable(this, Emociones("Feliz", pH, R.color.orange, happy))
            graphNeutral.background = CustomBarDrawable(this, Emociones("Neutral", pN, R.color.greenie, neutral))
            graphSad.background = CustomBarDrawable(this, Emociones("Triste", pS, R.color.blue, sad))
            graphVerySad.background = CustomBarDrawable(this, Emociones("Muy Triste", pVS, R.color.deepBlue, verySad))

            graph.background = fondo
        }



        fun guardar(){
            var jsonArray = JSONArray()
            var o : Int = 0
            for(i in lista!!){
                Log.d("Objetos", i.toString())
                var j: JSONObject = JSONObject()
                j.put("nombre", i.nombre)
                j.put("porcentaje", i.porcentaje)
                j.put("color", i.color)
                j.put("total", i.total)

                jsonArray.put(o,j)
                o++
            }
            jsonFile?.saveData(this, jsonArray.toString())

            Toast.makeText(this, "Datos guardados", Toast.LENGTH_SHORT).show()
        }

        if(!data){
            var emociones = ArrayList<Emociones>()
            val fondo = CustomCircleDrawable(this, emociones)
            graph.background = fondo

            graphVeryHappy.background = CustomBarDrawable(this, Emociones("Muy Feliz", 0.0F, R.color.mustard, veryHappy))
            graphHappy.background = CustomBarDrawable(this, Emociones("Feliz", 0.0F, R.color.orange, happy))
            graphNeutral.background = CustomBarDrawable(this, Emociones("Neutral", 0.0F, R.color.greenie, neutral))
            graphSad.background = CustomBarDrawable(this, Emociones("Triste", 0.0F, R.color.blue, sad))
            graphVerySad.background = CustomBarDrawable(this, Emociones("Muy Triste", 0.0F, R.color.deepBlue, verySad))

        }else{
            actualizarGrafica()
            iconoMayoria()
        }

        guardarButton.setOnClickListener{
            guardar()
        }
        veryHappyButton.setOnClickListener{
            veryHappy++
            iconoMayoria()
            actualizarGrafica()
        }
        happyButton.setOnClickListener{
            happy++
            iconoMayoria()
            actualizarGrafica()
        }
        neutralButton.setOnClickListener{
            neutral++
            iconoMayoria()
            actualizarGrafica()
        }
        sadButton.setOnClickListener{
            sad++
            iconoMayoria()
            actualizarGrafica()
        }
        verySadButton.setOnClickListener{
            verySad++
            iconoMayoria()
            actualizarGrafica()
        }
    }

}