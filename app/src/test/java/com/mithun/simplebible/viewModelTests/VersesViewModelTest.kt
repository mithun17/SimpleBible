package com.mithun.simplebible.viewModelTests

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import app.cash.turbine.test
import com.mithun.simplebible.R
import com.mithun.simplebible.data.repository.Resource
import com.mithun.simplebible.data.repository.VersesRepository
import com.mithun.simplebible.testutils.TestCoroutineRule
import com.mithun.simplebible.testutils.TestDataProvider
import com.mithun.simplebible.utilities.ResourcesUtil
import com.mithun.simplebible.viewmodels.VersesViewModel
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import kotlin.time.ExperimentalTime

@ExperimentalTime
@RunWith(PowerMockRunner::class)
@PrepareForTest(VersesRepository::class, ResourcesUtil::class)
@ExperimentalCoroutinesApi
class VersesViewModelTest {

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @Mock
    private lateinit var mockVersesRepository: VersesRepository

    @Mock
    private lateinit var mockResourcesUtil: ResourcesUtil

    private lateinit var testBibleId: String
    private lateinit var testChapterId: String
    private lateinit var genericErrorString: String
    private lateinit var genericErrorFromStringResource: String

    @Rule
    @JvmField
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val versesViewModel by lazy {
        VersesViewModel(mockVersesRepository, mockResourcesUtil)
    }

    @Before
    fun setup() {
        mockVersesRepository = PowerMockito.mock(VersesRepository::class.java)
        mockResourcesUtil = PowerMockito.mock(ResourcesUtil::class.java)
        testBibleId = "123"
        testChapterId = "JHN:3"
    }

    @Test
    fun `getVersesTest success`() = testCoroutineRule.testDispatcher.runBlockingTest {
        val sampleVerseData = TestDataProvider.getTestVerseEntities()

        Mockito.`when`(mockVersesRepository.getAllVersesForChapter(testBibleId, testChapterId))
            .thenReturn(sampleVerseData)

        versesViewModel.verses.test {
            versesViewModel.getVerses(testBibleId, testChapterId)
            assertEquals(expectItem()::class, Resource.Loading::class)
            assertEquals(expectItem()::class, Resource.Loading::class)
            val successResponse = expectItem()
            assertTrue(successResponse is Resource.Success)
            assertTrue(successResponse.data?.first?.size == 3)
            successResponse.data?.first?.forEachIndexed { index, verse ->
                assertTrue(verse.number == (index + 1))
                assertTrue(verse.reference == "John ${index + 1}")
                assertTrue(verse.text == "verse text ${index + 1}")
            }
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `getVersesTest error`() = testCoroutineRule.testDispatcher.runBlockingTest {
        genericErrorString = "Unknown error"
        val sampleVerseData = RuntimeException(genericErrorString)
        Mockito.`when`(mockVersesRepository.getAllVersesForChapter(testBibleId, testChapterId))
            .thenThrow(sampleVerseData)

        versesViewModel.verses.test {
            versesViewModel.getVerses(testBibleId, testChapterId)
            assertEquals(expectItem()::class, Resource.Loading::class)
            assertEquals(expectItem()::class, Resource.Loading::class)
            val errorResponse = expectItem()
            assertTrue(errorResponse is Resource.Error)
            assertTrue(errorResponse.message == genericErrorString)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `getVersesTest error with no error text`() =
        testCoroutineRule.testDispatcher.runBlockingTest {
            genericErrorFromStringResource = "Something went wrong. please try again later."
            val sampleVerseData = RuntimeException()
            Mockito.`when`(mockResourcesUtil.getString(R.string.errorGenericString))
                .thenReturn(genericErrorFromStringResource)
            Mockito.`when`(mockVersesRepository.getAllVersesForChapter(testBibleId, testChapterId))
                .thenThrow(sampleVerseData)

            versesViewModel.verses.test {
                versesViewModel.getVerses(testBibleId, testChapterId)
                assertEquals(expectItem()::class, Resource.Loading::class)
                assertEquals(expectItem()::class, Resource.Loading::class)
                val errorResponse = expectItem()
                assertTrue(errorResponse is Resource.Error)
                assertTrue(errorResponse.message == genericErrorFromStringResource)
                cancelAndConsumeRemainingEvents()
            }
        }
}
