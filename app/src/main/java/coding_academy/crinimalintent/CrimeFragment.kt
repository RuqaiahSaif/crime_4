package coding_academy.crinimalintent
import android.app.DatePickerDialog
import android.app.ProgressDialog.show
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import java.util.*
private const val ARG_CRIME_ID = "crime_id"
private const val DIALOG_DATE = "DialogDate"
private const val DIALOG_TIME  = "Dialogtime"
private const val REQUEST_DATE = 0
private const val REQUEST_TIME = 1

 class CrimeFragment: Fragment() ,DataPicketFragment.Callbacks,TimePickerFragment.Callbacks{
   private lateinit var crime: Crime
   private lateinit var titleText: TextView
    private lateinit var dateButton: Button
     private lateinit var timeButton: Button
  private  lateinit var solvedCheckBox: CheckBox


    private val crimeListViewModel: CrimeListViewModel by lazy {
        ViewModelProviders.of(this).get(CrimeListViewModel::class.java)
    }
    private val crimeDetailViewModel: CrimeDetailViewModel by lazy {
        ViewModelProviders.of(this).get(CrimeDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crime=Crime()
        val crimeId: UUID = arguments?.getSerializable(ARG_CRIME_ID) as UUID;
       // Toast.makeText(context, crimeId.toString(), Toast.LENGTH_SHORT).show()
        crimeDetailViewModel.loadCrime(crimeId)



    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
       // return super.onCreateView(inflater, container, savedInstanceState)
        val view=inflater.inflate(R.layout.fragment_crime,container,false)
        titleText=view.findViewById(R.id.crime_title)as EditText
        dateButton = view.findViewById(R.id.crime_date) as Button
        timeButton = view.findViewById(R.id.crime_time) as Button
        solvedCheckBox = view.findViewById(R.id.crime_solved) as CheckBox
 /*       dateButton.apply {
            text = crime.date.toString()
            isEnabled = false }
            */




        return view
    }
    private fun updateUI() {
        titleText.setText(crime.title)
        dateButton.text = crime.date.toString()
        solvedCheckBox.isChecked = crime.isSolved
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        crimeDetailViewModel.crimeLiveData.observe(
            viewLifecycleOwner,
            Observer { crime ->
                crime?.let {
                    this.crime = crime
                    updateUI()
                }
            })


    }
    override fun onStart() {
        super.onStart()
        val titleWatcher = object : TextWatcher {

            override fun beforeTextChanged(
                sequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
// This space intentionally left blank
            }


            override fun onTextChanged(
                sequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                crime.title = sequence.toString()
            }

            override fun afterTextChanged(sequence: Editable?) {
// This one too
            }
        }
        titleText.addTextChangedListener(titleWatcher)

        solvedCheckBox.apply {
            setOnCheckedChangeListener { _, isChecked ->
                crime.isSolved = isChecked
            }

        }
        dateButton.setOnClickListener {
            DataPicketFragment.newInstance(crime.date).apply {
                setTargetFragment(this@CrimeFragment, REQUEST_DATE)
                show(this@CrimeFragment.requireFragmentManager(), DIALOG_DATE)
            }
        }
        timeButton.setOnClickListener{

            TimePickerFragment.newInstance(crime.date).apply{
                setTargetFragment(this@CrimeFragment, REQUEST_TIME)
                show(this@CrimeFragment.requireFragmentManager(), DIALOG_TIME)
                            }
        }
   }
    override fun onStop() {
        super.onStop()

        crimeDetailViewModel.saveCrime(crime)
    }
    companion object {
        fun newInstance(crimeId: UUID): CrimeFragment {
            val args = Bundle().apply {
                putSerializable(ARG_CRIME_ID, crimeId)
            }
            return CrimeFragment().apply {
                arguments = args
            }
        }

    }

     override fun onDateSelected(date: Date) {
         crime.date = date
         updateUI()

     }

     override fun onTimeSelected(time: Date) {
         crime.date = time
         updateUI()
     }


 }


