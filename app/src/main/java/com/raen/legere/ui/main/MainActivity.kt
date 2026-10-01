@file:Suppress("DEPRECATION")

package com.raen.legere.ui.main

import android.os.Bundle
import android.text.format.DateUtils
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.core.view.postDelayed
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.ViewPager
import com.raen.legere.BuildConfig
import com.raen.legere.R
import com.raen.legere.base.VMBaseActivity
import com.raen.legere.constant.AppConst.appInfo
import com.raen.legere.constant.EventBus
import com.raen.legere.constant.PreferKey
import com.raen.legere.databinding.ActivityMainBinding
import com.raen.legere.databinding.DialogEditTextBinding
import com.raen.legere.help.AppWebDav
import com.raen.legere.help.book.BookHelp
import com.raen.legere.help.config.AppConfig
import com.raen.legere.help.config.LocalConfig
import com.raen.legere.help.coroutine.Coroutine
import com.raen.legere.help.storage.Backup
import com.raen.legere.lib.dialogs.alert
import com.raen.legere.lib.theme.primaryColor
import com.raen.legere.service.BaseReadAloudService
import com.raen.legere.ui.about.CrashLogsDialog
import com.raen.legere.ui.association.ImportBookSourceDialog
import com.raen.legere.ui.association.ImportReplaceRuleDialog
import com.raen.legere.ui.association.ImportRssSourceDialog
import com.raen.legere.ui.main.bookshelf.BaseBookshelfFragment
import com.raen.legere.ui.main.bookshelf.style1.BookshelfFragment1
import com.raen.legere.ui.main.bookshelf.style2.BookshelfFragment2
import com.raen.legere.ui.main.my.MyFragment
import com.raen.legere.ui.widget.dialog.TextDialog
import com.raen.legere.utils.isCreated
import com.raen.legere.utils.observeEvent
import com.raen.legere.utils.setEdgeEffectColor
import com.raen.legere.utils.showDialogFragment
import com.raen.legere.utils.toastOnUi
import com.raen.legere.utils.viewbindingdelegate.viewBinding
import com.raen.legere.utils.startActivity
import com.raen.legere.data.entities.Book
import com.raen.legere.data.entities.BookGroup
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

// Compose Imports
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.raen.legere.ui.components.GlassDefaults
import com.raen.legere.ui.components.GlassSurface
import com.raen.legere.ui.components.LocalHazeState
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource

/**
 * 主界面
 */
@Suppress("PrivatePropertyName")
class MainActivity : VMBaseActivity<ActivityMainBinding, MainViewModel>(),
    MainViewModel.CallBack {

    override val binding by viewBinding(ActivityMainBinding::inflate)
    override val viewModel by viewModels<MainViewModel>()
    private val idBookshelf = 0
    private val idBookshelf1 = 11
    private val idBookshelf2 = 12
    private val idExplore = 1
    private val idRss = 2
    private val idMy = 3
    private var exitTime: Long = 0
    private var bookshelfReselected: Long = 0
    private var exploreReselected: Long = 0
    private var pagePosition = 0
    private val fragmentMap = hashMapOf<Int, Fragment>()
    private var bottomMenuCount = 2
    private val EXIT_INTERVAL = 2000L
    private val realPositions = arrayOf(idBookshelf, idMy)
    private val adapter by lazy {
        TabFragmentPageAdapter(supportFragmentManager)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        initView()
        upHomePage()
        onBackPressedDispatcher.addCallback(this) {
            if (pagePosition != 0) {
                binding.viewPagerMain.currentItem = 0
                return@addCallback
            }
            (fragmentMap[getFragmentId(0)] as? BookshelfFragment2)?.let {
                if (it.back()) {
                    return@addCallback
                }
            }
            if (System.currentTimeMillis() - exitTime > EXIT_INTERVAL) {
                toastOnUi(R.string.double_click_exit)
                exitTime = System.currentTimeMillis()
            } else {
                if (BaseReadAloudService.pause) {
                    finish()
                } else {
                    moveTaskToBack(true)
                }
            }
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        lifecycleScope.launch {
            //隐私协议
            if (!privacyPolicy()) return@launch
            //版本更新
            upVersion()
            //设置本地密码
            setLocalPassword()
            notifyAppCrash()
            //备份同步
            backupSync()
            //设置回调
            viewModel.setActivityCallback(this@MainActivity)
            // Disabled online updates for offline reader
            binding.viewPagerMain.postDelayed(3000) {
                viewModel.postLoad()
            }
        }
    }

    private fun initView() = binding.run {
        viewPagerMain.setEdgeEffectColor(primaryColor)
        viewPagerMain.offscreenPageLimit = 3
        viewPagerMain.adapter = adapter
        viewPagerMain.addOnPageChangeListener(PageChangeCallback())
        setupComposeBottomNavigation()
    }

    @OptIn(ExperimentalFoundationApi::class)
    private fun setupComposeBottomNavigation() {
        binding.composeBottomNavigation.setContent {
            val hazeState = remember { HazeState() }
            var selectedTab by remember { mutableStateOf(0) }
            
            val bookGroups by com.raen.legere.data.appDb.bookGroupDao.show.observeAsState(emptyList())
            val currentGroupId by viewModel.currentGroupId.observeAsState(BookGroup.IdRoot)
            val badgeCount by viewModel.onUpBooksLiveData.observeAsState(0)
            
            var isBottomBarVisible by remember { mutableStateOf(true) }
            
            DisposableEffect(Unit) {
                val observer = androidx.lifecycle.Observer<Boolean> { hide ->
                    isBottomBarVisible = !hide
                }
                com.jeremyliao.liveeventbus.LiveEventBus.get<Boolean>("hideBottomBar")
                    .observeForever(observer)
                onDispose {
                    com.jeremyliao.liveeventbus.LiveEventBus.get<Boolean>("hideBottomBar")
                        .removeObserver(observer)
                }
            }
            
            LaunchedEffect(pagePosition) {
                selectedTab = pagePosition
                isBottomBarVisible = true
            }
            
            MaterialTheme {
                CompositionLocalProvider(LocalHazeState provides hazeState) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Detach viewPagerMain from parent FrameLayout and host inside AndroidView
                        AndroidView(
                            factory = { ctx ->
                                binding.viewPagerMain.apply {
                                    (parent as? ViewGroup)?.removeView(this)
                                }
                            },
                            modifier = Modifier
                                .fillMaxSize()
                                .hazeSource(state = hazeState)
                        )
                        
                        // Floating Frosted Glass UI Container
                        AnimatedVisibility(
                            visible = isBottomBarVisible,
                            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                            modifier = Modifier.align(Alignment.BottomCenter)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .windowInsetsPadding(WindowInsets.navigationBars)
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                        // Category Pills Sub-bar (Only when bookshelf is selected)
                        AnimatedVisibility(
                            visible = selectedTab == 0 && bookGroups.isNotEmpty(),
                            enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                            exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                        ) {
                            GlassSurface(
                                style = GlassDefaults.subtleStyle(),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState())
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    bookGroups.forEach { group ->
                                        val isSelected = currentGroupId == group.groupId
                                        val pillBgColor = if (isSelected) {
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
                                        } else {
                                            Color.Transparent
                                        }
                                        val pillTextColor = if (isSelected) {
                                            MaterialTheme.colorScheme.onPrimary
                                        } else {
                                            MaterialTheme.colorScheme.onSurface
                                        }
                                        
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = pillBgColor,
                                            modifier = Modifier.combinedClickable(
                                                onClick = {
                                                    val bookshelfFragment = fragmentMap[getFragmentId(0)] as? BaseBookshelfFragment
                                                    bookshelfFragment?.selectGroup(group.groupId)
                                                },
                                                onLongClick = {
                                                    val bookshelfFragment = fragmentMap[getFragmentId(0)] as? BaseBookshelfFragment
                                                    bookshelfFragment?.showDialogFragment(
                                                        com.raen.legere.ui.book.group.GroupEditDialog(group)
                                                    )
                                                }
                                            )
                                        ) {
                                            Text(
                                                text = group.groupName,
                                                color = pillTextColor,
                                                style = MaterialTheme.typography.labelMedium,
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        
                        // Floating main navigation bar
                        GlassSurface(
                            style = GlassDefaults.prominentStyle(),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            NavigationBar(
                                containerColor = Color.Transparent,
                                tonalElevation = 0.dp,
                                modifier = Modifier.height(64.dp)
                            ) {
                                NavigationBarItem(
                                    selected = selectedTab == 0,
                                    onClick = {
                                        binding.viewPagerMain.setCurrentItem(0, false)
                                    },
                                    icon = {
                                        BadgedBox(badge = {
                                            if (badgeCount > 0) {
                                                Badge { Text(badgeCount.toString()) }
                                            }
                                        }) {
                                            Icon(Icons.Default.Home, contentDescription = "Library")
                                        }
                                    },
                                    label = { Text("Library") }
                                )
                                
                                NavigationBarItem(
                                    selected = false,
                                    onClick = {
                                        startActivity<com.raen.legere.ui.book.search.SearchActivity>()
                                    },
                                    icon = {
                                        Icon(Icons.Default.Search, contentDescription = "Search")
                                    },
                                    label = { Text("Search") }
                                )
                                
                                NavigationBarItem(
                                    selected = selectedTab == 1,
                                    onClick = {
                                        binding.viewPagerMain.setCurrentItem(1, false)
                                    },
                                    icon = {
                                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                                    },
                                    label = { Text("Settings") }
                                )
                                
                                var showMoreMenu by remember { mutableStateOf(false) }
                                NavigationBarItem(
                                    selected = false,
                                    onClick = { showMoreMenu = true },
                                    icon = {
                                        Box {
                                            Icon(Icons.Default.MoreVert, contentDescription = "More")
                                            DropdownMenu(
                                                expanded = showMoreMenu,
                                                onDismissRequest = { showMoreMenu = false }
                                            ) {
                                                DropdownMenuItem(
                                                    text = { Text("Update Bookshelf") },
                                                    onClick = {
                                                        showMoreMenu = false
                                                        val bookshelfFragment = fragmentMap[getFragmentId(0)] as? BaseBookshelfFragment
                                                        bookshelfFragment?.let {
                                                            viewModel.upToc(it.books, it.onlyUpdateRead)
                                                        }
                                                    }
                                                )
                                                DropdownMenuItem(
                                                    text = { Text("Import Local") },
                                                    onClick = {
                                                        showMoreMenu = false
                                                        val bookshelfFragment = fragmentMap[getFragmentId(0)] as? BaseBookshelfFragment
                                                        bookshelfFragment?.startActivity<com.raen.legere.ui.book.import.local.ImportBookActivity>()
                                                    }
                                                )
                                                DropdownMenuItem(
                                                    text = { Text("Import URL") },
                                                    onClick = {
                                                        showMoreMenu = false
                                                        val bookshelfFragment = fragmentMap[getFragmentId(0)] as? BaseBookshelfFragment
                                                        bookshelfFragment?.showAddBookByUrlAlert()
                                                    }
                                                )
                                                DropdownMenuItem(
                                                    text = { Text("Bookshelf Layout") },
                                                    onClick = {
                                                        showMoreMenu = false
                                                        val bookshelfFragment = fragmentMap[getFragmentId(0)] as? BaseBookshelfFragment
                                                        bookshelfFragment?.configBookshelf()
                                                    }
                                                )
                                                DropdownMenuItem(
                                                    text = { Text("Group Management") },
                                                    onClick = {
                                                        showMoreMenu = false
                                                        val bookshelfFragment = fragmentMap[getFragmentId(0)] as? BaseBookshelfFragment
                                                        bookshelfFragment?.showDialogFragment<com.raen.legere.ui.book.group.GroupManageDialog>()
                                                    }
                                                )
                                                DropdownMenuItem(
                                                    text = { Text("Bookshelf Management") },
                                                    onClick = {
                                                        showMoreMenu = false
                                                        val bookshelfFragment = fragmentMap[getFragmentId(0)] as? BaseBookshelfFragment
                                                        bookshelfFragment?.let {
                                                            it.startActivity<com.raen.legere.ui.book.manage.BookshelfManageActivity> {
                                                                putExtra("groupId", it.groupId)
                                                            }
                                                        }
                                                    }
                                                )
                                                DropdownMenuItem(
                                                    text = { Text("Download Cache") },
                                                    onClick = {
                                                        showMoreMenu = false
                                                        val bookshelfFragment = fragmentMap[getFragmentId(0)] as? BaseBookshelfFragment
                                                        bookshelfFragment?.let {
                                                            it.startActivity<com.raen.legere.ui.book.cache.CacheActivity> {
                                                                putExtra("groupId", it.groupId)
                                                            }
                                                        }
                                                    }
                                                )
                                                DropdownMenuItem(
                                                    text = { Text("App Log") },
                                                    onClick = {
                                                        showMoreMenu = false
                                                        val bookshelfFragment = fragmentMap[getFragmentId(0)] as? BaseBookshelfFragment
                                                        bookshelfFragment?.showDialogFragment<com.raen.legere.ui.about.AppLogDialog>()
                                                    }
                                                )
                                            }
                                        }
                                    },
                                    label = { Text("More") }
                                )
                            }
                        }
                    }
                    }
                }
            }
        }
    }
    }

    /**
     * 用户隐私与协议
     */
    private suspend fun privacyPolicy(): Boolean = suspendCancellableCoroutine sc@{ block ->
        if (LocalConfig.privacyPolicyOk) {
            block.resume(true)
            return@sc
        }
        val privacyPolicy = String(assets.open("privacyPolicy.md").readBytes())
        alert(getString(R.string.privacy_policy), privacyPolicy) {
            positiveButton(R.string.agree) {
                LocalConfig.privacyPolicyOk = true
                block.resume(true)
            }
            negativeButton(R.string.refuse) {
                finish()
                block.resume(false)
            }
        }
    }

    /**
     * 版本更新日志
     */
    private suspend fun upVersion() = suspendCancellableCoroutine sc@{ block ->
        if (LocalConfig.versionCode == appInfo.versionCode) {
            block.resume(null)
            return@sc
        }
        LocalConfig.versionCode = appInfo.versionCode
        if (LocalConfig.isFirstOpenApp) {
            val help = String(assets.open("web/help/md/appHelp.md").readBytes())
            val dialog = TextDialog(getString(R.string.help), help, TextDialog.Mode.MD)
            dialog.setOnDismissListener {
                block.resume(null)
            }
            showDialogFragment(dialog)
        } else if (!BuildConfig.DEBUG) {
            val log = String(assets.open("updateLog.md").readBytes())
            val dialog = TextDialog(getString(R.string.update_log), log, TextDialog.Mode.MD)
            dialog.setOnDismissListener {
                block.resume(null)
            }
            showDialogFragment(dialog)
        } else {
            block.resume(null)
        }
    }

    /**
     * 设置本地密码
     */
    private suspend fun setLocalPassword() = suspendCancellableCoroutine sc@{ block ->
        if (LocalConfig.password != null) {
            block.resume(null)
            return@sc
        }
        alert(R.string.set_local_password, R.string.set_local_password_summary) {
            val editTextBinding = DialogEditTextBinding.inflate(layoutInflater).apply {
                editView.hint = "password"
            }
            customView {
                editTextBinding.root
            }
            onDismiss {
                block.resume(null)
            }
            okButton {
                LocalConfig.password = editTextBinding.editView.text.toString()
            }
            cancelButton {
                LocalConfig.password = ""
            }
        }
    }

    private fun notifyAppCrash() {
        if (!LocalConfig.appCrash || BuildConfig.DEBUG) {
            return
        }
        LocalConfig.appCrash = false
        alert(getString(R.string.draw), "检测到阅读发生了崩溃，是否打开崩溃日志以便报告问题？") {
            yesButton {
                showDialogFragment<CrashLogsDialog>()
            }
            noButton()
        }
    }

    /**
     * 备份同步
     */
    private fun backupSync() {
        if (!AppConfig.autoCheckNewBackup) {
            return
        }
        lifecycleScope.launch {
            val lastBackupFile =
                withContext(IO) { AppWebDav.lastBackUp().getOrNull() } ?: return@launch
            if (lastBackupFile.lastModify - LocalConfig.lastBackup > DateUtils.MINUTE_IN_MILLIS) {
                LocalConfig.lastBackup = lastBackupFile.lastModify
                alert(R.string.restore, R.string.webdav_after_local_restore_confirm) {
                    cancelButton()
                    okButton {
                        viewModel.restoreWebDav(lastBackupFile.displayName)
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Coroutine.async {
            BookHelp.clearInvalidCache()
        }
        if (!BuildConfig.DEBUG) {
            Backup.autoBack(this)
        }
    }

    /**
     * 如果重启太快fragment不会重建,这里更新一下书架的排序
     */
    override fun recreate() {
        (fragmentMap[getFragmentId(0)] as? BaseBookshelfFragment)?.run {
            upSort()
        }
        super.recreate()
    }

    override fun observeLiveBus() {
        observeEvent<String>(EventBus.RECREATE) {
            recreate()
        }
        observeEvent<Boolean>(EventBus.NOTIFY_MAIN) {
            binding.apply {
                if (it) {
                    viewPagerMain.setCurrentItem(bottomMenuCount - 1, false)
                }
            }
        }
        observeEvent<String>(PreferKey.threadCount) {
            viewModel.upPool()
        }
    }

    private fun upHomePage() {
        when (AppConfig.defaultHomePage) {
            "bookshelf" -> {}
            "my" -> binding.viewPagerMain.setCurrentItem(realPositions.indexOf(idMy), false)
        }
    }

    private fun getFragmentId(position: Int): Int {
        val id = realPositions[position]
        if (id == idBookshelf) {
            return if (AppConfig.bookGroupStyle == 1) idBookshelf2 else idBookshelf1
        }
        return id
    }

    private inner class PageChangeCallback : ViewPager.SimpleOnPageChangeListener() {
        override fun onPageSelected(position: Int) {
            pagePosition = position
        }
    }

    private inner class TabFragmentPageAdapter(fm: FragmentManager) :
        FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        private fun getId(position: Int): Int {
            return getFragmentId(position)
        }

        override fun getItemPosition(any: Any): Int {
            val fragment = any as MainFragmentInterface
            val position = fragment.position ?: return POSITION_NONE
            val fragmentId = getId(position)
            if ((fragmentId == idBookshelf1 && any is BookshelfFragment1)
                || (fragmentId == idBookshelf2 && any is BookshelfFragment2)
                || (fragmentId == idMy && any is MyFragment)
            ) {
                return POSITION_UNCHANGED
            }
            return POSITION_NONE
        }

        override fun getItem(position: Int): Fragment {
            return when (getId(position)) {
                idBookshelf1 -> BookshelfFragment1(position)
                idBookshelf2 -> BookshelfFragment2(position)
                else -> MyFragment(position)
            }
        }

        override fun getCount(): Int {
            return bottomMenuCount
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            var fragment = super.instantiateItem(container, position) as Fragment
            if (fragment.isCreated && getItemPosition(fragment) == POSITION_NONE) {
                destroyItem(container, position, fragment)
                fragment = super.instantiateItem(container, position) as Fragment
            }
            fragmentMap[getId(position)] = fragment
            return fragment
        }
    }

    override fun openImportUi(type: Int, source: String) {
        when (type) {
            0 -> showDialogFragment(
                ImportBookSourceDialog(source)
            )
            1 -> showDialogFragment(
                ImportRssSourceDialog(source)
            )
            2 -> showDialogFragment(
                ImportReplaceRuleDialog(source)
            )
        }
    }
}