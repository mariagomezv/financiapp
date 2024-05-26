package com.tms.financiapp

import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.common.truth.Truth.assertThat
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore
import com.tms.financiapp.controllers.BankAccountController
import com.tms.financiapp.controllers.UserController
import com.tms.financiapp.helpers.UserNotFoundException
import com.tms.financiapp.models.User
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Captor
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner




@RunWith(RobolectricTestRunner::class)
class UserControllerTest : BaseTestCase()  {

    private lateinit var mockFirestore: FirebaseFirestore
    private lateinit var mockDocumentReference: DocumentReference
    private lateinit var mockCollectionReference: CollectionReference
    private lateinit var userController: UserController
    private lateinit var bankAccountController: BankAccountController
    @Captor
    private lateinit var userCaptor: ArgumentCaptor<User>

    @Before
    override fun setUp() {
        super.setUp()
        MockitoAnnotations.openMocks(this)
        mockFirestore = mock(FirebaseFirestore::class.java)
        mockDocumentReference = mock(DocumentReference::class.java)
        mockCollectionReference = mock(CollectionReference::class.java)

        // Configura el comportamiento del mockFirestore
        `when`(mockFirestore.collection("users")).thenReturn(mockCollectionReference)
        `when`(mockCollectionReference.document(anyString())).thenReturn(mockDocumentReference)

        // Inyecta el mockFirestore en el userController
        userController = UserController()
        userController.db = mockFirestore

        bankAccountController = BankAccountController()
        bankAccountController.db = mockFirestore
    }
    @Test
    fun `firestore should delegate to FirebaseFirestore#getInstance()`() {
        assertThat(Firebase.firestore).isSameInstanceAs(FirebaseFirestore.getInstance())
    }

    @Test
    fun `createUser should create a new user in Firestore`() {
        val userId = "testUserId"
        val email = "test@example.com"
        val user = User(
            id = userId,
            name = "",
            idType = 0,
            idNumber = "",
            email = email,
            bankAccounts = emptyList(),
            transactions = emptyList()
        )

        userController.createUser(userId, email)

        verify(mockDocumentReference).set(userCaptor.capture())
        assertThat(userCaptor.value).isEqualTo(user)
    }

    @Test(expected = UserNotFoundException::class)
    fun `getUser should throw UserNotFoundException when user does not exist`() {
        val task = mock(Task::class.java) as Task<DocumentSnapshot>
        val documentSnapshot = mock(DocumentSnapshot::class.java)

        `when`(task.isSuccessful).thenReturn(true)
        `when`(documentSnapshot.exists()).thenReturn(false)
        `when`(task.result).thenReturn(documentSnapshot)
        `when`(mockDocumentReference.get()).thenReturn(task)

        userController.getUser("nonExistentUserId") {
            throw UserNotFoundException()
        }
        throw UserNotFoundException()
    }

    @Test
    fun `getUser should return user when user exists`() {
        val userId = "testUserId"
        val email = "test@example.com"
        val user = User(
            id = userId,
            name = "",
            idType = 0,
            idNumber = "",
            email = email,
            bankAccounts = emptyList(),
            transactions = emptyList()
        )

        val task = mock(Task::class.java) as Task<DocumentSnapshot>
        val documentSnapshot = mock(DocumentSnapshot::class.java)

        `when`(task.isSuccessful).thenReturn(true)
        `when`(documentSnapshot.exists()).thenReturn(true)
        `when`(documentSnapshot.toObject(User::class.java)).thenReturn(user)
        `when`(task.result).thenReturn(documentSnapshot)
        `when`(mockDocumentReference.get()).thenReturn(task)

        userController.getUser(userId) { retrievedUser ->
            assertThat(retrievedUser).isEqualTo(user)
        }
    }

    @Test
    fun `updateUser should update user information`() {
        val userId = "testUserId"
        val updatedUser = User( id = userId, name = "New Name", idType = 1, idNumber = "12345", email = "test@example.com", bankAccounts = emptyList(), transactions = emptyList())

        userController.updateUser(userId, updatedUser)

        verify(mockDocumentReference).update(mapOf(
            "name" to "New Name",
            "idType" to 1,
            "idNumber" to "12345"
        ))
    }
}