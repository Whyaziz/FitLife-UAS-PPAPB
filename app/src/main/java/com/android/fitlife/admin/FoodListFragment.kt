package com.android.fitlife.admin

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.fitlife.R
import com.android.fitlife.data.DataMakanan
import com.android.fitlife.databinding.FragmentFoodListBinding
import com.google.firebase.firestore.FirebaseFirestore


class FoodListFragment : Fragment() {

    private lateinit var binding: FragmentFoodListBinding
    private lateinit var foodAdapter: FoodListAdapter

    private val firestore = FirebaseFirestore.getInstance()
    private val roleCollectionRef  = firestore.collection("data_makanan")

    private val dataMakananLiveData : MutableLiveData<List<DataMakanan>>
            by lazy {
                MutableLiveData<List<DataMakanan>>()
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
        binding = FragmentFoodListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {

            getAllData()

            // Observe changes in dataMakananLiveData
            dataMakananLiveData.observe(viewLifecycleOwner) { data ->
                foodAdapter.submitList(data)
            }

            foodAdapter = FoodListAdapter(emptyList()) { selectedFood ->

            }

            rvFoodList.adapter = foodAdapter
            rvFoodList.layoutManager = LinearLayoutManager(requireContext())

            btnAddFood.setOnClickListener {
                val intent = Intent(activity, AddFoodActivity::class.java)
                startActivity(intent)

                requireActivity().finish()
            }
        }

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
                // Handle failure
                Log.e(TAG, "Error getting documents: ", exception)
            }
    }
}