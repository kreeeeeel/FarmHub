package com.project.steamfarm.model

data class UserModel(
    var username: String,
    var userType: UserType = UserType.WAIT_AUTH,
    var name: String? = null,
    var photo: String? = null,
    var password: String,
)

enum class UserType {
    WAIT_AUTH, BAD_AUTH, AUTH_COMPLETED
}