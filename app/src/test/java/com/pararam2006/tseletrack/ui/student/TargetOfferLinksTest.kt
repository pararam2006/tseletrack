package com.pararam2006.tseletrack.ui.student

import com.pararam2006.tseletrack.domain.student.TargetOffer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TargetOfferLinksTest {
    @Test
    fun `http and https external urls are accepted and trimmed`() {
        assertEquals("https://trudvsem.ru/offer/1", "  https://trudvsem.ru/offer/1  ".toSafeExternalUrlOrNull())
        assertEquals("http://example.org/path", "http://example.org/path".toSafeExternalUrlOrNull())
    }

    @Test
    fun `unsafe malformed and local schemes are rejected`() {
        assertNull("javascript:alert(1)".toSafeExternalUrlOrNull())
        assertNull("file:///data/user/0/app/token".toSafeExternalUrlOrNull())
        assertNull("intent://offer#Intent;scheme=https;end".toSafeExternalUrlOrNull())
        assertNull("https:///missing-host".toSafeExternalUrlOrNull())
        assertNull("https://user:password@gosuslugi.ru/form".toSafeExternalUrlOrNull())
        assertNull("not a url".toSafeExternalUrlOrNull())
    }

    @Test
    fun `gosuslugi application keeps separate source link`() {
        val links = offer(
            officialUrl = "https://www.gosuslugi.ru/60074/1/form",
            sourceUrl = "https://trudvsem.ru/target-education/view?offeringId=1",
        ).externalLinks()

        assertEquals("https://www.gosuslugi.ru/60074/1/form", links.applicationUrl)
        assertEquals("https://trudvsem.ru/target-education/view?offeringId=1", links.sourceUrl)
        assertTrue(links.shouldShowSourceLink)
    }

    @Test
    fun `gosuslugi subdomain is accepted but deceptive host is not`() {
        assertTrue(
            offer(
                officialUrl = "https://gu.gosuslugi.ru/form",
                sourceUrl = "https://trudvsem.ru/offer/1",
            ).externalLinks().shouldShowSourceLink,
        )
        assertFalse(
            offer(
                officialUrl = "https://gosuslugi.ru.example.org/form",
                sourceUrl = "https://trudvsem.ru/offer/1",
            ).externalLinks().shouldShowSourceLink,
        )
    }

    @Test
    fun `same official and source url does not duplicate action`() {
        val links = offer(
            officialUrl = "HTTPS://TRUDVSEM.RU/offer/1/",
            sourceUrl = "https://trudvsem.ru/offer/1",
        ).externalLinks()

        assertFalse(links.shouldShowSourceLink)
    }

    @Test
    fun `invalid official url falls back to safe source`() {
        val links = offer(
            officialUrl = "javascript:alert(1)",
            sourceUrl = "https://trudvsem.ru/offer/1",
        ).externalLinks()

        assertEquals("https://trudvsem.ru/offer/1", links.applicationUrl)
        assertFalse(links.shouldShowSourceLink)
    }

    @Test
    fun `invalid application and source urls disable navigation`() {
        val links = offer(
            officialUrl = "file:///tmp/offer",
            sourceUrl = "content://offers/1",
        ).externalLinks()

        assertNull(links.applicationUrl)
        assertNull(links.sourceUrl)
        assertFalse(links.shouldShowSourceLink)
    }

    private fun offer(officialUrl: String, sourceUrl: String) = TargetOffer(
        id = "1",
        directionCode = null,
        directionName = "Строительство",
        profile = null,
        programName = null,
        programLevelName = null,
        educationFormName = null,
        universityName = "ТулГУ",
        proposerName = null,
        workplaceAddress = null,
        applicationDeadline = null,
        requestCount = null,
        requestLinkType = null,
        officialApplicationUrl = officialUrl,
        sourceUrl = sourceUrl,
    )
}
