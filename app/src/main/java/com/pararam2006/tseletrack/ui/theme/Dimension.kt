package com.pararam2006.tseletrack.ui.theme

import androidx.compose.ui.unit.dp

/** Shared layout tokens. Component-specific drawing coordinates should stay with the component. */
object Dimension {
    object Spacing {
        val xxs = 2.dp
        val space3 = 3.dp
        val xs = 4.dp
        val space5 = 5.dp
        val small = 6.dp
        val space7 = 7.dp
        val medium = 8.dp
        val space9 = 9.dp
        val mediumLarge = 10.dp
        val large = 12.dp
        val space13 = 13.dp
        val largeExtra = 14.dp
        val extraLarge = 16.dp
        val space18 = 18.dp
        val xxl = 20.dp
        val space22 = 22.dp
        val xxxl = 24.dp
        val huge = 32.dp
    }

    object Padding {
        val screen = Spacing.extraLarge
        val card = Spacing.largeExtra
        val buttonHorizontal = Spacing.extraLarge
    }

    /** Large, screen-specific gaps that are not part of the reusable spacing scale. */
    object Layout {
        val loginLogoTop = 96.dp
        val loginFormTop = 36.dp
        val adminActionsBottom = 110.dp
        val registrationResultTop = 88.dp
        val registrationResultContent = 23.dp
    }

    object Radius {
        val checkbox = 4.dp
        val taskMarker = 5.dp
        val compact = 6.dp
        val small = 8.dp
        val input = 10.dp
        val outlinedButton = 9.dp
        val button = 12.dp
        val card = 14.dp
        val cardLarge = 16.dp
        val large = 17.dp
        val hero = 18.dp
        val logo = 22.dp
        val pill = 50.dp
    }

    object Size {
        val stroke = 1.dp
        val strokeSelected = 2.dp
        val progressCompact = 5.dp
        val progress = 6.dp
        val progressLarge = 8.dp
        val chip = 30.dp
        val outlinedButton = 36.dp
        val button = 44.dp
        val input = 46.dp
        val navigationIndicatorWidth = 50.dp
        val navigationIndicatorHeight = 27.dp
        val header = 56.dp
        val floatingButton = 58.dp
        val bottomBar = 66.dp
        val metricCard = 78.dp
    }
}
