package com.ohyooo.shared

import com.ohyooo.shared.generated.resources.Res
import com.ohyooo.shared.generated.resources.source_han_sans_sc
import org.jetbrains.compose.resources.FontResource

actual fun getPlatformName(): String = "JS"

actual fun getFont(): FontResource? = Res.font.source_han_sans_sc
