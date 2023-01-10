package ac.telkomuniversity.mobapp.tt43g1.kel12.sisteminformasikependudukan

import ac.telkomuniversity.mobapp.tt43g1.kel12.sisteminformasikependudukan.retrofit.CountryConsensusAPI
import ac.telkomuniversity.mobapp.tt43g1.kel12.sisteminformasikependudukan.retrofit.RetrofitHelper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.net.URLEncoder
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private lateinit var countryTextInput: TextInputEditText
    private lateinit var countryButton: Button
    private lateinit var consensusEmptyTextView: TextView
    private lateinit var countryConsensusWrapper: LinearLayoutCompat
    private lateinit var countryFlagImageView: ImageView
    private lateinit var countryValueTextView: TextView
    private lateinit var capitalValueTextView: TextView
    private lateinit var populationValueTextView: TextView
    private lateinit var popGrowthValueTextView: TextView
    private lateinit var urbanPopValueTextView: TextView
    private lateinit var urbanGrowthValueTextView: TextView
    private lateinit var unemploymentValueTextView: TextView

    private fun initComponentViews() {
        this.countryTextInput = findViewById(R.id.countryTextInput)
        this.countryButton = findViewById(R.id.countryButton)
        this.consensusEmptyTextView = findViewById(R.id.consensusEmptyTextView)
        this.countryConsensusWrapper = findViewById(R.id.countryConsensusWrapper)
        this.countryFlagImageView = findViewById(R.id.countryFlagImageView)
        this.countryValueTextView = findViewById(R.id.countryValueTextView)
        this.capitalValueTextView = findViewById(R.id.capitalValueTextView)
        this.populationValueTextView = findViewById(R.id.populationValueTextView)
        this.popGrowthValueTextView = findViewById(R.id.popGrowthValueTextView)
        this.urbanPopValueTextView = findViewById(R.id.urbanPopValueTextView)
        this.urbanGrowthValueTextView = findViewById(R.id.urbanGrowthValueTextView)
        this.unemploymentValueTextView = findViewById(R.id.unemploymentValueTextView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.initComponentViews()

        this.countryButton.setOnClickListener {
            val countryValueTextInput = this.countryTextInput.text.toString()

            if (countryValueTextInput == "") return@setOnClickListener

            val countryConsensusAPI = RetrofitHelper.getInstance()
                .create(CountryConsensusAPI::class.java)

            runBlocking {
                val result = async {
                    countryConsensusAPI
                        .getCountryConsensus(
                            "/v1/country?name=${
                                withContext(Dispatchers.IO) {
                                    URLEncoder.encode(countryValueTextInput, "utf-8")
                                }
                            }"
                        )
                }
                val resultData = result.await().body()
                if (resultData != null) {
                    val resultDataIsEmpty = resultData.isEmpty()

                    if(resultDataIsEmpty) {
                        countryConsensusWrapper.visibility = View.INVISIBLE
                        consensusEmptyTextView.visibility = View.VISIBLE
                    } else {
                        val consensusData = resultData[0]
                        countryValueTextView.text = consensusData.name.toString()
                        capitalValueTextView.text = consensusData.capital.toString()
                        populationValueTextView.text = consensusData.population.toString()
                        popGrowthValueTextView.text =
                            getString(
                                R.string.pop_growth_value,
                                consensusData.pop_growth.toString(),
                                "%"
                            )

                        urbanPopValueTextView.text =
                            consensusData.urban_population.toString()

                        urbanGrowthValueTextView.text =
                            getString(
                                R.string.urban_pop_growth_value,
                                consensusData.urban_population_growth.toString(),
                                "%"
                            )

                        unemploymentValueTextView.text =
                            getString(
                                R.string.unemployment_value,
                                consensusData.unemployment.toString(),
                                "%"
                            )

                        val executor = Executors.newSingleThreadExecutor()
                        val handler = Handler(Looper.getMainLooper())
                        var image: Bitmap? = null
                        executor.execute {
                            val imageURL = "https://countryflagsapi.com/png/${consensusData.iso2}"
                            try {
                                val `in` = java.net.URL(imageURL).openStream()
                                image = BitmapFactory.decodeStream(`in`)
                                handler.post {
                                    countryFlagImageView.setImageBitmap(image)
                                }
                                consensusEmptyTextView.visibility = View.INVISIBLE
                                countryConsensusWrapper.visibility = View.VISIBLE
                            }
                            catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }
        }
    }
}