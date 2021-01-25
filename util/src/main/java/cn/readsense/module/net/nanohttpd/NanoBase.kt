package cn.readsense.module.net.nanohttpd

import cn.readsense.module.util.DLog
import fi.iki.elonen.NanoHTTPD

class NanoBase : NanoHTTPD(DEFAULT_PORT) {
    override fun serve(session: IHTTPSession): Response {
        val strUri = session.uri
        val method = session.method
        DLog.d("uri: $strUri method:$method")


        return response404("exception not found")
    }

    private fun response404(msg: String): Response {
        return newFixedLengthResponse(
            Response.Status.NOT_FOUND,
            MIME_PLAINTEXT,
            "{\"result\":\"error\",\"msg\":\"$msg\"}"
        )
    }

    private fun responseOk(msg: String): Response {
        return newFixedLengthResponse(
            Response.Status.OK,
            MIME_PLAINTEXT,
            "{\"result\":\"ok\",\"msg\":\"$msg\"}"
        )
    }

    companion object {
        private const val DEFAULT_PORT = 8091
    }
}