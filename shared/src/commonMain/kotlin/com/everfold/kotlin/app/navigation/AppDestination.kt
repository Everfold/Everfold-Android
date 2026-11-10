package com.everfold.kotlin.app.navigation

import com.everfold.kotlin.ui.components.AppSymbol

enum class AppDestination(val label: String, val icon: AppSymbol) {
    Sessions("会话", AppSymbol.Chat),
    Connections("连接", AppSymbol.Connection),
    Settings("设置", AppSymbol.Settings),
}
