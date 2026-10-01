package com.raen.legere.ui.main.my

import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.preference.Preference
import com.raen.legere.R
import com.raen.legere.base.BaseFragment
import com.raen.legere.constant.EventBus
import com.raen.legere.constant.PreferKey
import com.raen.legere.databinding.FragmentMyConfigBinding
import com.raen.legere.help.config.ThemeConfig
import com.raen.legere.lib.dialogs.selector
import com.raen.legere.lib.prefs.NameListPreference
import com.raen.legere.lib.prefs.SwitchPreference
import com.raen.legere.lib.prefs.fragment.PreferenceFragment
import com.raen.legere.lib.theme.primaryColor
import com.raen.legere.service.WebService
import com.raen.legere.ui.about.AboutActivity
import com.raen.legere.ui.about.ReadRecordActivity
import com.raen.legere.ui.book.bookmark.AllBookmarkActivity
// import com.raen.legere.ui.book.source.manage.BookSourceActivity
import com.raen.legere.ui.book.toc.rule.TxtTocRuleActivity
import com.raen.legere.ui.config.ConfigActivity
import com.raen.legere.ui.config.ConfigTag
import com.raen.legere.ui.dict.rule.DictRuleActivity
import com.raen.legere.ui.file.FileManageActivity
import com.raen.legere.ui.main.MainFragmentInterface
import com.raen.legere.ui.replace.ReplaceRuleActivity
import com.raen.legere.utils.LogUtils
import com.raen.legere.utils.getPrefBoolean
import com.raen.legere.utils.observeEventSticky
import com.raen.legere.utils.openUrl
import com.raen.legere.utils.putPrefBoolean
import com.raen.legere.utils.sendToClip
import com.raen.legere.utils.setEdgeEffectColor
import com.raen.legere.utils.showHelp
import com.raen.legere.utils.startActivity
import com.raen.legere.utils.viewbindingdelegate.viewBinding

class MyFragment() : BaseFragment(R.layout.fragment_my_config), MainFragmentInterface {

    constructor(position: Int) : this() {
        val bundle = Bundle()
        bundle.putInt("position", position)
        arguments = bundle
    }

    override val position: Int? get() = arguments?.getInt("position")

    private val binding by viewBinding(FragmentMyConfigBinding::bind)

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        setSupportToolbar(binding.titleBar.toolbar)
        val fragmentTag = "prefFragment"
        var preferenceFragment = childFragmentManager.findFragmentByTag(fragmentTag)
        if (preferenceFragment == null) preferenceFragment = MyPreferenceFragment()
        childFragmentManager.beginTransaction()
            .replace(R.id.pre_fragment, preferenceFragment, fragmentTag).commit()
    }

    override fun onCompatCreateOptionsMenu(menu: Menu) {
        menuInflater.inflate(R.menu.main_my, menu)
    }

    override fun onCompatOptionsItemSelected(item: MenuItem) {
        when (item.itemId) {
            R.id.menu_help -> showHelp("appHelp")
        }
    }

    /**
     * 配置
     */
    class MyPreferenceFragment : PreferenceFragment(),
        SharedPreferences.OnSharedPreferenceChangeListener {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            /*
            putPrefBoolean(PreferKey.webService, WebService.isRun)
            */
            addPreferencesFromResource(R.xml.pref_main)
            /*
            findPreference<SwitchPreference>("webService")?.onLongClick {
                if (!WebService.isRun) {
                    return@onLongClick false
                }
                context?.selector(arrayListOf("复制地址", "浏览器打开")) { _, i ->
                    when (i) {
                        0 -> context?.sendToClip(it.summary.toString())
                        1 -> context?.openUrl(it.summary.toString())
                    }
                }
                true
            }
            observeEventSticky<String>(EventBus.WEB_SERVICE) {
                findPreference<SwitchPreference>(PreferKey.webService)?.let {
                    it.isChecked = WebService.isRun
                    it.summary = if (WebService.isRun) {
                        WebService.hostAddress
                    } else {
                        getString(R.string.web_service_desc)
                    }
                }
            }
            */
            findPreference<NameListPreference>(PreferKey.themeMode)?.let {
                it.setOnPreferenceChangeListener { _, _ ->
                    view?.post { ThemeConfig.applyDayNight(requireContext()) }
                    true
                }
            }
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            listView.setEdgeEffectColor(primaryColor)
            listView.addOnScrollListener(object : androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: androidx.recyclerview.widget.RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 10) {
                        com.raen.legere.utils.postEvent("hideBottomBar", true)
                    } else if (dy < -10) {
                        com.raen.legere.utils.postEvent("hideBottomBar", false)
                    }
                }
            })
        }

        override fun onResume() {
            super.onResume()
            preferenceManager.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
        }

        override fun onPause() {
            preferenceManager.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
            super.onPause()
        }

        override fun onSharedPreferenceChanged(
            sharedPreferences: SharedPreferences?,
            key: String?
        ) {
            when (key) {
                /*
                PreferKey.webService -> {
                    if (requireContext().getPrefBoolean("webService")) {
                        WebService.start(requireContext())
                    } else {
                        WebService.stop(requireContext())
                    }
                }
                */

                "recordLog" -> LogUtils.upLevel()
            }
        }

        override fun onPreferenceTreeClick(preference: Preference): Boolean {
            when (preference.key) {
                // "bookSourceManage" -> {}
                "replaceManage" -> startActivity<ReplaceRuleActivity>()
                "dictRuleManage" -> startActivity<DictRuleActivity>()
                "txtTocRuleManage" -> startActivity<TxtTocRuleActivity>()
                "bookmark" -> startActivity<AllBookmarkActivity>()
                "setting" -> startActivity<ConfigActivity> {
                    putExtra("configTag", ConfigTag.OTHER_CONFIG)
                }

                "web_dav_setting" -> startActivity<ConfigActivity> {
                    putExtra("configTag", ConfigTag.BACKUP_CONFIG)
                }

                "theme_setting" -> startActivity<ConfigActivity> {
                    putExtra("configTag", ConfigTag.THEME_CONFIG)
                }

                "fileManage" -> startActivity<FileManageActivity>()
                "readRecord" -> startActivity<ReadRecordActivity>()
                "about" -> startActivity<AboutActivity>()
                "exit" -> activity?.finish()
            }
            return super.onPreferenceTreeClick(preference)
        }


    }
}