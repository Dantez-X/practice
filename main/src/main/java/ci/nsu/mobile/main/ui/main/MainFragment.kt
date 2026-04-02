package ci.nsu.mobile.main.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ci.nsu.mobile.main.R
import android.util.Log

class MainFragment : Fragment() {

    private lateinit var viewModel: MainViewModel
    private lateinit var counterView: MyView
    private lateinit var listViewHistory: ListView
    private lateinit var historyAdapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {

            viewModel = ViewModelProvider(this).get(MainViewModel::class.java)


            counterView = view.findViewById(R.id.counterView)
            listViewHistory = view.findViewById(R.id.listViewHistory)


            historyAdapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                ArrayList()
            )
            listViewHistory.adapter = historyAdapter


            viewModel.uiState.observe(viewLifecycleOwner) { state ->
                counterView.count = state.count

                historyAdapter.clear()
                historyAdapter.addAll(state.history)
                historyAdapter.notifyDataSetChanged()
            }


            view.findViewById<Button>(R.id.buttonIncrement).setOnClickListener {
                viewModel.increment()
            }

            view.findViewById<Button>(R.id.buttonDecrement).setOnClickListener {
                viewModel.decrement()
            }

            view.findViewById<Button>(R.id.buttonReset).setOnClickListener {
                viewModel.reset()
            }

            view.findViewById<Button>(R.id.buttonClearHistory).setOnClickListener {
                viewModel.clearHistory()
                android.widget.Toast.makeText(
                    requireContext(),
                    "История очищена",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }

        } catch (e: Exception) {
            Log.e("MainFragment", "Error", e)
        }
    }
}
