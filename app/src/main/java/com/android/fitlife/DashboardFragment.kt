package com.android.fitlife

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import com.android.fitlife.data.DataHarian
import com.android.fitlife.databinding.FragmentDashboardBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date


class DashboardFragment : Fragment() {

    private lateinit var binding: FragmentDashboardBinding
    private val firestore = FirebaseFirestore.getInstance()
    var updateId = ""
    private val dataHarianLiveData : MutableLiveData<List<DataHarian>>
            by lazy {
                MutableLiveData<List<DataHarian>>()
            }

    private val dataHarianCollectionRef  = firestore.collection("data_harian")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashboardBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        getAllDataHarian()

    }

    private fun getAllDataHarian() {
        dataHarianCollectionRef.get().addOnSuccessListener { snapshot ->
            val dataHarians = snapshot.documents.map { document ->
                val kalori = document.getLong("kalori")?.toInt() ?: 0
                val jumlah = document.getLong("jumlah")?.toInt() ?: 0
                val timestamp = document.getTimestamp("waktu")?.toDate() ?: Date()

                DataHarian(
                    document.id,
                    document.getString("nama") ?: "",
                    kalori,
                    jumlah,
                    timestamp,
                )
            }
            dataHarianLiveData.postValue(dataHarians)
            Log.d("DataHarian", "Retrieved data: $dataHarians")
        }.addOnFailureListener { exception ->
            Log.e("DataHarian", "Error getting data: $exception")
        }
    }
}