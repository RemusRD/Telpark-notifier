import com.fasterxml.jackson.annotation.JsonProperty
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.fuel.jackson.responseObject
import me.ivmg.telegram.bot
import mu.KotlinLogging
import java.io.InputStream
import java.io.OutputStream
import java.util.*

val TELEGRAM_TOKEN = System.getenv("TELEGRAM_TOKEN") ?: "default_value"
val TELPARK_API_KEY = System.getenv("TELPARK_API_KEY") ?: "default_value"
val BASE64_TELPARK_CREDENTIALS = System.getenv("BASE64_TELPARK_CREDENTIALS") ?: "default_value"
val loginJsonPayload =
    String(Base64.getDecoder().decode(BASE64_TELPARK_CREDENTIALS))
private val logger = KotlinLogging.logger {}

fun handler(input: InputStream, output: OutputStream) {
    logger.info { "Telpark notifier version: 0.0.2-SNAPSHOT" }
    val defaultHeaders = mapOf(
        "X-EOS-CLIENT-TOKEN" to TELPARK_API_KEY,
        "User-Agent" to "com.delaware.empark/3.12.00-101",
        "Host" to "eos.empark.com"
    )
    logger.info { "Building token request..." }
    val emparkUrl = "https://eos.empark.com/api/v1.0/"
    val telparkToken = "${emparkUrl}auth/accounts/".httpPost()
        .header(
            defaultHeaders
        )
        .jsonBody(loginJsonPayload)
        .responseObject<TokenResponse>().third.component1()!!

    logger.info { "Updating further requests with the telpark token.." }
    val authenticatedHeaders = defaultHeaders + ("X-EOS-USER-TOKEN" to (telparkToken.token))

    logger.info { "Querying vehicles" }
    queryVehicles(emparkUrl, telparkToken, authenticatedHeaders)

    logger.info { "Querying active sessions..." }
    val passesResponse =
        "${emparkUrl}parking/passes/?account=${telparkToken.accountId}&parking_context_owner_token=1&is_expired=false&was_activated=true"
            .httpGet()
            .header(authenticatedHeaders)
            .responseObject<Array<Pass>>().third.component1()!!


    val bot = bot {
        token = TELEGRAM_TOKEN
    }
    if (passesResponse.isNullOrEmpty()) {
        logger.info { "Sending telegram message" }
        bot.sendMessage(chatId = 632571873, text = "No tienes bonos activos, paga el parking!")
    } else {
        logger.info { "Not Sending telegram message" }
    }

}

private fun queryVehicles(
    emparkUrl: String,
    token: TokenResponse,
    authenticatedHeaders: Map<String, String>
) {
    val vehicles = "${emparkUrl}accounts/${token.accountId}"
        .httpGet()
        .header(authenticatedHeaders)
        .responseObject<String>().third
}

data class TokenResponse(
    @JsonProperty("account_token") val accountId: String,
    @JsonProperty("user_session_token") val token: String
)

data class PassesResponse(
    val passes: List<Pass>
)

data class Pass(
    val token: String
)


