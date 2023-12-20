package com.android.fitlife

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.android.fitlife.databinding.ActivityCustomMakananBinding
import com.android.fitlife.roomDb.DataHarian
import com.android.fitlife.roomDb.DataHarianDao
import com.android.fitlife.roomDb.DataHarianDatabase
import com.github.ihermandev.formatwatcher.FormatWatcher
import com.google.firebase.Timestamp
import java.sql.Date
import java.sql.Time
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CustomMakananActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    private lateinit var binding: ActivityCustomMakananBinding
    private lateinit var executorService: ExecutorService
    private lateinit var dataHarians: DataHarianDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomMakananBinding.inflate(layoutInflater)
        setContentView(binding.root)

        executorService = Executors.newSingleThreadExecutor()
        val db = DataHarianDatabase.getDatabase(this)
        dataHarians = db!!.dataHarianDao()

        with(binding) {

            edtTime.setOnClickListener {
                val timePicker = TimePicker()
                timePicker.show(supportFragmentManager, "date picker")
            }

            btnAddFood.setOnClickListener {
                val namaMakanan = edtNamaMakanan.text.toString()
                val kalori = edtKaloriMakanan.text.toString()
                val jumlah = edtJumlahMakanan.text.toString()
                val satuan = edtSatuanMakanan.text.toString()
                val tanggal = getTodayDate()
                val waktu = edtTime.text.toString()

                val timestamp = convertDateTimeToTimestamp(tanggal, waktu)

                if (namaMakanan.isEmpty() || kalori.isEmpty() || jumlah.isEmpty() || satuan.isEmpty() || waktu.isEmpty()) {
                    Toast.makeText(this@CustomMakananActivity, "Please fill all the fields", Toast.LENGTH_SHORT).show()
                } else {
                    insertDataHarian(DataHarian(
                        namaMakanan = namaMakanan,
                        kalori = kalori.toFloat(),
                        jumlah = jumlah.toFloat(),
                        satuan = satuan,
                        tanggal = tanggal,
                        waktu = waktu,
                    ))
                    Toast.makeText(this@CustomMakananActivity, "Data inserted $namaMakanan $kalori $jumlah $satuan $waktu", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    override fun onDateSet(
        view: android.widget.DatePicker?,
        year: Int,
        month: Int,
        dayOfMonth: Int
    ) {
        val selectedDAte = "$dayOfMonth/${month+1}/$year"
        Toast.makeText(this@CustomMakananActivity, selectedDAte, Toast.LENGTH_SHORT).show()
    }

    override fun onTimeSet(view: android.widget.TimePicker?, hourOfDay: Int, minute: Int) {
        val selectedTime = "$hourOfDay:$minute"
        binding.edtTime.setText(selectedTime)
    }

    private fun insertDataHarian(dataHarian: DataHarian) {
        executorService.execute {
            dataHarians.insert(dataHarian)
        }
    }

    fun convertDateTimeToTimestamp(dateString: String, timeString: String): Timestamp {

        val dateTimeString = "$dateString $timeString"
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")

        val date = dateFormat.parse(dateTimeString)

        return Timestamp(date!!)
    }

    fun getTodayDate(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1 // Ingat, bulan dimulai dari 0
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        return "$year-$month-$day"
    }
}