package com.pararam2006.tseletrack.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(Dimension.Radius.compact),
    small = RoundedCornerShape(Dimension.Radius.small),
    medium = RoundedCornerShape(Dimension.Radius.input),
    large = RoundedCornerShape(Dimension.Radius.button),
    extraLarge = RoundedCornerShape(Dimension.Radius.card),
)

object AppShapes {
    val checkbox = RoundedCornerShape(Dimension.Radius.checkbox)
    val taskMarker = RoundedCornerShape(Dimension.Radius.taskMarker)
    val outlinedButton = RoundedCornerShape(Dimension.Radius.outlinedButton)
    val floatingButton = RoundedCornerShape(Dimension.Radius.large)
    val cardLarge = RoundedCornerShape(Dimension.Radius.cardLarge)
    val hero = RoundedCornerShape(Dimension.Radius.hero)
    val logo = RoundedCornerShape(Dimension.Radius.logo)
    val pill = RoundedCornerShape(Dimension.Radius.pill)
}
