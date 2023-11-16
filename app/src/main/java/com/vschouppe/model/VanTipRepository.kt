package com.vschouppe.model

import com.vschouppe.R

object VanTipRepository{
    val vantips = listOf<VanTip>(
        VanTip(R.string.vantip_clean, R.string.vantip_info_clean, R.drawable.clean_result),
        VanTip(R.string.vantip_de_rust, R.string.vantip_info_de_rust, R.drawable.de_rust_result),
        VanTip(R.string.vantip_design, R.string.vantip_info_design, R.drawable.design_result),
        VanTip(R.string.vantip_repair, R.string.vantip_info_repair, R.drawable.repair_result),
        VanTip(R.string.vantip_window, R.string.vantip_info_window, R.drawable.window_result)
    )
}


