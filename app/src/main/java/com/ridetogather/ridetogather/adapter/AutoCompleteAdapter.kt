package com.ridetogather.ridetogather.adapter

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.google.android.gms.tasks.Tasks
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.ridetogather.ridetogather.R
import com.ridetogather.ridetogather.model.PlaceModel
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class AutoCompleteAdapter (context: Context, val resource: Int, val mPlacesClient: PlacesClient) :ArrayAdapter<PlaceModel>(context,resource),Filterable {

    private var mContext : Context = context
    private var resultList = arrayListOf<PlaceModel>()

    override fun getCount(): Int {
        return when {
            resultList.isNullOrEmpty() -> 0
            else -> resultList.size
        }
    }

    override fun getItem(position: Int): PlaceModel? {
        return when {
            resultList.isNullOrEmpty() -> null
            else -> resultList[position]
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val viewHolder: ViewHolder
        if (view == null) {
            viewHolder = ViewHolder()
            view = LayoutInflater.from(context).inflate(resource, parent, false)
            viewHolder.description = view.findViewById(R.id.searchFullText) as TextView
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }
        bindView(viewHolder, resultList, position)
        return view!!
    }

    private fun bindView(viewHolder: ViewHolder, place: ArrayList<PlaceModel>, position: Int) {
        if (!place.isNullOrEmpty()) {
            viewHolder.description?.text = place[position].toString()
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                if (results != null && results.count > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        mContext.mainExecutor.execute(object: Runnable{
                            override fun run() {
                                notifyDataSetChanged()
                            }

                        })
                    }
                } else {
                    notifyDataSetInvalidated()
                }
            }

            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                if (constraint != null) {
                    resultList.clear()
                    val address = getAutocomplete(mPlacesClient, constraint.toString())
                    address?.let {
                        for (i in address.indices) {
                            val item = address[i]
                            resultList.add(PlaceModel(item.placeId, item.getFullText(StyleSpan(
                                Typeface.BOLD)).toString()))
                        }
                    }
                    filterResults.values = resultList
                    filterResults.count = resultList.size

                }
                return filterResults
            }
        }
    }

    internal class ViewHolder {
        var description: TextView? = null
    }
    fun getAutocomplete(mPlacesClient: PlacesClient, constraint: CharSequence): List<AutocompletePrediction> {
        var list = listOf<AutocompletePrediction>()
        val token = AutocompleteSessionToken.newInstance()
        val request = FindAutocompletePredictionsRequest.builder().setTypeFilter(TypeFilter.ADDRESS).setSessionToken(token).setQuery(constraint.toString()).build()
        val prediction = mPlacesClient.findAutocompletePredictions(request)
        try {
            Tasks.await(prediction,1000, TimeUnit.SECONDS)
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: TimeoutException) {
            e.printStackTrace()
        }

        if (prediction.isSuccessful) {
            val findAutocompletePredictionsResponse = prediction.result
            findAutocompletePredictionsResponse?.let {
                list = findAutocompletePredictionsResponse.autocompletePredictions
            }
            return list
        }
        return list
    }
}
