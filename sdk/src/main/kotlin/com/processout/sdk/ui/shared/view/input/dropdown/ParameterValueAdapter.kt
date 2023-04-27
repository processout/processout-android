package com.processout.sdk.ui.shared.view.input.dropdown

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.LayoutRes
import com.processout.sdk.api.model.response.PONativeAlternativePaymentMethodParameter.ParameterValue
import com.processout.sdk.ui.nativeapm.applyStyle
import com.processout.sdk.ui.shared.style.POTextStyle

internal class ParameterValueAdapter(
    context: Context,
    @LayoutRes
    private val itemLayoutRes: Int,
    private val values: List<ParameterValue>,
    private val textStyle: POTextStyle? = null
) : ArrayAdapter<ParameterValue>(
    context, itemLayoutRes, values
) {

    override fun getCount(): Int {
        return values.size
    }

    override fun getItem(position: Int): ParameterValue? {
        return values.getOrNull(position)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: TextView = convertView as TextView?
            ?: LayoutInflater.from(context).inflate(
                itemLayoutRes, parent, false
            ) as TextView
        view.text = getItem(position)?.displayName
        textStyle?.let { view.applyStyle(it) }
        return view
    }
}
