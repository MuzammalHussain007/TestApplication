import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    // ViewBinding reference
    private var _binding: VB? = null
    protected val binding get() = _binding!!

    // Abstract method for initializing ViewBinding
    abstract fun getViewBinding(): VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = getViewBinding()
        setContentView(binding.root) // Set the view from ViewBinding
        setupViews() // Initialize views or set listeners here
    }

    // Method to set up views, override this in derived classes
    open fun setupViews() {}

    override fun onDestroy() {
        super.onDestroy()
        _binding = null // Avoid memory leaks
    }
}
