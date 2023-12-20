package com.android.fitlife

import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.fitlife.data.DataMakanan
import com.android.fitlife.databinding.ActivityTambahMakananBinding
import com.android.fitlife.roomDb.DataHarian
import com.android.fitlife.roomDb.DataHarianDao
import com.android.fitlife.roomDb.DataHarianDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.util.Calendar
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class TambahMakananActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTambahMakananBinding
    private lateinit var foodAdapter: AddFromListAdapter

    private lateinit var executorService: ExecutorService
    private lateinit var dataHarians: DataHarianDao

    private val firestore = FirebaseFirestore.getInstance()
    private val roleCollectionRef = firestore.collection("data_makanan")
    private lateinit var auth: FirebaseAuth

    private val dataMakananLiveData: MutableLiveData<List<DataMakanan>>
            by lazy {
                MutableLiveData<List<DataMakanan>>()
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTambahMakananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        executorService = Executors.newSingleThreadExecutor()
        val db = DataHarianDatabase.getDatabase(this)
        dataHarians = db!!.dataHarianDao()

        getAllData()

        dataMakananLiveData.observe(this@TambahMakananActivity) { data ->
            foodAdapter.submitList(data)
        }

        auth = Firebase.auth
        val currentUser = auth.currentUser

        foodAdapter = AddFromListAdapter(emptyList()) { selectedFood ->

            val uid = currentUser?.uid.toString()
            val namaMakanan = selectedFood.namaMakanan
            val kalori = selectedFood.kalori
            val jumlah = selectedFood.jumlah
            val satuan = selectedFood.satuan
            val tanggal = getTodayDate()
            val waktu = getTodayTime()

            val dataHarian = DataHarian(
                uid = uid,
                namaMakanan = namaMakanan,
                kalori = kalori,
                jumlah = jumlah,
                satuan = satuan,
                waktu = waktu,
                tanggal = tanggal
            )

            insertDataHarian(dataHarian)

            val intent = Intent(this@TambahMakananActivity, MainActivity::class.java)
            startActivity(intent)
        }

        binding.rvMakanan.adapter = foodAdapter
        binding.rvMakanan.layoutManager = LinearLayoutManager(this@TambahMakananActivity)

        with(binding) {
            btnCustomMakanan.setOnClickListener {
                navigateToCustomMakananActivity()
            }
        }

    }

    private fun navigateToCustomMakananActivity() {
        val intent = Intent(this@TambahMakananActivity, CustomMakananActivity::class.java)
        startActivity(intent)
    }

    private fun getAllData() {
        roleCollectionRef.get()
            .addOnSuccessListener { result ->
                val dataList = mutableListOf<DataMakanan>()
                for (document in result) {
                    val data = DataMakanan(
                        document.getString("namaMakanan") ?: "",
                        document.getLong("kalori")?.toFloat() ?: 0.0f,
                        document.getLong("jumlah")?.toFloat() ?: 0.0f,
                        document.getString("satuan") ?: "",
                    )
                    dataList.add(data)
                    foodAdapter.submitList(dataList)
                }
            }
            .addOnFailureListener { exception ->
                Log.e(ContentValues.TAG, "Error getting documents: ", exception)
            }
    }

    private fun insertDataHarian(dataHarian: DataHarian) {
        executorService.execute {
            dataHarians.insert(dataHarian)
        }
    }

    fun getTodayDate(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1 // Ingat, bulan dimulai dari 0
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        return "$year-$month-$day"
    }

    fun getTodayTime(): String {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        return "$hour:$minute"
    }
}