package eu.kanade.tachiyomi.extension.en.orderofthestick

import eu.kanade.tachiyomi.network.GET
import eu.kanade.tachiyomi.network.POST
import eu.kanade.tachiyomi.source.model.*
import eu.kanade.tachiyomi.source.online.ParsedHttpSource
import eu.kanade.tachiyomi.util.asJsoup
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class OrderOfTheStick : ParsedHttpSource() {
    override val name = "OrderOfTheStick"

    override val baseUrl = "http://www.giantitp.com"

    override val lang = "en"

    override val supportsLatest = true

    override val client: OkHttpClient = network.cloudflareClient

    private val thumbnailUrl = "$baseUrl/Images/comics/oots/Comics_OOTS_Pic001.gif"

    override fun popularMangaSelector() = "p.ComicList"

    override fun popularMangaRequest(page: Int)
        = GET("$baseUrl/comics/oots.html", headers)

    override fun popularMangaParse(response: Response): MangasPage {
        val mangas = listOf(SManga.create().apply {
            setUrlWithoutDomain("$baseUrl/comics/oots.html")
            title = "Order of the Stick"
        })
        val hasNextPage = false

        return MangasPage(mangas, hasNextPage)
    }

    override fun popularMangaFromElement(element: Element) = SManga.create().apply {
        setUrlWithoutDomain("$baseUrl/comics/oots.html")
        title = "Order of the Stick"
    }

    override fun popularMangaNextPageSelector() = null

    override fun latestUpdatesSelector() = popularMangaSelector()

    override fun latestUpdatesRequest(page: Int) = popularMangaRequest(page)

    override fun latestUpdatesParse(response: Response) = popularMangaParse(response)

    override fun latestUpdatesFromElement(element: Element) = popularMangaFromElement(element)

    override fun latestUpdatesNextPageSelector() = popularMangaNextPageSelector()

    override fun searchMangaRequest(page: Int, query: String, filters: FilterList): Request {
        throw Exception("Sorry, this source does not support searching.")
        val form = FormBody.Builder().apply {}
        return POST("$baseUrl/search", headers, form.build())
    }

    override fun searchMangaSelector() = popularMangaSelector()

    override fun searchMangaFromElement(element: Element): SManga {
        return popularMangaFromElement(element)
    }

    override fun searchMangaNextPageSelector() = null

    override fun mangaDetailsParse(document: Document) = SManga.create().apply {
        author = "Rich Burlew"
        artist = "Rich Burlew"
        status = SManga.UNKNOWN
        thumbnail_url = thumbnailUrl
    }

    override fun chapterListSelector() = "p.ComicList"

    override fun chapterFromElement(element: Element) = SChapter.create().apply {
        setUrlWithoutDomain(element.select("a").first().attr("href"))
        name = element.text().trim()
        date_upload = 0L
    }

    override fun pageListRequest(chapter: SChapter) = POST(baseUrl + chapter.url, headers)

    override fun pageListParse(response: Response): List<Page> {
        val document = response.asJsoup()
        val page = document.select("img[src*='/comics/images/']").first().attr("src")
        return listOf(
            Page(0, "", "$baseUrl/$page")
        )
    }

    override fun pageListParse(document: Document): List<Page> {
        throw Exception("Not used")
    }

    override fun imageUrlRequest(page: Page) = GET(page.url)

    override fun imageUrlParse(document: Document) = ""
}