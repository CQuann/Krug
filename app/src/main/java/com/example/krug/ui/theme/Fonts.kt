package com.example.krug.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.example.krug.R

object Fonts {
    val Montserrat = FontFamily(
        Font(R.font.montserrat_regular, FontWeight.Normal),
        Font(R.font.montserrat_italic, FontWeight.Normal, FontStyle.Italic),
        Font(R.font.montserrat_semi_bold, FontWeight.SemiBold),
        Font(R.font.montserrat_bold, FontWeight.Bold)
    )

    val SourceSansPro = FontFamily(
        Font(R.font.source_sans_pro_regular, FontWeight.Normal),
        Font(R.font.source_sans_pro_semi_bold, FontWeight.SemiBold),
        Font(R.font.source_sans_pro_bold, FontWeight.Bold)
    )
}