package ng.bossi.api.utils

import io.ktor.server.plugins.ratelimit.*
import io.ktor.server.routing.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

enum class RateLimits(val rateLimitName: RateLimitName, val limit: Int, val refillPeriod: Duration) {
    // Resource Download
    RESOURCES(RateLimitName("Resources"), 10, 30.minutes),
    // License Checks
    LICENSE_SINGLE(RateLimitName("License-Single"), 5, 1.minutes),
    // FeatureFlag Checks
    FEATUREFLAG(RateLimitName("Feature-Flag"), 60, 1.minutes),
    // Version Checks
    VERSION(RateLimitName("Version"), 10, 1.minutes),
}

fun RateLimitConfig.register(rateLimit: RateLimits) = register(rateLimit.rateLimitName) {
    rateLimiter(limit = rateLimit.limit, refillPeriod = rateLimit.refillPeriod)
}

fun Route.rateLimit(rateLimit: RateLimits, route: Route.() -> Unit) = rateLimit(rateLimit.rateLimitName, route)