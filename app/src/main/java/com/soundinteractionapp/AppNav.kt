package com.soundinteractionapp

sealed class Screen(val route: String) {
    object Auth : Screen("auth")
    object Welcome : Screen("welcome")
    object Profile : Screen("profile")
    object FreePlay : Screen("freeplay")
    object Relax : Screen("relax")
    object Game : Screen("game")

    object CatInteraction : Screen("interaction/cat")
    object PianoInteraction : Screen("interaction/piano")
    object DogInteraction : Screen("interaction/dog")
    object BirdInteraction : Screen("interaction/bird")
    object DrumInteraction : Screen("interaction/drum")
    object BellInteraction : Screen("interaction/bell")
    object OceanInteraction : Screen("interaction/ocean")
    object RainInteraction : Screen("interaction/rain")
    object WindInteraction : Screen("interaction/wind")

    object GameLevel1 : Screen("game/level1")
    object GameLevel2 : Screen("game/level2")
    object GameLevel3 : Screen("game/level3")
    object GameLevel4 : Screen("game/level4")
}