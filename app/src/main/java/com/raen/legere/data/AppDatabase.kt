package com.raen.legere.data

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.raen.legere.data.dao.*

import com.raen.legere.data.entities.Book
import com.raen.legere.data.entities.BookChapter
import com.raen.legere.data.entities.BookGroup
import com.raen.legere.data.entities.Bookmark
import com.raen.legere.data.entities.Cache
import com.raen.legere.data.entities.Cookie
import com.raen.legere.data.entities.DictRule
import com.raen.legere.data.entities.KeyboardAssist
import com.raen.legere.data.entities.ReadRecord
import com.raen.legere.data.entities.ReplaceRule
import com.raen.legere.data.entities.Server
import com.raen.legere.data.entities.TxtTocRule
import com.raen.legere.help.DefaultData
import org.intellij.lang.annotations.Language
import splitties.init.appCtx
import java.util.Locale

val appDb by lazy {
    Room.databaseBuilder(appCtx, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
        .fallbackToDestructiveMigration()
        .allowMainThreadQueries()
        .addCallback(AppDatabase.dbCallback)
        .build()
}

@Database(
    version = 1,
    exportSchema = false,
    entities = [Book::class, BookGroup::class, BookChapter::class,
        ReplaceRule::class, Cookie::class, Bookmark::class, 
        TxtTocRule::class, ReadRecord::class, Cache::class,
        DictRule::class, KeyboardAssist::class, Server::class]
)
abstract class AppDatabase : RoomDatabase() {

    abstract val bookDao: BookDao
    abstract val bookGroupDao: BookGroupDao
    abstract val bookChapterDao: BookChapterDao
    abstract val replaceRuleDao: ReplaceRuleDao
    abstract val bookmarkDao: BookmarkDao
    abstract val cookieDao: CookieDao
    abstract val txtTocRuleDao: TxtTocRuleDao
    abstract val readRecordDao: ReadRecordDao
    abstract val cacheDao: CacheDao
    abstract val dictRuleDao: DictRuleDao
    abstract val keyboardAssistsDao: KeyboardAssistsDao
    abstract val serverDao: ServerDao

    val bookSourceDao: BookSourceDao get() = BookSourceDao.impl
    val rssSourceDao: RssSourceDao get() = RssSourceDao.impl
    val httpTTSDao: HttpTTSDao get() = HttpTTSDao.impl
    val rssArticleDao: RssArticleDao get() = RssArticleDao.impl
    val rssReadRecordDao: RssReadRecordDao get() = RssReadRecordDao.impl
    val rssStarDao: RssStarDao get() = RssStarDao.impl
    val ruleSubDao: RuleSubDao get() = RuleSubDao.impl
    val searchBookDao: SearchBookDao get() = SearchBookDao.impl
    val searchKeywordDao: SearchKeywordDao get() = SearchKeywordDao.impl

    companion object {

        const val DATABASE_NAME = "legere.db"

        const val BOOK_TABLE_NAME = "books"

        val dbCallback = object : Callback() {

            override fun onCreate(db: SupportSQLiteDatabase) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    try {
                        Log.d("AppDatabaseCallback", "准备 设置 locale for API ${Build.VERSION.SDK_INT}...")
                        db.setLocale(Locale.getDefault())
                        Log.d("AppDatabaseCallback", "成功 设置 locale for API ${Build.VERSION.SDK_INT}.")
                    } catch (e: Exception) {
                        Log.e("AppDatabaseCallback", "错误 设置 locale in onCreate for API ${Build.VERSION.SDK_INT}", e)
                    }
                }
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                @Language("sql")
                val insertBookGroupAllSql = """
                    insert into book_groups(groupId, groupName, 'order', show) 
                    select ${BookGroup.IdAll}, 'All', -10, 1
                    where not exists (select * from book_groups where groupId = ${BookGroup.IdAll})
                """.trimIndent()
                db.execSQL(insertBookGroupAllSql)
                
                @Language("sql")
                val insertBookGroupLocalSql = """
                    insert into book_groups(groupId, groupName, 'order', enableRefresh, show) 
                    select ${BookGroup.IdLocal}, 'Local', -9, 0, 1
                    where not exists (select * from book_groups where groupId = ${BookGroup.IdLocal})
                """.trimIndent()
                db.execSQL(insertBookGroupLocalSql)
                
                @Language("sql")
                val insertBookGroupMusicSql = """
                    insert into book_groups(groupId, groupName, 'order', show) 
                    select ${BookGroup.IdAudio}, 'Audio', -8, 1
                    where not exists (select * from book_groups where groupId = ${BookGroup.IdAudio})
                """.trimIndent()
                db.execSQL(insertBookGroupMusicSql)
                
                @Language("sql")
                val insertBookGroupLocalNoneGroupSql = """
                    insert into book_groups(groupId, groupName, 'order', show) 
                    select ${BookGroup.IdLocalNone}, 'Local Ungrouped', -6, 0
                    where not exists (select * from book_groups where groupId = ${BookGroup.IdLocalNone})
                """.trimIndent()
                db.execSQL(insertBookGroupLocalNoneGroupSql)
                
                @Language("sql")
                val insertBookGroupVideoSql = """
                    insert into book_groups(groupId, groupName, 'order', show) 
                    select ${BookGroup.IdVideo}, 'Video', -5, 1
                    where not exists (select * from book_groups where groupId = ${BookGroup.IdVideo})
                    """.trimIndent()
                db.execSQL(insertBookGroupVideoSql)
                
                db.query("select * from keyboardAssists order by serialNo").use {
                    if (it.count == 0) {
                        DefaultData.keyboardAssists.forEach { keyboardAssist ->
                            val contentValues = ContentValues().apply {
                                put("type", keyboardAssist.type)
                                put("key", keyboardAssist.key)
                                put("value", keyboardAssist.value)
                                put("serialNo", keyboardAssist.serialNo)
                            }
                            db.insert(
                                "keyboardAssists",
                                SQLiteDatabase.CONFLICT_REPLACE,
                                contentValues
                            )
                        }
                    }
                }
            }
        }

    }

}