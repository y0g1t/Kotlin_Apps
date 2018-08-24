package assignment.briskon.com.briskonassignment.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import assignment.briskon.com.briskonassignment.R
import assignment.briskon.com.briskonassignment.model.StateFeed
import kotlinx.android.synthetic.main.state_item_list.view.*

class StateAdapter(val items : ArrayList<StateFeed>, val context: Context) : RecyclerView.Adapter<ViewHolder>() {

    // Gets the number of state date in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.state_item_list, parent, false))
    }

    // Binds each state date in the ArrayList to a view
    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.stateName?.text = items.get(position).stateName
        holder?.capital?.text = items.get(position).capitalOfState
    }
}

class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val stateName = view.state_name
    val capital = view.capilat
}