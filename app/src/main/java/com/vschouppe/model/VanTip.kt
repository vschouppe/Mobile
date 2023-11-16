package com.vschouppe.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class VanTip (
    @StringRes val name: Int,
    @StringRes val description: Int,
    @DrawableRes val image: Int
)


