package com.vimlesh.formvalidationsamplewithkotlinflow

import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Patterns
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.vimlesh.formvalidationsamplewithkotlinflow.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import kotlin.system.measureTimeMillis

class MainActivity : AppCompatActivity() {
    private lateinit var mainBinding: ActivityMainBinding
    private val defaultButtonTintColor = "#1B1717"
    private val onFormValidButtonTintColor = "#4F774F"
    private var errorMessage:String?=null
    private val email= MutableStateFlow("")
    private val password= MutableStateFlow("")
    private val confirmPass= MutableStateFlow("")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding= ActivityMainBinding.inflate(layoutInflater)
        val view=mainBinding.root
        setContentView(view)
        with(mainBinding){
            txtEmail.doOnTextChanged { text, start, before, count ->
                email.value=text.toString()
            }
            txtPassword.doOnTextChanged { text, start, before, count ->
                password.value=text.toString()
            }
            txtPasswordAgain.doOnTextChanged { text, start, before, count ->
                confirmPass.value=text.toString()
            }
        }
        lifecycleScope.launch {
            isFormValid.collect {
                mainBinding.btnLogin.apply {
                    backgroundTintList= ColorStateList.valueOf(
                            Color.parseColor(
                                    if(it)onFormValidButtonTintColor else
                                        defaultButtonTintColor
                            )
                    )
                    isClickable=it

                }
            }
        }
        val snackbar=Snackbar.make(mainBinding.root,"login successfully",Snackbar.LENGTH_LONG)
        mainBinding.btnLogin.setOnClickListener { snackbar.show() }
    }
    private val isFormValid= combine(email,password,confirmPass){
email,password,confirmpass->
        mainBinding.txtErrorMessage.text=""
       /*val reg ="[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"*/
        val emailIsValid= Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isPassvalid=password.length>5
        val isConfirmPassValid= password ==confirmpass
        errorMessage=when{
            emailIsValid.not()->"email is not valid"
            isPassvalid.not()->"password not valid"
            isConfirmPassValid.not()->"both password are not same"
            else -> null
        }
        errorMessage?.let { mainBinding.txtErrorMessage.text=it }
        emailIsValid and isPassvalid and isConfirmPassValid
    }
}

