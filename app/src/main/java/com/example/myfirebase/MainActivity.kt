package my.edu.tarc.demofirebase

import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.myfirebase.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private val db = FirebaseDatabase.getInstance()
    private val users = db.getReference("user")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonSave.setOnClickListener {

            val _name = editTextName.text.toString()
            val _phone = editTextPhone.text.toString()

            if(TextUtils.isEmpty(_name)){
                editTextName.error = getString(R.string.required)
                return@setOnClickListener
            }

            if(TextUtils.isEmpty(_phone)){
                editTextPhone.error = getString(R.string.required)
                return@setOnClickListener
            }

            val user = User(name = _name, phone = _phone)

            //Using phone as a key
            users.child(_phone).setValue(user)

            editTextName.text.clear()
            editTextPhone.text.clear()
        }

        //This will be called each time that data changes
        users.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                //This method is called once with the initial value and again
                //whenever data at this location is updated

                var output = ""

                for(i: DataSnapshot in dataSnapshot.children.iterator()) {
                    output = output.plus(String.format("Phone :%s\n",  i.key))
                    output = output.plus(String.format("Name  :%s\n",  i.child("name").getValue()))
                    val date = i.child("created_at").getValue()

                    output = output.plus(String.format("Created :%s\n\n",
                        SimpleDateFormat("dd-MM-yyyy HH:MM:SS", Locale.getDefault()).format(date)))
                }
                textViewContact.text = output
            }

            override fun onCancelled(error: DatabaseError) {
                //Failed to read data
                textViewContact.text = String.format("Failed to read data. Error: %s", error.toException())
            }
        })
    }

}
