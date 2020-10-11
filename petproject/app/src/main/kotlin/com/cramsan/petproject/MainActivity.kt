package com.cramsan.petproject

import com.cramsan.petproject.base.BaseActivity
import com.cramsan.petproject.mainmenu.AllPlantListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<AllPlantListViewModel>() {

    override val contentViewLayout: Int
        get() = R.layout.activity_main_menu
    override val toolbarViewId: Int?
        get() = R.id.main_menu_toolbar
    override val logTag: String
        get() = "MainMenuActivity"
}