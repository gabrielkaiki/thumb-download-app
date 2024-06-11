package com.gabrielkaiki.utubetools.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.gabrielkaiki.utubetools.R
import com.gabrielkaiki.utubetools.model.ItemGrid

class AdapterGrid(context: Context, listItensTools: ArrayList<ItemGrid>) :
    ArrayAdapter<ItemGrid>(context, 0, listItensTools) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.adapter_grid, parent, false)
        }

        val itemGrid = getItem(position)!!
        val imagemTool = view!!.findViewById<ImageView>(R.id.imageViewAdapterGrid)
        val textTool = view!!.findViewById<TextView>(R.id.textToolAdapter)

        imagemTool.setImageResource(itemGrid.imagemTool!!)
        textTool.text = itemGrid.textTool
        return view
    }
}