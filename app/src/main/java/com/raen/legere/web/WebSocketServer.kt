package com.raen.legere.web

import fi.iki.elonen.NanoWSD
import com.raen.legere.service.WebService

class WebSocketServer(port: Int) : NanoWSD(port) {

    override fun openWebSocket(handshake: IHTTPSession): WebSocket? {
        WebService.serve()
        return null
    }
}
