package vn.hexagon.vietnhat.binding

import android.content.Context
import androidx.databinding.DataBindingComponent

/**
 * A Data Binding Component implementation for fragments.
 */
class FragmentDataBindingComponent(context: Context) : DataBindingComponent {
    private val adapter = FragmentBindingAdapters(context)

    override fun getFragmentBindingAdapters() = adapter
}
