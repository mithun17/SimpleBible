package com.mithun.simplebible.data.api

import com.google.gson.JsonObject
import com.mithun.simplebible.data.database.model.Bible
import com.mithun.simplebible.data.database.model.Book
import com.mithun.simplebible.data.model.BaseData
import com.mithun.simplebible.data.model.Chapter
import com.mithun.simplebible.utilities.ASV_BIBLE_ID
import retrofit2.http.GET
import retrofit2.http.Path

interface BibleApi {

    @GET("bibles")
    suspend fun getBibles(): BaseData<List<Bible>>

    @GET("bibles/{bibleId}/books?include-chapters=true")
    suspend fun getBooks(@Path("bibleId") bibleId: String = ASV_BIBLE_ID): BaseData<List<Book>>

    @GET("bibles/{bibleId}/chapters/{chapterId}?content-type=json&include-notes=false&include-titles=false&include-chapter-numbers=false&include-verse-numbers=false&include-verse-spans=false")
    suspend fun getChapter(@Path("bibleId") bibleId: String = ASV_BIBLE_ID, @Path("chapterId") chapterId: String): BaseData<Chapter>

    @GET("bibles/{bibleId}/chapters/{chapterId}?content-type=json&include-notes=false&include-titles=false&include-chapter-numbers=false&include-verse-numbers=false&include-verse-spans=false")
    suspend fun getChapterJson(@Path("bibleId") bibleId: String = ASV_BIBLE_ID, @Path("chapterId") chapterId: String): JsonObject
}
