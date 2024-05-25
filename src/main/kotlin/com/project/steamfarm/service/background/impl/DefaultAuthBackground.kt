package com.project.steamfarm.service.background.impl

import com.project.steamfarm.model.UserModel
import com.project.steamfarm.model.UserType
import com.project.steamfarm.repository.Repository
import com.project.steamfarm.repository.impl.UserRepository
import com.project.steamfarm.service.background.AuthBackground
import com.project.steamfarm.service.steam.ClientSteam
import com.project.steamfarm.service.steam.impl.DefaultClientSteam
import java.util.concurrent.CompletableFuture

class DefaultAuthBackground: AuthBackground {

    private val clientSteam: ClientSteam = DefaultClientSteam()
    private val userRepository: Repository<UserModel> = UserRepository()

    override fun authenticate(username: String, password: String) {
        CompletableFuture.supplyAsync {

            userRepository.findById(username)?.let {

                it.time = System.currentTimeMillis()
                userRepository.save(it)

                val isLogin = clientSteam.authentication(username, password)
                it.userType = if (isLogin) UserType.AUTH_COMPLETED else UserType.BAD_AUTH

                clientSteam.getProfileData()?.let { s ->
                    it.photo = s.avatar
                }

                userRepository.save(it)
            }
        }
    }
}