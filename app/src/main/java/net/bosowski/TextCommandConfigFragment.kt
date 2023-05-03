package net.bosowski

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.bosowski.databinding.FragmentTextCommandConfigBinding

class TextCommandConfigFragment : Fragment() {

    private var _binding: FragmentTextCommandConfigBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val textCommandConfigViewModel =
            ViewModelProvider(this)[TextCommandConfigViewModel::class.java]
        _binding = FragmentTextCommandConfigBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}