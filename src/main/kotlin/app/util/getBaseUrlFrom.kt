package app.util

import org.springframework.web.util.UriComponentsBuilder
import javax.servlet.http.HttpServletRequest

fun getBaseUrlFrom(httpRequest: HttpServletRequest) = UriComponentsBuilder
        .fromHttpUrl(httpRequest.requestURL.toString())
        .replacePath(httpRequest.contextPath)
        .build()
        .toUriString()