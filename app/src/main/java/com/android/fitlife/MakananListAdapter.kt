package com.android.fitlife

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.fitlife.databinding.MakananListBinding
import com.android.fitlife.roomDb.DataHarian

typealias OnClickFood = (DataHarian) -> Unit

class MakananListAdapter(private var listFood: List<DataHarian>, private val onClickFood: OnClickFood) :
    RecyclerView.Adapter<MakananListAdapter.ItemFoodViewHolder>() {

    inner class ItemFoodViewHolder(private val binding: MakananListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: DataHarian) {
            with(binding) {
                txtNamaMakanan.text = data.namaMakanan
                txtJumlahKalori.text = data.kalori.toString()
                txtJumlah.text = data.jumlah.toString()
                txtSatuan.text = data.satuan
                waktu.text = data.waktu

                itemView.setOnClickListener {
                    onClickFood(data)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemFoodViewHolder {
        val binding = MakananListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemFoodViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listFood.size
    }

    override fun onBindViewHolder(holder: ItemFoodViewHolder, position: Int) {
        holder.bind(listFood[position])
    }

    fun submitList(newList: List<DataHarian>) {
        listFood = newList
        notifyDataSetChanged()
    }
}