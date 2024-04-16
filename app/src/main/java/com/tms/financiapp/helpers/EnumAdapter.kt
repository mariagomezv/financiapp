package com.tms.financiapp.helpers

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class EnumAdapter<T : Enum<T>>(context: Context, private val enumClass: Class<T>)
    : ArrayAdapter<T>(context, android.R.layout.simple_spinner_item, enumClass.enumConstants) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        view.findViewById<TextView>(android.R.id.text1).text = enumClass.getEnumConstants()[position].toString()
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        view.findViewById<TextView>(android.R.id.text1).text = enumClass.getEnumConstants()[position].toString()
        return view
    }

}