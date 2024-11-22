package com.pedrodev.tabletrack

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TableAdapter(
    private val tables: List<Table>
) : RecyclerView.Adapter<TableAdapter.TableViewHolder>() {

    class TableViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tableNumber: TextView = view.findViewById(R.id.table_number)
        val tableStatus: TextView = view.findViewById(R.id.table_status)
        val optionsMenu: ImageButton = view.findViewById(R.id.table_options)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tables, parent, false)
        return TableViewHolder(view)
    }

    override fun onBindViewHolder(holder: TableViewHolder, position: Int) {
        val table = tables[position]
        holder.tableNumber.text = "Mesa ${table.number}"
        holder.tableStatus.text = if (table.isAvailable) "Disponible" else "Ocupada"
    }

    override fun getItemCount(): Int = tables.size
}