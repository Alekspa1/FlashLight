package com.exampl3.flashlight.Presentation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.exampl3.flashlight.Const
import com.exampl3.flashlight.Const.RUSTORE
import com.exampl3.flashlight.Const.THEME_ZABOR
import com.exampl3.flashlight.Data.Room.Database
import com.exampl3.flashlight.Data.ThemeImp
import com.exampl3.flashlight.Data.sharedPreference.SettingsSharedPreference
import com.exampl3.flashlight.Domain.ItemListClickHandler
import com.exampl3.flashlight.Domain.UpgrateRustore
import com.exampl3.flashlight.Presentation.adapters.ListMenuAdapter
import com.exampl3.flashlight.Presentation.adapters.VpAdapter
import com.exampl3.flashlight.R
import com.exampl3.flashlight.databinding.FragmentMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.AdRequest
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class FragmentMain : Fragment() {
    private var bannerAd: BannerAdView? = null

    @Inject
    lateinit var db: Database

    @Inject
    lateinit var pref: SettingsSharedPreference

    @Inject
    lateinit var theme: ThemeImp

    @Inject
    lateinit var upgrare: UpgrateRustore


    private lateinit var itemListClickHandler: ItemListClickHandler
    private lateinit var binding: FragmentMainBinding
    private lateinit var vpAdapter: VpAdapter

    private lateinit var calendarZero: Calendar
    val modelFlashLight: ViewModelFlashLight by activityViewModels()
    private lateinit var adapter: ListMenuAdapter
    private lateinit var pLauncher: ActivityResultLauncher<String>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAll()
        upgrare(binding.tvNewUpgrate)
        theme()
        pLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {}
        modelFlashLight.getListShopingProducts(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                modelFlashLight.statePremiumFlow.collect { premium ->
                    if (premium) {
                        binding.yaBaner.visibility = View.GONE
                        binding.tvNewPremium.text = requireContext().getString(R.string.premium_on)
                        binding.tvNewPremium.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            ContextCompat.getDrawable(requireContext(), R.drawable.ic_premium_on),
                            null, // Top
                            null, // End
                            null  // Bottom
                        )

                    } else {
                        val icon =
                            if (modelFlashLight.getTheme() == THEME_ZABOR) R.drawable.ic_premium_off_zabor
                            else R.drawable.ic_premium_off
                        initYaBaner()
                        binding.yaBaner.visibility = View.VISIBLE
                        binding.tvNewPremium.text =
                            requireActivity().getString(R.string.premium_off)
                        binding.tvNewPremium.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            ContextCompat.getDrawable(requireContext(), icon),
                            null, // Top
                            null, // End
                            null  // Bottom
                        )
                    }

                }
            }
        }

        with(binding) {

            imMenu.setOnClickListener {
                drawer.openDrawer(GravityCompat.START)
            } //  Меню
            tvNewPremium.setOnClickListener {
                modelFlashLight.getAllListProducts(requireContext())
                drawer.closeDrawer(GravityCompat.START)
            } // ПРЕМИУМ
            tvNewUpgrate.setOnClickListener {
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, RUSTORE.toUri()))
                } catch (e: Exception) {
                    Toast.makeText(requireActivity(), "Ошибка", Toast.LENGTH_SHORT).show()
                }
            } // Проверить обновления
            tvTitileMenu.setOnClickListener {
                modelFlashLight.updateCategory("Повседневные")
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(1))
                drawer.closeDrawer(GravityCompat.START)
            }

            imBAddMenu.setOnClickListener {
                if (modelFlashLight.getPremium()) {
                    DialogItemList.alertList(requireActivity(), object : DialogItemList.Listener {
                        override fun onClickItem(
                            name: String,
                            action: Int?,
                            id: Int?,
                            desc: String?,
                            uri: String?,
                            category: String?
                        ) {
                            modelFlashLight.insertCategory(name, requireActivity())
                        }
                    }, null)
                } else Toast.makeText(
                    requireActivity(),
                    "Категории доступны в PREMIUM версии",
                    Toast.LENGTH_SHORT
                ).show()


            }

            draverTvTitileMenu.setOnClickListener {
                stub("Общие дела")
            }
            tvNewSettings.setOnClickListener {
                findNavController().navigate(R.id.action_fragmentMain_to_fragmentSettings)
                drawer.closeDrawer(GravityCompat.START)

            }

        }

    }


    private fun initYaBaner() {
        bannerAd = BannerAdView(requireActivity())
        binding.yaBaner.setAdSize(BannerAdSize.sticky(requireActivity(), 350))
        val adRequest = AdRequest.Builder(Const.BANER).build()
        binding.yaBaner.loadAd(adRequest)

    } // Инициализирую Яндекс Рекламу


    private fun initAll() {
        modelFlashLight.updateCategory(getString(R.string.everyday))
        calendarZero = Calendar.getInstance()

        // инициализирую ViewPager
        vpAdapter = VpAdapter(requireActivity())
        binding.placeHolder.adapter = vpAdapter
        TabLayoutMediator(binding.tabLayout, binding.placeHolder) { tab, pos ->
            tab.text = resources.getStringArray(R.array.vp_title_main)[pos]
        }.attach()

        // инициализировал ресайклер
        itemListClickHandler = ItemListClickHandler(
            requireContext(),
            modelFlashLight,
            binding.drawer,
            binding.tabLayout,
            db
        )
        val rcView = binding.rcView
        adapter = ListMenuAdapter(itemListClickHandler, pref, theme)
        rcView.layoutManager = LinearLayoutManager(requireActivity())
        rcView.adapter = adapter


        modelFlashLight.updateAlarm(calendarZero.timeInMillis)

        modelFlashLight.getAllListCategory().asLiveData().observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }


    } // Инициализирую все


    private fun stub(text: String) {
        Toast.makeText(
            requireActivity(),
            "$text появятся в следующих обновлениях",
            Toast.LENGTH_SHORT
        ).show()
    }


    private fun theme() {

        with(modelFlashLight) {
            with(binding) {

                val listView = mapOf<Const.Action, Map<View, Int>>(
                    Const.Action.BACKGROUND_RESOURCE to
                            mapOf
                                (
                                drawer to R.drawable.zabor,
                                navView to R.drawable.zabor,
                                cardZone to R.color.black,
                                cardZone2 to R.color.black,
                                cardZone3 to R.color.black,
                                cardZone4 to R.color.black,
                                tvTitileMenu to R.drawable.button_background_item_category_zabor,
                                draverTvTitileMenu to R.drawable.button_background_item_category_zabor
                            ),
                    Const.Action.IMAGE_RESOURCE to
                            mapOf
                                (
                                imMenuMain to R.drawable.ic_menu_zabor,
                                imSharedMain to R.drawable.ic_share_zabor,
                                imSharedMain to R.drawable.ic_share_zabor,
                                imBAddMenu to R.drawable.ic_add_zabor
                            ),
                    Const.Action.TEXT_IMAGE to
                            mapOf
                                (
                                tvNewUpgrate to R.drawable.ic_update_zabor,
                                tvNewSettings to R.drawable.ic_settings_zabor
                            ),
                    Const.Action.TEXT_STYLE to mapOf
                        (
                        tvNewPremium to R.style.StyleButtonZabor,
                        tvNewUpgrate to R.style.StyleButtonZabor,
                        tvNewSettings to R.style.StyleButtonZabor,
                        tvCategoryDrawer to R.style.StyleMenuZabor,
                        tvTitileMenu to R.style.StyleItemZabor,
                        draverTvTitileMenu to R.style.StyleItemZabor
                    )
                )

                if (getTheme() == THEME_ZABOR) {
                    modelFlashLight.setView(listView)
                }
                modelFlashLight.setSize(listView)


            }
        }


    }
}