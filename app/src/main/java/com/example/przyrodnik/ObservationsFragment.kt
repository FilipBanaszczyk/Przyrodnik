package com.example.przyrodnik

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ObservationsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ObservationsFragment() : Fragment(), JournalItemActivity.SelectableItems  {
    // TODO: Rename and change types of parameters
    lateinit var param1: LongArray
    lateinit var param2: String
    lateinit var controller:ApplicationController
    lateinit var adapter: ObservationsGroupAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getLongArray(ARG_PARAM1)!!
            param2 = it.getString(ARG_PARAM2)!!
        }
        Log.d("fragment on create", param1.size.toString())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Log.d("fragment on createview", param1.size.toString())
        val view = inflater.inflate(R.layout.fragment_observations, container, false)
        val rv = view.findViewById<RecyclerView>(R.id.rv)
        controller = activity?.applicationContext as ApplicationController

        val activity = activity as JournalItemActivity
        val result = ArrayList<Observation>()
        for(id in param1){
            result.add(controller.getObservationWithId(id)!!)
        }
        adapter = ObservationsGroupAdapter(result, param2, activity)
        rv.adapter = adapter
        rv.layoutManager = LinearLayoutManager(activity)
        return view
    }
    override fun notifyAdapter(state: Boolean) {
        adapter.turnSelectMode(state)

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ObservationsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: LongArray, param2: String): Fragment {
            Log.d("frag new instance", param1.size.toString())
            return ObservationsFragment().apply {
                arguments = Bundle().apply {
                    putLongArray(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
        }


    }


}