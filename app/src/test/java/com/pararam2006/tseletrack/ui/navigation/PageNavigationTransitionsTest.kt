package com.pararam2006.tseletrack.ui.navigation

import org.junit.Assert.assertEquals
import org.junit.Test

class PageNavigationTransitionsTest {
    @Test
    fun `moving to a tab on the right resolves to forward animation`() {
        assertEquals(
            PageNavigationDirection.Forward,
            resolvePageNavigationDirection(
                initialPageIndex = 0,
                targetPageIndex = 2,
                fallback = PageNavigationDirection.Backward,
            ),
        )
    }

    @Test
    fun `moving from profile to route resolves to backward animation`() {
        assertEquals(
            PageNavigationDirection.Backward,
            resolvePageNavigationDirection(
                initialPageIndex = 3,
                targetPageIndex = 0,
                fallback = PageNavigationDirection.Forward,
            ),
        )
    }

    @Test
    fun `nested destination uses navigation operation fallback`() {
        assertEquals(
            PageNavigationDirection.Forward,
            resolvePageNavigationDirection(
                initialPageIndex = 1,
                targetPageIndex = null,
                fallback = PageNavigationDirection.Forward,
            ),
        )
        assertEquals(
            PageNavigationDirection.Backward,
            resolvePageNavigationDirection(
                initialPageIndex = null,
                targetPageIndex = 1,
                fallback = PageNavigationDirection.Backward,
            ),
        )
    }

    @Test
    fun `same tab does not animate`() {
        assertEquals(
            PageNavigationDirection.Stationary,
            resolvePageNavigationDirection(
                initialPageIndex = 2,
                targetPageIndex = 2,
                fallback = PageNavigationDirection.Forward,
            ),
        )
    }
}
