package com.project.steamfarm.retrofit.response

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "profile", strict = false)
data class SteamProfileResponse(
    @field:Element(name = "avatarFull") var avatar: String = "",
)