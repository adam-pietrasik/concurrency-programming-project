package pl.kele.concurrency

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform