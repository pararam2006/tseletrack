package com.pararam2006.tseletrack.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost

internal enum class PageNavigationDirection {
    Forward,
    Backward,
    Stationary,
}

internal fun resolvePageNavigationDirection(
    initialPageIndex: Int?,
    targetPageIndex: Int?,
    fallback: PageNavigationDirection,
): PageNavigationDirection = when {
    initialPageIndex == null || targetPageIndex == null -> fallback
    targetPageIndex > initialPageIndex -> PageNavigationDirection.Forward
    targetPageIndex < initialPageIndex -> PageNavigationDirection.Backward
    else -> PageNavigationDirection.Stationary
}

@Composable
internal fun PageNavHost(
    navController: NavHostController,
    startDestination: Any,
    pageIndex: (NavDestination) -> Int?,
    modifier: Modifier = Modifier,
    builder: NavGraphBuilder.() -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = {
            pageEnterTransition(pageIndex, PageNavigationDirection.Forward)
        },
        exitTransition = {
            pageExitTransition(pageIndex, PageNavigationDirection.Forward)
        },
        popEnterTransition = {
            pageEnterTransition(pageIndex, PageNavigationDirection.Backward)
        },
        popExitTransition = {
            pageExitTransition(pageIndex, PageNavigationDirection.Backward)
        },
        builder = builder,
    )
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.pageEnterTransition(
    pageIndex: (NavDestination) -> Int?,
    fallback: PageNavigationDirection,
): EnterTransition {
    val direction = resolvePageNavigationDirection(
        initialPageIndex = pageIndex(initialState.destination),
        targetPageIndex = pageIndex(targetState.destination),
        fallback = fallback,
    )
    if (direction == PageNavigationDirection.Stationary) return EnterTransition.None

    return slideIntoContainer(
        towards = direction.asSlideDirection(),
        animationSpec = tween(
            durationMillis = PageTransitionDurationMillis,
            easing = FastOutSlowInEasing,
        ),
    )
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.pageExitTransition(
    pageIndex: (NavDestination) -> Int?,
    fallback: PageNavigationDirection,
): ExitTransition {
    val direction = resolvePageNavigationDirection(
        initialPageIndex = pageIndex(initialState.destination),
        targetPageIndex = pageIndex(targetState.destination),
        fallback = fallback,
    )
    if (direction == PageNavigationDirection.Stationary) return ExitTransition.None

    return slideOutOfContainer(
        towards = direction.asSlideDirection(),
        animationSpec = tween(
            durationMillis = PageTransitionDurationMillis,
            easing = FastOutSlowInEasing,
        ),
    )
}

private fun PageNavigationDirection.asSlideDirection(): AnimatedContentTransitionScope.SlideDirection =
    when (this) {
        PageNavigationDirection.Forward -> AnimatedContentTransitionScope.SlideDirection.Left
        PageNavigationDirection.Backward -> AnimatedContentTransitionScope.SlideDirection.Right
        PageNavigationDirection.Stationary -> error("Stationary navigation has no slide direction")
    }

private const val PageTransitionDurationMillis = 320
