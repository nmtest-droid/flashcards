package com.example.flashcards

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flashcards.databinding.FragmentDecksBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class DecksFragment : Fragment() {
    private var _binding: FragmentDecksBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: DecksViewModel
    private lateinit var adapter: DeckAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDecksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[DecksViewModel::class.java]

        adapter = DeckAdapter(
            onItemClick = { deck ->
                findNavController().navigate(DecksFragmentDirections.actionDecksFragmentToDeckDetailFragment(deck.id))
            },
            onDeleteClick = { deck ->
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Удалить колоду")
                    .setMessage("Удалить '${deck.title}'?")
                    .setPositiveButton("Удалить") { _, _ -> viewModel.deleteDeck(deck.id) }
                    .setNegativeButton("Отмена", null)
                    .show()
            }
        )
        binding.recyclerDecks.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerDecks.adapter = adapter

        viewModel.decks.observe(viewLifecycleOwner) { adapter.submitList(it) }
        binding.fabAddDeck.setOnClickListener {
            AddEditDeckDialog.newInstance(null) { title -> viewModel.addDeck(title) }
                .show(childFragmentManager, "AddEditDeckDialog")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}