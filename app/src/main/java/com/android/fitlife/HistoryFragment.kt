package com.android.fitlife

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.fitlife.databinding.FragmentHistoryBinding
import com.android.fitlife.roomDb.DataHarian
import com.android.fitlife.roomDb.DataHarianDao
import com.android.fitlife.roomDb.DataHarianDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class HistoryFragment : Fragment() {

    private lateinit var binding: FragmentHistoryBinding
    private lateinit var executorService: ExecutorService
    private lateinit var dataHarians: DataHarianDao
    private lateinit var foodAdapter: MakananListAdapter

    private lateinit var auth: FirebaseAuth

    private val dataMakananLiveData : MutableLiveData<List<DataHarian>>
            by lazy {
                MutableLiveData<List<DataHarian>>()
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        executorService = Executors.newSingleThreadExecutor()
        val db = DataHarianDatabase.getDatabase(requireContext())
        dataHarians = db!!.dataHarianDao()

        getAllData()

        foodAdapter = MakananListAdapter(emptyList()) { selectedFood ->

        }

        dataMakananLiveData.observe(viewLifecycleOwner) { data ->
            foodAdapter.submitList(data)
        }

        binding.rvMakanan.adapter = foodAdapter
        binding.rvMakanan.layoutManager = LinearLayoutManager(requireContext())

        with(binding) {

            btnTambahMakanan.setOnClickListener {
                navigateToTambahMakananFragment()
            }

        }
    }

    private fun navigateToTambahMakananFragment() {
        val intent = Intent(requireContext(), TambahMakananActivity::class.java)
        startActivity(intent)
    }

    private fun getAllData() {
        dataHarians.allDataHarian.observe(viewLifecycleOwner) { data ->

            auth = Firebase.auth
            val currentUserUid = auth.currentUser?.uid.toString()

            val dataList = mutableListOf<DataHarian>()
            for (document in data) {

                if (currentUserUid == document.uid) {
                    val dataHarian = DataHarian(
                        uid = document.uid,
                        namaMakanan = document.namaMakanan,
                        kalori = document.kalori,
                        jumlah = document.jumlah,
                        satuan = document.satuan,
                        tanggal = document.tanggal,
                        waktu = document.waktu
                    )
                    dataList.add(dataHarian)
                }
            }

            Log.d("dataList", dataList.toString())
            foodAdapter.submitList(dataList)
        }
    }

}