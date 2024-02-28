package com.example.chatapplictionlikewhastapp.featureHome.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatapplictionlikewhastapp.featureHome.pojo.ContactsUserinfo
import com.example.chatapplictionlikewhastapp.featureHome.pojo.RecentChatUserDataClass
import com.example.chatapplictionlikewhastapp.featureHome.repository.FirebaseClientRepository
import com.example.chatapplictionlikewhastapp.featureHome.repository.UsersRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class HomeViewModel(private val usersRepository: UsersRepository, private val firebaseClientRepository: FirebaseClientRepository) : ViewModel() {
    private val _recentChatsList = MutableLiveData<List<RecentChatUserDataClass>>()
    val recentChatsList: LiveData<List<RecentChatUserDataClass>> = _recentChatsList

    private lateinit var _contactsListCache: List<ContactsUserinfo>
    private val _contactsList = MutableLiveData<List<ContactsUserinfo>>()
    val contactsList: LiveData<List<ContactsUserinfo>> = _contactsList

    var selectedChat: RecentChatUserDataClass? = null

    fun getRecentChats() {
        val chats = listOf(usersRepository.provideDummyData())
        _recentChatsList.postValue(usersRepository.provideDummyRecentChatsList())
    }

     fun getContactsList() {
        viewModelScope.launch(Dispatchers.IO) {
             val rawContacts = usersRepository.getAllContactsFromDevice()
            _contactsListCache = firebaseClientRepository.checkForAppUsers(rawContacts)
            _contactsList.postValue(_contactsListCache)
        }
    }

//    fun isAppUser(phoneNumber: String): LiveData<Boolean> {
//        val status = MutableLiveData<Boolean>()
//        usersRepository.checkIfAppUser(phoneNumber, object: UsersRepository.UserFetchCallBack {
//            override fun onSuccess(uid: String) {
//                status.postValue(true)
//            }
//
//            override fun onFailure() {
//                status.postValue(false)
//            }
//        })
//
//        return status
//    }

    fun filterContactsList(text: String) {
        val filteredList = ArrayList<ContactsUserinfo>()

        //TODO add number based search
        for(contact in _contactsListCache) {

            if(contact.name.lowercase().contains(text.lowercase()))
                    filteredList.add(contact)
        }

        _contactsList.postValue(filteredList)
    }

}