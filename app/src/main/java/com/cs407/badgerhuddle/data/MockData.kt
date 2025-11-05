package com.cs407.badgerhuddle.data

data class Game(
    val id: String,
    val sport: String,
    val courtName: String,
    val date: String,
    val time: String,
    val hostName: String,
    val currentPlayers: Int,
    val maxPlayers: Int,
    val skillLevel: String,
    val description: String
)

data class Court(
    val id: String,
    val name: String,
    val location: String,
    val distance: String,
    val capacity: Int,
    val currentPlayers: Int
)

data class Friend(
    val id: String,
    val name: String,
    val isActive: Boolean,
    val currentCourt: String?,
    val favoriteSports: List<String>
)

val mockGames = listOf(
    Game("1", "Basketball", "Nick’s Gym", "Nov 6", "6:00 PM", "Alex", 8, 10, "All Levels", "Evening pickup"),
    Game("2", "Soccer", "Union Fields", "Nov 7", "5:30 PM", "Jordan", 10, 12, "Intermediate", "")
)

val mockCourts = listOf(
    Court("1", "Nick’s Gym", "Lakeshore Dr", "0.5 mi", 10, 6),
    Court("2", "Union Fields", "Langdon St", "1.2 mi", 12, 4),
    Court("3", "Bakke Center", "University Ave", "0.9 mi", 8, 2)
)

val mockFriends = listOf(
    Friend("1", "Taylor", true, "Nick’s Gym", listOf("Basketball", "Tennis")),
    Friend("2", "Jordan", false, null, listOf("Soccer")),
    Friend("3", "Riley", true, "Union Fields", listOf("Volleyball"))
)

