package com.tms.financiapp

import com.google.common.truth.Truth
import com.google.firebase.Firebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import com.tms.financiapp.controllers.BankAccountController
import com.tms.financiapp.helpers.Helper
import com.tms.financiapp.models.BankAccount
import com.tms.financiapp.models.User
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BankAccountControllerTest : BaseTestCase() {

    private lateinit var mockFirestore: FirebaseFirestore
    private lateinit var mockDocumentReference: DocumentReference
    private lateinit var mockCollectionReference: CollectionReference
    private lateinit var bankAccountController: BankAccountController
    @Before
    override fun setUp() {
        super.setUp()
        MockitoAnnotations.openMocks(this)
        mockFirestore = mock(FirebaseFirestore::class.java)
        mockDocumentReference = mock(DocumentReference::class.java)
        mockCollectionReference = mock(CollectionReference::class.java)

        // Configura el comportamiento del mockFirestore
        `when`(mockFirestore.collection("users")).thenReturn(mockCollectionReference)
        `when`(mockCollectionReference.document(Mockito.anyString())).thenReturn(mockDocumentReference)

        `when`(mockFirestore.collection("accounts")).thenReturn(mockCollectionReference)
        `when`(mockCollectionReference.document(ArgumentMatchers.anyString())).thenReturn(mockDocumentReference)

        bankAccountController = BankAccountController()
        bankAccountController.db = mockFirestore
    }
    @Test
    fun `firestore should delegate to FirebaseFirestore#getInstance()`() {
        Truth.assertThat(Firebase.firestore).isSameInstanceAs(FirebaseFirestore.getInstance())
    }

    @Test
    fun addBankAccount() {
        val userId = "testUserId"
        val bankAccount = BankAccount(
            accountType = 1,
            userId = userId,
            accountNumber = (0..9999).random().toString(),
            balance = 1000.0,
            isActive = true,
            openDateAccount = Helper().getCurrentDateString()
        )

        bankAccountController.addBankAccount(userId, bankAccount)

        verify(mockDocumentReference).update(Mockito.eq("bankAccounts"), Mockito.any())
    }

    @Test
    fun getBankAccounts() {
        val userId = "testUserId"
        val mockUser = User(
            id = userId,
            name = "",
            idType = 0,
            idNumber = "",
            email = "",
            bankAccounts = emptyList(),
            transactions = emptyList()
        )
        val mockDocumentSnapshot = Mockito.mock(DocumentSnapshot::class.java) as DocumentSnapshot // Crear un DocumentSnapshot mock
        `when`(mockDocumentSnapshot.exists()).thenReturn(true)
        `when`(mockDocumentSnapshot.toObject(User::class.java)).thenReturn(mockUser)
        `when`(mockDocumentReference.get()).thenReturn(TaskMock(mockDocumentSnapshot))

        var retrievedAccounts: List<BankAccount>? = null
        bankAccountController.getBankAccounts(userId) { accounts ->
            retrievedAccounts = accounts
        }

        // Verificar que se llame al método correcto y que el resultado sea el esperado
        Truth.assertThat(retrievedAccounts).isEqualTo(mockUser.bankAccounts)
    }

}