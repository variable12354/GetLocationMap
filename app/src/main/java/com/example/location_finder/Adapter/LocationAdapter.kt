package com.example.location_finder.Adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.location_finder.Model.Locations
import com.example.location_finder.databinding.ListRawBinding

class LocationAdapter(val context:Context) :RecyclerView.Adapter<LocationAdapter.ViewHolder>() {
    private var proList:List<Locations> = arrayListOf()
    private var listener: ((postition:Int) -> Unit)? = null
    private var editlistener: ((postition:Int,name:String,address:String,lati:Double,long:Double) -> Unit)? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list:List<Locations>){
        this.proList = list
        notifyDataSetChanged()
    }

    fun setListener(listener: ((id:Int) -> Unit)?) {
        this.listener = listener
    }

    fun onEdit(editlistner:((id:Int,name:String,address:String,lati:Double,long:Double) -> Unit)?){
        this.editlistener = editlistner
    }

    inner class ViewHolder(val binding: ListRawBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ListRawBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int = proList?.size!!

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder){
            with(proList!![position]){
                binding.name.text = this.name
                binding.address.text = this.address

                binding.delete.setOnClickListener{
                    listener?.invoke(this.id!!)
                    notifyDataSetChanged()
                    Log.e("TAG", "onBindViewHolder:delete ", )
                }

                binding.cardViewTop.setOnClickListener {
                    editlistener?.invoke(this.id!!,this.name,this.address,this.lati,this.longitude)
                }

            }
        }
    }

}