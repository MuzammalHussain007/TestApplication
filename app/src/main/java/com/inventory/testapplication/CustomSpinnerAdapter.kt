import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.inventory.testapplication.R

class CustomSpinnerAdapter(context: Context, private val items: List<String>) : ArrayAdapter<String>(context, 0, items) {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    var isSpinnerOpens: Boolean = false
    // Get the number of items
    override fun getCount(): Int = items.size

    // Get the item at the specified position
    override fun getItem(position: Int): String = items[position]

    // Get the item ID (not used in this case)
    override fun getItemId(position: Int): Long = position.toLong()

    // View for the closed spinner
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = inflater.inflate(R.layout.spinner_item, parent, false)
        val text = view.findViewById<TextView>(R.id.spinnerText)
        val iconView = view.findViewById<ImageView>(R.id.spinnerIcon)

        val item = getItem(position)
        if (isSpinnerOpens) {
            iconView.setImageResource(R.drawable.ic_collapse)
        }else
        {
            iconView.setImageResource(R.drawable.ic_not_collapse)
        }

        text.text = item

        return view
    }

    // View for the dropdown items
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = inflater.inflate(R.layout.dropdown_item, parent, false)
        val text = view.findViewById<TextView>(R.id.dropdownText)

        val item = getItem(position)
        text.text = item



        return view
    }

    fun setSpinnerOpen(isOpen: Boolean) {
        isSpinnerOpens = isOpen
        notifyDataSetChanged() // Refresh the view to show updated icon
    }
}