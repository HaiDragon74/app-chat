package com.example.appchat.frament

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appchat.R
import com.example.appchat.activity.ChatActivity
import com.example.appchat.adapter.FramentAdapter
import com.example.appchat.adapter.MessageAdapter
import com.example.appchat.cosodulieusql.MessageLastSqlHelper
import com.example.appchat.cosodulieusql.MessageSqlHelper
import com.example.appchat.cosodulieusql.UserSqlHelper
import com.example.appchat.databinding.FragmentMessageBinding
import com.example.appchat.dataclass.Message
import com.example.appchat.dataclass.User
import com.example.appchat.dataclass.dataMessage
import com.example.appchat.`interface`.ViewModelUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceIdReceiver

class MessageFragment : Fragment() {
    private lateinit var binding: FragmentMessageBinding
    private lateinit var fbUser: FirebaseUser
    private lateinit var messageLastSqlHelper: MessageLastSqlHelper
    private lateinit var framentAdapter: FramentAdapter
    private var mutableListMessageLast: MutableList<Message> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fbUser=FirebaseAuth.getInstance().currentUser!!
        framentAdapter= FramentAdapter()
        messageLastSqlHelper= MessageLastSqlHelper(requireContext())
        binding=FragmentMessageBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rclMessageFrm.layoutManager=LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        binding.rclMessageFrm.adapter=framentAdapter
        clickRcl()

    }
    override fun onResume() {
        super.onResume()
        val listMessageLast=messageLastSqlHelper.readListMessageLastSql()
        mutableListMessageLast.clear()
        listMessageLast.forEach { message ->
            mutableListMessageLast.add(0, message)
            framentAdapter.getMessage(requireContext(),mutableListMessageLast)
            framentAdapter.notifyDataSetChanged()
            Log.d("HAHAHAHAHHHAA",mutableListMessageLast.toString())
        }
    }

    private fun clickRcl() {
        framentAdapter.onClick={
            val intent=Intent(requireContext(),ChatActivity::class.java)
            intent.putExtra("ID",it.idReceiver)
            intent.putExtra("NAME",it.nameReceiver)
            intent.putExtra("IMG",it.imgReceiver)
            startActivity(intent)

        }
    }
}